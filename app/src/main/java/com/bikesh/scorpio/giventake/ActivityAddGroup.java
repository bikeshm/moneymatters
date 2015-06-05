package com.bikesh.scorpio.giventake;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bikesh.scorpio.giventake.adapters.Adapter_RecyclerViewList;
import com.bikesh.scorpio.giventake.libraries.functions;
import com.bikesh.scorpio.giventake.model.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.bikesh.scorpio.giventake.libraries.functions.md5;
import static com.bikesh.scorpio.giventake.libraries.parsePhone.parsePhone;


public class ActivityAddGroup extends ActivityBase {

    String fromActivity=null;

    //View addGroupView;

    private DBHelper myDb ;

    Intent backActivityIntent=null;

    RecyclerView recyclerView;
    Adapter_RecyclerViewList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

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
            ((Button) currentView.findViewById(R.id.cancelBtn)).setVisibility(View.GONE);
            ((LinearLayout) currentView.findViewById(R.id.passwordLayer)).setVisibility(View.VISIBLE);

            getSupportActionBar().setTitle("Register");
            backActivityIntent = new Intent(ActivityAddGroup.this, ActivityHome.class);

        } else if (fromActivity.equals("ActivityPersonalExpense")) {
            ((LinearLayout) currentView.findViewById(R.id.emailLayer)).setVisibility(View.GONE);
            ((LinearLayout) currentView.findViewById(R.id.phoneLayer)).setVisibility(View.GONE);
            backActivityIntent = new Intent(ActivityAddGroup.this, ActivityPersonalExpense.class);
            getSupportActionBar().setTitle("Create Collection");

        } else if (fromActivity.equals("ActivityJointExpense")) {
            getSupportActionBar().setTitle("Create a Group");

            ((LinearLayout) currentView.findViewById(R.id.isOnlineLayer)).setVisibility(View.VISIBLE);
            ((LinearLayout) currentView.findViewById(R.id.groupTypeLayer)).setVisibility(View.VISIBLE);
            ((LinearLayout) currentView.findViewById(R.id.grupMembersLayer)).setVisibility(View.VISIBLE);


            ((LinearLayout) currentView.findViewById(R.id.emailLayer)).setVisibility(View.GONE);
            ((LinearLayout) currentView.findViewById(R.id.phoneLayer)).setVisibility(View.GONE);

            backActivityIntent = new Intent(ActivityAddGroup.this, ActivityJointExpense.class);

            //getting all user from db
            //Cursor cursor = myDb.getAllUsers();

            //getting all user from contact
            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

            //Adapter_CustomSimpleCursor adapter = new Adapter_CustomSimpleCursor(this, R.layout.listview_item_with_checkbox_template, cursor);

            //((ListView) currentView.findViewById(R.id.users)).setAdapter(adapter);
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



        ((Button) currentView.findViewById(R.id.saveBtn)).setOnClickListener(new saveData());
        ((Button) currentView.findViewById(R.id.cancelBtn)).setOnClickListener(new cancelActivity());




    }


    private class saveData implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if(((EditText) currentView.findViewById(R.id.name) ).getText().toString().trim().equals("")){
                Toast.makeText(getApplicationContext(),"Name required", Toast.LENGTH_LONG).show();
                return;
            }

            Map<String, String> data = new HashMap<String, String>();

            //Todo:- validate and escape incomming data
            data.put("name",  ((EditText) currentView.findViewById(R.id.name) ).getText().toString());
            data.put("description", ((EditText) currentView.findViewById(R.id.description)).getText().toString());


            if(fromActivity.equals("ActivityLendAndBorrow") || fromActivity.equals("ActivitySplash") ) {


                data.put("email",  ((EditText) currentView.findViewById(R.id.email) ).getText().toString() );
                data.put("phone", parsePhone(((EditText) currentView.findViewById(R.id.phone)).getText().toString()) );

                if (((LinearLayout) currentView.findViewById(R.id.passwordLayer)).getVisibility()==View.VISIBLE){
                    data.put("password", md5(((EditText) currentView.findViewById(R.id.phone)).getText().toString()) );
                }

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

                HashMap<String, Map<String, String>> members1 = new HashMap<String,  Map<String, String>>(adapter.selectedUsers1);



                /*
                CheckBox cb;
                ListView mainListView =((ListView) currentView.findViewById(R.id.users));
                for (int x = 0; x<mainListView.getChildCount();x++){
                    cb = (CheckBox)mainListView.getChildAt(x).findViewById(R.id.item_name);

                    if(cb.isChecked()){
                        Log.i ( "selected", ((TextView) mainListView.getChildAt(x).findViewById(R.id.item_id)).getText().toString()  );
                        members.add( Integer.parseInt( ((TextView) mainListView.getChildAt(x).findViewById(R.id.item_id)).getText().toString()  )  );
                    }
                }
                */


                int id = ((RadioGroup) currentView.findViewById(R.id.groupType)).getCheckedRadioButtonId();
                if (id == -1){
                    //no item selected
                }
                else {
                    if (id == R.id.radioMonthlyRenewing) {
                        ismonthlytask=1;
                    }
                }

                id = ((RadioGroup) currentView.findViewById(R.id.isOnline)).getCheckedRadioButtonId();
                if (members1.size() == 0){
                    //no item selected
                    Toast.makeText(getApplicationContext(), "Select Group members", Toast.LENGTH_SHORT).show();
                }
                else {

                    //for(int i=0;i<members1.size();i++) {

                        //CheckBoxSelected.remove(rid);
                        //getUserFromContactId
                        //members.get(i);
                    //}

                    for (Map.Entry<String, Map<String, String>> entry : members1.entrySet())
                    {
                        Log.i("kv pair", "Key -> " + entry.getKey() + " value -> " + entry.getValue().get("name"));

                        members.add( ""+registreUserFromContact(entry.getValue().get("phone"),entry.getValue().get("name")) );
                    }

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
