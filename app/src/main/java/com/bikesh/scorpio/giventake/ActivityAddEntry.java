package com.bikesh.scorpio.giventake;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ActivityAddEntry extends ActionBarActivity {

    String fromActivity=null;

    DBHelper myDb;

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
        }




        // getting options from xml string array
        ArrayAdapter<String> actionSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.addEntryAction));
        ((Spinner)findViewById(R.id.actionSpinner)).setAdapter(actionSpinnerArrayAdapter);
        ((Spinner)findViewById(R.id.actionSpinner)).setOnItemSelectedListener(new selectedAction());


        // Spinner element
        //Spinner spinner = (Spinner) addEntryView.findViewById(R.id.fromUser);

        Cursor cursor = myDb.getAllUsers();


        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Select a User");
        categories.add("Raju");
        categories.add("Kiran");
        categories.add("Prasath");
        categories.add("Siva");
        categories.add("Sid");
        categories.add("rech");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        //((Spinner) addEntryView.findViewById(R.id.fromUser)).setAdapter(dataAdapter);

        //((Spinner) addEntryView.findViewById(R.id.fromUser)).setAdapter(new populateUserListAdapter());

        ((Spinner) addEntryView.findViewById(R.id.fromUser)).setAdapter(new populateUserListAdapter(this, R.layout.custom_spinner_item_template, cursor));


    }

    private class selectedAction implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(id==0){
                ((TextView) addEntryView.findViewById (R.id.selectUserLabel)).setText("Give to ");
            }
            else{
                ((TextView) addEntryView.findViewById (R.id.selectUserLabel)).setText("Borrow from ");
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }



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

            ((TextView)view.findViewById(R.id.text_main_seen)).setText(cursor.getString(cursor.getColumnIndex("name")));
            //((TextView)view.findViewById(R.id.item_amt)).setText("100.00");

        }

    }






}
