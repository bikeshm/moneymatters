package com.bikesh.scorpio.giventake;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;


public class ActivityJointExpenseIndividual extends ActionBarActivity {

    View JointExpenseIndividual;

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
        AC.setupDrawer(view, ActivityJointExpenseIndividual.this, toolbar);

        //loading home activity templet in to template frame
        FrameLayout frame = (FrameLayout) findViewById(R.id.mainFrame);
        frame.removeAllViews();
        Context darkTheme = new ContextThemeWrapper(this, R.style.AppTheme);
        LayoutInflater inflater = (LayoutInflater) darkTheme.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        JointExpenseIndividual=  inflater.inflate(R.layout.activity_joint_expense_individual, null);

        frame.addView(JointExpenseIndividual);


        ((ImageButton)JointExpenseIndividual.findViewById(R.id.addEntry)).setOnClickListener(new openAddnewEntrry());

        ((LinearLayout)JointExpenseIndividual.findViewById(R.id.restore)).setOnClickListener(new restoreTable());
        ((ImageView)JointExpenseIndividual.findViewById(R.id.restorebtn)).setOnClickListener(new restoreTable());


    }



    private class restoreTable implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if(((ScrollView) JointExpenseIndividual.findViewById(R.id.scrollView1) ).getVisibility() == View.VISIBLE){
                ((ScrollView) JointExpenseIndividual.findViewById(R.id.scrollView1) ).setVisibility(View.GONE);
                ((ImageView)JointExpenseIndividual.findViewById(R.id.restorebtn)).setImageResource(R.drawable.double_arrow_down);
            }
            else{
                ((ScrollView) JointExpenseIndividual.findViewById(R.id.scrollView1) ).setVisibility(View.VISIBLE);
                ((ImageView)JointExpenseIndividual.findViewById(R.id.restorebtn)).setImageResource(R.drawable.double_arrow_up);
            }


        }
    }





    private class openAddnewEntrry implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ActivityJointExpenseIndividual.this, ActivityAddEntry.class);
            i.putExtra("fromActivity", "ActivityJointExpenseIndividual");
            startActivity(i);

        }
    }








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_joint_expense_individual, menu);
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


}
