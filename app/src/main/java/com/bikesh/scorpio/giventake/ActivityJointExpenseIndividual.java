package com.bikesh.scorpio.giventake;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bikesh.scorpio.giventake.adapters.CustomDatePicker;
import com.bikesh.scorpio.giventake.model.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ActivityJointExpenseIndividual extends ActivityBase {

    //View JointExpenseIndividual;

    //DBHelper myDb;
    String fromActivity=null;
    int  groupId=0;
    //String groupName="";
    boolean isMonthlyRenewing=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint_expense_individual);

        //myDb = new DBHelper(this);

        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            fromActivity= null;
        } else {
            fromActivity= extras.getString("fromActivity");
            groupId = Integer.parseInt(extras.getString("groupId"));
            //groupName = extras.getString("colName");
        }


        ((ImageButton)currentView.findViewById(R.id.addEntry)).setOnClickListener(new openAddnewEntrry());

        //slide up and down the table
        ((LinearLayout)currentView.findViewById(R.id.restore)).setOnClickListener(new restoreTable());
        ((ImageView)currentView.findViewById(R.id.restorebtn)).setOnClickListener(new restoreTable());



        SimpleDateFormat dmy = new SimpleDateFormat("MM-yyyy");
        String cDate = dmy.format(new Date());

        SimpleDateFormat dbmy = new SimpleDateFormat("yyyy-MM");
        String cdbDate = dbmy.format(new Date());

        TextView dateChanger= (TextView)currentView.findViewById(R.id.dateChanger);
        TextView dateChangerForDb= (TextView)currentView.findViewById(R.id.dateChangerForDb);
        dateChanger.setOnClickListener(new CustomDatePicker(ActivityJointExpenseIndividual.this, dateChanger, dateChangerForDb, true));

        dateChanger.setText(cDate);
        dateChangerForDb.setText(cdbDate);

        dateChangerForDb.addTextChangedListener(new dateChange());



        generateTables();

    }

    @Override
    public void onResume() {
        super.onResume();
        generateTables();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityJointExpenseIndividual.this,ActivityJointExpense.class));
    }


    private class dateChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            generateTables();
        }
    }



    private void generateTables() {

        Map<String, String> data = new HashMap<String, String>();


        if(isMonthlyRenewing == false) {
            data = myDb.getGroupEntryTotalPerHead(groupId + "");
        }
        else {
            data = myDb.getGroupEntryTotalPerHead(groupId+"", ((TextView)currentView.findViewById(R.id.dateChanger)).getText().toString()) ;
        }



        ((TextView)currentView.findViewById(R.id.amtTotal)).setText(data.get("total"));
        ((TextView)currentView.findViewById(R.id.amtPerHead)).setText(data.get("perhead"));


        Cursor cursor = myDb.getJointGroupbyId(groupId+"");

        if(cursor.getInt(cursor.getColumnIndex("ismonthlytask"))==0){
            ((LinearLayout)currentView.findViewById(R.id.l1)).setVisibility(View.GONE);
            isMonthlyRenewing=false;
        }


        generateGroupUsersTable();

        generateEntryTable();
    }



    private void generateGroupUsersTable() {

        TableLayout tableLayout = (TableLayout) currentView.findViewById(R.id.groupUserTableLayout);
        TableRow tr, th;
        boolean colorFlag=false;
        TextView tv;
        //String fields[]={"created_date", "description",  "amt"};

        //setting headder
        String tablehead[]={"Name", "Amt Spend", "Amt Balance"};
        tableLayout.removeAllViews();
        th = new TableRow(this);
        th.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));




        //Log.i("bm info", "" + colId + " cc " + cursor.getCount());
        //Log.i("bm info", "" + tablehead.length);

        //Creating Table Header
        for (int i=0 ;i< tablehead.length; i++) {
            tv = generateTextview();
            tv.setText(tablehead[i]);
            tv.setTypeface(null, Typeface.BOLD);
            tr.addView(tv);
        }
        tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        //----
        Cursor cursor;
        if(isMonthlyRenewing == false) {
            cursor = myDb.getGroupUsersData(groupId + "");
        }
        else {
            cursor = myDb.getGroupUsersData(groupId + "", ((TextView)currentView.findViewById(R.id.dateChanger)).getText().toString()) ;
        }

        while(cursor.isAfterLast() == false){

            tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            //tr.setClickable(true);
            //tr.setOnClickListener(new tableRowClicked(Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")))));

            //Log.i("bm info", "" + fields.length);

            for (int i=0 ;i<3; i++) {

                tv = generateTextview();

                if(i==2 &&  Float.parseFloat(cursor.getString(i))<0 ){

                    tv.setText(String.format("%.2f", (Float.parseFloat(cursor.getString(i))*-1) )+" Get" );

                }
                else if(i==2 &&  Float.parseFloat(cursor.getString(i))>0 ) {

                    tv.setText( String.format("%.2f", Float.parseFloat(cursor.getString(i)) )+ " Give");

                }
                else{
                    tv.setText(cursor.getString(i));
                }

                tr.addView(tv);
            }

            cursor.moveToNext();

            if(colorFlag){
                tr.setBackgroundColor(Color.rgb(255, 235, 230));
                colorFlag=false;
            }
            else {
                tr.setBackgroundColor(Color.rgb(236, 251, 255));
                colorFlag=true;
            }
            // Add row to TableLayout.
            tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }

    }

    private void generateEntryTable() {

        TableLayout tableLayout = (TableLayout) currentView.findViewById(R.id.groupEntryTableLayout);
        TableRow tr, th;
        boolean colorFlag=false;
        TextView tv;

        //setting headder
        String tablehead[]={"Date", "Description", "Name","Amount"};
        tableLayout.removeAllViews();
        th = new TableRow(this);
        th.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        //Log.i("bm info", "" + colId + " cc " + cursor.getCount());
        //Log.i("bm info", "" + tablehead.length);

        //Creating Table Header
        for (int i=0 ;i< tablehead.length; i++) {
            tv = generateTextview();
            tv.setText(tablehead[i]);
            tv.setTypeface(null, Typeface.BOLD);
            tr.addView(tv);
        }
        tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        //----

        String fields[]={"created_date", "description",  "name", "amt" }; /*, "is_split"*/

        //Cursor cursor =  myDb.getGroupEntrys(groupId + "");

        Cursor cursor;
        if(isMonthlyRenewing == false) {
            cursor = myDb.getGroupEntrys(groupId + "");
        }
        else {
            cursor = myDb.getGroupEntrys(groupId + "", ((TextView) currentView.findViewById(R.id.dateChanger)).getText().toString()) ;
        }


        while(cursor.isAfterLast() == false){

            tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            //tr.setClickable(true);
            //tr.setOnClickListener(new tableRowClicked(Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")))));

            //Log.i("bm info", "" + fields.length);

            for (int i=0 ;i<fields.length; i++) {

                tv = generateTextview();

                tv.setText(cursor.getString(cursor.getColumnIndex(fields[i])));

                tr.addView(tv);
            }

            cursor.moveToNext();

            if(colorFlag){
                tr.setBackgroundColor(Color.rgb(255, 235, 230));
                colorFlag=false;
            }
            else {
                tr.setBackgroundColor(Color.rgb(236, 251, 255));
                colorFlag=true;
            }
            // Add row to TableLayout.
            tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }



    }







    private class restoreTable implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if(((ScrollView) currentView.findViewById(R.id.scrollView1) ).getVisibility() == View.VISIBLE){
                ((ScrollView) currentView.findViewById(R.id.scrollView1) ).setVisibility(View.GONE);
                ((ImageView)currentView.findViewById(R.id.restorebtn)).setImageResource(R.drawable.double_arrow_down);
            }
            else{
                ((ScrollView) currentView.findViewById(R.id.scrollView1) ).setVisibility(View.VISIBLE);
                ((ImageView)currentView.findViewById(R.id.restorebtn)).setImageResource(R.drawable.double_arrow_up);
            }

        }
    }





    private class openAddnewEntrry implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ActivityJointExpenseIndividual.this, ActivityAddEntry.class);
            i.putExtra("fromActivity", "ActivityJointExpenseIndividual");
            i.putExtra("ID", ""+groupId );
            startActivity(i);

        }
    }


    private class tableRowClicked implements View.OnClickListener {
        int rowId=0;
        public tableRowClicked(int id) {
            rowId=id;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "clicked" + rowId, Toast.LENGTH_LONG).show();
        }
    }


    private TextView generateTextview() {
        TextView tv = new TextView(this);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tv.setPadding(5, 5, 5, 5);
        tv.setClickable(false);
        return tv;
    }

}
