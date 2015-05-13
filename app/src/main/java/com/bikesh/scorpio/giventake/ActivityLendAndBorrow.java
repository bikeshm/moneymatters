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
import android.widget.Toast;

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

        Cursor cursor = myDb.getAllUsers();

        float amt=0, togive=0,toget=0;
        listView.setAdapter(new Custom_Adapter(this,		// Context
                R.layout.listview_item_template,	// Row layout template
                cursor					// cursor (set of DB records to map)
                ));


        //cursor = myDb.getAllUsers();
        if(cursor!=null){
            cursor.moveToFirst();


            while(cursor.isAfterLast() == false){



                amt= myDb.getTotalBalance(Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id"))) );

                if(amt<0){
                    amt=amt*-1;
                    toget=toget+amt;
                }
                else{
                    togive=togive+amt;
                }
                cursor.moveToNext();
            }
        }

        ((TextView)lendAndBorrowView.findViewById(R.id.amt_togive)).setText(": "+togive);
        ((TextView)lendAndBorrowView.findViewById(R.id.amt_toget)).setText(": " + toget);


    }

    public class Custom_Adapter extends SimpleCursorAdapter {

        private Context mContext;
        private Context appContext;
        private int layout;
        private Cursor cr;
        private final LayoutInflater inflater;

        public Custom_Adapter(Context context,int layout, Cursor c ) {
            super(context,layout,c,new String[]{},new int[]{},0);
            this.layout=layout;
            this.mContext = context;
            this.inflater=LayoutInflater.from(context);
            this.cr=c;
        }

        @Override
        public View newView (Context context, Cursor cursor, ViewGroup parent) {
            return inflater.inflate(layout, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);


            ((TextView)view.findViewById(R.id.item_name)).setText(cursor.getString(cursor.getColumnIndex("name")));

            float balanceAmt=  myDb.getTotalBalance(Long.parseLong(( cursor.getString(cursor.getColumnIndex("_id")) )) );

            ((TextView)view.findViewById(R.id.item_amt)).setText("" + balanceAmt);



            if(balanceAmt<0){
                ((TextView)view.findViewById(R.id.item_description)).setText("Amount get from him/her");
                balanceAmt=balanceAmt*-1;
                ((TextView)view.findViewById(R.id.item_amt)).setText(""+balanceAmt);
            }
            else if (balanceAmt>0){
                ((TextView)view.findViewById(R.id.item_description)).setText("Amount give to him/her");
            }
            else{
                ((TextView)view.findViewById(R.id.item_description)).setText("");

            }

        }

    }


























    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDb != null) {
            myDb.close();
        }
    }

}