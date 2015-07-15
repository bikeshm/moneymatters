package com.tricon.labs.giventake;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.tricon.labs.giventake.adapters.Adapter_CustomSimpleCursor;
import com.tricon.labs.giventake.libraries.CustomRequest;
import com.tricon.labs.giventake.database.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.tricon.labs.giventake.libraries.functions.getInternetType;


public class ActivityJointExpense extends ActivityBase {

    //View currentView;
    ListView listView;


    Map<String, String> dbUser = new HashMap<String, String>();



    RequestQueue Rqueue;

    //Api
    String apiUrl_GetByPhone = "http://givntake.workassis.com/api/user/getbyphone/" ;
    //String apiUrl_RegisterUser = "http://givntake.workassis.com/api/user/register" ;
    String apiUrl_LoginRegisterUser = "http://givntake.workassis.com/api/user/login_register" ;

    String apiUrl_DeleteGroup= "http://givntake.workassis.com/api/group/delete";




    boolean registerUserFlag= false;

    DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loading templet xml
        setContentView(R.layout.activity_joint_expense);


        listView = (ListView) currentView.findViewById(R.id.listViewFromDB);
        listView.setOnItemClickListener(new listItemClicked());
        listView.setOnItemLongClickListener(new listItemLongClicked() );


        ((ImageButton)currentView.findViewById(R.id.addUser)).setOnClickListener(new openAddnewGroup());


        myDb = new DBHelper(this);



