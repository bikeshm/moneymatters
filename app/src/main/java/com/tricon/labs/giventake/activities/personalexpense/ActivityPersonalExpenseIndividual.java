package com.tricon.labs.giventake.activities.personalexpense;

/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SimpleDateFormat dmy = new SimpleDateFormat("MM-yyyy");
        String cDate = dmy.format(new Date());

        SimpleDateFormat dbmy = new SimpleDateFormat("yyyy-MM");
        String cdbDate = dbmy.format(new Date());

        TextView dateChanger = (TextView) currentView.findViewById(R.id.dateChanger);
        TextView dateChangerForDb = (TextView) currentView.findViewById(R.id.dateChangerForDb);

        dateChanger.setOnClickListener(new CustomDatePicker(ActivityPersonalExpenseIndividual.this, dateChanger, dateChangerForDb, true));

        dateChanger.setText(cDate);
        dateChangerForDb.setText(cdbDate);

        dateChangerForDb.addTextChangedListener(new dateChange());
    }

    private class dateChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            Toast.makeText(getApplicationContext(), "" + ((TextView) currentView.findViewById(R.id.dateChangerForDb)).getText(), Toast.LENGTH_LONG).show();

            Cursor entrys = myDb.getPersonalExpense(colId, ((TextView) currentView.findViewById(R.id.dateChanger)).getText().toString());

            generateTable(entrys);

        }
    }
}*/

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tricon.labs.giventake.R;
import com.tricon.labs.giventake.adapters.AdapterPersonalExpenseEntryList;
import com.tricon.labs.giventake.database.DBHelper;
import com.tricon.labs.giventake.interfaces.EntryClickedListener;
import com.tricon.labs.giventake.interfaces.EntryLongClickedListener;
import com.tricon.labs.giventake.libraries.MonthYearPicker;
import com.tricon.labs.giventake.models.PersonalExpenseEntry;

import java.util.ArrayList;
import java.util.List;

public class ActivityPersonalExpenseIndividual extends AppCompatActivity implements EntryClickedListener, EntryLongClickedListener {

    private Button mBtnDate;
    private TextView mTVMonthlyTotal;

    private List<PersonalExpenseEntry> mEntries = new ArrayList<>();
    private AdapterPersonalExpenseEntryList mAdapter;

    private DBHelper mDBHelper;

    private int mCategoryId;
    private String mCategoryName;
    private String mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_expense_individual);

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

        //setup views
        mBtnDate = (Button) findViewById(R.id.btn_date);
        mTVMonthlyTotal = (TextView) findViewById(R.id.tv_monthly_total);
        RecyclerView rvEntries = (RecyclerView) findViewById(R.id.rv_entries);

        //get db instance
        mDBHelper = DBHelper.getInstance(this);

        //get extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCategoryId = extras.getInt("CATEGORYID", -1);
            mCategoryName = extras.getString("CATEGORYNAME", "");
            mDate = extras.getString("SELECTEDDATE", "");
            mBtnDate.setText(extras.getString("BTNDATE", ""));
        }

        //set actionbar title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(mCategoryName);
        }

        //set recycler view layout manager
        rvEntries.setHasFixedSize(true);
        rvEntries.setLayoutManager(new LinearLayoutManager(this));

        //set adapter
        mAdapter = new AdapterPersonalExpenseEntryList(mEntries);
        rvEntries.setAdapter(mAdapter);

        //set listeners
        findViewById(R.id.btn_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonalExpenseEntry entry = new PersonalExpenseEntry();
                entry.category = mCategoryName;
                Intent intent = new Intent(ActivityPersonalExpenseIndividual.this, ActivityPersonalExpenseAddEntry.class);
                intent.putExtra("ENTRY", entry);
                intent.putExtra("SPECIFICENTRY", true);
                startActivity(intent);
            }
        });
        mBtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] monthAndYear = mDate.split("-");
                openDatePicker(Integer.parseInt(monthAndYear[0].trim()) - 1, Integer.parseInt(monthAndYear[1].trim()));
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
    protected void onDestroy() {
        super.onDestroy();
        if (mDBHelper != null) {
            mDBHelper.close();
        }
    }

    @Override
    public void onEntryClicked(int position) {
        Intent intent = new Intent(ActivityPersonalExpenseIndividual.this, ActivityPersonalExpenseAddEntry.class);
        intent.putExtra("ENTRY", mEntries.get(position));
        startActivity(intent);
    }

    @Override
    public void onEntryLongClicked(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Entry")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEntryFromDB(position);
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }

    private void openDatePicker(int selectedMonth, int selectedYear) {
        final MonthYearPicker monthPicker = new MonthYearPicker(this);
        monthPicker.build(selectedMonth, selectedYear, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBtnDate.setText(monthPicker.getSelectedMonthName() + "-" + monthPicker.getSelectedYear());

                String dateString = ((monthPicker.getSelectedMonth() + 1) < 10 ? "0" : "") + (monthPicker.getSelectedMonth() + 1);
                mDate = dateString + "-" + monthPicker.getSelectedYear();

                //fetch entries
                new FetchEntriesTask().execute();
            }
        }, null);
        monthPicker.show();
    }

    private void deleteEntryFromDB(int position) {
        if (mDBHelper.deletePersonalExpense(mEntries.get(position).entryId + "") > 0) {
            PersonalExpenseEntry deletedEntry = mEntries.remove(position);
            Toast.makeText(this, "Expense Entry Deleted", Toast.LENGTH_SHORT).show();
            mAdapter.notifyItemRemoved(position);

            //subtract expense of deleted entry from total expense and set that in "Total Expense Text View"
            Double newExpense = Double.parseDouble(mTVMonthlyTotal.getText().toString()) - deletedEntry.amount;
            mTVMonthlyTotal.setText(newExpense + "");
        } else {
            Toast.makeText(this, "Something went wrong while deleting expense", Toast.LENGTH_SHORT).show();
        }
    }

    private class FetchEntriesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mEntries.clear();
            mEntries.addAll(mDBHelper.getPersonalExpense(mCategoryId, mCategoryName, mDate));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mAdapter.notifyDataSetChanged();
            new FetchMonthlyTotalAmount().execute();
        }
    }

    private class FetchMonthlyTotalAmount extends AsyncTask<Void, Void, Double> {

        @Override
        protected Double doInBackground(Void... params) {
            return mDBHelper.getMonthTotalOfPersonalExpenseIndividual(mCategoryId, mDate);
        }

        @Override
        protected void onPostExecute(Double result) {
            super.onPostExecute(result);
            mTVMonthlyTotal.setText(result + "");
        }
    }
}
