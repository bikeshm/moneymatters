package com.bikesh.scorpio.giventake;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.bikesh.scorpio.giventake.model.DBHelper;

import java.util.Map;


public class ActivityLendAndBorrow extends ActionBarActivity {

    DBHelper myDb;
    View lendAndBorrowView;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_lend_and_borrow);

        super.onCreate(savedInstanceState);
        //loading templet xml
        setContentView(R.layout.main_template);


        //setting up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //setting up navigation drawer
        GiveNTakeApplication AC = (GiveNTakeApplication)getApplicationContext();
        View view = getWindow().getDecorView().findViewById(android.R.id.content);
        AC.setupDrawer(view, ActivityLendAndBorrow.this, toolbar);

        //loading lendAndBorrow activity templet in to template frame
        FrameLayout frame = (FrameLayout) findViewById(R.id.mainFrame);
        frame.removeAllViews();
        Context darkTheme = new ContextThemeWrapper(this, R.style.AppTheme);
        LayoutInflater inflater = (LayoutInflater) darkTheme.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lendAndBorrowView=  inflater.inflate(R.layout.activity_lend_and_borrow, null);

        frame.addView(lendAndBorrowView);

        listView = (ListView) lendAndBorrowView.findViewById(R.id.listViewFromDB);

        listView.setOnItemClickListener(new listItemClicked());

        myDb = new DBHelper(this);
        populateListViewFromDB();

        ((ImageButton)lendAndBorrowView.findViewById(R.id.addEntry)).setOnClickListener(new openAddnewEntrry());
        ((ImageButton)lendAndBorrowView.findViewById(R.id.addUser)).setOnClickListener(new openAddnewGroup());



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

            Intent i = new Intent(ActivityLendAndBorrow.this, ActivityAddGroup.class);
            i.putExtra("fromActivity", "ActivityLendAndBorrow");
            startActivity(i);

        }
    }



    private void populateListViewFromDB() {

        //Todo :- 1. insted of listing all user just list the user who all are having amt balance
        //Todo :- need to implement pagination
        Cursor cursor = myDb.getAllUsers();



        listView.setAdapter(new Adapter_CustomSimpleCursor(this,		// Context
                R.layout.listview_item_template,	// Row layout template
                cursor					// cursor (set of DB records to map)
        ));



        Map<String, String> finalResult = myDb.getFinalResult();

        ((TextView)lendAndBorrowView.findViewById(R.id.amt_togive)).setText(": "+finalResult.get("amt_toGive"));
        ((TextView)lendAndBorrowView.findViewById(R.id.amt_toget)).setText(": " + finalResult.get("amt_toGet"));


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDb != null) {
            myDb.close();
        }
    }

}