package com.tricon.labs.crumbs.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tricon.labs.crumbs.R;
import com.tricon.labs.crumbs.activities.groupexpense.ActivityAddOrEditEntry;
import com.tricon.labs.crumbs.activities.groupexpense.ActivityGroupExpenseIndividual;
import com.tricon.labs.crumbs.adapters.AdapterGroupExpenseIndividualExpenses;
import com.tricon.labs.crumbs.database.DBHelper;
import com.tricon.labs.crumbs.models.Group;
import com.tricon.labs.crumbs.models.GroupExpensesEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


public class FragmentGroupExpenseIndividualExpenses extends Fragment {

    private DBHelper mDBHelper;


    private String mSelectedDate;

    Group mGroup;

    ActivityGroupExpenseIndividual mActivityInstance;

    private List<GroupExpensesEntry> mGroupExpensesEntriesList = new ArrayList<>();
    private AdapterGroupExpenseIndividualExpenses mAdapter;

    private ProgressDialog mProgressDialog;

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
        mAdapter = new AdapterGroupExpenseIndividualExpenses(mGroupExpensesEntriesList);
        rvExpenses.setAdapter(mAdapter);

        //get current date
        Calendar calenderInstance = Calendar.getInstance();
        String dayString = ((calenderInstance.get(Calendar.MONTH) + 1) < 10 ? "0" : "") + (calenderInstance.get(Calendar.MONTH) + 1);
        mSelectedDate = dayString + "-" + calenderInstance.get(Calendar.YEAR);


        //set progress dialog
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Saving Data...");

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
            mGroupExpensesEntriesList.clear();

            if (mGroup.ismonthlytask == 0) {
                mGroupExpensesEntriesList.addAll(mDBHelper.getGroupEntriesList(mGroup.id));
            } else {
                mGroupExpensesEntriesList.addAll(mDBHelper.getGroupEntriesList(mGroup.id, mSelectedDate));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mAdapter.notifyDataSetChanged();

        }
    }


    //this function will call from activity
    public void onEntryClicked(int position) {

        Intent intent = new Intent(getActivity(), ActivityAddOrEditEntry.class);
        intent.putExtra(ActivityAddOrEditEntry.INTENT_GROUP_EXPENSE_ENTRY, mGroupExpensesEntriesList.get(position));
        intent.putExtra(ActivityAddOrEditEntry.INTENT_GROUP_ID, mGroupExpensesEntriesList.get(position).groupId + "");
        startActivity(intent);

    }


    //this function will call from activity
    public void generatePopupMenu(final int position) {
        final CharSequence[] options = {"Edit", "Delete"};

        new AlertDialog.Builder(getActivity())
                .setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Delete")) {

                            new DeleteEntryTask(position).execute();

                        } else if (options[item].equals("Edit")) {
                            Intent intent = new Intent(getActivity(), ActivityAddOrEditEntry.class);
                            intent.putExtra(ActivityAddOrEditEntry.INTENT_GROUP_EXPENSE_ENTRY, mGroupExpensesEntriesList.get(position));
                            intent.putExtra(ActivityAddOrEditEntry.INTENT_GROUP_ID, mGroupExpensesEntriesList.get(position).groupId + "");
                            startActivity(intent);
                        }
                    }
                })
                .show();
    }


    private class DeleteEntryTask extends AsyncTask<Void, Void, Map<String, String>> {


        int position;

        public DeleteEntryTask(int position) {
            this.position= position;
        }


        @Override
        protected void onPreExecute() {

            mProgressDialog.setMessage("Deleting Data...");
            mProgressDialog.show();
        }

        @Override
        protected Map<String, String> doInBackground(Void... params) {
            mDBHelper.deleteGroupEntry(mGroupExpensesEntriesList.get(position).expenseId + "");
            return null;
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {

            super.onPostExecute(result);
            mGroupExpensesEntriesList.remove(position);
            mAdapter.notifyItemRemoved(position);

            mProgressDialog.dismiss();

            mActivityInstance.entryDeleted();
        }
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
