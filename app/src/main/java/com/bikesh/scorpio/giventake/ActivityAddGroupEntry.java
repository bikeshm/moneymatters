package com.bikesh.scorpio.giventake;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bikesh.scorpio.giventake.adapters.Adapter_CustomSimpleCursor;
import com.bikesh.scorpio.giventake.adapters.CustomDatePicker;
import com.bikesh.scorpio.giventake.libraries.CustomRequest;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityAddGroupEntry extends ActivityBase {

    RecyclerView recyclerView;

    String fromActivity=null;

    String groupId=null;
    //String Name="";
    //String rowId=null;
    String groupOnlineId=null;

    EditText datePicker,created_date_forDB;

    Intent backActivityIntent=null;



    RequestQueue Rqueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_entry);

        //myDb = new DBHelper(this);

        //--- Face 2  //initialising RecyclerView otherwise it is throwing null pointer exception
        //recyclerView = (RecyclerView) findViewById(R.id.recycler_Users);
        //recyclerView.setHasFixedSize(true);

        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //recyclerView.setLayoutManager(layoutManager);
        //--

        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            fromActivity= null;
        } else {
            fromActivity= extras.getString("fromActivity");

            groupId = extras.getString("groupId",null);

            //rowId = extras.getString("rowId",null);
            //Name = extras.getString("Name");

            groupOnlineId = extras.getString("groupOnlineId",null);
        }


        //implementing date picker

        datePicker = ((EditText) currentView.findViewById(R.id.datePicker));
        //created_date is hidden field for serving date to db (YY-mm-dd format)
        created_date_forDB = ((EditText) currentView.findViewById(R.id.created_date));

        //initial date values
        SimpleDateFormat dmy = new SimpleDateFormat("dd-MM-yyyy");
        String dmyDate = dmy.format(new Date());
        datePicker.setText(dmyDate);

        SimpleDateFormat tmpdmy = new SimpleDateFormat("yyyy-MM-dd");
        String tmpdmyDate = tmpdmy.format(new Date());
        created_date_forDB.setText(tmpdmyDate);

        //setting datepicker adapter
        datePicker.setOnClickListener(new CustomDatePicker(ActivityAddGroupEntry.this, datePicker, created_date_forDB, false));
        //----implementing date picker


        backActivityIntent = new Intent(ActivityAddGroupEntry.this, ActivityJointExpenseIndividual.class);
        backActivityIntent.putExtra("groupId",  groupId);

        //=====split --face 2
        //Cursor cursor = myDb.getAllUsersIncludedMe();
        //adapter = new Adapter_TextRecyclerViewList(cursor, this);
        //recyclerView.setAdapter(adapter);
        //=====split --face 2

        generateDataForJointExpenseIndividual();


        ((Button)currentView.findViewById(R.id.saveBtn)).setOnClickListener(new saveData());
        ((Button) currentView.findViewById(R.id.cancelBtn)).setOnClickListener(new cancelActivity());

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




    private void generateDataForJointExpenseIndividual() {


        //===================face 2
        //((LinearLayout) addEntryView.findViewById(R.id.isSplitLayer) ).setVisibility(View.VISIBLE);
        //((LinearLayout) addEntryView.findViewById(R.id.grupMembersLayer) ).setVisibility(View.VISIBLE);
        //=====================face 2
        Map<String, String> data = new HashMap<String, String>();
        data.put("dataFrom","db"  );
        ((TextView) currentView.findViewById(R.id.selectUserLabel)).setText("Spend By");
        Adapter_CustomSimpleCursor adapter = new Adapter_CustomSimpleCursor(this, R.layout.custom_spinner_item_template, myDb.getAllUsersInGroup(groupId) , data );
        ((Spinner) currentView.findViewById(R.id.fromUser)).setAdapter(adapter);


        //face2
        //((RadioGroup) currentView.findViewById(R.id.isSplit)).setOnCheckedChangeListener(new isSplitChanged());

        //recyclerView.setOnKeyListener(new recyclerViewKeyListener());

    }

    /* face 2
    private class isSplitChanged implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            Toast.makeText(getApplicationContext(), "" + checkedId, Toast.LENGTH_LONG).show();

            if(checkedId==R.id.isSplitradioYes){
                ((LinearLayout) currentView.findViewById(R.id.grupMembersLayer) ).setVisibility(View.VISIBLE);
                ((EditText)currentView.findViewById(R.id.amount)).setEnabled(false);
            }
            else{
                ((LinearLayout) currentView.findViewById(R.id.grupMembersLayer) ).setVisibility(View.GONE);
                ((EditText)currentView.findViewById(R.id.amount)).setEnabled(true);
            }
        }
    }


    private class recyclerViewKeyListener implements View.OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            Toast.makeText(getApplicationContext(),"keyup",Toast.LENGTH_LONG).show();

            return false;
        }
    }
 */


    private class saveData implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            ((Button)currentView.findViewById(R.id.saveBtn)).setEnabled(false);
            showProgress("Saving ...");

            Map<String, String> data = new HashMap<String, String>();

            //common field
            data.put("created_date",  ((EditText) currentView.findViewById(R.id.created_date) ).getText().toString() );
            data.put("description", ((EditText) currentView.findViewById(R.id.description)).getText().toString());


            try{
                data.put("amt", Float.parseFloat(((EditText) currentView.findViewById(R.id.amount)).getText().toString()) + "");
            }
            catch (Exception e){
                data.put("amt", "0");
            }


            //if(fromActivity.equals("ActivityJointExpenseIndividual")  ){
                int is_split=0;

                data.put("joint_group_id",  groupId);
                data.put("user_id", ((Spinner) currentView.findViewById(R.id.fromUser)).getSelectedItemId() + "");

                /*face 2
                int id = ((RadioGroup) currentView.findViewById(R.id.isSplit)).getCheckedRadioButtonId();
                if (id == -1){
                    //no item selected
                }
                else {
                    if (id == R.id.isSplitradioYes) {
                        is_split = 1;
                    }
                }
                */

                //face2
                data.put("is_split",  is_split+"" );

            Log.i("save", "online id"+groupOnlineId);

                //onlineId = >online group id
                if(groupOnlineId==null || groupOnlineId.equals("0")) {
                    save_JointEntryToLocal(data);
                }
                else{
                    save_JointEntryToOnlne(data);
                }
            //}

        }
    }




    private void save_JointEntryToLocal(Map<String, String> data) {

        Log.i("save", "online id save_JointEntryToLocal"+data);

        if (myDb.insertGroupEntry(data) == 1) {
            Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

            closeProgress();
            goBack();

        } else {

            Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
            ((Button)currentView.findViewById(R.id.saveBtn)).setEnabled(true);
            closeProgress();

        }
    }


    private void save_JointEntryToOnlne(Map<String, String> data) {

        Log.i("save", "online id save_JointEntryToOnlne"+data);

        Rqueue = Volley.newRequestQueue(this);
        String apiUrl_AddEntry = "http://givntake.workassis.com/api/entry/add/";

        Map<String, String> dataForPost = new HashMap<String,String>(data);

        //onlineId = >online group id
        dataForPost.put("group_id",groupOnlineId); // myDb.getJointGroupField(dataForPost.get("user_id").toString(),"onlineid"));
        dataForPost.remove("joint_group_id");

        dataForPost.put("user_id",myDb.getUserField( dataForPost.get("user_id").toString(),"onlineid"));

        CustomRequest jsObjRequest =   new CustomRequest

                (Request.Method.POST, apiUrl_AddEntry, dataForPost, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i("api call 1", response.toString()+ "");
                        // no need to edit local db

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
                        ((Button)currentView.findViewById(R.id.saveBtn)).setEnabled(true);
                    }
                });
        Rqueue.add(jsObjRequest);
    }
















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_add_group_entry, menu);
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