/*
package com.tricon.labs.giventake;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tricon.labs.giventake.adapters.Adapter_CustomSimpleCursor;
import com.tricon.labs.giventake.adapters.CustomDatePicker;
import com.tricon.labs.giventake.database.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ActivityLendAndBorrowAddEntry extends ActivityBase {

    String fromActivity = null;

    String fromUserId=null;
    String fromUserName="";

    String rowId=null;

    //String onlineId=null;


    boolean actionFlag = false; //if false giving or borrowing

    Intent backActivityIntent = null;

    //RecyclerView recyclerView;

    //Adapter_TextRecyclerViewList adapter;


    EditText datePicker, created_date_forDB;

    Adapter_CustomSimpleCursor adapter_CustomSimpleCursor;
    AutoCompleteTextView autoCompleteFromUser;

    Map<String, String> selectedFromUser = new HashMap<String, String>();

    Button saveBtn;

    boolean clearFlag=false;

    DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_and_borrow_add_entry);


        myDb = new DBHelper(this);

        autoCompleteFromUser = (AutoCompleteTextView) findViewById(R.id.fromUser);
        saveBtn = ((Button) currentView.findViewById(R.id.saveBtn));


        Bundle extras = getIntent().getExtras();

        if (extras == null) {
            fromActivity = null;
        } else {
            fromActivity = extras.getString("fromActivity");

            fromUserId = extras.getString("fromUserId", null);
            fromUserName = extras.getString("Name");

            rowId = extras.getString("rowId",null);

        }


        //implementing date picker

        datePicker = ((EditText) currentView.findViewById(R.id.datePicker));
        //created_date is hidden field for serving date to db (YY-mm-dd format)
        created_date_forDB = ((EditText) currentView.findViewById(R.id.created_date));

        //initial date values
        SimpleDateFormat dmy = new SimpleDateFormat("dd-MM-yyyy");
        String dmyDate = dmy.format(new Date());
        datePicker.setText(dmyDate);

        SimpleDateFormat tmpdmy = new SimpleDateFormat("yyyy-MM-dd");
        String tmpdmyDate = tmpdmy.format(new Date());
        created_date_forDB.setText(tmpdmyDate);

        //setting datepicker adapter
        datePicker.setOnClickListener(new CustomDatePicker(ActivityLendAndBorrowAddEntry.this, datePicker, created_date_forDB, false));
        //----implementing date picker


        if (fromActivity.equals("ActivityLendAndBorrow")) {
            //backActivityIntent = new Intent(ActivityLendAndBorrowAddEntry.this, LendAndBorrow.class);
            generateDataForLendNBorrow();

        } else if (fromActivity.equals("ActivityLendAndBorrowPersonal")) {
            backActivityIntent = new Intent(ActivityLendAndBorrowAddEntry.this, ActivityLendAndBorrowIndividual.class);
            backActivityIntent.putExtra("fromActivity", "ActivityLendAndBorrow");
            backActivityIntent.putExtra("fromUserId", "" + fromUserId);
            backActivityIntent.putExtra("userName", fromUserName);
            generateDataForLendNBorrow();

        } else {
            throw new IllegalArgumentException("Invalid  ");
        }


        saveBtn.setOnClickListener(new saveData());
        ((Button) currentView.findViewById(R.id.cancelBtn)).setOnClickListener(new cancelActivity());

    }

    private void generateDataForLendNBorrow() {


        String[] entryAction = {"Giving to", "Borrow from",};

        if(fromUserId!=null){ // add button clicked from individual page
            selectedFromUser=myDb.getUser( Long.parseLong(fromUserId));
            autoCompleteFromUser.setText(selectedFromUser.get("name"));
            autoCompleteFromUser.setEnabled(false);
            clearFlag=true;

        }

        // getting options from xml string array
        ArrayAdapter<String> actionSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, entryAction);
        ((Spinner) findViewById(R.id.actionSpinner)).setAdapter(actionSpinnerArrayAdapter);
        ((Spinner) findViewById(R.id.actionSpinner)).setOnItemSelectedListener(new selectedAction());

        //getting user from contact
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        Map<String, String> data = new HashMap<String, String>();

        data.put("dataFrom", "contact");

        adapter_CustomSimpleCursor = new Adapter_CustomSimpleCursor(this, R.layout.custom_spinner_item_template, cursor, data);
        //auto complete
        autoCompleteFromUser.setAdapter(adapter_CustomSimpleCursor);
        autoCompleteFromUser.setThreshold(1);

        adapter_CustomSimpleCursor.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {

                if(clearFlag==true){
                    autoCompleteFromUser.setText("");
                    //autoCompleteFromUser.setText(""+ str.charAt(str.length() - 1) );
                    //autoCompleteFromUser.setSelection(autoCompleteFromUser.getText().toString().length());
                    clearFlag=false;
                    str="";
                }
                return getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like '%" + str + "%'", null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            }
        });

        adapter_CustomSimpleCursor.setCursorToStringConverter(new Adapter_CustomSimpleCursor.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                int index = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                return cur.getString(index);
            }
        });


        autoCompleteFromUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                View clickedView = adapter_CustomSimpleCursor.getView(pos, null, null);
                selectedFromUser.put("name", ((TextView) clickedView.findViewById(R.id.item_name)).getText() + "");
                selectedFromUser.put("phone", ((TextView) clickedView.findViewById(R.id.item_phone)).getText() + "");

                clearFlag=true;
            }
        });

        //autocomplete

        //generate_FromuserSpinner(cursor,"contact");


        //Update entry (clicked on the table row)
        if(rowId!=null){
             generateEditData(myDb.getEntryById(rowId));
        }


    }




    private void generateEditData(Cursor currentEntry){
        //created_date DATE, description text, from_user INTEGER, to_user INTEGER,

        //((EditText) currentView.findViewById(R.id.id)).setText( rowId );
        datePicker.setText( formatDate(currentEntry.getString(currentEntry.getColumnIndex("created_date")), "ddmmyy") );
        created_date_forDB.setText(currentEntry.getString(currentEntry.getColumnIndex("created_date")));

        ((EditText) currentView.findViewById(R.id.description)).setText( currentEntry.getString(currentEntry.getColumnIndex("description")) );
        ((EditText) currentView.findViewById(R.id.amount)).setText( currentEntry.getString(currentEntry.getColumnIndex("amt")) );


        //Action spinner settings
        if(currentEntry.getColumnIndex("from_user")>0) {
            if (currentEntry.getInt(currentEntry.getColumnIndex("from_user")) == 1) {
                ((Spinner) currentView.findViewById(R.id.actionSpinner)).setSelection(0);
            } else {
                ((Spinner) currentView.findViewById(R.id.actionSpinner)).setSelection(1);
            }
        }


        //assuming current user and from user in the entry are same bcz it is from individual
        autoCompleteFromUser.setEnabled(true);

    }



    private class selectedAction implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (id == 0) {
                ((TextView) currentView.findViewById(R.id.selectUserLabel)).setText("Give to ");
                actionFlag = false;
            } else {
                ((TextView) currentView.findViewById(R.id.selectUserLabel)).setText("Borrow from ");
                actionFlag = true;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }


    //todo :- insert user to local db from contact while saving the data

    private class saveData implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            showProgress("Saving. . .");
            saveBtn.setEnabled(false);

            Log.i("save", selectedFromUser+"  =="+selectedFromUser.size());

            if (selectedFromUser.size() == 0 || !autoCompleteFromUser.getText().toString().equals(selectedFromUser.get("name"))) {
                closeProgress();
                saveBtn.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Invalid User", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> data = new HashMap<String, String>();

            //common field
            data.put("created_date", ((EditText) currentView.findViewById(R.id.created_date)).getText().toString());
            data.put("description", ((EditText) currentView.findViewById(R.id.description)).getText().toString());


            try {
                data.put("amt", Float.parseFloat(((EditText) currentView.findViewById(R.id.amount)).getText().toString()) + "");
            } catch (Exception e) {
                data.put("amt", "0");
            }





            String userId = registreUserFromContact(selectedFromUser.get("phone"), selectedFromUser.get("name"));

            Log.i("Phone id", userId);

            if (actionFlag == false) {
                data.put("from_user", "1");
                data.put("to_user", userId);
            } else {
                data.put("from_user", userId);
                data.put("to_user", "1");
            }

            //String recodId = ((EditText) currentView.findViewById(R.id.id)).getText().toString();

            Log.i("save", data+"");


            if (rowId==null || rowId.equals("0")) {
                if (myDb.insertEntry(data) == 1) {
                    Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

                    backActivityIntent = new Intent(ActivityLendAndBorrowAddEntry.this, ActivityLendAndBorrowIndividual.class);
                    backActivityIntent.putExtra("fromActivity", "ActivityLendAndBorrow");
                    backActivityIntent.putExtra("fromUserId", "" + userId);
                    backActivityIntent.putExtra("userName", selectedFromUser.get("name"));


                    goBack();

                } else {
                    Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                }
            } else { //update
                data.put("_id", rowId);

                if (myDb.updateEntry(data) == 1) {
                    Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

                    goBack();

                } else {
                    Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                }


            }


            closeProgress();

        }
    }


    private class cancelActivity implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            goBack();
        }
    }

    private void goBack() {
        startActivity(backActivityIntent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDb != null) {
            myDb.close();
        }
    }
}
*/

