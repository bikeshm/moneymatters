package com.tricon.labs.giventake;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tricon.labs.giventake.adapters.AdapterLendAndBorrowEntryList;
import com.tricon.labs.giventake.adapters.AdapterPersonalExpenseEntryList;
import com.tricon.labs.giventake.database.DBHelper;
import com.tricon.labs.giventake.interfaces.EntryClickedListener;
import com.tricon.labs.giventake.interfaces.EntryLongClickedListener;
import com.tricon.labs.giventake.models.LendAndBorrowEntry;
import com.tricon.labs.giventake.models.PersonalExpenseEntry;

import java.util.ArrayList;
import java.util.List;


public class ActivityLendAndBorrowIndividual extends AppCompatActivity implements EntryClickedListener, EntryLongClickedListener {

    private DBHelper mDBHelper;

    int mUserId;
    String mUserName;

    private List<LendAndBorrowEntry> mEntries = new ArrayList<>();
    private AdapterLendAndBorrowEntryList mAdapter;

    private TextView mTVTotalBalance;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_and_borrow_individual);

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.widget_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //get db instance
        mDBHelper = DBHelper.getInstance(this);

        //setup views
        RecyclerView rvEntries = (RecyclerView) findViewById(R.id.rv_entries);

        mTVTotalBalance = (TextView) findViewById(R.id.tv_balance);

        //get extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mUserId = extras.getInt("USERID", -1);
            mUserName = extras.getString("USERNAME", "");
        }

        //set actionbar title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(mUserName);
        }

        //set recycler view layout manager
        rvEntries.setHasFixedSize(true);
        rvEntries.setLayoutManager(new LinearLayoutManager(this));

        //set adapter
        mAdapter = new AdapterLendAndBorrowEntryList(mEntries);
        rvEntries.setAdapter(mAdapter);

        //set listeners
        findViewById(R.id.btn_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LendAndBorrowEntry entry = new LendAndBorrowEntry();
                entry.toUser = mUserId;
                entry.toUserName = mUserName;

                Intent intent = new Intent(ActivityLendAndBorrowIndividual.this, ActivityLendAndBorrowAddEntry.class);
                intent.putExtra("ENTRY", entry);
                intent.putExtra("EDITENTRY", false);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //fetch entries
        new FetchEntriesTask().execute();
    }

    private class FetchEntriesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mEntries.clear();
            mEntries.addAll(mDBHelper.getLendAndBorrowEntrysListByPerson(mUserId));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mAdapter.notifyDataSetChanged();
            new FetchBalancelAmount().execute();
        }
    }


    private class FetchBalancelAmount extends AsyncTask<Void, Void, Double> {

        @Override
        protected Double doInBackground(Void... params) {
            return mDBHelper.getLendAndBorrowBalanceAmount(mUserId);

        }

        @Override
        protected void onPostExecute(Double result) {
            super.onPostExecute(result);
            mTVTotalBalance.setText(result + "");
            mTVTotalBalance.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

            if(result<0) {
                mTVTotalBalance.setText((result * -1) + "");

                mTVTotalBalance.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }

        }
    }

    @Override
    public void onEntryClicked(int position) {

    }

    @Override
    public void onEntryLongClicked(int position) {

    }
}

