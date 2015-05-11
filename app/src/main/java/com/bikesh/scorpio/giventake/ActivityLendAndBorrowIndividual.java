package com.bikesh.scorpio.giventake;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;


public class ActivityLendAndBorrowIndividual extends ActionBarActivity {

    View lendAndBorrowPersonalView;
    DBHelper myDb;
    String fromActivity=null;
    long userId=0;
    String userName="";

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
        AC.setupDrawer(view, ActivityLendAndBorrowIndividual.this, toolbar);

        //loading home activity templet in to template frame
        FrameLayout frame = (FrameLayout) findViewById(R.id.mainFrame);
        frame.removeAllViews();
        Context darkTheme = new ContextThemeWrapper(this, R.style.AppTheme);
        LayoutInflater inflater = (LayoutInflater) darkTheme.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lendAndBorrowPersonalView =  inflater.inflate(R.layout.activity_lend_and_borrow_personal, null);

        frame.addView(lendAndBorrowPersonalView);

        myDb = new DBHelper(this);

        Bundle extras = getIntent().getExtras();



        if(extras == null) {
            fromActivity= null;
        } else {
            fromActivity= extras.getString("fromActivity");
            userId = Long.parseLong(extras.getString("userId"));
            userName = extras.getString("userName");
        }

        ((TextView) lendAndBorrowPersonalView.findViewById(R.id.username)).setText(userName);

        ((ImageButton) lendAndBorrowPersonalView.findViewById(R.id.addEntry)).setOnClickListener(new openAddnewEntrry());


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDb != null) {
            myDb.close();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_lend_and_borrow_personal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class openAddnewEntrry implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ActivityLendAndBorrowIndividual.this, ActivityAddEntry.class);
            i.putExtra("fromActivity", "ActivityLendAndBorrowPersonal");
            i.putExtra("userId", ""+userId);
            i.putExtra("userName",  userName );
            startActivity(i);

        }
    }


}
