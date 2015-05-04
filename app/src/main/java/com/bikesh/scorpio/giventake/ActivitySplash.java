package com.bikesh.scorpio.giventake;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


public class ActivitySplash extends ActionBarActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {

            // Using handler with postDelayed called runnable run method

            @Override
            public void run() {
                Intent i = new Intent(ActivitySplash.this, ActivityHome.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, 3*1000); // wait for 3 seconds




        /* Assinging the toolbar object ot the view
        and setting the the Action bar to our toolbar
         */


        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        GiveNTakeApplication AC = (GiveNTakeApplication)getApplicationContext();
        View view = getWindow().getDecorView().findViewById(android.R.id.content);
        AC.setupDrawer(view, ActivitySplash.this, getFragmentManager(), toolbar );
        */


        /*
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("sc");
            //Set SubTitle
           // toolbar.setSubtitle("Sub Title");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/




    } // onreate end




}
