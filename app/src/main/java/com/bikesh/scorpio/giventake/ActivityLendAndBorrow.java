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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.Random;


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




        listView = (ListView) lendAndBorrowView.findViewById(R.id.listViewFromDB);

        /*
        String[] values = new String[] {
                "Manoj",
                "Vyshakh",
                "Bikesh",
                "Anjane",
                "MSR",
                "Riju",
                "ANsar",
                "Suneesh",

                "Anees",
                "Gokul",
                "Lakshmi",
                "Savi",
                "Sanju",
                "Prasadh",
                "Luttan",
                "Sapna",
        };
        */

        //MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(ActivityLendAndBorrow.this, values);
        //listView.setAdapter(adapter);

        listView.setOnItemClickListener(new listItemClicked());


        frame.addView(lendAndBorrowView);

        myDb = new DBHelper(this);
        populateListViewFromDB();

        ((ImageButton)lendAndBorrowView.findViewById(R.id.addEntry)).setOnClickListener(new openAddnewEntrry());
        ((ImageButton)lendAndBorrowView.findViewById(R.id.addUser)).setOnClickListener(new openAddnewGroup());



    }

    private class listItemClicked implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(ActivityLendAndBorrow.this, ActivityLendAndBorrowIndividual.class);
            startActivity(i);
        }
    }


    class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public MySimpleArrayAdapter(Context context, String[] values) {
            super(context, R.layout.item_lend_and_borrow, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //View rowView = inflater.inflate(R.layout.item_lend_and_borrow, parent, false);

            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.item_lend_and_borrow, null);

            TextView textView = (TextView) rowView.findViewById(R.id.item_name);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.item_icon);
            textView.setText(values[position]);
            // change the icon for Windows and iPhone
            String s = values[position];
            //if (s.startsWith("iPhone")) {
            //    imageView.setImageResource(R.drawable.gear);
            //} else {
            imageView.setImageResource(R.drawable.marker);
            //}

            Random r = new Random();
            //rand.nextInt((max - min) + 1) + min;
            int amt = r.nextInt((500 - 80) + 1) + 80;
            TextView textPrice = (TextView) rowView.findViewById(R.id.item_studentnum);
            textPrice.setText(""+amt);

            Log.d("Log", "=============" + position);

            return rowView;
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
        Cursor cursor = myDb.getAllUsers();

        /*
        while(cursor.isAfterLast() == false){
            Log.i("DB", cursor.getString(cursor.getColumnIndex("name")) );
            cursor.moveToNext();
        }
        */

        // Allow activity to manage lifetime of the cursor.
        // DEPRECATED! Runs on the UI thread, OK for small/short queries.
        //startManagingCursor(cursor); // manually closing cursor


        // Setup mapping from cursor to view fields:
        String[] fromFieldNames = new String[] {DBHelper.USER_COLUMN_NAME,  DBHelper.USER_COLUMN_PHONE};
        int[] toViewIDs = new int[]            {R.id.item_name,      R.id.item_amt};

        // Create adapter to may columns of the DB onto elemesnt in the UI.
        SimpleCursorAdapter myCursorAdapter =
                new SimpleCursorAdapter(
                        this,		// Context
                        R.layout.listview_item_template,	// Row layout template
                        cursor,					// cursor (set of DB records to map)
                        fromFieldNames,			// DB Column names
                        toViewIDs, 				// View IDs to put information in
                        0
                );

        // Set the adapter for the list view
        //ListView myList = (ListView) findViewById(R.id.listViewFromDB);
        listView.setAdapter(myCursorAdapter);

        //cursor.close();
        //myDb.close();
    }

}