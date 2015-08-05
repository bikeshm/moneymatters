package com.tricon.labs.crumbs.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tricon.labs.crumbs.R;
import com.tricon.labs.crumbs.activities.groupexpense.ActivityAddOrEditGroup;
import com.tricon.labs.crumbs.activities.groupexpense.ActivityGroupExpenseIndividual;
import com.tricon.labs.crumbs.adapters.AdapterGroupExpense;
import com.tricon.labs.crumbs.database.DBHelper;
import com.tricon.labs.crumbs.models.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FragmentGroupExpense extends Fragment {

    private DBHelper mDBHelper;

    private TextView mTVSpent;
    private TextView mTVGive;
    private TextView mTVGet;

    private List<Group> mGroupList = new ArrayList<>();

    private AdapterGroupExpense mAdapter;

    private ProgressDialog mPDSaveData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_group_expense, container, false);

        //setup views
        mTVSpent = (TextView) rootView.findViewById(R.id.tv_spent_amt);
        mTVGive = (TextView) rootView.findViewById(R.id.tv_give_amt);
        mTVGet = (TextView) rootView.findViewById(R.id.tv_get_amt);
        ListView lvGroup = (ListView) rootView.findViewById(R.id.lv_groups);

        //get db instance
        mDBHelper = DBHelper.getInstance(getActivity());

        //set list view adapter
        mAdapter = new AdapterGroupExpense(mGroupList);
        lvGroup.setAdapter(mAdapter);

        //set empty view
        lvGroup.setEmptyView(rootView.findViewById(android.R.id.empty));

        //set listeners
        lvGroup.setOnItemClickListener(new ListItemClicked());
        lvGroup.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                generatePopupMenu(position);
                return true;
            }
        });


        //set progress dialog
        mPDSaveData = new ProgressDialog(getActivity());
        mPDSaveData.setCancelable(false);
        mPDSaveData.setIndeterminate(true);
        mPDSaveData.setMessage("Saving Data..."); //retrieving


        return rootView;

    }

    public void generatePopupMenu(final int position) {
        final CharSequence[] options = {"Edit", "Delete"};

        new AlertDialog.Builder(getActivity())
                .setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Delete")) {

                            new DeleteGroupTask(position).execute();

                        } else if (options[item].equals("Edit")) {
                            Intent intent = new Intent(getActivity(), ActivityAddOrEditGroup.class);
                            intent.putExtra(ActivityAddOrEditGroup.INTENT_GROUP_DETAILS, mGroupList.get(position));
                            startActivity(intent);
                        }
                    }
                })
                .show();
    }


    private class ListItemClicked implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent i = new Intent(getActivity(), ActivityGroupExpenseIndividual.class);
            i.putExtra("GROUP", mGroupList.get(position) );
            startActivity(i);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        new FetchGroupExpenseDataTask().execute();
    }

    private class FetchGroupExpenseDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            //mPDSaveData.setMessage("Fetching Data...");
            //mPDSaveData.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mGroupList.clear();

            mGroupList.addAll(mDBHelper.getJointGroupsList());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mAdapter.notifyDataSetChanged();
            //mPDSaveData.dismiss();
            new FetchBalanceAmountTask().execute();
        }
    }

    private class FetchBalanceAmountTask  extends AsyncTask<Void, Void, Map<String, String>> {

        @Override
        protected void onPreExecute() {

            //mPDSaveData.setMessage("Fetching Data...");
            //mPDSaveData.show();
        }

        @Override
        protected Map<String, String> doInBackground(Void... params) {

            Map<String, String> finalResult = mDBHelper.getAllGroupTotalSpendGiveGet();
            return finalResult;
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            super.onPostExecute(result);

            if (result.size() > 0) {
                mTVSpent.setText(result.get("amt_spent"));
                mTVGive.setText(result.get("amt_toGive"));
                mTVGet.setText(result.get("amt_toGet"));
            }
            //mPDSaveData.dismiss();

        }
    }




    private class DeleteGroupTask extends AsyncTask<Void, Void, Map<String, String>> {


        int position;

        public DeleteGroupTask(int position) {
            this.position= position;
        }


        @Override
        protected void onPreExecute() {

            mPDSaveData.setMessage("Deleting Data...");
            mPDSaveData.show();
        }

        @Override
        protected Map<String, String> doInBackground(Void... params) {

            mDBHelper.deleteGroup( mGroupList.get(position).id +"" );

            return null;
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {

            super.onPostExecute(result);
            mGroupList.remove(position);
            //mAdapter.notifyItemRemoved(position);
            mAdapter.notifyDataSetChanged();
            mPDSaveData.dismiss();

            new FetchBalanceAmountTask().execute();
        }
    }

}


/*
public class ActivityJointExpense extends ActivityBase {

    //View currentView;
    ListView mLVPersons;


    Map<String, String> dbUser = new HashMap<String, String>();



    RequestQueue Rqueue;

    //Api
    String apiUrl_GetByPhone = "http://givntake.workassis.com/api/user/getbyphone/" ;
    //String apiUrl_RegisterUser = "http://givntake.workassis.com/api/user/register" ;
    String apiUrl_LoginRegisterUser = "http://givntake.workassis.com/api/user/login_register" ;

    String apiUrl_DeleteGroup= "http://givntake.workassis.com/api/group/delete";




    boolean registerUserFlag= false;

    DBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loading templet xml
        setContentView(R.layout.fragment_group_expense);


        mLVPersons = (ListView) currentView.findViewById(R.id.listViewFromDB);
        mLVPersons.setOnItemClickListener(new listItemClicked());
        mLVPersons.setOnItemLongClickListener(new listItemLongClicked() );


        ((FloatingActionButton)currentView.findViewById(R.id.addUser)).setOnClickListener(new openAddnewGroup());


        mDBHelper = new DBHelper(this);



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

        dbUser=mDBHelper.getUser(1);

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
                                        mDBHelper.updateUser(updateData);

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
                                            mDBHelper.insertOnlineGroup(incommingGroup);

                                            onlineExistingGroups.add(incommingGroup.get("onlineid"));
                                        }

                                        mDBHelper.cleanupOnlineGroup(onlineExistingGroups);

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
        //Cursor cursor = mDBHelper.getAllJointGroups();
        Cursor cursor =mDBHelper.getAllJointGroupsWithData();



        mLVPersons.setAdapter(new Adapter_CustomSimpleCursor(this,        // Context
                R.layout.listview_item_template,    // Row layout template
                cursor                    // cursor (set of DB records to map)
        ));

        Map<String, String> finalResult = mDBHelper.getAllGroupTotalSpendGiveGet();

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
        public void onClick(View mRootView) {

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
        if (mDBHelper != null) {
            mDBHelper.close();
        }
    }

    private class listItemLongClicked implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            generatePopupMenu(id+"");

            return true;
        }
    }

    public void generatePopupMenu(String groupId) {

        CharSequence[] options  = { "Cancel" };

        Map<String, String> dbGroup = mDBHelper.fetchJointGroupbyId(groupId );




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
                    mDBHelper.deleteGroup( dbGroup.get("_id") );
                    populateListViewFromDB();

                }
                else{ //online

                    showProgress("Deleting ...");

                    Map<String, String> data = new HashMap<String, String>();

                    data.put("id", dbGroup.get("onlineid") );
                    data.put("current_user_id",  mDBHelper.getUserField("1", "onlineid") );
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

                //mDBHelper.deleteEntry(dbrowId);

                //Cursor entrys =  mDBHelper.getUserEntrys(userId,((TextView)currentView.findViewById(R.id.dateChanger)).getText().toString() );
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


*/