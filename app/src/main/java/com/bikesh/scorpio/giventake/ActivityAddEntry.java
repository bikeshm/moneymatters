package com.bikesh.scorpio.giventake;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.bikesh.scorpio.giventake.adapters.Adapter_CustomSimpleCursor;
import com.bikesh.scorpio.giventake.adapters.Adapter_TextRecyclerViewList;
import com.bikesh.scorpio.giventake.adapters.CustomDatePicker;
import com.bikesh.scorpio.giventake.model.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ActivityAddEntry extends ActivityBase {

    String fromActivity=null;
    long ID=0;
    String Name="";
    DBHelper myDb;

    boolean actionFlag=false; //if false giving or borrowing

    Intent backActivityIntent=null;

    RecyclerView recyclerView;

    Adapter_TextRecyclerViewList adapter;


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

            Name = extras.getString("Name");
        }


        if (fromActivity.equals("ActivityLendAndBorrow")) {
            backActivityIntent = new Intent(ActivityAddEntry.this, ActivityLendAndBorrow.class);
            generateDataForLendNBorrow();

        } else if (fromActivity.equals("ActivityLendAndBorrowPersonal")) {
            backActivityIntent = new Intent(ActivityAddEntry.this, ActivityLendAndBorrowIndividual.class);
            backActivityIntent.putExtra("fromActivity", "ActivityLendAndBorrow");
            backActivityIntent.putExtra("userId", "" + ID);
            backActivityIntent.putExtra("userName", Name);
            generateDataForLendNBorrow();

        } else if (fromActivity.equals("ActivityPersonalExpense")) {
            backActivityIntent = new Intent(ActivityAddEntry.this, ActivityPersonalExpense.class);
            generateDataForPersonalExpense();

        } else if (fromActivity.equals("ActivityPersonalExpenseIndividual")) {
            backActivityIntent = new Intent(ActivityAddEntry.this, ActivityPersonalExpenseIndividual.class);
            backActivityIntent.putExtra("colId", "" + ID);
            backActivityIntent.putExtra("colName", Name);
            generateDataForPersonalExpense();

        } else if (fromActivity.equals("ActivityJointExpenseIndividual")) {
            backActivityIntent = new Intent(ActivityAddEntry.this, ActivityJointExpenseIndividual.class);
            backActivityIntent.putExtra("groupId", "" + ID);

            //=====split --face 2
            //Cursor cursor = myDb.getAllUsersIncludedMe();
            //adapter = new Adapter_TextRecyclerViewList(cursor, this);
            //recyclerView.setAdapter(adapter);
            //=====split --face 2

            generateDataForJointExpenseIndividual();


        } else {
            throw new IllegalArgumentException("Invalid  ");
        }


        //implementing date picker

        EditText datePicker = ((EditText) currentView.findViewById(R.id.datePicker));
        //created_date is hidden field for serving date to db (YY-mm-dd format)
        EditText created_date = ((EditText) currentView.findViewById(R.id.created_date));

        //initial date values
        SimpleDateFormat dmy = new SimpleDateFormat("dd-MM-yyyy");
        String dmyDate = dmy.format(new Date());
        datePicker.setText(dmyDate);

        SimpleDateFormat tmpdmy = new SimpleDateFormat("yyyy-MM-dd");
        String tmpdmyDate = tmpdmy.format(new Date());
        created_date.setText(tmpdmyDate);

        //setting datepicker adapter
        datePicker.setOnClickListener(new CustomDatePicker(ActivityAddEntry.this, datePicker, created_date, false));
        //----implementing date picker


        ((Button)currentView.findViewById(R.id.saveBtn)).setOnClickListener(new saveData());
        ((Button) currentView.findViewById(R.id.cancelBtn)).setOnClickListener(new cancelActivity());

    }




    private void generateDataForJointExpenseIndividual() {
        ((LinearLayout) currentView.findViewById(R.id.l3) ).setVisibility(View.GONE);

        //===================face 2
        //((LinearLayout) addEntryView.findViewById(R.id.isSplitLayer) ).setVisibility(View.VISIBLE);
        //((LinearLayout) addEntryView.findViewById(R.id.grupMembersLayer) ).setVisibility(View.VISIBLE);
        //=====================face 2

        ((TextView) currentView.findViewById(R.id.selectUserLabel)).setText("Spend By");
        Adapter_CustomSimpleCursor adapter = new Adapter_CustomSimpleCursor(this, R.layout.custom_spinner_item_template, myDb.getAllUsersInGroup(ID + "")  );
        ((Spinner) currentView.findViewById(R.id.fromUser)).setAdapter(adapter);



        ((RadioGroup) currentView.findViewById(R.id.isSplit)).setOnCheckedChangeListener(new isSplitChanged());

        recyclerView.setOnKeyListener(new recyclerViewKeyListener());








    }

    private void generateDataForPersonalExpense(){

        ((LinearLayout) currentView.findViewById(R.id.l3) ).setVisibility(View.GONE);
        ((TextView)currentView.findViewById(R.id.selectUserLabel)).setText("Select Collection : ");

        Cursor cursor = myDb.getAllCollection();
        generate_FromuserSpinner(cursor);
    }

    private void generate_FromuserSpinner(Cursor cursor) {

        Adapter_CustomSimpleCursor adapter = new Adapter_CustomSimpleCursor(this, R.layout.custom_spinner_item_template, cursor);

        ((Spinner) currentView.findViewById(R.id.fromUser)).setAdapter(adapter);

        //setting passed/selected user name in spinner
        int cpos = 0;
        for(int i = 0; i < adapter.getCount(); i++){
            cursor.moveToPosition(i);
            Double temp = Double.parseDouble( cursor.getString(cursor.getColumnIndex("_id")) );
            if ( temp == ID ){
                cpos = i;
                break;
            }
        }
        ((Spinner) currentView.findViewById(R.id.fromUser)).setSelection(cpos);

    }


    private void generateDataForLendNBorrow(){

        // getting options from xml string array
        ArrayAdapter<String> actionSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.addEntryAction));
        ((Spinner)findViewById(R.id.actionSpinner)).setAdapter(actionSpinnerArrayAdapter);
        ((Spinner)findViewById(R.id.actionSpinner)).setOnItemSelectedListener(new selectedAction());


        Cursor cursor = myDb.getAllUsers();

        //getting user from contact
        //Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        generate_FromuserSpinner(cursor);

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


    private class saveData implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Map<String, String> data = new HashMap<String, String>();

            //common field
            data.put("created_date",  ((EditText) currentView.findViewById(R.id.created_date) ).getText().toString() );
            data.put("description", ((EditText) currentView.findViewById(R.id.description)).getText().toString());



            data.put("amt", ((EditText) currentView.findViewById(R.id.amount)).getText().toString());

            if(fromActivity.equals("ActivityLendAndBorrowPersonal") || fromActivity.equals("ActivityLendAndBorrow")  ) {

                if(actionFlag==false) {
                    data.put("from_user", "1" );
                    data.put("to_user", ((Spinner) currentView.findViewById(R.id.fromUser)).getSelectedItemId()+"" );
                }
                else{
                    data.put("from_user",  ((Spinner) currentView.findViewById(R.id.fromUser)).getSelectedItemId()+"" );
                    data.put("to_user", "1" );
                }


                if (myDb.insertEntry(data)==1) {
                    Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

                    goBack();

                } else {
                    Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                }
            }


            if(fromActivity.equals("ActivityPersonalExpense") || fromActivity.equals("ActivityPersonalExpenseIndividual") ){
                data.put("collection_id",  ((Spinner) currentView.findViewById(R.id.fromUser)).getSelectedItemId()+"" );

                if (myDb.insertPersonalExpense(data)==1) {
                    Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

                    goBack();

                } else {
                    Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                }

            }

            if(fromActivity.equals("ActivityJointExpenseIndividual")  ){
                int is_split=0;

                data.put("joint_group_id",  ID+"" );
                data.put("user_id",  ((Spinner) currentView.findViewById(R.id.fromUser)).getSelectedItemId()+"" );

                int id = ((RadioGroup) currentView.findViewById(R.id.isSplit)).getCheckedRadioButtonId();
                if (id == -1){ /*no item selected*/ }
                else {
                    if (id == R.id.isSplitradioYes) {
                        is_split = 1;
                    }
                }


                data.put("is_split",  is_split+"" );



                if (myDb.insertGroupEntry(data) == 1) {
                    Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

                    goBack();

                } else {
                    Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDb != null) {
            myDb.close();
        }
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
}
