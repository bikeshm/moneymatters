/*
package com.tricon.labs.pepper.activities.jointexpense;

import android.os.Bundle;

import com.tricon.labs.pepper.R;
import com.tricon.labs.pepper.activities.ActivityBase;


public class ActivityJointExpenseAddGroup extends ActivityBase {

    Face 2

    String fromActivity=null;
    Intent backActivityIntent=null;
    UserCheckBoxRecycler adapter_userCheckBoxRecycler;

    DBHelper myDb;

    String apiUrl_AddGroup = "http://givntake.workassis.com/api/group/add/";
    String apiUrl_GroupDetails = "http://givntake.workassis.com/api/group/get/" ;
    RequestQueue Rqueue;

    String groupId="0";
    Map<String, String> dbGroup;

    EditText nameEditText, descriptionEditText, UsersFilter;
    RadioGroup isOnlineRadio,groupTypeRadio;
    RecyclerView usersRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint_expense_add_group);

        nameEditText        = ((EditText) currentView.findViewById(R.id.name) );
        descriptionEditText = ((EditText) currentView.findViewById(R.id.description));
        isOnlineRadio       = ((RadioGroup) currentView.findViewById(R.id.isOnline));
        groupTypeRadio      = ((RadioGroup) currentView.findViewById(R.id.groupType));
        usersRecyclerView   = (RecyclerView) findViewById(R.id.recycler_Users);;

        UsersFilter = ((EditText) currentView.findViewById(R.id.recycler_Users_Filter) );

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

        getSupportActionBar().setTitle("Create a Group");

        //edit



        if(!groupId.equals("0")){ //edit group

            dbGroup = myDb.fetchJointGroupbyId(groupId );

            getSupportActionBar().setTitle("Edit Group");
            // note :- there is a chance to make offline group to online currently we are not allowing
            //isOnlineRadio.setEnabled(false);
            ((RadioButton) currentView.findViewById(R.id.radioYes)).setEnabled(false);
            ((RadioButton) currentView.findViewById(R.id.radioNo)).setEnabled(false);


            if(dbGroup.get("isonline").equals("1")) {
                setupOnlineGroupEditField();
            }
            else {
                setupOfflineGroupEditField();
            }

        }
        else{ //insert
            generateRecylerView(null);
        }

        //backActivityIntent = new Intent(ActivityJointExpenseAddGroup.this, ActivityJointExpense.class);
    }

    private void setupOnlineGroupEditField() {

        showProgress("Loading . . ");

        isOnlineRadio.check(R.id.radioYes);

        Map<String, String> data = new HashMap<String, String>();

        data.put("id",dbGroup.get("onlineid").toString());

        data.put("required","members"); //shoud be seprated with comma and space

        CustomRequest jsObjRequest =   new CustomRequest
                (Request.Method.POST, apiUrl_GroupDetails, data, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i("api call", "get group " + response.toString());

                        JSONObject group_dataJSON = response.optJSONObject("data").optJSONObject("group");
                        JSONArray members_dataJSON = response.optJSONObject("data").optJSONArray("members");


                        try {

                            ArrayList<String> existingMembersPhoneList = new ArrayList<String>();

                            nameEditText.setText(group_dataJSON.getString("name"));
                            descriptionEditText.setText(group_dataJSON.getString("description"));

                            if(group_dataJSON.getString("ismonthlytask").equals("1")) {
                                groupTypeRadio.check(R.id.radioMonthlyRenewing);
                            }
                            else {
                                groupTypeRadio.check(R.id.radioOnGoingSingle);
                            }

                            if(members_dataJSON != null) {
                                processOnlineGroupMembers(members_dataJSON);
                            }

                            for(int i = 0 ; i < members_dataJSON.length(); i++){

                                JSONObject jsonObj = members_dataJSON.getJSONObject(i);

                                Iterator<String> keysIterator = jsonObj.keys();
                                while (keysIterator.hasNext()) {
                                    String keyStr = (String) keysIterator.next();
                                    String valueStr = jsonObj.getString(keyStr);

                                    if(keyStr.equals("phone")){
                                        existingMembersPhoneList.add(valueStr);
                                    }
                                }
                            }

                            generateRecylerView(existingMembersPhoneList);



                            ((LinearLayout) currentView.findViewById(R.id.exsistingGroupMembers)).setVisibility(View.VISIBLE);
                            Cursor exuser = myDb.getAllUsersInGroup(dbGroup.get("_id"));
                            String userString="";
                            while (exuser.isAfterLast() == false) {
                                userString = userString +  exuser.getString(exuser.getColumnIndex("name")) + ", ";
                                exuser.moveToNext();
                            }
                            ((TextView)currentView.findViewById(R.id.existingMembers)).setText(userString);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            closeProgress();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("api call", "ERROR "+apiUrl_GroupDetails+error.getMessage());
                        Toast.makeText(getApplicationContext(), "Error while accessing online data", Toast.LENGTH_LONG).show();
                        closeProgress();
                    }
                });

        Rqueue.add(jsObjRequest);


    }



    private void processOnlineGroupMembers(JSONArray members_dataJSON) {

        Map<String, String> tempStorage = new HashMap<String, String>();
        ArrayList onlineGroupExistingUsers = new ArrayList();

        try {
            for(int i = 0 ; i < members_dataJSON.length(); i++){

                tempStorage = new HashMap<String, String>();

                JSONObject jsonObj = members_dataJSON.getJSONObject(i);
                Iterator<String> keysIterator = jsonObj.keys();
                while (keysIterator.hasNext())
                {
                    String keyStr = (String)keysIterator.next();
                    String valueStr = jsonObj.getString(keyStr);

                    //Log.i("api call","members_data "+ keyStr + " => " + valueStr );

                    String[] requiredKeys = new String[] {"user_id","name","phone","country_code"}; //,"created_date","photo"

                    if( Arrays.asList(requiredKeys).contains(keyStr) ){
                        //Log.i("api call r", keyStr + " - "+ valueStr);
                        tempStorage.put(keyStr,valueStr );
                    }

                }

                tempStorage.put("onlineid",tempStorage.get("user_id") );
                tempStorage.remove("user_id");

                //insert to db
                myDb.updateOnlineUserGroupRelation(tempStorage, dbGroup.get("_id").toString(),getContentResolver());

                Map user = myDb.getUserbyOnlineId(tempStorage.get("onlineid"));
                onlineGroupExistingUsers.add(user.get("_id"));
            }

            myDb.cleanupOnlineGroupRelation(onlineGroupExistingUsers, dbGroup.get("_id").toString() );

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void setupOfflineGroupEditField() {

        ArrayList<String> existingMembersPhoneList = new ArrayList<String>();




        nameEditText.setText(dbGroup.get("name"));
        descriptionEditText.setText(dbGroup.get("description"));

        if(dbGroup.get("ismonthlytask").equals("1")) {
            groupTypeRadio.check(R.id.radioMonthlyRenewing);
        }
        else {
            groupTypeRadio.check(R.id.radioOnGoingSingle);
        }


        isOnlineRadio.check(R.id.radioNo);

        existingMembersPhoneList = myDb.getAllUsersPhoneInGroup(groupId);

        generateRecylerView(existingMembersPhoneList);


        ((LinearLayout) currentView.findViewById(R.id.exsistingGroupMembers)).setVisibility(View.VISIBLE);
        Cursor exuser = myDb.getAllUsersInGroup(dbGroup.get("_id"));
        String userString="";
        while (exuser.isAfterLast() == false) {
            userString = userString +  exuser.getString(exuser.getColumnIndex("name")) + ", ";
            exuser.moveToNext();
        }
        ((TextView)currentView.findViewById(R.id.existingMembers)).setText(userString);



    }


    private void generateRecylerView(ArrayList<String> existingMembersPhoneList) {


        //getting all user from db
        //Cursor cursor = myDb.getAllUsers();

        //getting all user from contact
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC" );


        //Adapter_CustomSimpleCursor adapter = new Adapter_CustomSimpleCursor(this, R.layout.listview_item_with_checkbox_template, cursor);

        //((ListView) currentView.findViewById(R.id.users)).setAdapter(adapter);

            recyclerView = (RecyclerView) findViewById(R.id.users);
            recyclerView.setHasFixedSize(true);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);



        //adapter = new Adapter_RecyclerViewList(cursor, this);
        adapter_userCheckBoxRecycler = new UserCheckBoxRecycler(cursor, this, existingMembersPhoneList);
        usersRecyclerView.setAdapter(adapter_userCheckBoxRecycler);

        ((Button) currentView.findViewById(R.id.saveBtn)).setOnClickListener(new saveData());
        ((Button) currentView.findViewById(R.id.cancelBtn)).setOnClickListener(new cancelActivity());

        UsersFilter.addTextChangedListener(filterTextWatcher);

        UsersFilter.setInputType( ~(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT) );
        UsersFilter.setInputType( ~(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS) );

        usersRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) { }
        });


        closeProgress();
    }


    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            adapter_userCheckBoxRecycler.getFilter().filter(s);

        }

    };

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

                    members.add( ""+registerUserFromContact(entry.getValue().get("phone"),entry.getValue().get("name")) );

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

                    //Toast.makeText(getApplicationContext(),"group id save"+ dbGroup.get("_id"), Toast.LENGTH_LONG).show();

                    if(groupId.equals("0")) { //insert

                        if (myDb.insertJointGroup(data) == 1) { //insert

                            Toast.makeText(getApplicationContext(), "Group created", Toast.LENGTH_SHORT).show();

                            //getting group details
                            Map<String, String> newGroupDetails = new HashMap<String, String>();
                            newGroupDetails = myDb.getJointGroup(data);

                            //insert user relation
                            if (newGroupDetails.size() > 0) {
                                if (myDb.insertUserGroupRelation( newGroupDetails.get("_id"), members) == 1) {
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

                        data.put("_id", groupId );

                        //update group
                        if (myDb.updateJointGroup(data) == 1) {

                            //update user group relation
                            myDb.insertUserGroupRelation(groupId, members);

                            //clean up
                            //myDb.cleanupUserGroupRelation(groupId, members);
                            myDb.cleanupOnlineGroupRelation(members,groupId );
                        }
                        closeProgress();
                        goBack();
                    }



                } else{ //selected  online

                    // note :- while editing group, there is a chance to make offline group to online currently we are not allowing

                    data.put("members_json", membersDataJSON);

                    String currentUserOnlineId = myDb.getUserField("1", "onlineid");

                    if(currentUserOnlineId.equals("0") || currentUserOnlineId.equals("") || currentUserOnlineId ==null ){
                        Log.i("creating group","Invalid user online id "+currentUserOnlineId  );
                        Toast.makeText(getApplicationContext(), "Error while creating Online group ", Toast.LENGTH_LONG).show();
                        closeProgress();
                        goBack();
                        return;
                    }





                    if(groupId.equals("0")) { //insert

                        data.put("owner", currentUserOnlineId );


                    }
                    else{ //update
                        data.put("id", dbGroup.get("onlineid") );
                        data.put("current_user_id", currentUserOnlineId );
                        data.put("owner", dbGroup.get("owner") );

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

}

*/

