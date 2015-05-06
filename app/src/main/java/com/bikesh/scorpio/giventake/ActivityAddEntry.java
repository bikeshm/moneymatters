package com.bikesh.scorpio.giventake;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class ActivityAddEntry extends ActionBarActivity {

    String fromActivity=null;

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
        View addEntryView=  inflater.inflate(R.layout.activity_add_entry, null);

        frame.addView(addEntryView);


        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            fromActivity= null;
        } else {
            fromActivity= extras.getString("fromActivity");
        }

        // Spinner element
        Spinner spinner = (Spinner) addEntryView.findViewById(R.id.fromUser);


        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Raju");
        categories.add("Kiran");
        categories.add("Prasath");
        categories.add("Siva");
        categories.add("Sid");
        categories.add("rech");

        // Creating adapter for spinner
        //ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
       // dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        //spinner.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinner.setAdapter(dataAdapter);




        Spinner actionSpinner = (Spinner)findViewById(R.id.actionSpinner);
        ArrayAdapter<String> actionSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.addEntryAction));
        actionSpinner.setAdapter(actionSpinnerArrayAdapter);

    }
}
