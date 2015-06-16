package com.bikesh.scorpio.giventake;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bikesh.scorpio.giventake.adapters.CustomDatePicker;
import com.bikesh.scorpio.giventake.libraries.CustomRequest;
import com.bikesh.scorpio.giventake.model.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.bikesh.scorpio.giventake.libraries.functions.getInternetType;


public class ActivityJointExpenseIndividual extends ActivityBase {

    //View JointExpenseIndividual;

    //DBHelper myDb;
    String fromActivity=null;
    int  groupId=0;
    //String groupName="";
    boolean isMonthlyRenewing=true;

    String apiUrl_GroupDetails = "http://givntake.workassis.com/api/group/get/" ;
    String apiUrl_LoginRegisterUser = "http://givntake.workassis.com/api/user/login_register" ;
    RequestQueue Rqueue;

    Map<String, String> dbUser = new HashMap<String, String>();
    Map<String, String> JointGroup = new HashMap<String, String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint_expense_individual);

        //myDb = new DBHelper(this);

        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            fromActivity= null;
        } else {
            fromActivity= extras.getString("fromActivity");
            groupId = Integer.parseInt(extras.getString("groupId"));
            //groupName = extras.getString("colName");
        }


        ((ImageButton)currentView.findViewById(R.id.addEntry)).setOnClickListener(new openAddnewEntrry());

        //slide up and down the table
        ((LinearLayout)currentView.findViewById(R.id.restore)).setOnClickListener(new restoreTable());
        ((ImageView)currentView.findViewById(R.id.restorebtn)).setOnClickListener(new restoreTable());



        SimpleDateFormat dmy = new SimpleDateFormat("MM-yyyy");
        String cDate = dmy.format(new Date());

        SimpleDateFormat dbmy = new SimpleDateFormat("yyyy-MM");
        String cdbDate = dbmy.format(new Date());

        TextView dateChanger= (TextView)currentView.findViewById(R.id.dateChanger);
        TextView dateChangerForDb= (TextView)currentView.findViewById(R.id.dateChangerForDb);
        dateChanger.setOnClickListener(new CustomDatePicker(ActivityJointExpenseIndividual.this, dateChanger, dateChangerForDb, true));

        dateChanger.setText(cDate);
        dateChangerForDb.setText(cdbDate);

        dateChangerForDb.addTextChangedListener(new dateChange());



        Rqueue = Volley.newRequestQueue(this);
        //generateTables();

    }

    @Override
    public void onResume() {
        super.onResume();
        //generateTables();
        apiAccess();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityJointExpenseIndividual.this,ActivityJointExpense.class));
    }


    private void apiAccess() {

        JointGroup=myDb.fetchJointGroupbyId(groupId + "");
        dbUser=myDb.getUser(1);

        //chking offline or online
        if(JointGroup.get("isonline").equals("0")) {
            Log.i("api call","it is not an online group ");
            generateTables();
            return;
        }
        else{
            //it is an online group

            if (!getInternetType(getApplicationContext()).equals("?")) {

                //checking user registerd online
                if (dbUser.get("onlineid").toString().equals("") || dbUser.get("onlineid").toString().equals("0")) {
                    //user not registered
                    //so register user
                    Log.i("api call","user not registered ");
                    registerUser();

                } else {
                    //user already registered
                    Log.i("api call","user already registered");
                    getGroupData();
                }

            }
            else{
                Toast.makeText(getApplicationContext(), "No internet connection to update Online Groups", Toast.LENGTH_LONG).show();
                generateTables();
            }

        }











    }

    private class dateChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            generateTables();
        }
    }



    private void generateTables() {

        Map<String, String> data = new HashMap<String, String>();


        if(isMonthlyRenewing == false) {
            data = myDb.getGroupEntryTotalPerHead(groupId + "");
        }
        else {
            data = myDb.getGroupEntryTotalPerHead(groupId+"", ((TextView)currentView.findViewById(R.id.dateChanger)).getText().toString()) ;
        }



        ((TextView)currentView.findViewById(R.id.amtTotal)).setText(data.get("total"));
        ((TextView)currentView.findViewById(R.id.amtPerHead)).setText(data.get("perhead"));


        Cursor cursor = myDb.getJointGroupbyId(groupId+"");

        if(cursor.getInt(cursor.getColumnIndex("ismonthlytask"))==0){
            ((LinearLayout)currentView.findViewById(R.id.l1)).setVisibility(View.GONE);
            isMonthlyRenewing=false;
        }


        generateGroupUsersTable();

        generateEntryTable();
    }



    private void generateGroupUsersTable() {

        TableLayout tableLayout = (TableLayout) currentView.findViewById(R.id.groupUserTableLayout);
        TableRow tr, th;
        boolean colorFlag=false;
        TextView tv;
        //String fields[]={"created_date", "description",  "amt"};

        //setting headder
        String tablehead[]={"Name", "Amt Spend", "Amt Balance"};
        tableLayout.removeAllViews();
        th = new TableRow(this);
        th.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));




        //Log.i("bm info", "" + colId + " cc " + cursor.getCount());
        //Log.i("bm info", "" + tablehead.length);

        //Creating Table Header
        for (int i=0 ;i< tablehead.length; i++) {
            tv = generateTextview();
            tv.setText(tablehead[i]);
            tv.setTypeface(null, Typeface.BOLD);
            tr.addView(tv);
        }
        tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        //----
        Cursor cursor;
        if(isMonthlyRenewing == false) {
            cursor = myDb.getGroupUsersData(groupId + "");
        }
        else {
            cursor = myDb.getGroupUsersData(groupId + "", ((TextView)currentView.findViewById(R.id.dateChanger)).getText().toString()) ;
        }

        while(cursor.isAfterLast() == false){

            tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            //tr.setClickable(true);
            //tr.setOnClickListener(new tableRowClicked(Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")))));

            //Log.i("bm info", "" + fields.length);

            for (int i=0 ;i<3; i++) {

                tv = generateTextview();

                if(i==2 &&  Float.parseFloat(cursor.getString(i))<0 ){

                    tv.setText(String.format("%.2f", (Float.parseFloat(cursor.getString(i))*-1) )+" Get" );

                }
                else if(i==2 &&  Float.parseFloat(cursor.getString(i))>0 ) {

                    tv.setText( String.format("%.2f", Float.parseFloat(cursor.getString(i)) )+ " Give");

                }
                else{
                    tv.setText(cursor.getString(i));
                }

                tr.addView(tv);
            }

            cursor.moveToNext();

            if(colorFlag){
                tr.setBackgroundColor(Color.rgb(255, 235, 230));
                colorFlag=false;
            }
            else {
                tr.setBackgroundColor(Color.rgb(236, 251, 255));
                colorFlag=true;
            }
            // Add row to TableLayout.
            tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }

    }

    private void generateEntryTable() {

        TableLayout tableLayout = (TableLayout) currentView.findViewById(R.id.groupEntryTableLayout);
        TableRow tr, th;
        boolean colorFlag=false;
        TextView tv;

        //setting headder
        String tablehead[]={"Date", "Description", "Name","Amount"};
        tableLayout.removeAllViews();
        th = new TableRow(this);
        th.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        //Log.i("bm info", "" + colId + " cc " + cursor.getCount());
        //Log.i("bm info", "" + tablehead.length);

        //Creating Table Header
        for (int i=0 ;i< tablehead.length; i++) {
            tv = generateTextview();
            tv.setText(tablehead[i]);
            tv.setTypeface(null, Typeface.BOLD);
            tr.addView(tv);
        }
        tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        //----

        String fields[]={"created_date", "description",  "name", "amt" }; /*, "is_split"*/

        //Cursor cursor =  myDb.getGroupEntrys(groupId + "");

        Cursor cursor;
        if(isMonthlyRenewing == false) {
            cursor = myDb.getGroupEntrys(groupId + "");
        }
        else {
            cursor = myDb.getGroupEntrys(groupId + "", ((TextView) currentView.findViewById(R.id.dateChanger)).getText().toString()) ;
        }


        while(cursor.isAfterLast() == false){

            tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            //tr.setClickable(true);
            //tr.setOnClickListener(new tableRowClicked(Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")))));

            //Log.i("bm info", "" + fields.length);

            for (int i=0 ;i<fields.length; i++) {

                tv = generateTextview();

                tv.setText(cursor.getString(cursor.getColumnIndex(fields[i])));

                tr.addView(tv);
            }

            cursor.moveToNext();

            if(colorFlag){
                tr.setBackgroundColor(Color.rgb(255, 235, 230));
                colorFlag=false;
            }
            else {
                tr.setBackgroundColor(Color.rgb(236, 251, 255));
                colorFlag=true;
            }
            // Add row to TableLayout.
            tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }



    }







    private class restoreTable implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if(((ScrollView) currentView.findViewById(R.id.scrollView1) ).getVisibility() == View.VISIBLE){
                ((ScrollView) currentView.findViewById(R.id.scrollView1) ).setVisibility(View.GONE);
                ((ImageView)currentView.findViewById(R.id.restorebtn)).setImageResource(R.drawable.double_arrow_down);
            }
            else{
                ((ScrollView) currentView.findViewById(R.id.scrollView1) ).setVisibility(View.VISIBLE);
                ((ImageView)currentView.findViewById(R.id.restorebtn)).setImageResource(R.drawable.double_arrow_up);
            }

        }
    }





    private class openAddnewEntrry implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ActivityJointExpenseIndividual.this, ActivityAddEntry.class);
            i.putExtra("fromActivity", "ActivityJointExpenseIndividual");
            i.putExtra("ID", ""+groupId );
            startActivity(i);

        }
    }


    private class tableRowClicked implements View.OnClickListener {
        int rowId=0;
        public tableRowClicked(int id) {
            rowId=id;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "clicked" + rowId, Toast.LENGTH_LONG).show();
        }
    }


    private TextView generateTextview() {
        TextView tv = new TextView(this);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tv.setPadding(5, 5, 5, 5);
        tv.setClickable(false);
        return tv;
    }


    private void registerUser() {

        Log.i("api call", "register url "+apiUrl_LoginRegisterUser  );



        //dbUser.put("required_data","group_info");

        CustomRequest jsObjRequest =   new CustomRequest

                (Request.Method.POST, apiUrl_LoginRegisterUser, dbUser, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i("api call 1", response.toString()+ "");

                        if(response.optString("msg").equals("Invalid Password")){

                            Toast.makeText(getApplicationContext(),"Invalid password please Update from settings", Toast.LENGTH_LONG).show();
                            Log.i("api call", "Invalid password please Update from settings");

                            generateTables();
                        }
                        else if(response.optString("status").equals("success")){

                            Toast.makeText(getApplicationContext(),response.optString("msg"), Toast.LENGTH_LONG).show();

                            Log.i("api call", response+"" );
                            //updating local user onlineid
                            if(dbUser.get("onlineid").equals("0")) {

                                Map<String, String> updateData = new HashMap<String, String>();
                                updateData.put("_id", dbUser.get("_id"));
                                updateData.put("onlineid", response.optJSONObject("data").optString("user_id"));
                                myDb.updateUser(updateData);

                                dbUser.put("onlineid", response.optJSONObject("data").optString("user_id"));
                            }

                            //get group information
                            getGroupData();

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("api call", "ERROR "+error.getMessage());
                    }
                });
        Rqueue.add(jsObjRequest);
    }



    //this will retreve data from server if it exist otherwise it will create group
    private void getGroupData() {

        if(JointGroup.get("onlineid").toString().equals("") || JointGroup.get("onlineid").toString().equals("0")){
            //not registerd
            Log.i("api call","not registred group ");

            // TODO: 6/16/2015 :- need to work on it
            // generateTables();
        }
        else{
            //registred group
            Log.i("api call","calling get group ");
            getGroup();

        }


    }


    private void getGroup(){


        Log.i("api call","inside get group url : "+ apiUrl_GroupDetails+JointGroup.get("onlineid").toString() );

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, apiUrl_GroupDetails+JointGroup.get("onlineid").toString(), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i("api call","get group "+response.toString());

                        JSONObject group_dataJSON = response.optJSONObject("data").optJSONObject("group");
                        JSONArray members_dataJSON = response.optJSONObject("data").optJSONArray("members");
                        JSONArray entrys_dataJSON = response.optJSONObject("data").optJSONArray("entrys");

                        Log.i("api call","group_dataJSON "+ group_dataJSON );
                        Log.i("api call","members_dataJSON "+ members_dataJSON );
                        Log.i("api call","entrys_dataJSON "+ entrys_dataJSON );


                        processOnlineGroupMembers(members_dataJSON);

                        processOnlineGroupEntrys(entrys_dataJSON);

                        generateTables();



                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("api call", "ERROR "+error.getMessage());
                    }
                });

        Rqueue.add(jsObjRequest);
    }

    private void processOnlineGroupMembers(JSONArray members_dataJSON) {

        //JSONArray members_dataJSONArray = response.optJSONObject("data").optJSONArray("group");
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

                    Log.i("api call","members_data "+ keyStr + " => " + valueStr );

                    String[] requiredKeys = new String[] {"user_id","name","phone"}; //,"created_date","photo"

                    if( Arrays.asList(requiredKeys).contains(keyStr) ){
                        Log.i("api call r", keyStr + " - "+ valueStr);
                        tempStorage.put(keyStr,valueStr );
                    }

                }

                tempStorage.put("onlineid",tempStorage.get("user_id") );
                tempStorage.remove("user_id");

                //insert to db
                myDb.updateOnlineUserGroupRelation(tempStorage, JointGroup.get("_id").toString(),getContentResolver());

                Map user = myDb.getUserbyOnlineId(tempStorage.get("onlineid"));
                onlineGroupExistingUsers.add(user.get("_id"));
            }

            myDb.cleanupOnlineGroupRelation(onlineGroupExistingUsers, JointGroup.get("_id").toString() );




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void processOnlineGroupEntrys(JSONArray entrys_dataJSON) {

        //JSONArray members_dataJSONArray = response.optJSONObject("data").optJSONArray("group");
        Map<String, String> tempStorage = new HashMap<String, String>();
        ArrayList onlineGroupExistingUsers = new ArrayList();

        try {
            for(int i = 0 ; i < entrys_dataJSON.length(); i++){

                tempStorage = new HashMap<String, String>();

                JSONObject jsonObj = entrys_dataJSON.getJSONObject(i);
                Iterator<String> keysIterator = jsonObj.keys();
                while (keysIterator.hasNext())
                {

                    String keyStr = (String)keysIterator.next();
                    String valueStr = jsonObj.getString(keyStr);


                    Log.i("api call","entry_data "+ keyStr + " => " + valueStr );

                    String[] requiredKeys = new String[] {"entry_id","created_date","description","user_id","amt","is_split","last_updated"}; //,"group_id","created_date","photo"




                    if( Arrays.asList(requiredKeys).contains(keyStr) ){
                        Log.i("api call r", keyStr + " - "+ valueStr);
                        tempStorage.put(keyStr,valueStr );
                    }


                }


                tempStorage.put("onlineid",tempStorage.get("entry_id") );
                tempStorage.remove("entry_id");
                tempStorage.put("joint_group_id", JointGroup.get("_id").toString() );
                tempStorage.remove("group_id");
                tempStorage.put("status","updated" );

                Map userdatadb = myDb.getUserbyOnlineId(tempStorage.get("user_id"));
                tempStorage.put("user_id", userdatadb.get("_id").toString()  );


                //insert to db
                myDb.updateOnlineUserGroupEntry(tempStorage, JointGroup.get("_id").toString());

                 /*
                Map user = myDb.getUserbyOnlineId(tempStorage.get("onlineid"));
                onlineGroupExistingUsers.add(user.get("_id"));
                */
            }

            /*
            myDb.cleanupOnlineGroupRelation(onlineGroupExistingUsers, JointGroup.get("_id").toString() );
               */



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}
