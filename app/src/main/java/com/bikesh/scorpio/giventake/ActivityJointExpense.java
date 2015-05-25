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
import android.widget.Toast;

import java.util.Map;
import java.util.Random;


public class ActivityJointExpense extends ActionBarActivity {

    View jointExpenseView;
    ListView listView;
    DBHelper myDb;

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
        AC.setupDrawer(view, ActivityJointExpense.this, toolbar);

        //loading home activity templet in to template frame
        FrameLayout frame = (FrameLayout) findViewById(R.id.mainFrame);
        frame.removeAllViews();
        Context darkTheme = new ContextThemeWrapper(this, R.style.AppTheme);
        LayoutInflater inflater = (LayoutInflater) darkTheme.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        jointExpenseView=  inflater.inflate(R.layout.activity_joint_expense, null);

        frame.addView(jointExpenseView);


        listView = (ListView) jointExpenseView.findViewById(R.id.listViewFromDB);
        listView.setOnItemClickListener(new listItemClicked());

        //((ImageButton)jointExpenseView.findViewById(R.id.addEntry)).setOnClickListener(new openAddnewEntrry());
        ((ImageButton)jointExpenseView.findViewById(R.id.addUser)).setOnClickListener(new openAddnewGroup());


        myDb = new DBHelper(this);
        populateListViewFromDB();


    }

    private void populateListViewFromDB() {

        //Todo :- 1. insted of listing all user just list the user who all are having amt balance
        //Todo :- need to implement pagination
        Cursor cursor = myDb.getAllJointGroups();



        listView.setAdapter(new Adapter_CustomSimpleCursor(this,		// Context
                R.layout.listview_item_template,	// Row layout template
                cursor					// cursor (set of DB records to map)
        ));



        /*
        Map<String, String> finalResult = myDb.getFinalResult();

        ((TextView)lendAndBorrowView.findViewById(R.id.amt_togive)).setText(": "+finalResult.get("amt_toGive"));
        ((TextView)lendAndBorrowView.findViewById(R.id.amt_toget)).setText(": " + finalResult.get("amt_toGet"));
        */

    }


    @Override
    public void onResume() {
        super.onResume();

        //populateListViewFromDB();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityJointExpense.this,ActivityHome.class));
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


    /*
    class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public MySimpleArrayAdapter(Context context, String[] values) {
            //super(context, R.layout.xxxxxxxxxitem_lend_and_borrow, values);
            super(context, R.layout.listview_item_template, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //View rowView = inflater.inflate(R.layout.xxxxxxxxxitem_lend_and_borrow, parent, false);

            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //View rowView = inflater.inflate(R.layout.xxxxxxxxxitem_lend_and_borrow, null);
            View rowView = inflater.inflate(R.layout.listview_item_template, null);

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
            TextView textPrice = (TextView) rowView.findViewById(R.id.item_amt);
            textPrice.setText(""+amt);


            return rowView;
        }


    }
    */

    private class openAddnewGroup implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ActivityJointExpense.this, ActivityAddGroup.class);
            i.putExtra("fromActivity", "ActivityJointExpense");
            startActivity(i);

        }
    }

    private class openAddnewEntrry implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ActivityJointExpense.this, ActivityAddEntry.class);
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
}
