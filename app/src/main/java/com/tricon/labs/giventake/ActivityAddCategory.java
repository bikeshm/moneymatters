package com.tricon.labs.giventake;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tricon.labs.giventake.adapters.Adapter_RecyclerViewList;
import com.tricon.labs.giventake.database.DBHelper;

import java.util.HashMap;
import java.util.Map;

public class ActivityAddCategory extends ActivityBase {

    String fromActivity=null;

    //View addGroupView;

    //private DBHelper myDb ;

    Intent backActivityIntent=null;

    RecyclerView recyclerView;
    Adapter_RecyclerViewList adapter;

    String groupId=null;

    DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

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

            groupId = extras.getString("groupId",null);

        }


        if (fromActivity.equals("ActivityLendAndBorrow")) {//((TextView) addGroupView.findViewById(R.id.op)).setText("Add user");
            //removed
           // backActivityIntent = new Intent(ActivityAddCategory.this, LendAndBorrow.class);
            getSupportActionBar().setTitle("Create User");

        }
        else if (fromActivity.equals("ActivityPersonalExpense")) {
            ((LinearLayout) currentView.findViewById(R.id.emailLayer)).setVisibility(View.GONE);
            ((LinearLayout) currentView.findViewById(R.id.phoneLayer)).setVisibility(View.GONE);
            //backActivityIntent = new Intent(ActivityAddCategory.this, ActivityPersonalExpense.class);
            getSupportActionBar().setTitle("Create Collection");

            if(groupId!=null){

                Cursor currentGroup = myDb.getCollectionById(groupId);

                ((EditText) currentView.findViewById(R.id.name)).setText( currentGroup.getString(currentGroup.getColumnIndex("name")) );
                ((EditText) currentView.findViewById(R.id.description)).setText( currentGroup.getString(currentGroup.getColumnIndex("description")) );
                ((EditText) currentView.findViewById(R.id.id)).setText(groupId );


            }



        }

        else {
            throw new IllegalArgumentException("Invalid  ");
        }



        ((Button) currentView.findViewById(R.id.saveBtn)).setOnClickListener(new saveData());
        ((Button) currentView.findViewById(R.id.cancelBtn)).setOnClickListener(new cancelActivity());




    }


    private class saveData implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if(((EditText) currentView.findViewById(R.id.name) ).getText().toString().trim().equals("")){
                Toast.makeText(getApplicationContext(), "Name required", Toast.LENGTH_LONG).show();
                return;
            }

            Map<String, String> data = new HashMap<String, String>();

            //Todo:- validate and escape incomming data
            data.put("name",  ((EditText) currentView.findViewById(R.id.name) ).getText().toString());
            data.put("description", ((EditText) currentView.findViewById(R.id.description)).getText().toString());



            if(fromActivity.equals("ActivityPersonalExpense")) {

                Log.i("saving colection", "in");
                String currentGroupId = ((EditText) currentView.findViewById(R.id.id)).getText().toString();

                Log.i("saving colection", "currentGroupId "+currentGroupId);

                if(currentGroupId.equals("0")) {



                    if (myDb.insertCollection(data) == 1) {
                        Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

                        goBack();

                    } else {
                        Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    data.put("_id",  currentGroupId );

                    if (myDb.updateCollection(data) == 1) {
                        Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

                        goBack();

                    } else {
                        Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
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