package com.tricon.labs.crumbs.activities.groupexpense;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tricon.labs.crumbs.R;
import com.tricon.labs.crumbs.adapters.AdapterContactList;
import com.tricon.labs.crumbs.adapters.MemberListAdapter;
import com.tricon.labs.crumbs.database.DBHelper;
import com.tricon.labs.crumbs.interfaces.RemoveMemberListener;
import com.tricon.labs.crumbs.libraries.Utils;
import com.tricon.labs.crumbs.models.Contact;
import com.tricon.labs.crumbs.models.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ActivityAddOrEditGroup extends AppCompatActivity implements RemoveMemberListener {

    private TextInputLayout mTILGroupName;
    private EditText mETGroupName;
    private AutoCompleteTextView mACTVMemberName;

    private MemberListAdapter mMemberListAdapter;
    private AdapterContactList mContactListAdapter;

    private ProgressDialog mPDSaveData;
    private DBHelper mDBHelper;

    private int monthlyTask = 1;
    private Group group;
    public static final String INTENT_GROUP_DETAILS = "com.tricon.labs.crumbs.activities.groupexpense.GROUP_DETAILS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_group);

        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top);

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.widget_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_clear_mtrl_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }

        //setup views
        mTILGroupName = (TextInputLayout) findViewById(R.id.til_group_name);
        mETGroupName = (EditText) findViewById(R.id.et_group_name);
        mACTVMemberName = (AutoCompleteTextView) findViewById(R.id.actv_member_name);
        RecyclerView rvMembers = (RecyclerView) findViewById(R.id.rv_members);

        //set layout manager
        rvMembers.setHasFixedSize(true);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));

        //set members adapter
        mMemberListAdapter = new MemberListAdapter(new ArrayList<Contact>());
        rvMembers.setAdapter(mMemberListAdapter);

        //set contacts adapter for autocomplete text view
        mContactListAdapter = new AdapterContactList(ActivityAddOrEditGroup.this, new ArrayList<Contact>());
        mACTVMemberName.setAdapter(mContactListAdapter);

        //set autocomplete threshold
        mACTVMemberName.setThreshold(1);

        //set progress dialog
        mPDSaveData = new ProgressDialog(this);
        mPDSaveData.setCancelable(false);
        mPDSaveData.setIndeterminate(true);
        mPDSaveData.setMessage("Saving Data...");

        //set listeners
        mETGroupName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTILGroupName.setError(null);
                return false;
            }
        });
        mACTVMemberName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact member = (Contact) parent.getAdapter().getItem(position);
                mACTVMemberName.setText("");
                mMemberListAdapter.addMember(member);
                mContactListAdapter.removeMember(member);
            }
        });
        ((RadioGroup) findViewById(R.id.rg_group_type)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_monthly) {
                    monthlyTask = 1;
                } else {
                    monthlyTask = 0;
                }
            }
        });

        //get db instance
        mDBHelper = DBHelper.getInstance(this);

        //get extras. if extras exist that means user is editing existing group
        group = getIntent().getParcelableExtra(INTENT_GROUP_DETAILS);
        if (group != null) {
            monthlyTask = group.ismonthlytask;
            if (group.ismonthlytask == 1) {
                ((RadioButton) findViewById(R.id.rb_monthly)).setChecked(true);
            } else {
                ((RadioButton) findViewById(R.id.rb_ongoing)).setChecked(true);
            }
            mETGroupName.setText(group.name);
            mETGroupName.setSelection(group.name.length());
        }

        //fetch contacts and set them in autocomplete text view's adapter
        new FetchContactsTask().execute();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_top, R.anim.slide_out_to_bottom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_group_expenses_add_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                validateAndSave();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void removeMember(final int position) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_remove_member))
                .setMessage("Do you want to remove " + mMemberListAdapter.getItem(position).name + "?")
                .setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mContactListAdapter.addMember(mMemberListAdapter.removeMember(position));
                    }
                })
                .setNegativeButton(getString(android.R.string.cancel), null)
                .show();
    }

    private void validateAndSave() {
        String groupName = mETGroupName.getText().toString().trim();

        if (TextUtils.isEmpty(groupName)) {
            mTILGroupName.setError("Field Required");
            mETGroupName.setText("");
            return;
        }

        if (mMemberListAdapter.getItemCount() == 0) {
            Toast.makeText(this, "Add at least one more member other than you", Toast.LENGTH_SHORT).show();
            return;
        }

        new SaveDataTask().execute();
    }

    private class SaveDataTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            mPDSaveData.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            int count = mMemberListAdapter.getItemCount();
            ArrayList<String> memberIds = new ArrayList<>();
            memberIds.add("1"); // adding root user id

            //save members in user table
            for (int i = 0; i < count; i++) {
                Contact member = mMemberListAdapter.getItem(i);
                memberIds.add(mDBHelper.registerUserFromContact(member.phone, member.name) + "");
            }

            //save group expense (update existing or insert new)
            HashMap<String, String> data = new HashMap<>();
            data.put("name", mETGroupName.getText().toString());
            data.put("description", "");
            data.put("members_count", count + "");
            data.put("ismonthlytask", monthlyTask + "");

            //if updating previous group info
            if (group != null) {
                data.put("_id", group.id + "");
                if (mDBHelper.updateJointGroup(data) > 0) {
                    //update user group relation
                    mDBHelper.insertUserGroupRelation(group.id + "", memberIds);

                    //clean up
                    mDBHelper.cleanupOnlineGroupRelation(memberIds, group.id + "");

                    return true;
                } else {
                    return false;
                }
            } else {
                long groupId = mDBHelper.insertJointGroup(data);
                return groupId > 0 && mDBHelper.insertUserGroupRelation(groupId + "", memberIds) > 0;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mPDSaveData.dismiss();
            if (success) {
                Toast.makeText(ActivityAddOrEditGroup.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ActivityAddOrEditGroup.this, "Data saved failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FetchContactsTask extends AsyncTask<Void, Void, HashSet<Contact>> {

        private HashSet<Contact> existingMembers;

        @Override
        protected HashSet<Contact> doInBackground(Void... params) {
            //fetch all phone contacts
            HashSet<Contact> allContacts = Utils.getContactList(getApplicationContext());

            //if editing existing group then fetch existing members and remove them from allContacts
            //add the existing members in members adapter data set (in onPostExecute)
            if (group != null) {
                existingMembers = mDBHelper.getGroupMembers(group.id + "");
                allContacts.removeAll(existingMembers);
            }
            return allContacts;
        }

        @Override
        protected void onPostExecute(HashSet<Contact> members) {
            //if editing then add members members adapter data set
            if (group != null && existingMembers.size() > 0) {
                mMemberListAdapter.addMembers(existingMembers);
            }
            mContactListAdapter.addMembers(members);
        }
    }
}