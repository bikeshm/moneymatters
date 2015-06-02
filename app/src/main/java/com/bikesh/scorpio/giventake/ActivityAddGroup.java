package com.bikesh.scorpio.giventake;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bikesh.scorpio.giventake.model.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ActivityAddGroup extends ActionBarActivity {

    String fromActivity=null;

    View addGroupView;

    private DBHelper myDb ;

    Intent backActivityIntent=null;

    RecyclerView recyclerView;
    Adapter_RecyclerViewList adapter;

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


        myDb = new DBHelper(this);

        //--- initialising RecyclerView otherwise it is throwing null pointer exception
        recyclerView = (RecyclerView) findViewById(R.id.recycler_Users);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //--

        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            fromActivity= "";
        } else {
            fromActivity= extras.getString("fromActivity");

        }


        if (fromActivity.equals("ActivityLendAndBorrow")) {//((TextView) addGroupView.findViewById(R.id.op)).setText("Add user");
            backActivityIntent = new Intent(ActivityAddGroup.this, ActivityLendAndBorrow.class);
            getSupportActionBar().setTitle("Create User");

        } else if (fromActivity.equals("ActivitySplash")) {
            ((Button) addGroupView.findViewById(R.id.cancelBtn)).setVisibility(View.GONE);
            getSupportActionBar().setTitle("Register");
            backActivityIntent = new Intent(ActivityAddGroup.this, ActivityHome.class);

        } else if (fromActivity.equals("ActivityPersonalExpense")) {
            ((LinearLayout) addGroupView.findViewById(R.id.emailLayer)).setVisibility(View.GONE);
            ((LinearLayout) addGroupView.findViewById(R.id.phoneLayer)).setVisibility(View.GONE);
            backActivityIntent = new Intent(ActivityAddGroup.this, ActivityPersonalExpense.class);
            getSupportActionBar().setTitle("Create Collection");

        } else if (fromActivity.equals("ActivityJointExpense")) {
            getSupportActionBar().setTitle("Create a Group");

            ((LinearLayout) addGroupView.findViewById(R.id.isOnlineLayer)).setVisibility(View.VISIBLE);
            ((LinearLayout) addGroupView.findViewById(R.id.groupTypeLayer)).setVisibility(View.VISIBLE);
            ((LinearLayout) addGroupView.findViewById(R.id.grupMembersLayer)).setVisibility(View.VISIBLE);


            ((LinearLayout) addGroupView.findViewById(R.id.emailLayer)).setVisibility(View.GONE);
            ((LinearLayout) addGroupView.findViewById(R.id.phoneLayer)).setVisibility(View.GONE);

            backActivityIntent = new Intent(ActivityAddGroup.this, ActivityJointExpense.class);


            Cursor cursor = myDb.getAllUsers();

            //Adapter_CustomSimpleCursor adapter = new Adapter_CustomSimpleCursor(this, R.layout.listview_item_with_checkbox_template, cursor);

            //((ListView) addGroupView.findViewById(R.id.users)).setAdapter(adapter);
                /*
                recyclerView = (RecyclerView) findViewById(R.id.users);
                recyclerView.setHasFixedSize(true);

                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
                */
            adapter = new Adapter_RecyclerViewList(cursor, this);
            recyclerView.setAdapter(adapter);


        } else {
            throw new IllegalArgumentException("Invalid  ");
        }



        ((Button) addGroupView.findViewById(R.id.saveBtn)).setOnClickListener(new saveData());
        ((Button) addGroupView.findViewById(R.id.cancelBtn)).setOnClickListener(new cancelActivity());




    }


    private class saveData implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if(((EditText) addGroupView.findViewById(R.id.name) ).getText().toString().trim().equals("")){
                Toast.makeText(getApplicationContext(),"Name required", Toast.LENGTH_LONG).show();
                return;
            }

            Map<String, String> data = new HashMap<String, String>();

            //Todo:- validate and escape incomming data
            data.put("name",  ((EditText) addGroupView.findViewById(R.id.name) ).getText().toString());
            data.put("description", ((EditText) addGroupView.findViewById(R.id.description)).getText().toString());


            if(fromActivity.equals("ActivityLendAndBorrow") || fromActivity.equals("ActivitySplash") ) {


                data.put("email",  ((EditText) addGroupView.findViewById(R.id.email) ).getText().toString() );
                data.put("phone", ((EditText) addGroupView.findViewById(R.id.phone) ).getText().toString() );


                if (myDb.insertUser(data)==1) {
                    Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

                    goBack();

                } else {
                    Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                }
            }
            else  if(fromActivity.equals("ActivityPersonalExpense")) {

                if (myDb.insertCollection(data)==1) {
                    Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

                    goBack();

                } else {
                    Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                }


            }

            else  if(fromActivity.equals("ActivityJointExpense")) {

                int ismonthlytask=0;

                ArrayList<String> members =new ArrayList<String>(adapter.CheckBoxSelected);




                /*
                CheckBox cb;
                ListView mainListView =((ListView) addGroupView.findViewById(R.id.users));
                for (int x = 0; x<mainListView.getChildCount();x++){
                    cb = (CheckBox)mainListView.getChildAt(x).findViewById(R.id.item_name);

                    if(cb.isChecked()){
                        Log.i ( "selected", ((TextView) mainListView.getChildAt(x).findViewById(R.id.item_id)).getText().toString()  );
                        members.add( Integer.parseInt( ((TextView) mainListView.getChildAt(x).findViewById(R.id.item_id)).getText().toString()  )  );
                    }
                }
                */


                int id = ((RadioGroup) addGroupView.findViewById(R.id.groupType)).getCheckedRadioButtonId();
                if (id == -1){
                    //no item selected
                }
                else {
                    if (id == R.id.radioMonthlyRenewing) {
                        ismonthlytask=1;
                    }
                }

                id = ((RadioGroup) addGroupView.findViewById(R.id.isOnline)).getCheckedRadioButtonId();
                if (id == -1){
                    //no item selected
                }
                else{

                    members.add("1"); // adding root user id

                    data.put("members_count", "" + members.size());
                    data.put("ismonthlytask",""+ismonthlytask);

                    if (id == R.id.radioNo){  //selected offline save to local db

                        if (myDb.insertJointGroup(data)==1) {

                            Toast.makeText(getApplicationContext(), "Group created", Toast.LENGTH_SHORT).show();

                           //getting group details
                            Map<String, String> result = new HashMap<String, String>();


                            result=myDb.getJointGroup(data);



                            //insert user relation
                            if(result.size()>0){
                                if(myDb.insertUserGroupRelation( Integer.parseInt(result.get("_id")), members)==1){
                                    Toast.makeText(getApplicationContext(), "Data saved", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                                }
                            }

                            goBack();

                        } else {
                            Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                        }

                    } else{ //selected  online save groupdetails to local online group table and  parse

                    }
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


    private class cancelActivity implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            goBack();
        }
    }

    private void goBack(){
        startActivity(backActivityIntent);
        finish();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDb != null) {
            myDb.close();
        }
    }

}
