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
    long userId=0;
    String userName="";
    DBHelper myDb;

    boolean actionFlag=false; //if false giving or borrowing

    View addEntryView;

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
            userId = Long.parseLong(extras.getString("userId"));
            userName = extras.getString("userName");
        }






        // getting options from xml string array
        ArrayAdapter<String> actionSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.addEntryAction));
        ((Spinner)findViewById(R.id.actionSpinner)).setAdapter(actionSpinnerArrayAdapter);
        ((Spinner)findViewById(R.id.actionSpinner)).setOnItemSelectedListener(new selectedAction());


        Cursor cursor = myDb.getAllUsers();

        populateUserListAdapter adapter = new populateUserListAdapter(this, R.layout.custom_spinner_item_template, cursor);
        ((Spinner) addEntryView.findViewById(R.id.fromUser)).setAdapter(adapter);

        //setting defalut user name in spinner
        int cpos = 0;
        for(int i = 0; i < adapter.getCount(); i++){
            cursor.moveToPosition(i);
            Double temp = Double.parseDouble( cursor.getString(cursor.getColumnIndex("_id")) );
            if ( temp == userId ){
                cpos = i;
                break;
            }
        }
        ((Spinner) addEntryView.findViewById(R.id.fromUser)).setSelection(cpos);



        SimpleDateFormat dmy = new SimpleDateFormat("dd-MM-yyyy");
        String dmyDate = dmy.format(new Date());

        ((EditText) addEntryView.findViewById(R.id.datePicker)).setText(dmyDate);

        //((EditText) addEntryView.findViewById(R.id.datePicker)).setOnClickListener(new datePicker());

        EditText datePicker = ((EditText) addEntryView.findViewById(R.id.datePicker));
        EditText created_date = ((EditText) addEntryView.findViewById(R.id.created_date));
        datePicker.setOnClickListener(new CustomDatePicker(ActivityAddEntry.this, datePicker, created_date,false));


        SimpleDateFormat tmpdmy = new SimpleDateFormat("yyyy-MM-dd");
        String tmpdmyDate = tmpdmy.format(new Date());

        ((EditText) addEntryView.findViewById(R.id.created_date)).setText(tmpdmyDate);


        ((Button)addEntryView.findViewById(R.id.saveBtn)).setOnClickListener(new saveData());



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



    // need to make this class as global,
    public class populateUserListAdapter extends SimpleCursorAdapter {

        private Context mContext;
        private Context appContext;
        private int layout;
        private Cursor cr;
        private final LayoutInflater inflater;

        public populateUserListAdapter(Context context,int layout, Cursor c ) {
            super(context,layout,c,new String[]{},new int[]{},0);
            this.layout=layout;
            this.mContext = context;
            this.inflater=LayoutInflater.from(context);
            this.cr=c;
        }

        @Override
        public View newView (Context context, Cursor cursor, ViewGroup parent) {
            return inflater.inflate(layout, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);

            ((TextView)view.findViewById(R.id.item_name)).setText(cursor.getString(cursor.getColumnIndex("name")));
            ((TextView)view.findViewById(R.id.item_phone)).setText(cursor.getString(cursor.getColumnIndex("phone")));

        }

    }


    /*
    private class datePicker implements View.OnClickListener {
        @Override
        public void onClick(View v) {


            //To show current date in the datepicker
            Calendar mcurrentDate=Calendar.getInstance();
            int mYear = mcurrentDate.get(Calendar.YEAR);
            int mMonth = mcurrentDate.get(Calendar.MONTH);
            int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog mDatePicker=new DatePickerDialog(ActivityAddEntry.this, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                    selectedmonth++;
                    String actualMonth=""+selectedmonth;
                    if(selectedmonth<10){
                        actualMonth="0"+actualMonth;
                    }

                    String actualDay=""+selectedday;
                    if(selectedday<10){
                        actualDay="0"+actualDay;
                    }

                    ((EditText) addEntryView.findViewById(R.id.datePicker)).setText(actualDay + "-" + actualMonth + "-" + selectedyear);

                    ((EditText) addEntryView.findViewById(R.id.created_date)).setText(selectedyear + "-" + actualMonth + "-" + actualDay );
                }
            },mYear, mMonth, mDay);
            mDatePicker.setTitle("Select date");
            mDatePicker.show();

        }
    }
*/


    private class saveData implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Map<String, String> data = new HashMap<String, String>();

            if(fromActivity.equals("ActivityLendAndBorrowPersonal")) {

                data.put("created_date",  ((EditText) addEntryView.findViewById(R.id.created_date) ).getText().toString() );
                data.put("description",  ((EditText) addEntryView.findViewById(R.id.description) ).getText().toString() );

                if(actionFlag==false) {
                    data.put("from_user", "1" );
                    data.put("to_user", ((Spinner) addEntryView.findViewById(R.id.fromUser)).getSelectedItemId()+"" );
                }
                else{
                    data.put("from_user",  ((Spinner) addEntryView.findViewById(R.id.fromUser)).getSelectedItemId()+"" );
                    data.put("to_user", "1" );
                }

                data.put("amt", ((EditText) addEntryView.findViewById(R.id.amount) ).getText().toString() );

                if (myDb.insertEntry(data)==1) {
                    Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();




                    Intent i = new Intent(ActivityAddEntry.this, ActivityLendAndBorrowIndividual.class);
                    i.putExtra("fromActivity", "ActivityLendAndBorrow");
                    i.putExtra("userId", ""+userId);
                    i.putExtra("userName",  userName );
                    startActivity(i);
                    finish();


                } else {
                    Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


}