package com.tricon.labs.giventake;


import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.tricon.labs.giventake.adapters.AdapterCategoryList;
import com.tricon.labs.giventake.adapters.AdapterContactList;
import com.tricon.labs.giventake.database.DBHelper;
import com.tricon.labs.giventake.models.Contact;
import com.tricon.labs.giventake.models.Person;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.tricon.labs.giventake.libraries.functions.getContactList;

public class ActivityLendAndBorrowAddEntry extends AppCompatActivity {

    private DBHelper mDBHelper;

    Button mBtnDate;

    List<Contact> mContacts;

    AutoCompleteTextView mACTVUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_and_borrow_add_entry);

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.widget_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_clear_mtrl_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportActionBar().setHomeButtonEnabled(true);

        mDBHelper = DBHelper.getInstance(this);

        mBtnDate = (Button) findViewById(R.id.btn_date);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_lend_and_borrow);
        TextInputLayout tilUserName = (TextInputLayout) findViewById(R.id.til_user_name);
        TextInputLayout tilAmount = (TextInputLayout) findViewById(R.id.til_amount);
        TextInputLayout tilDescription = (TextInputLayout) findViewById(R.id.til_description);
        mACTVUserName = (AutoCompleteTextView) findViewById(R.id.actv_user_name);
        EditText etAmount = (EditText) findViewById(R.id.et_amount);
        EditText etDescription = (EditText) findViewById(R.id.et_description);

        //set autocomplete threshold
        mACTVUserName.setThreshold(1);
        new FetchUserFromContactTask().execute();

        //initial date values
        SimpleDateFormat dmy = new SimpleDateFormat("dd-MM-yyyy");
        String dmyDate = dmy.format(new Date());

        mBtnDate.setText(dmyDate);

        mBtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
    }


    private class FetchUserFromContactTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            //getting user from contact
            mContacts = getContactList(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mACTVUserName.setAdapter(new AdapterContactList(ActivityLendAndBorrowAddEntry.this, mContacts));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_lend_and_borrow_add_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                //saveData();
                break;

            default:
                break;
        }
        return true;
    }

    public void openDatePicker() {
        //To show current date in the datepicker
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                selectedmonth++;
                String actualMonth = "" + selectedmonth;
                if (selectedmonth < 10) {
                    actualMonth = "0" + actualMonth;
                }

                String actualDay = "" + selectedday;
                if (selectedday < 10) {
                    actualDay = "0" + actualDay;
                }

                mBtnDate.setText(actualDay + "-" + actualMonth + "-" + selectedyear);
            }
        }, year, month, day).show();
    }
}
