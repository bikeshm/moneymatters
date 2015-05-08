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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class ActivityAddGroup extends ActionBarActivity {

    String fromActivity=null;

    View addGroupView;

    private DBHelper mydb ;

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
        AC.setupDrawer(view, ActivityAddGroup.this, toolbar);

        //loading home activity templet in to template frame
        FrameLayout frame = (FrameLayout) findViewById(R.id.mainFrame);
        frame.removeAllViews();
        Context darkTheme = new ContextThemeWrapper(this, R.style.AppTheme);
        LayoutInflater inflater = (LayoutInflater) darkTheme.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addGroupView=  inflater.inflate(R.layout.activity_add_group, null);

        frame.addView(addGroupView);


        mydb = new DBHelper(this);


        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            fromActivity= null;
        } else {
            fromActivity= extras.getString("fromActivity");
        }


        if(fromActivity.equals("ActivityLendAndBorrow")){

        }

        switch (fromActivity) {
            case "ActivityLendAndBorrow":
                //((TextView) addGroupView.findViewById(R.id.op)).setText("Add user");
                break;
            case "ActivityPersonalExpense":
                ((LinearLayout) addGroupView.findViewById(R.id.emailLayer) ).setVisibility(View.GONE);
                ((LinearLayout) addGroupView.findViewById(R.id.phoneLayer) ).setVisibility(View.GONE);
                break;

            default:
                throw new IllegalArgumentException("Invalid  ");
        }



        ((Button) addGroupView.findViewById(R.id.saveBtn)).setOnClickListener(new saveData());




    }


    private class saveData implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Map<String, String> data = new HashMap<String, String>();

            if(fromActivity.equals("ActivityLendAndBorrow")) {

                data.put("name",  ((EditText) addGroupView.findViewById(R.id.name) ).getText().toString() );
                data.put("email",  ((EditText) addGroupView.findViewById(R.id.email) ).getText().toString() );
                data.put("phone", ((EditText) addGroupView.findViewById(R.id.phone) ).getText().toString() );
                data.put("description", ((EditText) addGroupView.findViewById(R.id.description) ).getText().toString() );

                if (mydb.insertUser(data)==1) {
                    Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_add_user, menu);
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
