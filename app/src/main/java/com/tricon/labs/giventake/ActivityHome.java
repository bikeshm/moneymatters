package com.tricon.labs.giventake;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tricon.labs.giventake.database.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ActivityHome extends ActivityBase {

    DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //setting up toolbar
        //Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //setSupportActionBar(toolbar);


        myDb = new DBHelper(this);

        ((LinearLayout) currentView.findViewById(R.id.lendAndBorrow)).setOnClickListener(new linkClicked(1));
        ((LinearLayout) currentView.findViewById(R.id.personalExpense)).setOnClickListener(new linkClicked(2));
        ((LinearLayout) currentView.findViewById(R.id.jointExpense)).setOnClickListener(new linkClicked(3));


        Map<String, String> finalResult = myDb.getFinalResult();
        ((TextView)currentView.findViewById(R.id.amt_togive)).setText(" " + finalResult.get("amt_toGive"));
        ((TextView)currentView.findViewById(R.id.amt_toget)).setText(" " + finalResult.get("amt_toGet"));

        Map<String, String> jointfinalResult = myDb.getAllGroupTotalSpendGiveGet();
        ((TextView)currentView.findViewById(R.id.joint_amtSpend)).setText(" "+jointfinalResult.get("total"));
        ((TextView)currentView.findViewById(R.id.joint_amtGet)).setText(" " + jointfinalResult.get("toget"));
        ((TextView)currentView.findViewById(R.id.joint_amtGive)).setText(" " + jointfinalResult.get("togive"));



        SimpleDateFormat dmy = new SimpleDateFormat("MM-yyyy");
        String cDate = dmy.format(new Date());
        float amtHolder;
        amtHolder = myDb.getMonthTotalOfPersonalExpense(cDate );
        ((TextView)currentView.findViewById(R.id.personalExpenseTotal)).setText(" " + amtHolder);


        Log.i("dbpath", getDatabasePath("GivnTake.db").getAbsolutePath());
        Log.i("dbpath", Environment.getExternalStorageDirectory().toString() );


    }



    private class linkClicked implements View.OnClickListener {
        int item;

        public linkClicked(int i) {
            item = i;
        }

        @Override
        public void onClick(View v) {
            switch (item){
                case 1:
                    startActivity(new Intent(ActivityHome.this, ActivityLendAndBorrow.class));
                    break;
                case 2:
                    startActivity(new Intent(ActivityHome.this, ActivityPersonalExpense.class));
                    break;
                case 3:
                    startActivity(new Intent(ActivityHome.this, ActivityJointExpense.class));
                    break;

            }

        }
    }


    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
            .setTitle("Close")
            .setMessage("Do you really want to Close?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {

                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_HOME);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(a);

                }})
            .setNegativeButton(android.R.string.no, null).show();

    }


    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDb != null) {
            myDb.close();
        }
    }
    */













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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDb != null) {
            myDb.close();
        }
    }
}
