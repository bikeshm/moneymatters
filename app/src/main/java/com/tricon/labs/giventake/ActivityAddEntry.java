package com.tricon.labs.giventake;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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

import static com.tricon.labs.giventake.libraries.parsePhone.parsePhone;


public class ActivityAddEntry extends ActivityBase {

    String fromActivity=null;
    long ID=0;
    String Name="",rowId=null;

    String onlineId=null;
    //DBHelper myDb;

    boolean actionFlag=false; //if false giving or borrowing

    Intent backActivityIntent=null;

    RecyclerView recyclerView;

    //Adapter_TextRecyclerViewList adapter;


    EditText datePicker,created_date_forDB;


    DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);


        myDb = new DBHelper(this);

        //--- initialising RecyclerView otherwise it is throwing null pointer exception
        recyclerView = (RecyclerView) findViewById(R.id.recycler_Users);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //--

        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            fromActivity= null;
        } else {
            fromActivity= extras.getString("fromActivity");

            if(extras.getString("ID")!=null)
                ID = Long.parseLong(extras.getString("ID"));

            rowId = extras.getString("rowId",null);
            Name = extras.getString("Name");

            onlineId = extras.getString("onlineId",null);
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
        datePicker.setOnClickListener(new CustomDatePicker(ActivityAddEntry.this, datePicker, created_date_forDB, false));
        //----implementing date picker




        if (fromActivity.equals("ActivityLendAndBorrow")) {
            //backActivityIntent = new Intent(ActivityAddEntry.this, LendAndBorrow.class);
            generateDataForLendNBorrow();

        } else if (fromActivity.equals("ActivityLendAndBorrowPersonal")) {
            backActivityIntent = new Intent(ActivityAddEntry.this, ActivityLendAndBorrowIndividual.class);
            backActivityIntent.putExtra("fromActivity", "ActivityLendAndBorrow");
            backActivityIntent.putExtra("userId", "" + ID);
            backActivityIntent.putExtra("userName", Name);
            generateDataForLendNBorrow();

        } else if (fromActivity.equals("ActivityPersonalExpense")) {
            //backActivityIntent = new Intent(ActivityAddEntry.this, ActivityPersonalExpense.class);
            generateDataForPersonalExpense();

        } else if (fromActivity.equals("ActivityPersonalExpenseIndividual")) {
            backActivityIntent = new Intent(ActivityAddEntry.this, ActivityPersonalExpenseIndividual.class);
            backActivityIntent.putExtra("colId", "" + ID);
            backActivityIntent.putExtra("colName", Name);
            generateDataForPersonalExpense();

        }

        else {
            throw new IllegalArgumentException("Invalid  ");
        }





        ((Button)currentView.findViewById(R.id.saveBtn)).setOnClickListener(new saveData());
        ((Button) currentView.findViewById(R.id.cancelBtn)).setOnClickListener(new cancelActivity());

    }




    private void generateDataForJointExpenseIndividual() {
        ((LinearLayout) currentView.findViewById(R.id.l3) ).setVisibility(View.GONE);

        //===================face 2
        //((LinearLayout) addEntryView.findViewById(R.id.isSplitLayer) ).setVisibility(View.VISIBLE);
        //((LinearLayout) addEntryView.findViewById(R.id.grupMembersLayer) ).setVisibility(View.VISIBLE);
        //=====================face 2
        Map<String, String> data = new HashMap<String, String>();
        data.put("dataFrom","db"  );
        ((TextView) currentView.findViewById(R.id.selectUserLabel)).setText("Spend By");
        Adapter_CustomSimpleCursor adapter = new Adapter_CustomSimpleCursor(this, R.layout.custom_spinner_item_template, myDb.getAllUsersInGroup(ID + "") , data );
        ((Spinner) currentView.findViewById(R.id.fromUser)).setAdapter(adapter);



        ((RadioGroup) currentView.findViewById(R.id.isSplit)).setOnCheckedChangeListener(new isSplitChanged());

        recyclerView.setOnKeyListener(new recyclerViewKeyListener());

    }

    private void generateDataForPersonalExpense(){

        ((LinearLayout) currentView.findViewById(R.id.l3) ).setVisibility(View.GONE);
        ((TextView)currentView.findViewById(R.id.selectUserLabel)).setText("Select Collection : ");

        Cursor cursor = myDb.getCategories();
        generate_FromuserSpinner(cursor, "db");


        if(rowId!=null){
            generateEditData(myDb.getPersonalExpense(rowId));
        }



    }

    private void generate_FromuserSpinner(Cursor cursor, String dataFrom) {

        Map<String, String> data = new HashMap<String, String>();

        data.put("dataFrom",dataFrom  );

        Adapter_CustomSimpleCursor adapter = new Adapter_CustomSimpleCursor(this, R.layout.custom_spinner_item_template, cursor,data);

        ((Spinner) currentView.findViewById(R.id.fromUser)).setAdapter(adapter);

        //// TODO: 6/3/2015 :-
        //setting passed/selected user name in spinner

        int cpos = 0;



        if(dataFrom.equals("contact")) {

            String phone = myDb.getUserPhone(ID + "");

            for (int i = 0; i < adapter.getCount(); i++) {
                cursor.moveToPosition(i);
                String temp = (parsePhone(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)), myDb.getdefaultContryCode()));//cursor.getString(cursor.getColumnIndex("phone"));
                if (temp.equals(phone)) {
                    cpos = i;
                    break;
                }
            }
        }
        else{

            for(int i = 0; i < adapter.getCount(); i++){
                cursor.moveToPosition(i);
                Double temp = Double.parseDouble(cursor.getString(cursor.getColumnIndex("_id")));
                if ( temp == ID ){
                    cpos = i;
                    break;
                }
            }

        }

        ((Spinner) currentView.findViewById(R.id.fromUser)).setSelection(cpos);

    }





    private void generateDataForLendNBorrow(){


        String[] entryAction = { "Giving to", "Borrow from",};
        // getting options from xml string array
        ArrayAdapter<String> actionSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,entryAction);
        ((Spinner)findViewById(R.id.actionSpinner)).setAdapter(actionSpinnerArrayAdapter);
        ((Spinner)findViewById(R.id.actionSpinner)).setOnItemSelectedListener(new selectedAction());


        //Cursor cursor = myDb.getAllUsers();

        //getting user from contact
        //Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        generate_FromuserSpinner(cursor,"contact");


        //clicked on the table row
        if(rowId!=null){
            generateEditData(myDb.getEntryById(rowId));
        }

    }



    private void generateEditData(Cursor currentEntry){
        //created_date DATE, description text, from_user INTEGER, to_user INTEGER,

        ((EditText) currentView.findViewById(R.id.id)).setText( rowId );
        datePicker.setText( formatDate(currentEntry.getString(currentEntry.getColumnIndex("created_date")), "ddmmyy") );
        created_date_forDB.setText(currentEntry.getString(currentEntry.getColumnIndex("created_date")));

        ((EditText) currentView.findViewById(R.id.description)).setText( currentEntry.getString(currentEntry.getColumnIndex("description")) );
        ((EditText) currentView.findViewById(R.id.amount)).setText( currentEntry.getString(currentEntry.getColumnIndex("amt")) );


        //for lennd and borrow
        if(currentEntry.getColumnIndex("from_user")>0) {
            if (currentEntry.getInt(currentEntry.getColumnIndex("from_user")) == 1) {
                ((Spinner) currentView.findViewById(R.id.actionSpinner)).setSelection(0);
            } else {
                ((Spinner) currentView.findViewById(R.id.actionSpinner)).setSelection(1);
            }
        }

    }




    private class selectedAction implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(id==0){
                ((TextView) currentView.findViewById (R.id.selectUserLabel)).setText("Give to ");
                actionFlag=false;
            }
            else{
                ((TextView) currentView.findViewById (R.id.selectUserLabel)).setText("Borrow from ");
                actionFlag=true;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) { }
    }


    //todo :- insert user to local db from contact while saving the data

    private class saveData implements View.OnClickListener {
        @Override
        public void onClick(View v) {



            Map<String, String> data = new HashMap<String, String>();

            //common field
            data.put("created_date",  ((EditText) currentView.findViewById(R.id.created_date) ).getText().toString() );
            data.put("description", ((EditText) currentView.findViewById(R.id.description)).getText().toString());


            try{
                data.put("amt", Float.parseFloat(((EditText) currentView.findViewById(R.id.amount)).getText().toString()) + "");
            }
            catch (Exception e){
                data.put("amt", "0");
            }





            if(fromActivity.equals("ActivityLendAndBorrowPersonal") || fromActivity.equals("ActivityLendAndBorrow")  ) {


                ContentResolver cr = getContentResolver();

                View spinnerView = (((Spinner) currentView.findViewById(R.id.fromUser)).getSelectedView());


                if(spinnerView==null){
                    Toast.makeText(getApplicationContext(), "Select a User", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userId = registreUserFromContact(
                        ((TextView) spinnerView.findViewById(R.id.item_phone)).getText().toString(),
                        ((TextView) spinnerView.findViewById(R.id.item_name)).getText().toString()
                );

                Log.i("Phone id",userId);

                if(actionFlag==false) {
                    data.put("from_user", "1" );
                    data.put("to_user", userId );
                }
                else{
                    data.put("from_user",  userId );
                    data.put("to_user", "1" );
                }

                String recodId=((EditText) currentView.findViewById(R.id.id)).getText().toString();

                if(recodId.equals("0")) {
                    if (myDb.insertEntry(data) == 1) {
                        Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

                        backActivityIntent = new Intent(ActivityAddEntry.this, ActivityLendAndBorrowIndividual.class);
                        backActivityIntent.putExtra("fromActivity", "ActivityLendAndBorrow");
                        backActivityIntent.putExtra("userId", "" + userId);
                        backActivityIntent.putExtra("userName", ((TextView) spinnerView.findViewById(R.id.item_name)).getText().toString() );

                        goBack();

                    } else {
                        Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                    }
                }
                else { //update
                    data.put("_id", recodId );

                    if (myDb.updateEntry(data) == 1) {
                        Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

                        goBack();

                    } else {
                        Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                    }


                }

            }


            if(fromActivity.equals("ActivityPersonalExpense") || fromActivity.equals("ActivityPersonalExpenseIndividual") ){
                data.put("collection_id",  ((Spinner) currentView.findViewById(R.id.fromUser)).getSelectedItemId()+"" );

                if(rowId == null) {
                    if (myDb.insertPersonalExpense(data) == 1) {
                        Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

                        goBack();

                    } else {
                        Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    data.put("_id", rowId );
                    if (myDb.updatePersonalExpense(data) == 1) {
                        Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

                        goBack();

                    } else {
                        Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                    }

                }

            }



        }
    }




    private class cancelActivity implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            goBack();
        }
    }

    private void goBack(){
        startActivity(backActivityIntent);
        finish();
    }



    private class isSplitChanged implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            Toast.makeText( getApplicationContext(),""+ checkedId,Toast.LENGTH_LONG).show();

            if(checkedId==R.id.isSplitradioYes){
                ((LinearLayout) currentView.findViewById(R.id.grupMembersLayer) ).setVisibility(View.VISIBLE);
                ((EditText)currentView.findViewById(R.id.amount)).setEnabled(false);
            }
            else{
                ((LinearLayout) currentView.findViewById(R.id.grupMembersLayer) ).setVisibility(View.GONE);
                ((EditText)currentView.findViewById(R.id.amount)).setEnabled(true);
            }
        }
    }

    private class recyclerViewKeyListener implements View.OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            Toast.makeText(getApplicationContext(),"keyup",Toast.LENGTH_LONG).show();

            return false;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDb != null) {
            myDb.close();
        }
    }
}
