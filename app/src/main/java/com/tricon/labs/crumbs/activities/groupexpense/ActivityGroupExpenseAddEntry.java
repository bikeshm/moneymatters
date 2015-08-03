package com.tricon.labs.crumbs.activities.groupexpense;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.tricon.labs.crumbs.R;
import com.tricon.labs.crumbs.adapters.AdapterMembersSpinner;
import com.tricon.labs.crumbs.database.DBHelper;
import com.tricon.labs.crumbs.models.Contact;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;


public class ActivityGroupExpenseAddEntry extends AppCompatActivity {

    private Button mBtnDate;
    private Spinner mSpinnerMembers;
    private TextInputLayout mTILAmount;
    private EditText mETAmount;
    private TextInputLayout mTILDescription;
    private EditText mETDescription;

    private ProgressDialog mPDSaveData;
    private DBHelper mDBHelper;

    private String mGroupId;

    public static final String INTENT_GROUP_ID = "com.tricon.labs.pepper.activities.groupexpense.GROUP_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_expense_add_entry);

        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top);

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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }

        //setup views
        mBtnDate = (Button) findViewById(R.id.btn_date);
        mTILAmount = (TextInputLayout) findViewById(R.id.til_amount);
        mETAmount = (EditText) findViewById(R.id.et_amount);
        mTILDescription = (TextInputLayout) findViewById(R.id.til_description);
        mETDescription = (EditText) findViewById(R.id.et_description);
        mSpinnerMembers = (Spinner) findViewById(R.id.spinner_members);

        //set progress dialog
        mPDSaveData = new ProgressDialog(this);
        mPDSaveData.setCancelable(false);
        mPDSaveData.setIndeterminate(true);
        mPDSaveData.setMessage("Saving Data...");

        //get db instance
        mDBHelper = DBHelper.getInstance(this);

        //get extras
        Intent intent = getIntent();
        mGroupId = intent.getStringExtra(INTENT_GROUP_ID);

        //set data in views
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        mBtnDate.setText(simpleDateFormat.format(new Date()));

        //set listeners
        mBtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        mETAmount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTILAmount.setError(null);
                return false;
            }
        });
        mETDescription.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTILDescription.setError(null);
                return false;
            }
        });

        //get members and set them in spinner's adapter
        new FetchGroupMembersTask().execute();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_top, R.anim.slide_out_to_bottom);
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
                saveData();
                break;
            default:
                break;
        }
        return true;
    }

    private void openDatePicker() {
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

    private void saveData() {
        if (TextUtils.isEmpty(mETAmount.getText().toString().trim())) {
            mTILAmount.setError("Amount Required");
            return;
        }

        if (TextUtils.isEmpty(mETDescription.getText().toString())) {
            mTILDescription.setError("Description Required");
            return;
        }

        new SaveDataTask().execute();
    }

    private class SaveDataTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            mPDSaveData.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HashMap<String, String> data = new HashMap<>();

            //convert date into "yyyy MM dd" format
            SimpleDateFormat localDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String date = mBtnDate.getText().toString();
            try {
                date = dbDateFormat.format(localDateFormat.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            data.put("created_date", date);
            data.put("amt", mETAmount.getText().toString().trim());
            data.put("description", mETDescription.getText().toString());
            data.put("joint_group_id", mGroupId);
            data.put("user_id", ((Contact) mSpinnerMembers.getSelectedItem()).id + "");

            return mDBHelper.insertGroupEntry(data) > 0;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mPDSaveData.dismiss();
            if (success) {
                Toast.makeText(ActivityGroupExpenseAddEntry.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ActivityGroupExpenseAddEntry.this, "Data saved failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FetchGroupMembersTask extends AsyncTask<Void, Void, HashSet<Contact>> {

        @Override
        protected HashSet<Contact> doInBackground(Void... params) {
            return mDBHelper.getGroupMembers(mGroupId);
        }

        @Override
        protected void onPostExecute(HashSet<Contact> members) {
            mSpinnerMembers.setAdapter(new AdapterMembersSpinner(ActivityGroupExpenseAddEntry.this, members));
        }
    }
}

    /*
    RecyclerView recyclerView;

    String fromActivity=null;

    String groupId="0";

    String groupOnlineId="0";

    EditText datePicker,created_date_forDB;

    Intent backActivityIntent=null;

    String currentUserId ="0",  currentUserOnlineId="0", onlineOwnerId="0",currentUserName="";

    String rowId="0", rowOnlineId="0", rowUserOnlineId="0";

    String apiUrl_AddEntry = "http://givntake.workassis.com/api/entry/add/";

    RequestQueue Rqueue;

    DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint_expense_add_entry);

        myDb = new DBHelper(this);

        //--- Face 2  //initialising RecyclerView otherwise it is throwing null pointer exception
        //recyclerView = (RecyclerView) findViewById(R.id.recycler_Users);
        //recyclerView.setHasFixedSize(true);

        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //recyclerView.setLayoutManager(layoutManager);
        //--

        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            fromActivity= null;
        } else {
            fromActivity= extras.getString("fromActivity");

            groupId = extras.getString("groupId","0");
            groupOnlineId = extras.getString("groupOnlineId","0");

            currentUserId = extras.getString("currentUserId","0");
            currentUserOnlineId= extras.getString("currentUserOnlineId","0");

            onlineOwnerId= extras.getString("onlineOwnerId","0");

            currentUserName= extras.getString("currentUserName","");



            //for updating
            rowId = extras.getString("rowId","0");
            rowOnlineId = extras.getString("rowOnlineId","0");
            rowUserOnlineId = extras.getString("rowUserOnlineId","0");

            Log.i("row online Id",rowId+" --- "+rowOnlineId);

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
        datePicker.setOnClickListener(new CustomDatePicker(ActivityJointExpenseAddEntry.this, datePicker, created_date_forDB, false));
        //----implementing date picker


        backActivityIntent = new Intent(ActivityJointExpenseAddEntry.this, ActivityJointExpenseIndividual.class);
        backActivityIntent.putExtra("groupId",  groupId);

        //=====split --face 2
        //Cursor cursor = myDb.getAllUsersIncludedMe();
        //adapter = new Adapter_TextRecyclerViewList(cursor, this);
        //recyclerView.setAdapter(adapter);
        //=====split --face 2

        generateDataForJointExpenseIndividual();



        ((Button)currentView.findViewById(R.id.saveBtn)).setOnClickListener(new saveData());
        ((Button) currentView.findViewById(R.id.cancelBtn)).setOnClickListener(new cancelActivity());

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




    private void generateDataForJointExpenseIndividual() {


        //===================face 2
        //((LinearLayout) addEntryView.findViewById(R.id.isSplitLayer) ).setVisibility(View.VISIBLE);
        //((LinearLayout) addEntryView.findViewById(R.id.grupMembersLayer) ).setVisibility(View.VISIBLE);
        //=====================face 2

        Log.i("add entry", currentUserOnlineId + " == " + onlineOwnerId );

        if(currentUserOnlineId.equals(onlineOwnerId) || groupOnlineId.equals("0")) {

            Map<String, String> data = new HashMap<String, String>();
            data.put("dataFrom", "db");
            //((TextView) currentView.findViewById(R.id.selectUserLabel)).setText("Spend By");
            Cursor allGroupMembers = myDb.getAllUsersInGroup(groupId);
            Adapter_CustomSimpleCursor adapter = new Adapter_CustomSimpleCursor(this, R.layout.custom_spinner_item_template, allGroupMembers  , data);
            ((Spinner) currentView.findViewById(R.id.fromUser)).setAdapter(adapter);

            int cpos = 0;
            for(int i = 0; i < adapter.getCount(); i++){
                allGroupMembers.moveToPosition(i);
                String temp = allGroupMembers.getString(allGroupMembers.getColumnIndex("_id"));
                if ( temp.equals(myDb.getGroupsingleEntryField(rowId,"user_id")) ){
                    cpos = i;
                    break;
                }
            }
            ((Spinner) currentView.findViewById(R.id.fromUser)).setSelection(cpos);

        }
        else{
            ((Spinner) currentView.findViewById(R.id.fromUser)).setVisibility(View.GONE);
            ((EditText) currentView.findViewById(R.id.fromUserText)).setVisibility(View.VISIBLE);

            if(rowId.equals("0")){
                ((EditText) currentView.findViewById(R.id.fromUserText)).setText( currentUserName);
            }
        }


        if(!rowOnlineId.equals("0") && !currentUserOnlineId.equals(onlineOwnerId) && !currentUserOnlineId.equals(rowUserOnlineId) ) {
            ((Button)currentView.findViewById(R.id.saveBtn)).setEnabled(false);
        }

        //face2
        //((RadioGroup) currentView.findViewById(R.id.isSplit)).setOnCheckedChangeListener(new isSplitChanged());

        //recyclerView.setOnKeyListener(new recyclerViewKeyListener());


        if(rowId!=null){
            generateEditData(myDb.getGroupsingleEntry(rowId));
        }

    }

    /* face 2
    private class isSplitChanged implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            Toast.makeText(getApplicationContext(), "" + checkedId, Toast.LENGTH_LONG).show();

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
 *--/

    private void generateEditData(Cursor currentEntry){
        //created_date DATE, description text, from_user INTEGER, to_user INTEGER,

        if(currentEntry.getCount()>0) {
            ((EditText) currentView.findViewById(R.id.id)).setText(rowId);
            datePicker.setText(formatDate(currentEntry.getString(currentEntry.getColumnIndex("created_date")), "ddmmyy"));
            created_date_forDB.setText(currentEntry.getString(currentEntry.getColumnIndex("created_date")));

            ((EditText) currentView.findViewById(R.id.description)).setText(currentEntry.getString(currentEntry.getColumnIndex("description")));
            ((EditText) currentView.findViewById(R.id.amount)).setText(currentEntry.getString(currentEntry.getColumnIndex("amt")));

            ((EditText) currentView.findViewById(R.id.fromUserText)).setText(myDb.getUserField(currentEntry.getString(currentEntry.getColumnIndex("user_id")),"name"));
        }

    }

    private class saveData implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            ((Button)currentView.findViewById(R.id.saveBtn)).setEnabled(false);
            showProgress("Saving ...");

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



            int is_split=0;

            data.put("joint_group_id",  groupId);

            if(currentUserOnlineId.equals(onlineOwnerId) || groupOnlineId.equals("0")) {
            //if(currentUserOnlineId.equals(onlineOwnerId)) {
                data.put("user_id", ((Spinner) currentView.findViewById(R.id.fromUser)).getSelectedItemId() + "");

                Log.i("user_id","spinner");
            }
            else{
                data.put("user_id",currentUserId);
            }

            Log.i("user_id", "cuid"+ currentUserId + " cuonid "+ currentUserOnlineId+ "ownerid "+ onlineOwnerId+ "id spinner"+ ((Spinner) currentView.findViewById(R.id.fromUser)).getSelectedItemId() );
            /*face 2
            int id = ((RadioGroup) currentView.findViewById(R.id.isSplit)).getCheckedRadioButtonId();
            if (id == -1){
                //no item selected
            }
            else {
                if (id == R.id.isSplitradioYes) {
                    is_split = 1;
                }
            }
            *--/

            //face2
            data.put("is_split",  is_split+"" );

            Log.i("save", "online id"+groupOnlineId);

            //onlineId = >online group id
            if(groupOnlineId==null || groupOnlineId.equals("0")) {

                save_JointEntryToLocal(data);
            }
            else{

                save_JointEntryToOnlne(data);



            }


        }
    }

    private void save_JointEntryToLocal(Map<String, String> data) {

        Log.i("save", "online id save_JointEntryToLocal"+data);
        //data.put("current_user",currentUserOnlineId);

        if(rowId.equals("0")) {
            if (myDb.insertGroupEntry(data) == 1) {
                closeProgress();
                Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();
                goBack();

            } else {

                closeProgress();
                Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                ((Button) currentView.findViewById(R.id.saveBtn)).setEnabled(true);


            }
        }
        else{

            data.put("_id", rowId);

            if (myDb.updateGroupEntry(data) == 1) {

                closeProgress();
                Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();


                goBack();

            } else {
                closeProgress();
                Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void save_JointEntryToOnlne(Map<String, String> data) {

        Log.i("save", "online id save_JointEntryToOnlne"+data);

        Rqueue = Volley.newRequestQueue(this);

        //data.put("current_user",currentUserOnlineId);

        Map<String, String> dataForPost = new HashMap<String,String>(data);

        //onlineId = >online group id
        dataForPost.put("group_id",groupOnlineId); // myDb.getJointGroupField(dataForPost.get("user_id").toString(),"onlineid"));
        dataForPost.remove("joint_group_id");


        dataForPost.put("user_id",myDb.getUserField( dataForPost.get("user_id")+"","onlineid"));

        dataForPost.put("current_user",currentUserOnlineId);

        if(!rowOnlineId.equals("0")) {
            dataForPost.put("id", rowOnlineId);
        }

        //Log.i("save", "sending data to server save_JointEntryToOnlne"+dataForPost);

        CustomRequest jsObjRequest =   new CustomRequest

                (Request.Method.POST, apiUrl_AddEntry, dataForPost, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        //Log.i("api call 1", response.toString()+ "");
                        // no need to edit local db

                        Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

                        closeProgress();
                        goBack();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("api call", "ERROR "+error.getMessage());
                        Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                        closeProgress();
                        //((Button)currentView.findViewById(R.id.saveBtn)).setEnabled(true);
                        goBack();
                    }
                });
        Rqueue.add(jsObjRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_add_group_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDb != null) {
            myDb.close();
        }
    }

    */
