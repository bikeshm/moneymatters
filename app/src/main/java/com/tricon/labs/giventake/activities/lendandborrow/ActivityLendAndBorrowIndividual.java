package com.tricon.labs.giventake.activities.lendandborrow;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.tricon.labs.giventake.R;
import com.tricon.labs.giventake.adapters.AdapterLendAndBorrowEntryList;
import com.tricon.labs.giventake.database.DBHelper;
import com.tricon.labs.giventake.interfaces.EntryClickedListener;
import com.tricon.labs.giventake.interfaces.EntryLongClickedListener;
import com.tricon.labs.giventake.models.LendAndBorrowEntry;

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

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

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


                Intent intent = new Intent(ActivityLendAndBorrowIndividual.this, ActivityLendAndBorrowAddEntry.class);
                intent.putExtra("ENTRY", entry);
                intent.putExtra("EDITENTRY", false);

                intent.putExtra("SELECTEDUSERID", mUserId);
                intent.putExtra("SELECTEDUSERNAME", mUserName);

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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void onEntryClicked(int position) {
        Intent intent = new Intent(ActivityLendAndBorrowIndividual.this, ActivityLendAndBorrowAddEntry.class);
        intent.putExtra("ENTRY", mEntries.get(position));
        intent.putExtra("EDITENTRY", true);
        intent.putExtra("SELECTEDUSERID", mUserId);
        intent.putExtra("SELECTEDUSERNAME", mUserName);
        startActivity(intent);
    }

    @Override
    public void onEntryLongClicked(final int position) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Entry")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDBHelper.deleteLendAndBorrowEntry(mEntries.get(position).entryId);

                        LendAndBorrowEntry entry = mEntries.remove(position);
                        mAdapter.notifyItemRemoved(position);
                        double newBalance;
                        if (entry.status == LendAndBorrowEntry.STATUS_GET) {
                            newBalance = Double.parseDouble(mTVTotalBalance.getText().toString()) + entry.amount;
                        } else {
                            newBalance = Double.parseDouble(mTVTotalBalance.getText().toString()) - entry.amount;
                        }
                        mTVTotalBalance.setText(Math.abs(newBalance) + "");

                    }
                })
                .setNegativeButton("NO", null)
                .show();
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

            if (result < 0) {
                mTVTotalBalance.setText((result * -1) + "");

                mTVTotalBalance.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }

        }
    }

}