        //populateListViewFromDB();

    }


    @Override
    public void onResume() {
        super.onResume();
        populateListViewFromDB();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityJointExpense.this,ActivityHome.class));
    }




    private void populateListViewFromDB() {

        showProgress();

        dbUser=myDb.getUser(1);

        //chking user updated in db with online user id
        //if(dbUser.get("onlineid").equals("0")) {

            //checking is internet is available
            if (!getInternetType(getApplicationContext()).equals("?")) {

                Log.i("api call", apiUrl_LoginRegisterUser +Uri.encode(dbUser.get("phone")) );

                Rqueue = Volley.newRequestQueue(this);

                dbUser.put("required_data","group_info");

                CustomRequest jsObjRequest =   new CustomRequest
                        (Request.Method.POST, apiUrl_LoginRegisterUser, dbUser, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                Log.i("api call 1", response.toString() + "" + response.optString("status"));
                                Log.i("api call 2", response.optJSONObject("data") + "");
                                if(response.optString("msg").equals("Invalid Password")){

                                    Toast.makeText(getApplicationContext(),"Invalid password please Update from settings", Toast.LENGTH_LONG).show();
                                    Log.i("api call", "Invalid password please Update from settings");

                                    populateListViewFromDB_populate();
                                }
                                else if(response.optString("status").equals("success")){

                                    Toast.makeText(getApplicationContext(),response.optString("msg"), Toast.LENGTH_LONG).show();

                                    Log.i("api call", response+"" );
                                    //updating local user onlineid
                                    if(dbUser.get("onlineid").toString().equals("") || dbUser.get("onlineid").toString().equals("0")) {

                                        Map<String, String> updateData = new HashMap<String, String>();
                                        updateData.put("_id", dbUser.get("_id"));
                                        updateData.put("onlineid", response.optJSONObject("data").optString("user_id"));
                                        myDb.updateUser(updateData);

                                        dbUser.put("onlineid", response.optJSONObject("data").optString("user_id"));
                                    }

                                    //insert group to local db

                                    JSONArray requested_dataJSON = response.optJSONObject("data").optJSONArray("requested_data");
                                    Map<String, String> incommingGroup = new HashMap<String, String>();

                                    ArrayList onlineExistingGroups= new ArrayList();


                                    try {
                                        for(int i = 0 ; i < requested_dataJSON.length(); i++){

                                            JSONObject jsonObj = requested_dataJSON.getJSONObject(i);
                                            Iterator<String> keysIterator = jsonObj.keys();
                                            while (keysIterator.hasNext())
                                            {
                                                String keyStr = (String)keysIterator.next();
                                                String valueStr = jsonObj.getString(keyStr);

                                                String[] requiredKeys = new String[] {"group_id","owner","name","members_count","ismonthlytask","description"}; //,"totalamt","balanceamt" //,"created_date","photo"

                                               if( Arrays.asList(requiredKeys).contains(keyStr) ){
                                                   Log.i("api call r", keyStr + " - "+ valueStr);
                                                   incommingGroup.put(keyStr,valueStr );
                                               }
                                            }

                                            incommingGroup.put("onlineid",incommingGroup.get("group_id") );
                                            incommingGroup.put("isonline","1" );
                                            //insert to db
                                            myDb.insertOnlineGroup(incommingGroup);

                                            onlineExistingGroups.add(incommingGroup.get("onlineid"));
                                        }

                                        myDb.cleanupOnlineGroup(onlineExistingGroups);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    populateListViewFromDB_populate();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("api call", "ERROR "+error.getMessage());
                                Toast.makeText(getApplicationContext(), "Error while accessing online data", Toast.LENGTH_LONG).show();
                                populateListViewFromDB_populate();
                                closeProgress();
                            }
                        });
                Rqueue.add(jsObjRequest);
            }
            else{
                Toast.makeText(getApplicationContext(), "No internet connection to update Online Groups", Toast.LENGTH_LONG).show();
                populateListViewFromDB_populate();
            }
    }

    private void populateListViewFromDB_populate() {
        //if ineternet avialable user login
        // fetch data to local db
        //else if internet avilable user not loged in
        //return;




        //Todo :- 1. insted of listing all user just list the user who all are having amt balance
        //Todo :- need to implement pagination
        //Cursor cursor = myDb.getAllJointGroups();
        Cursor cursor =myDb.getAllJointGroupsWithData();



        listView.setAdapter(new Adapter_CustomSimpleCursor(this,        // Context
                R.layout.listview_item_template,    // Row layout template
                cursor                    // cursor (set of DB records to map)
        ));

        Map<String, String> finalResult = myDb.getAllGroupTotalSpendGiveGet();

        ((TextView)currentView.findViewById(R.id.totalspend)).setText(": "+finalResult.get("total"));
        ((TextView)currentView.findViewById(R.id.amtToGive)).setText(": " + finalResult.get("togive"));
        ((TextView)currentView.findViewById(R.id.amtToGet)).setText(": " + finalResult.get("toget"));


        //progressDialog.dismiss();
        closeProgress();
    }



    private class listItemClicked implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(ActivityJointExpense.this, ActivityJointExpenseIndividual.class);
            i.putExtra("fromActivity", "ActivityJointExpense");
            i.putExtra("groupId", ""+id);
            startActivity(i);

            Toast.makeText(getApplicationContext(),"groupId : "+id, Toast.LENGTH_LONG).show();
        }
    }




    private class openAddnewGroup implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ActivityJointExpense.this, ActivityJointExpenseAddGroup.class);
            i.putExtra("fromActivity", "ActivityJointExpense");
            startActivity(i);

        }
    }




























    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_joint_expense, menu);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDb != null) {
            myDb.close();
        }
    }

    private class listItemLongClicked implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            generatePopupmenu(id+"");

            return true;
        }
    }

    public void generatePopupmenu(String groupId) {

        CharSequence[] options  = { "Cancel" };

        Map<String, String> dbGroup = myDb.fetchJointGroupbyId(groupId );




        if(dbUser.get("onlineid").equals(dbGroup.get("owner")) ||
                dbGroup.get("onlineid").equals("0") ||
                dbGroup.get("onlineid").equals("")) {

            options = new CharSequence[]{"Edit","Delete", "Cancel"};
        }

        final CharSequence[] menuItems= options;

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityJointExpense.this);
        //builder.setTitle("Add Photo!");
        builder.setItems(menuItems, new popupmenuClickedListener(dbGroup, menuItems ));
        builder.show();
    }



    private class popupmenuClickedListener implements DialogInterface.OnClickListener {

        Map<String, String> dbGroup;
        CharSequence[] menuItems;

        public popupmenuClickedListener(Map<String, String> group, CharSequence[] mnItems) {
            dbGroup= group;

            menuItems=mnItems;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            //Toast.makeText(getApplicationContext(),menuItems[which], Toast.LENGTH_LONG).show();

            if (menuItems[which].equals("Delete")) {
                //if online delete from online
                //if offline delete group and group entry

                //populateListViewFromDB();

                if(dbGroup.get("onlineid").equals("0")){ //offline
                    myDb.deleteGroup( dbGroup.get("_id") );
                    populateListViewFromDB();

                }
                else{ //online

                    showProgress("Deleting ...");

                    Map<String, String> data = new HashMap<String, String>();

                    data.put("id", dbGroup.get("onlineid") );
                    data.put("current_user_id",  myDb.getUserField("1", "onlineid") );
                    //data.put("owner", dbGroup.get("owner") );

                    deleteOnlienGroup( data );

                }


                //Toast.makeText(getApplicationContext(),""+ dbGroup.get("onlineid") , Toast.LENGTH_LONG).show();

            }

            else if (menuItems[which].equals("Edit")) {

               // Toast.makeText(getApplicationContext(),"group id"+ dbGroup.get("_id"), Toast.LENGTH_LONG).show();

                Intent i = new Intent(ActivityJointExpense.this, ActivityJointExpenseAddGroup.class);
                i.putExtra("fromActivity", "ActivityJointExpense");
                i.putExtra("groupId", dbGroup.get("_id"));
                startActivity(i);

                //Toast.makeText(getApplicationContext(),menuItems[which], Toast.LENGTH_LONG).show();

                //myDb.deleteEntry(dbrowId);

                //Cursor entrys =  myDb.getUserEntrys(userId,((TextView)currentView.findViewById(R.id.dateChanger)).getText().toString() );
                //generateTable(entrys);

                //deleteGroupEntry(rowId, rowOnlineId, rowUseronlineId);

            }
            else if (menuItems[which].equals("Cancel")) {
                dialog.dismiss();
            }
        }
    }



    public void deleteOnlienGroup( Map<String, String> data ){

        Log.i("data to server",data +"" );

        if (!getInternetType(getApplicationContext()).equals("?")) {
            CustomRequest jsObjRequest = new CustomRequest

                    (Request.Method.POST, apiUrl_DeleteGroup, data, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            Log.i("api call delete Group", response.toString() + "");
                            closeProgress();
                            populateListViewFromDB();

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("api call delete Group", "ERROR " + error.getMessage() + error);
                            Toast.makeText(getApplicationContext(), "Error while deleting group", Toast.LENGTH_SHORT).show();
                            closeProgress();
                            //((Button)currentView.findViewById(R.id.saveBtn)).setEnabled(true);
                        }
                    });
            Rqueue.add(jsObjRequest);
        }
        else{
            Toast.makeText(getApplicationContext(), "No internet connection to update Online Groups", Toast.LENGTH_LONG).show();
        }

    }

}
