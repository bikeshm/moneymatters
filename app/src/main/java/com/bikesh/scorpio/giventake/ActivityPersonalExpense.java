package com.bikesh.scorpio.giventake;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bikesh.scorpio.giventake.adapters.Adapter_CustomSimpleCursor;
import com.bikesh.scorpio.giventake.adapters.CustomDatePicker;
import com.bikesh.scorpio.giventake.database.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ActivityPersonalExpense extends ActivityBase {

    //View ActivityPersonalExpenseView;

    ListView listView;

    DBHelper myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_expense);

        myDb = new DBHelper(this);

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


        //myDb = new DBHelper(this);
        populateListViewFromDB();


        //((ImageButton)currentView.findViewById(R.id.addEntry)).setOnClickListener(new openAddnewEntrry());
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
        //Cursor cursor = myDb.getAllCollectionByMonth( ((TextView) currentView.findViewById(R.id.dateChanger)).getText().toString() );
        Cursor cursor = myDb.getAllCollection();

        Map<String, String> dataExtra = new HashMap<String, String>();

        dataExtra.put("selectedDate",  ((TextView) currentView.findViewById(R.id.dateChanger)).getText().toString() );

        listView.setAdapter(new Adapter_CustomSimpleCursor(this,		// Context
                R.layout.listview_item_template,	// Row layout template
                cursor,					// cursor (set of DB records to map)
                dataExtra
        ));

        listView.setOnItemClickListener(new listItemClicked());
        listView.setOnItemLongClickListener(new listItemLongClicked());


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


    private class listItemLongClicked implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            generatePopupmenu(id+"");

            return true;
        }
    }


    public void generatePopupmenu(String rowId) {

        final CharSequence[] options = { "Edit","Delete"};
        final String dbrowId = rowId+"";

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityPersonalExpense.this);
        //builder.setTitle("Add Photo!");


        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Delete")) {

                    //myDb.deleteEntry(dbrowId);

                   // Cursor entrys =  myDb.getUserEntrys(userId,((TextView)currentView.findViewById(R.id.dateChanger)).getText().toString() );
                    //generateTable(entrys);

                    new AlertDialog.Builder(ActivityPersonalExpense.this)
                            .setTitle("Delete")
                            .setMessage("Do you really want to delete?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {

                                    Toast.makeText(ActivityPersonalExpense.this,"id"+dbrowId, Toast.LENGTH_LONG).show();

                                    //delete collection
                                    if( myDb.deleteCollection(dbrowId) ==1){
                                        Log.i("delete collection","collection deleted");

                                        //delete all the entrys in that collection
                                        if( myDb.deleteCollectionEntrys(dbrowId) ==1){
                                            Log.i("delete collection","deleted all the collection entrys");
                                        }
                                        else{
                                            Log.i("delete collection","Not deleted collection entrys");
                                        }

                                        //reloading the data
                                        populateListViewFromDB();

                                    }
                                    else{
                                        Log.i("delete collection","collection Not deleted ");
                                    }



                                }})
                            .setNegativeButton(android.R.string.no, null).show();


                }
                else if (options[item].equals("Edit")) {
                    //dialog.dismiss();

                    Toast.makeText(ActivityPersonalExpense.this,"id"+dbrowId, Toast.LENGTH_LONG).show();

                    Intent i = new Intent(ActivityPersonalExpense.this, ActivityJointExpenseAddGroup.class);
                    i.putExtra("fromActivity", "ActivityPersonalExpense");
                    i.putExtra("groupId", dbrowId);
                    startActivity(i);

                }
            }
        });

        builder.show();
    }


    /*
    private class openAddnewEntrry implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ActivityPersonalExpense.this, ActivityAddEntry.class);
            i.putExtra("fromActivity", "ActivityPersonalExpense");
            startActivity(i);

        }
    }
    */


    private class openAddnewGroup implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ActivityPersonalExpense.this, ActivityAddCategory.class);
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
