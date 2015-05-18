package com.bikesh.scorpio.giventake;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivityAddEntry extends ActionBarActivity {

    String fromActivity=null;
    long ID=0;
    String Name="";
    DBHelper myDb;

    boolean actionFlag=false; //if false giving or borrowing

    View addEntryView;

    Intent backActivityIntent=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loading templet xml
        setContentView(R.layout.main_template);


        //setting up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //setting up navigation drawer
        GiveNTakeApplication AC = (GiveNTakeApplication)getApplicationContext();
        View view = getWindow().getDecorView().findViewById(android.R.id.content);
        AC.setupDrawer(view, ActivityAddEntry.this, toolbar);

        //loading home activity templet in to template frame
        FrameLayout frame = (FrameLayout) findViewById(R.id.mainFrame);
        frame.removeAllViews();
        Context darkTheme = new ContextThemeWrapper(this, R.style.AppTheme);
        LayoutInflater inflater = (LayoutInflater) darkTheme.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addEntryView=  inflater.inflate(R.layout.activity_add_entry, null);

        frame.addView(addEntryView);

        myDb = new DBHelper(this);


        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            fromActivity= null;
        } else {
            fromActivity= extras.getString("fromActivity");

            if(extras.getString("ID")!=null)
                ID = Long.parseLong(extras.getString("ID"));

            Name = extras.getString("Name");
        }


        switch (fromActivity) {
            case "ActivityLendAndBorrow":
                backActivityIntent=new Intent(ActivityAddEntry.this, ActivityLendAndBorrow.class);
                generateDataForLendNBorrow();
                break;

            case "ActivityLendAndBorrowPersonal":
                backActivityIntent=new Intent(ActivityAddEntry.this, ActivityLendAndBorrowIndividual.class);
                backActivityIntent.putExtra("fromActivity", "ActivityLendAndBorrow");
                backActivityIntent.putExtra("userId", "" + ID);
                backActivityIntent.putExtra("userName", Name);
                generateDataForLendNBorrow();
                break;


            case "ActivityPersonalExpense":
                backActivityIntent=new Intent(ActivityAddEntry.this, ActivityPersonalExpense.class);
                generateDataForPersonalExpense();
                break;
            case "ActivityPersonalExpenseIndividual":
                backActivityIntent=new Intent(ActivityAddEntry.this, ActivityPersonalExpenseIndividual.class);
                backActivityIntent.putExtra("colId", "" + ID);
                backActivityIntent.putExtra("colName", Name);
                generateDataForPersonalExpense();
                break;

            default:
                throw new IllegalArgumentException("Invalid  ");
        }


        //implementing date picker

        EditText datePicker = ((EditText) addEntryView.findViewById(R.id.datePicker));
        //created_date is hidden field for serving date to db (YY-mm-dd format)
        EditText created_date = ((EditText) addEntryView.findViewById(R.id.created_date));

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


        ((Button)addEntryView.findViewById(R.id.saveBtn)).setOnClickListener(new saveData());
        ((Button) addEntryView.findViewById(R.id.cancelBtn)).setOnClickListener(new cancelActivity());

    }

    private void generateDataForPersonalExpense(){

        ((LinearLayout) addEntryView.findViewById(R.id.l3) ).setVisibility(View.GONE);
        ((TextView)addEntryView.findViewById(R.id.selectUserLabel)).setText("Select Collection : ");

        Cursor cursor = myDb.getAllCollection();
        generate_FromuserSpinner(cursor);
    }

    private void generate_FromuserSpinner(Cursor cursor) {

        Adapter_CustomSimpleCursor adapter = new Adapter_CustomSimpleCursor(this, R.layout.custom_spinner_item_template, cursor);

        ((Spinner) addEntryView.findViewById(R.id.fromUser)).setAdapter(adapter);

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
        ((Spinner) addEntryView.findViewById(R.id.fromUser)).setSelection(cpos);

    }


    private void generateDataForLendNBorrow(){

        // getting options from xml string array
        ArrayAdapter<String> actionSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.addEntryAction));
        ((Spinner)findViewById(R.id.actionSpinner)).setAdapter(actionSpinnerArrayAdapter);
        ((Spinner)findViewById(R.id.actionSpinner)).setOnItemSelectedListener(new selectedAction());


        Cursor cursor = myDb.getAllUsers();

        generate_FromuserSpinner(cursor);

    }





    private class selectedAction implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(id==0){
                ((TextView) addEntryView.findViewById (R.id.selectUserLabel)).setText("Give to ");
                actionFlag=false;
            }
            else{
                ((TextView) addEntryView.findViewById (R.id.selectUserLabel)).setText("Borrow from ");
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
            data.put("created_date",  ((EditText) addEntryView.findViewById(R.id.created_date) ).getText().toString() );
            data.put("description",  ((EditText) addEntryView.findViewById(R.id.description) ).getText().toString() );

            data.put("amt", ((EditText) addEntryView.findViewById(R.id.amount) ).getText().toString() );

            if(fromActivity.equals("ActivityLendAndBorrowPersonal") || fromActivity.equals("ActivityLendAndBorrow")  ) {

                if(actionFlag==false) {
                    data.put("from_user", "1" );
                    data.put("to_user", ((Spinner) addEntryView.findViewById(R.id.fromUser)).getSelectedItemId()+"" );
                }
                else{
                    data.put("from_user",  ((Spinner) addEntryView.findViewById(R.id.fromUser)).getSelectedItemId()+"" );
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
                data.put("collection_id",  ((Spinner) addEntryView.findViewById(R.id.fromUser)).getSelectedItemId()+"" );

                if (myDb.insertPersonalExpense(data)==1) {
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

}
