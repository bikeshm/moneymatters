package com.bikesh.scorpio.giventake;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class ActivityHome extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_template);



        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        GiveNTakeApplication AC = (GiveNTakeApplication)getApplicationContext();
        View view = getWindow().getDecorView().findViewById(android.R.id.content);
        AC.setupDrawer(view, ActivityHome.this, getFragmentManager(), toolbar );


        FrameLayout frame = (FrameLayout) findViewById(R.id.mainFrame);
        frame.removeAllViews();
        View home= LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_splash, null);
        frame.addView(home);

        ((Button) home.findViewById(R.id.clickme)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"clicked form home", Toast.LENGTH_LONG).show();
            }
        });

    }

}
