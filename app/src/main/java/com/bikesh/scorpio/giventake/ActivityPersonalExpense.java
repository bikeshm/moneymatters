package com.bikesh.scorpio.giventake;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bikesh.scorpio.giventake.model.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ActivityPersonalExpense extends ActivityBase {

    DBHelper myDb;
    //View ActivityPersonalExpenseView;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personal_expense);



        listView = (ListView) currentView.findViewById(R.id.listViewFromDB);


        SimpleDateFormat dmy = new SimpleDateFormat("MM-yyyy");
        String cDate = dmy.format(new Date());

        SimpleDateFormat dbmy = new SimpleDateFormat("yyyy-MM");
        String cdbDate = dbmy.format(new Date());

        TextView dateChanger= (TextView)currentView.findViewById(R.id.dateChanger);
        TextView dateChangerForDb= (TextView)currentView.findViewById(R.id.dateChangerForDb);

        dateChanger.setOnClickListener(new CustomDatePicker(ActivityPersonalExpense.this, dateChanger, dateChangerForDb, true));

        dateChanger.setText(cDate);
        dateChangerForDb.setText(cdbDate);

        dateChangerForDb.addTextChangedListener(new dateChange());


        myDb = new DBHelper(this);
        populateListViewFromDB();


        ((ImageButton)currentView.findViewById(R.id.addEntry)).setOnClickListener(new openAddnewEntrry());
        ((ImageButton)currentView.findViewById(R.id.addExpenseGroup)).setOnClickListener(new openAddnewGroup());

    }


    private class dateChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            Toast.makeText(getApplicationContext(), "" + ((TextView) currentView.findViewById(R.id.dateChangerForDb)).getText(), Toast.LENGTH_LONG).show();


            populateListViewFromDB();

        }
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
        Cursor cursor = myDb.getAllCollectionByMonth( ((TextView) currentView.findViewById(R.id.dateChanger)).getText().toString() );

        Map<String, String> dataExtra = new HashMap<String, String>();

        dataExtra.put("selectedDate",  ((TextView) currentView.findViewById(R.id.dateChanger)).getText().toString() );

        listView.setAdapter(new Adapter_CustomSimpleCursor(this,		// Context
                R.layout.listview_item_template,	// Row layout template
                cursor,					// cursor (set of DB records to map)
                dataExtra
        ));

        listView.setOnItemClickListener(new listItemClicked());


        float amtHolder;
        amtHolder = myDb.getMonthTotalOfPersonalExpense(((TextView) currentView.findViewById(R.id.dateChanger)).getText().toString());
        ((TextView)currentView.findViewById(R.id.monthlyTotal)).setText(": " + amtHolder);

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
