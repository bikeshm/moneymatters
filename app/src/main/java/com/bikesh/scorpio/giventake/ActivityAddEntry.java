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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
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
        ((Spinner) addEntryView.findViewById(R.id.fromUser)).setAdapter(dataAdapter);




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
}
