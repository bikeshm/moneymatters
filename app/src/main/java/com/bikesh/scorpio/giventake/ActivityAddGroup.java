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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bikesh.scorpio.giventake.libraries.CustomRequest;
import com.bikesh.scorpio.giventake.database.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ActivityAddGroup extends ActivityBase {

    String fromActivity=null;
    Intent backActivityIntent=null;
    userCheckBoxRecycler adapter_userCheckBoxRecycler;

    DBHelper myDb;

    String apiUrl_AddGroup = "http://givntake.workassis.com/api/group/add/";

    RequestQueue Rqueue;

    String groupId="0";
    Map<String, String> dbGroup;

    EditText nameEditText, descriptionEditText;
    RadioGroup isOnlineRadio,groupTypeRadio;
    RecyclerView usersRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);


        nameEditText        = ((EditText) currentView.findViewById(R.id.name) );
        descriptionEditText = ((EditText) currentView.findViewById(R.id.description));
        isOnlineRadio       = ((RadioGroup) currentView.findViewById(R.id.isOnline));
        groupTypeRadio      = ((RadioGroup) currentView.findViewById(R.id.groupType));
        usersRecyclerView   = (RecyclerView) findViewById(R.id.recycler_Users);;

        myDb = new DBHelper(this);

        Rqueue = Volley.newRequestQueue(this);

        //--- initialising RecyclerView otherwise it is throwing null pointer exception

        usersRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        usersRecyclerView.setLayoutManager(layoutManager);
        //--

        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            fromActivity= "";
        } else {
            fromActivity= extras.getString("fromActivity");

            groupId = extras.getString("groupId","0");

        }


        //edit

        ArrayList<String> existingMembersPhoneList = new ArrayList<String>();

        if(!groupId.equals("0")){

            dbGroup = myDb.fetchJointGroupbyId(groupId );

            nameEditText.setText(dbGroup.get("name"));
            descriptionEditText.setText("description");

            if(dbGroup.get("isonline").equals("1")) {
                isOnlineRadio.check(R.id.radioYes);
            }
            else {
                isOnlineRadio.check(R.id.radioNo);
            }
            // note :- there is a chance to make offline group to online currently we are not allowing
            //isOnlineRadio.setEnabled(false);
            ((RadioButton) currentView.findViewById(R.id.radioYes)).setEnabled(false);
            ((RadioButton) currentView.findViewById(R.id.radioNo)).setEnabled(false);


            if(dbGroup.get("ismonthlytask").equals("1")) {
                groupTypeRadio.check(R.id.radioMonthlyRenewing);
            }
            else {
                groupTypeRadio.check(R.id.radioOnGoingSingle);
            }

            //usersRecyclerView

            existingMembersPhoneList = myDb.getAllUsersPhoneInGroup(groupId);
        }



        getSupportActionBar().setTitle("Create a Group");

        backActivityIntent = new Intent(ActivityAddGroup.this, ActivityJointExpense.class);

        //getting all user from db
        //Cursor cursor = myDb.getAllUsers();

        //getting all user from contact
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC" );


        //Adapter_CustomSimpleCursor adapter = new Adapter_CustomSimpleCursor(this, R.layout.listview_item_with_checkbox_template, cursor);

        //((ListView) currentView.findViewById(R.id.users)).setAdapter(adapter);
            /*
            recyclerView = (RecyclerView) findViewById(R.id.users);
            recyclerView.setHasFixedSize(true);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            */


        //adapter = new Adapter_RecyclerViewList(cursor, this);
        adapter_userCheckBoxRecycler = new userCheckBoxRecycler(cursor, this, existingMembersPhoneList);
        usersRecyclerView.setAdapter(adapter_userCheckBoxRecycler);






        ((Button) currentView.findViewById(R.id.saveBtn)).setOnClickListener(new saveData());
        ((Button) currentView.findViewById(R.id.cancelBtn)).setOnClickListener(new cancelActivity());






    }


    private class saveData implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            showProgress("Saving ...");

            if(nameEditText.getText().toString().trim().equals("")){
                Toast.makeText(getApplicationContext(),"Name required", Toast.LENGTH_LONG).show();
                closeProgress();
                return;
            }

            Map<String, String> data = new HashMap<String, String>();

            //Todo:- validate and escape incomming data
            data.put("name",  nameEditText.getText().toString());
            data.put("description", descriptionEditText.getText().toString());



            ArrayList<String> members =new ArrayList<String>(adapter_userCheckBoxRecycler.CheckBoxSelected);

            String membersDataJSON  = "[]";


            HashMap<String, Map<String, String>> members1 = new HashMap<String,  Map<String, String>>(adapter_userCheckBoxRecycler.selectedUsers1);

            int ismonthlytask=0;
            int groupTypeRadioButtonId = groupTypeRadio.getCheckedRadioButtonId();
            if (groupTypeRadioButtonId == -1){
                //no item selected
            }
            else {
                if (groupTypeRadioButtonId == R.id.radioMonthlyRenewing) {
                    ismonthlytask=1;
                }
            }
            data.put("ismonthlytask",""+ismonthlytask);


            if (members1.size() == 0){
                //no item selected
                Toast.makeText(getApplicationContext(), "Select Group members", Toast.LENGTH_SHORT).show();
                closeProgress();
            }
            else {

                int i=0;

                membersDataJSON="[";
                for (Map.Entry<String, Map<String, String>> entry : members1.entrySet())
                {
                    Log.i("kv pair", "Key -> " + entry.getKey() + " value -> " + entry.getValue().get("name"));

                    members.add( ""+registreUserFromContact(entry.getValue().get("phone"),entry.getValue().get("name")) );

                    membersDataJSON = membersDataJSON + "{'name' : '"+ entry.getValue().get("name") +"', 'phone' : '"+entry.getValue().get("phone")+"'},";

                }

                membersDataJSON = membersDataJSON + "{'name' : '"+  myDb.getUserField("1","name") +"', 'phone' : '"+ myDb.getUserField("1","phone")+"'}";

                //removing last comma
                //if (membersDataJSON.charAt(membersDataJSON.length()-1)==',') {
                //    membersDataJSON = membersDataJSON.substring(0, membersDataJSON.length()-1);
                //}
                membersDataJSON=membersDataJSON+"]";

                members.add("1"); // adding root user id

                data.put("members_count", "" + members.size());


                int isOnlineRadioButtonId = isOnlineRadio.getCheckedRadioButtonId();

                if (isOnlineRadioButtonId == R.id.radioNo){  //selected offline save to local db

                    if(groupId.equals("0")) {

                        if (myDb.insertJointGroup(data) == 1) { //insert

                            Toast.makeText(getApplicationContext(), "Group created", Toast.LENGTH_SHORT).show();

                            //getting group details
                            Map<String, String> result = new HashMap<String, String>();
                            result = myDb.getJointGroup(data);

                            //insert user relation
                            if (result.size() > 0) {
                                if (myDb.insertUserGroupRelation(Integer.parseInt(result.get("_id")), members) == 1) {
                                    Toast.makeText(getApplicationContext(), "Data saved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                                }
                            }

                            closeProgress();
                            goBack();

                        } else {
                            Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                            closeProgress();
                        }
                    }
                    else{ //update

                        Log.i("group update",data+""+members );
                        closeProgress();
                    }



                } else{ //selected  online save groupdetails to local online group table and  parse

                    // note :- there is a chance to make offline group to online currently we are not allowing

                    data.put("members_json", membersDataJSON);

                    String ownerId = myDb.getUserField("1", "onlineid");

                    if(ownerId.equals("0") || ownerId.equals("") || ownerId ==null ){
                        Log.i("creating group","Invalid user online id "+ownerId  );
                        Toast.makeText(getApplicationContext(), "Error while creating Online group ", Toast.LENGTH_LONG).show();
                        closeProgress();
                        goBack();
                        return;
                    }

                    data.put("owner", ownerId );



                    if(groupId.equals("0")) {} //insert
                    else{ //update
                        data.put("id", dbGroup.get("onlineid") );
                    }




                    Log.i("data to server",data +"" );

                    CustomRequest jsObjRequest =   new CustomRequest

                            (Request.Method.POST, apiUrl_AddGroup, data, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {

                                    Log.i("api call addGroup", response.toString()+ "");
                                    // no need to edit local db

                                    //update user online id
                                    //myDb.updatUserOnlineIdByPhone()

                                    JSONArray user_dataJSON = response.optJSONArray("user_data");
                                    HashMap<String, String> tempStorage;

                                    try{
                                        for(int i = 0 ; i < user_dataJSON.length(); i++){

                                            tempStorage = new HashMap<String, String>();

                                            JSONObject jsonObj = user_dataJSON.getJSONObject(i);
                                            Iterator<String> keysIterator = jsonObj.keys();
                                            while (keysIterator.hasNext())
                                            {
                                                String keyStr = (String)keysIterator.next();
                                                String valueStr = jsonObj.getString(keyStr);
                                                tempStorage.put(keyStr,valueStr );
                                            }

                                            myDb.updatUserOnlineIdByPhone(tempStorage.get("phone"),tempStorage.get("onlineid")  );

                                        }

                                    }
                                    catch (JSONException e) {
                                    e.printStackTrace();
                                    }


                                    Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

                                    closeProgress();
                                    goBack();

                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i("api call", "ERROR "+error.getMessage());
                                    Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                                    closeProgress();
                                    //((Button)currentView.findViewById(R.id.saveBtn)).setEnabled(true);
                                }
                            });
                    Rqueue.add(jsObjRequest);

                    //closeProgress();
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
