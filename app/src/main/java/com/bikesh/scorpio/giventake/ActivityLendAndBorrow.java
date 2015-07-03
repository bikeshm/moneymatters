package com.bikesh.scorpio.giventake;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.bikesh.scorpio.giventake.adapters.Adapter_CustomSimpleCursor;
import com.bikesh.scorpio.giventake.database.DBHelper;

import java.util.Map;


public class ActivityLendAndBorrow extends ActivityBase {

    DBHelper myDb;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_and_borrow);

        //setting up toolbar
        //Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //setSupportActionBar(toolbar);

        listView = (ListView) currentView.findViewById(R.id.listViewFromDB);

        listView.setOnItemClickListener(new listItemClicked());

        myDb = new DBHelper(this);
        populateListViewFromDB();

        ((ImageButton)currentView.findViewById(R.id.addEntry)).setOnClickListener(new openAddnewEntrry());
        //((ImageButton)currentView.findViewById(R.id.addUser)).setOnClickListener(new openAddnewGroup());

    }

    @Override
    public void onResume() {
        super.onResume();

        populateListViewFromDB();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityLendAndBorrow.this,ActivityHome.class));
    }

    private class listItemClicked implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //id == table id
            Intent i = new Intent(ActivityLendAndBorrow.this, ActivityLendAndBorrowIndividual.class);
            i.putExtra("fromActivity", "ActivityLendAndBorrow");
            i.putExtra("userId", ""+id);
            i.putExtra("userName", ""+ ((TextView) view.findViewById(R.id.item_name)).getText().toString() );
            startActivity(i);
            //Toast.makeText(ActivityLendAndBorrow.this, "Id "+ id , Toast.LENGTH_LONG).show();
        }
    }





    private class openAddnewEntrry implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ActivityLendAndBorrow.this, ActivityAddEntry.class);
            i.putExtra("fromActivity", "ActivityLendAndBorrow");
            startActivity(i);

        }
    }
    private class openAddnewGroup implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ActivityLendAndBorrow.this, ActivityJointExpenseAddGroup.class);
            i.putExtra("fromActivity", "ActivityLendAndBorrow");
            startActivity(i);

        }
    }



    private void populateListViewFromDB() {

        //Todo :- 1. insted of listing all user just list the user who all are having amt balance
        //Todo :- need to implement pagination

         Cursor cursor = myDb.getLendAndBorrowList();
        //Cursor cursor = myDb.getAllUsers();



        listView.setAdapter(new Adapter_CustomSimpleCursor(this,		// Context
                R.layout.listview_item_template,	// Row layout template
                cursor					// cursor (set of DB records to map)
        ));



        Map<String, String> finalResult = myDb.getFinalResult();

        ((TextView)currentView.findViewById(R.id.amt_togive)).setText(": "+finalResult.get("amt_toGive"));
        ((TextView)currentView.findViewById(R.id.amt_toget)).setText(": " + finalResult.get("amt_toGet"));


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDb != null) {
            myDb.close();
        }
    }

}