package com.bikesh.scorpio.giventake;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityHome extends ActionBarActivity {

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
        AC.setupDrawer(view, ActivityHome.this, toolbar );

        //loading home activity templet in to template frame
        FrameLayout frame = (FrameLayout) findViewById(R.id.mainFrame);
        frame.removeAllViews();
        Context darkTheme = new ContextThemeWrapper(this, R.style.AppTheme);
        LayoutInflater inflater = (LayoutInflater) darkTheme.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View homeView=  inflater.inflate(R.layout.activity_home, null);



        frame.addView(homeView);

        ((TextView) homeView.findViewById(R.id.lendAndBorrow)).setOnClickListener(new linkClicked(1));
        ((TextView) homeView.findViewById(R.id.personalExpense)).setOnClickListener(new linkClicked(2));
        ((TextView) homeView.findViewById(R.id.jointExpense)).setOnClickListener(new linkClicked(3));

    }

    private class linkClicked implements View.OnClickListener {
        int item;

        public linkClicked(int i) {
            item = i;
        }

        @Override
        public void onClick(View v) {
            switch (item){
                case 1:
                    startActivity(new Intent(ActivityHome.this, ActivityLendAndBorrow.class));
                    break;
                case 2:
                    startActivity(new Intent(ActivityHome.this, ActivityPersonalExpense.class));
                    break;
                case 3:
                    startActivity(new Intent(ActivityHome.this, ActivityJointExpense.class));
                    break;

            }

        }
    }
}
