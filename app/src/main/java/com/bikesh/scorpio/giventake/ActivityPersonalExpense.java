package com.bikesh.scorpio.giventake;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Random;


public class ActivityPersonalExpense extends ActionBarActivity {

    DBHelper myDb;
    View ActivityPersonalExpenseView;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //loading templet xml
        setContentView(R.layout.main_template);


        //setting up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //setting up navigation drawer
        GiveNTakeApplication AC = (GiveNTakeApplication)getApplicationContext();
        View view = getWindow().getDecorView().findViewById(android.R.id.content);
        AC.setupDrawer(view, ActivityPersonalExpense.this, toolbar);

        //loading home activity templet in to template frame
        FrameLayout frame = (FrameLayout) findViewById(R.id.mainFrame);
        frame.removeAllViews();
        Context darkTheme = new ContextThemeWrapper(this, R.style.AppTheme);
        LayoutInflater inflater = (LayoutInflater) darkTheme.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ActivityPersonalExpenseView=  inflater.inflate(R.layout.activity_personal_expense, null);
        frame.addView(ActivityPersonalExpenseView);


        listView = (ListView) ActivityPersonalExpenseView.findViewById(R.id.listViewFromDB);

        myDb = new DBHelper(this);
        populateListViewFromDB();


        ((ImageButton)ActivityPersonalExpenseView.findViewById(R.id.addEntry)).setOnClickListener(new openAddnewEntrry());
        ((ImageButton)ActivityPersonalExpenseView.findViewById(R.id.addExpenseGroup)).setOnClickListener(new openAddnewGroup());

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityPersonalExpense.this,ActivityHome.class));
    }

    @Override
    public void onResume() {
        super.onResume();

        populateListViewFromDB();
    }

    private void populateListViewFromDB() {

        //Todo :- 1. insted of listing all category just list the category which is having entry
        //Todo :- need to implement pagination
        //Todo : - implement search option
        Cursor cursor = myDb.getAllCollection();

        listView.setAdapter(new Adapter_CustomSimpleCursor(this,		// Context
                R.layout.listview_item_template,	// Row layout template
                cursor					// cursor (set of DB records to map)
        ));

        listView.setOnItemClickListener(new listItemClicked());
    }

    private class listItemClicked implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(ActivityPersonalExpense.this, ActivityPersonalExpenseIndividual.class);
            i.putExtra("fromActivity", "ActivityPersonalExpense");
            i.putExtra("colId", ""+id);
            i.putExtra("colName", "" + ((TextView) view.findViewById(R.id.item_name)).getText().toString() );
            startActivity(i);
        }
    }


    private class openAddnewEntrry implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ActivityPersonalExpense.this, ActivityAddEntry.class);
            i.putExtra("fromActivity", "ActivityPersonalExpense");
            startActivity(i);

        }
    }
    private class openAddnewGroup implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ActivityPersonalExpense.this, ActivityAddGroup.class);
            i.putExtra("fromActivity", "ActivityPersonalExpense");
            startActivity(i);

        }
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_personal_expense, menu);
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


}
