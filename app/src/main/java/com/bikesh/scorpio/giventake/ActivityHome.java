package com.bikesh.scorpio.giventake;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class ActivityHome extends ActionBarActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* Assinging the toolbar object ot the view
        and setting the the Action bar to our toolbar
         */


        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        GiveNTakeApplication AC = (GiveNTakeApplication)getApplicationContext();
        View view = getWindow().getDecorView().findViewById(android.R.id.content);
        AC.setupDrawer(view, ActivityHome.this, getFragmentManager(), toolbar );



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



    public void loginfunc(View v){

        Toast.makeText(ActivityHome.this,"Inside login ",Toast.LENGTH_SHORT).show();
    }

}

