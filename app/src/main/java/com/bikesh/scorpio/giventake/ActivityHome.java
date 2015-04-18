package com.bikesh.scorpio.giventake;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class ActivityHome extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        GiveNTakeApplication AC = (GiveNTakeApplication)getApplicationContext();
        View view = getWindow().getDecorView().findViewById(android.R.id.content);
        AC.setupDrawer(view, ActivityHome.this, getFragmentManager(), toolbar );

    }

}