/*
public class ActivityLendAndBorrowIndividual extends ActivityBase {

    //View lendAndBorrowPersonalView;
    //DBHelper myDb;
    String fromActivity=null;
    int  fromUserId=0;
    String userName="";

    DBHelper myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_and_borrow_individual);

       myDb = new DBHelper(this);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            fromActivity= null;
        } else {
            fromActivity= extras.getString("fromActivity");
            fromUserId = Integer.parseInt(extras.getString("ID"));
            userName = extras.getString("NAME");
        }

        ((TextView) currentView.findViewById(R.id.username)).setText(userName);

        ((FloatingActionButton) currentView.findViewById(R.id.addEntry)).setOnClickListener(new openAddnewEntrry());


        SimpleDateFormat dmy = new SimpleDateFormat("MM-yyyy");
        String cDate = dmy.format(new Date());

        SimpleDateFormat dbmy = new SimpleDateFormat("yyyy-MM");
        String cdbDate = dbmy.format(new Date());

        Cursor entrys =  myDb.getUserEntrys(fromUserId,cDate);

        TextView dateChanger= (TextView)currentView.findViewById(R.id.dateChanger);
        TextView dateChangerForDb= (TextView)currentView.findViewById(R.id.dateChangerForDb);
        dateChanger.setOnClickListener(new CustomDatePicker(ActivityLendAndBorrowIndividual.this, dateChanger, dateChangerForDb, true));

        dateChanger.setText(cDate);
        dateChangerForDb.setText(cdbDate);

        dateChangerForDb.addTextChangedListener(new dateChange());

        generateTable(entrys);
    }


    @Override
    public void onBackPressed() {
        //startActivity(new Intent(ActivityLendAndBorrowIndividual.this,LendAndBorrow.class));
        finish();
    }

    //Todo :- handle back click , and goto prev activity page
    @Override
    public void onResume() {
        super.onResume();

        Cursor entrys =  myDb.getUserEntrys(fromUserId, ((TextView)currentView.findViewById(R.id.dateChanger)).getText().toString() );
        generateTable(entrys);
    }



    private class dateChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            Toast.makeText(getApplicationContext(),""+((TextView)currentView.findViewById(R.id.dateChangerForDb)).getText(),Toast.LENGTH_LONG).show();

            Cursor entrys =  myDb.getUserEntrys(fromUserId,((TextView)currentView.findViewById(R.id.dateChanger)).getText().toString() );

            generateTable(entrys);

        }
    }



    private void generateTable(Cursor cursor) {

        TableLayout tableLayout = (TableLayout) currentView.findViewById(R.id.tableLayout);
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
            tr.setOnLongClickListener(new tableRowLongClicked ( Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id"))) ));

            for (int i=0 ;i<4; i++) {

                tv = generateTextview();


                //changing date format
                if(fields[i].equals("created_date")){

                    try {

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//set format of date you receiving from db
                        Date date = (Date) sdf.parse(  cursor.getString(cursor.getColumnIndex(fields[i]))  );
                        SimpleDateFormat newDate = new SimpleDateFormat("dd-MM-yyyy");//set format of new date
                        tv.setText(""+ newDate.format(date) );
                    }
                    catch(ParseException pe) {
                        tv.setText(cursor.getString(cursor.getColumnIndex(fields[i])));
                    }


                } else if(i!=2 ) {
                    tv.setText(cursor.getString(cursor.getColumnIndex(fields[i])));
                }
                else {
                    // for from user
                    if( Integer.parseInt(cursor.getString(cursor.getColumnIndex("from_user"))) == 1 ){ //1== myId

                        tv.setText("Me");
                    }
                    else{
                        tv.setText("Him/Her");
                    }
                }



                /* Add Button to row. *./
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
        amtHolder = myDb.getMonthTotalOfGive(fromUserId,((TextView)currentView.findViewById(R.id.dateChanger)).getText().toString() );

        ((TextView)currentView.findViewById(R.id.monthTotalToGive)).setText(": "+amtHolder);

        amtHolder = myDb.getMonthTotalOfGet(fromUserId, ((TextView) currentView.findViewById(R.id.dateChanger)).getText().toString() );
        ((TextView)currentView.findViewById(R.id.monthTotalToGet)).setText(": " + amtHolder);

        float balanceAmt=  myDb.getTotalBalance(fromUserId);

        ((TextView)currentView.findViewById(R.id.balanceAmt)).setText(": "+balanceAmt);

        if(balanceAmt<0){
            ((TextView)currentView.findViewById(R.id.balanceAmtLabel)).setText("Balance amount get from him/her");
            balanceAmt=balanceAmt*-1;
            ((TextView)currentView.findViewById(R.id.balanceAmt)).setText(": "+balanceAmt);
        }
        else if (balanceAmt>0){
            ((TextView)currentView.findViewById(R.id.balanceAmtLabel)).setText("Balance amount give to him/her");
        }
        else{
            ((TextView)currentView.findViewById(R.id.balanceAmtLabel)).setText("Balance amount");
        }


        amtHolder = myDb.getPrevBalance(fromUserId, ((TextView)currentView.findViewById(R.id.dateChanger)).getText().toString() );

        if(amtHolder<0){
            ((TextView)currentView.findViewById(R.id.prevBalanceAmtLabel)).setText("Previous balance amount get from him/her");
            amtHolder=amtHolder*-1;
            ((TextView)currentView.findViewById(R.id.prevBalanceAmt)).setText(": "+amtHolder);
        }
        else if (amtHolder>0){
            ((TextView)currentView.findViewById(R.id.prevBalanceAmtLabel)).setText("Previous balance amount give to him/her");
            ((TextView)currentView.findViewById(R.id.prevBalanceAmt)).setText(": "+amtHolder);
        }
        else{
            ((TextView)currentView.findViewById(R.id.prevBalanceAmtLabel)).setText("Previous balance amount");
            ((TextView)currentView.findViewById(R.id.prevBalanceAmt)).setText(": "+amtHolder);
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

            Intent i = new Intent(ActivityLendAndBorrowIndividual.this, ActivityLendAndBorrowAddEntry.class);
            i.putExtra("fromActivity", "ActivityLendAndBorrowPersonal");
            i.putExtra("fromUserId", ""+fromUserId);
            i.putExtra("Name",  userName );
            i.putExtra("rowId",  rowId+"" );
            startActivity(i);


        }
    }


    private class tableRowLongClicked implements View.OnLongClickListener {
        int rowId=0;
        public tableRowLongClicked(int id)  {
            rowId=id;
        }

        @Override
        public boolean onLongClick(View v) {

            Toast.makeText(getApplicationContext(),"Long pressed ", Toast.LENGTH_LONG).show();
            generatePopupmenu(rowId);
            return true;
        }
    }


    public void generatePopupmenu(int rowId) {

        final CharSequence[] options = { "Delete","Cancel" };
        final String dbrowId = rowId+"";

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLendAndBorrowIndividual.this);
        //builder.setTitle("Add Photo!");


        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Delete")) {

                    myDb.deleteEntry(dbrowId);

                    Cursor entrys =  myDb.getUserEntrys(fromUserId,((TextView)currentView.findViewById(R.id.dateChanger)).getText().toString() );
                    generateTable(entrys);

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
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

        return super.onOptionsItemSelected(item);
    }


    private class openAddnewEntrry implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ActivityLendAndBorrowIndividual.this, ActivityLendAndBorrowAddEntry.class);
            i.putExtra("fromActivity", "ActivityLendAndBorrowPersonal");
            i.putExtra("fromUserId", ""+fromUserId);
            i.putExtra("Name",  userName );
            startActivity(i);

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
*/