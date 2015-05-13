package com.bikesh.scorpio.giventake;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ActivityLendAndBorrowIndividual extends ActionBarActivity {

    View lendAndBorrowPersonalView;
    DBHelper myDb;
    String fromActivity=null;
    int  userId=0;
    String userName="";

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
        AC.setupDrawer(view, ActivityLendAndBorrowIndividual.this, toolbar);

        //loading home activity templet in to template frame
        FrameLayout frame = (FrameLayout) findViewById(R.id.mainFrame);
        frame.removeAllViews();
        Context darkTheme = new ContextThemeWrapper(this, R.style.AppTheme);
        LayoutInflater inflater = (LayoutInflater) darkTheme.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lendAndBorrowPersonalView =  inflater.inflate(R.layout.activity_lend_and_borrow_personal, null);

        frame.addView(lendAndBorrowPersonalView);

        myDb = new DBHelper(this);

        Bundle extras = getIntent().getExtras();



        if(extras == null) {
            fromActivity= null;
        } else {
            fromActivity= extras.getString("fromActivity");
            userId = Integer.parseInt(extras.getString("userId"));
            userName = extras.getString("userName");
        }

        ((TextView) lendAndBorrowPersonalView.findViewById(R.id.username)).setText(userName);

        ((ImageButton) lendAndBorrowPersonalView.findViewById(R.id.addEntry)).setOnClickListener(new openAddnewEntrry());


        SimpleDateFormat dmy = new SimpleDateFormat("MM-yyyy");
        String cDate = dmy.format(new Date());

        SimpleDateFormat dbmy = new SimpleDateFormat("yyyy-MM");
        String cdbDate = dbmy.format(new Date());





        Cursor entrys =  myDb.getUserEntrys(userId,cDate);



        TextView dateChanger= (TextView)lendAndBorrowPersonalView.findViewById(R.id.dateChanger);
        TextView dateChangerForDb= (TextView)lendAndBorrowPersonalView.findViewById(R.id.dateChangerForDb);
        dateChanger.setOnClickListener(new CustomDatePicker(ActivityLendAndBorrowIndividual.this, dateChanger, dateChangerForDb, true));

        dateChanger.setText(cDate);
        dateChangerForDb.setText(cdbDate);

        dateChangerForDb.addTextChangedListener(new dateChange());

        generageTable(entrys);
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityLendAndBorrowIndividual.this,ActivityLendAndBorrow.class));
    }

    //Todo :- handle back click , and goto prev activity page
    @Override
    public void onResume() {
        super.onResume();

        Cursor entrys =  myDb.getUserEntrys(userId, ((TextView)lendAndBorrowPersonalView.findViewById(R.id.dateChanger)).getText().toString() );
        generageTable(entrys);
    }



    private class dateChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            Toast.makeText(getApplicationContext(),""+((TextView)lendAndBorrowPersonalView.findViewById(R.id.dateChangerForDb)).getText(),Toast.LENGTH_LONG).show();

            Cursor entrys =  myDb.getUserEntrys(userId,((TextView)lendAndBorrowPersonalView.findViewById(R.id.dateChanger)).getText().toString() );

            generageTable(entrys);

        }
    }



    private void generageTable(Cursor cursor) {

        TableLayout tableLayout = (TableLayout) lendAndBorrowPersonalView.findViewById(R.id.tableLayout);
        TableRow tr, th;
        boolean colorFlag=false;
        TextView tv;
        String fields[]={"created_date", "description", "from_user", "amt"};

        //setting headder
        String tablehead[]={"Date", "Description", "From", "Amt"};
        tableLayout.removeAllViews();
        th = new TableRow(this);
        th.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        for (int i=0 ;i<4; i++) {
            tv = generateTextview();
            tv.setText(tablehead[i]);
            tv.setTypeface(null, Typeface.BOLD);
            tr.addView(tv);
        }
        tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        //----

        while(cursor.isAfterLast() == false){

            tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            //tr.setId(cursor.getString(cursor.getColumnIndex("_id")));

            tr.setClickable(true);
            tr.setOnClickListener(new tableRowClicked( Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id"))) ));

            for (int i=0 ;i<4; i++) {

                tv = generateTextview();


                if(i!=2 ) {
                    tv.setText(cursor.getString(cursor.getColumnIndex(fields[i])));
                }
                else {
                    if( Integer.parseInt(cursor.getString(cursor.getColumnIndex("from_user"))) == 1 ){ //1== myId

                        tv.setText("Me");
                    }
                    else{
                        tv.setText("Him/Her");
                    }
                }



                /* Add Button to row. */
                tr.addView(tv);
            }

            cursor.moveToNext();

            if(colorFlag){
                tr.setBackgroundColor(Color.rgb(240, 242, 242));
                colorFlag=false;
            }
            else {
                tr.setBackgroundColor(Color.rgb(234, 237, 237));
                colorFlag=true;
            }
            // Add row to TableLayout.
            tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }


        float amtHolder;
        amtHolder = myDb.getMonthTotalOfGive(userId,((TextView)lendAndBorrowPersonalView.findViewById(R.id.dateChanger)).getText().toString() );

        ((TextView)lendAndBorrowPersonalView.findViewById(R.id.monthTotalToGive)).setText(": "+amtHolder);

        amtHolder = myDb.getMonthTotalOfGet(userId, ((TextView) lendAndBorrowPersonalView.findViewById(R.id.dateChanger)).getText().toString() );
        ((TextView)lendAndBorrowPersonalView.findViewById(R.id.monthTotalToGet)).setText(": " + amtHolder);

        float balanceAmt=  myDb.getTotalBalance(userId);

        ((TextView)lendAndBorrowPersonalView.findViewById(R.id.balanceAmt)).setText(": "+balanceAmt);

        if(balanceAmt<0){
            ((TextView)lendAndBorrowPersonalView.findViewById(R.id.balanceAmtLabel)).setText("Balance amount get from him/her");
            balanceAmt=balanceAmt*-1;
            ((TextView)lendAndBorrowPersonalView.findViewById(R.id.balanceAmt)).setText(": "+balanceAmt);
        }
        else if (balanceAmt>0){
            ((TextView)lendAndBorrowPersonalView.findViewById(R.id.balanceAmtLabel)).setText("Balance amount give to him/her");
        }
        else{
            ((TextView)lendAndBorrowPersonalView.findViewById(R.id.balanceAmtLabel)).setText("Balance amount");
        }


        amtHolder = myDb.getPrevBalance(userId, ((TextView)lendAndBorrowPersonalView.findViewById(R.id.dateChanger)).getText().toString() );

        if(amtHolder<0){
            ((TextView)lendAndBorrowPersonalView.findViewById(R.id.prevBalanceAmtLabel)).setText("Previous balance amount get from him/her");
            amtHolder=amtHolder*-1;
            ((TextView)lendAndBorrowPersonalView.findViewById(R.id.prevBalanceAmt)).setText(": "+amtHolder);
        }
        else if (amtHolder>0){
            ((TextView)lendAndBorrowPersonalView.findViewById(R.id.prevBalanceAmtLabel)).setText("Previous balance amount give to him/her");
            ((TextView)lendAndBorrowPersonalView.findViewById(R.id.prevBalanceAmt)).setText(": "+amtHolder);
        }
        else{
            ((TextView)lendAndBorrowPersonalView.findViewById(R.id.prevBalanceAmtLabel)).setText("Previous balance amount");
            ((TextView)lendAndBorrowPersonalView.findViewById(R.id.prevBalanceAmt)).setText(": "+amtHolder);
        }


    }

    private TextView generateTextview() {
        TextView tv = new TextView(this);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tv.setPadding(5, 5, 5, 5);
        tv.setClickable(false);
        return tv;
    }


    private class tableRowClicked implements View.OnClickListener {
        int rowId=0;
        public tableRowClicked(int id) {
            rowId=id;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(),"clicked"+rowId, Toast.LENGTH_LONG).show();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDb != null) {
            myDb.close();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_lend_and_borrow_personal, menu);
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


    private class openAddnewEntrry implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ActivityLendAndBorrowIndividual.this, ActivityAddEntry.class);
            i.putExtra("fromActivity", "ActivityLendAndBorrowPersonal");
            i.putExtra("userId", ""+userId);
            i.putExtra("userName",  userName );
            startActivity(i);

        }
    }



}
