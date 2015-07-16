package com.tricon.labs.giventake;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.tricon.labs.giventake.adapters.CustomDatePicker;
import com.tricon.labs.giventake.database.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ActivityPersonalExpenseIndividual extends ActivityBase {


    //View personalExpenseIndividualView;
    String fromActivity=null;
    int  colId=0;
    String colName="";

    DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_expense_individual);

        myDb = new DBHelper(this);

        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            fromActivity= null;
        } else {
            fromActivity= extras.getString("fromActivity");
            colId = Integer.parseInt(extras.getString("colId"));
            colName = extras.getString("colName");
        }


        SimpleDateFormat dmy = new SimpleDateFormat("MM-yyyy");
        String cDate = dmy.format(new Date());

        SimpleDateFormat dbmy = new SimpleDateFormat("yyyy-MM");
        String cdbDate = dbmy.format(new Date());

        TextView dateChanger= (TextView)currentView.findViewById(R.id.dateChanger);
        TextView dateChangerForDb= (TextView)currentView.findViewById(R.id.dateChangerForDb);

        dateChanger.setOnClickListener(new CustomDatePicker(ActivityPersonalExpenseIndividual.this, dateChanger, dateChangerForDb, true));

        dateChanger.setText(cDate);
        dateChangerForDb.setText(cdbDate);

        dateChangerForDb.addTextChangedListener(new dateChange());


        Cursor entrys =  myDb.getPersonalExpense(colId, cDate);
        generateTable(entrys);

        ((FloatingActionButton)currentView.findViewById(R.id.addExpenseGroup)).setOnClickListener(new openAddnewEntrry());
    }


    @Override
    public void onBackPressed() {
        //startActivity(new Intent(ActivityPersonalExpenseIndividual.this, ActivityPersonalExpense.class));
        finish();
    }

    //Todo :- handle back click , and goto prev activity page
    @Override
    public void onResume() {
        super.onResume();

        //Cursor entrys =  myDb.getPersonalExpense(colId, ((TextView) currentView.findViewById(R.id.dateChanger)).getText().toString());
        //generateTable(entrys);
    }

    private void generateTable(Cursor cursor) {

        TableLayout tableLayout = (TableLayout) currentView.findViewById(R.id.tableLayout);
        TableRow tr, th;
        boolean colorFlag=false;
        TextView tv;
        String fields[]={"created_date", "description",  "amt"};

        //setting headder
        String tablehead[]={"Date", "Description", "Amt"};
        tableLayout.removeAllViews();
        th = new TableRow(this);
        th.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));


        Log.i("bm info", "" + colId+ " cc "+ cursor.getCount());
        Log.i("bm info", "" + tablehead.length);


        for (int i=0 ;i< tablehead.length; i++) {
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

            tr.setClickable(true);
            tr.setOnClickListener(new tableRowClicked(Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")))));
            tr.setOnLongClickListener(new tableRowLongClicked ( Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id"))) ));

            Log.i("bm info", "" + fields.length);

            for (int i=0 ;i<fields.length; i++) {

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
                }
                else {
                    tv.setText(cursor.getString(cursor.getColumnIndex(fields[i])));
                }

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
        amtHolder = myDb.getMonthTotalOfPersonalExpenseIndividual(colId, ((TextView) currentView.findViewById(R.id.dateChanger)).getText().toString());
        ((TextView)currentView.findViewById(R.id.monthlyTotal)).setText(": "+amtHolder);

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
            Toast.makeText(getApplicationContext(), "clicked" + rowId, Toast.LENGTH_LONG).show();

            Intent i = new Intent(ActivityPersonalExpenseIndividual.this, ActivityAddEntry.class);
            i.putExtra("fromActivity", "ActivityPersonalExpenseIndividual");
            i.putExtra("ID", ""+colId );
            i.putExtra("Name",  colName );
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

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityPersonalExpenseIndividual.this);
        //builder.setTitle("Add Photo!");


        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Delete")) {

                    myDb.deletePersonalExpense(dbrowId);

                    Cursor entrys =  myDb.getPersonalExpense(colId, ((TextView) currentView.findViewById(R.id.dateChanger)).getText().toString() );

                    generateTable(entrys);



                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }






    private class dateChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            Toast.makeText(getApplicationContext(),""+((TextView)currentView.findViewById(R.id.dateChangerForDb)).getText(),Toast.LENGTH_LONG).show();

            Cursor entrys =  myDb.getPersonalExpense(colId, ((TextView) currentView.findViewById(R.id.dateChanger)).getText().toString() );

            generateTable(entrys);

        }
    }


    private class openAddnewEntrry implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ActivityPersonalExpenseIndividual.this, ActivityAddEntry.class);
            i.putExtra("fromActivity", "ActivityPersonalExpenseIndividual");
            i.putExtra("ID", ""+colId );
            i.putExtra("Name",  colName );
            startActivity(i);

        }
    }






















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_personal_expense_individual, menu);
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
