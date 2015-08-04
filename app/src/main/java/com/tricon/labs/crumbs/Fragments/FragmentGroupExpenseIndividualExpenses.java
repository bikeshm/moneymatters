package com.tricon.labs.crumbs.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tricon.labs.crumbs.R;
import com.tricon.labs.crumbs.activities.groupexpense.ActivityAddOrEditEntry;
import com.tricon.labs.crumbs.activities.groupexpense.ActivityGroupExpenseIndividual;
import com.tricon.labs.crumbs.adapters.AdapterGroupExpenseIndividualExpenses;
import com.tricon.labs.crumbs.database.DBHelper;
import com.tricon.labs.crumbs.interfaces.EntryClickedListener;
import com.tricon.labs.crumbs.interfaces.EntryLongClickedListener;
import com.tricon.labs.crumbs.models.Group;
import com.tricon.labs.crumbs.models.GroupExpensesEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class FragmentGroupExpenseIndividualExpenses extends Fragment {

    private DBHelper mDBHelper;


    private String mSelectedDate;

    Group mGroup;

    ActivityGroupExpenseIndividual mActivityInstance;

    private List<GroupExpensesEntry> mGroupExpensesEntries = new ArrayList<>();
    private AdapterGroupExpenseIndividualExpenses mAdapter;


    public static FragmentGroupExpenseIndividualExpenses getInstance(Group group) {
        Bundle args = new Bundle();
        args.putParcelable("GROUP", group);
        FragmentGroupExpenseIndividualExpenses expenses = new FragmentGroupExpenseIndividualExpenses();
        expenses.setArguments(args);
        return expenses;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_group_expense_individual_expenses, container, false);

        //getting group
        mGroup = getArguments().getParcelable("GROUP");

        //Activity instance
        mActivityInstance = ((ActivityGroupExpenseIndividual) getActivity());

        //get db instance
        mDBHelper = DBHelper.getInstance(getActivity());

        //setup views
        RecyclerView rvExpenses = (RecyclerView) rootView.findViewById(R.id.rv_expenses);

        //set recycler view layout manager
        rvExpenses.setHasFixedSize(true);
        rvExpenses.setLayoutManager(new LinearLayoutManager(getActivity()));

        //set adapter
        mAdapter = new AdapterGroupExpenseIndividualExpenses(mGroupExpensesEntries);
        rvExpenses.setAdapter(mAdapter);

        //get current date
        Calendar calenderInstance = Calendar.getInstance();
        String dayString = ((calenderInstance.get(Calendar.MONTH) + 1) < 10 ? "0" : "") + (calenderInstance.get(Calendar.MONTH) + 1);
        mSelectedDate = dayString + "-" + calenderInstance.get(Calendar.YEAR);


        return rootView;

    }



    @Override
    public void onResume() {
        super.onResume();
        new FetchEntryListTask().execute();
    }

    public void updateDateChange(String selectedDate) {

        this.mSelectedDate = selectedDate;
        new FetchEntryListTask().execute();
    }


    private class FetchEntryListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mGroupExpensesEntries.clear();

            if (mGroup.ismonthlytask == 0) {
                mGroupExpensesEntries.addAll( mDBHelper.getGroupEntriesList(mGroup.id) );
            } else {
                mGroupExpensesEntries.addAll( mDBHelper.getGroupEntriesList(mGroup.id, mSelectedDate));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mAdapter.notifyDataSetChanged();

        }
    }


    public void onEntryClicked(int position) {

        Intent intent = new Intent(getActivity(), ActivityAddOrEditEntry.class);
        intent.putExtra(ActivityAddOrEditEntry.INTENT_GROUP_EXPENSE_ENTRY, mGroupExpensesEntries.get(position));
        intent.putExtra(ActivityAddOrEditEntry.INTENT_GROUP_ID, mGroupExpensesEntries.get(position).groupId+"");
        startActivity(intent);

    }



            /*
        //setup views
        mTVSpent = (TextView) rootView.findViewById(R.id.tv_spent_amt);
        mTVGive = (TextView) rootView.findViewById(R.id.tv_give_amt);
        mTVGet = (TextView) rootView.findViewById(R.id.tv_get_amt);
        ListView lvPersons = (ListView) rootView.findViewById(R.id.lv_groups);

        //get db instance
        mDBHelper = new DBHelper(getActivity());

        //set list view adapter
        mAdapter = new AdapterGroupExpense(mGroupList);
        lvPersons.setAdapter(mAdapter);

        //set empty view
        lvPersons.setEmptyView(rootView.findViewById(android.R.id.empty));

        //set listeners
        lvPersons.setOnItemClickListener(new listItemClicked());


*/

    /*

    private class listItemClicked implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Group group= mGroupList.get(position);

            Intent i = new Intent(getActivity(), ActivityGroupExpenseIndividual.class);
            i.putExtra("GROUPID", ""+group.id);
            startActivity(i);

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        populateListViewFromDB();
    }


    private void populateListViewFromDB() {
        //Todo :- need to implement pagination
        mGroupList.clear();


        mGroupList= mDBHelper.getJointGroupsList();
        mGroupList.addAll(mGroupList);
        mAdapter.notifyDataSetChanged();

        Map<String, String> finalResult = mDBHelper.getAllGroupTotalSpendGiveGet();

        mTVSpent.setText(finalResult.get("amt_spent"));
        mTVGive.setText(finalResult.get("amt_toGive"));
        mTVGet.setText(finalResult.get("amt_toGet"));
    }

*/


}
