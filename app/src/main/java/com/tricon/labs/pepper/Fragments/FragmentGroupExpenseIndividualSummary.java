package com.tricon.labs.pepper.Fragments;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tricon.labs.pepper.R;
import com.tricon.labs.pepper.adapters.AdapterGroupExpenseIndividualSummaryMember;
import com.tricon.labs.pepper.adapters.AdapterLendAndBorrowEntryList;
import com.tricon.labs.pepper.database.DBHelper;
import com.tricon.labs.pepper.interfaces.EntryClickedListener;
import com.tricon.labs.pepper.interfaces.EntryLongClickedListener;
import com.tricon.labs.pepper.models.Group;
import com.tricon.labs.pepper.models.LendAndBorrowEntry;
import com.tricon.labs.pepper.models.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FragmentGroupExpenseIndividualSummary extends Fragment implements EntryClickedListener, EntryLongClickedListener {

    private DBHelper mDBHelper;

    private ProgressDialog mPDSaveData;

    private TextView mPerHead;
    private TextView mToala;

    Group mGroup;

    private List<Member> mMembers = new ArrayList<>();
    private AdapterGroupExpenseIndividualSummaryMember mAdapter;

    public static FragmentGroupExpenseIndividualSummary getInstance(Group group) {
        Bundle args = new Bundle();
        args.putParcelable("GROUP", group);
        FragmentGroupExpenseIndividualSummary summary = new FragmentGroupExpenseIndividualSummary();
        summary.setArguments(args);
        return summary;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_group_expense_individual_summary, container, false);

        mGroup = getArguments().getParcelable("GROUP");

        //get db instance
        mDBHelper = DBHelper.getInstance(getActivity());


        //setup views
        mPerHead = (TextView) rootView.findViewById(R.id.tv_per_head_amt);
        mToala = (TextView) rootView.findViewById(R.id.tv_total_amt);

        RecyclerView rvMembers = (RecyclerView) rootView.findViewById(R.id.rv_members);

        //set recycler view layout manager
        rvMembers.setHasFixedSize(true);
        rvMembers.setLayoutManager(new LinearLayoutManager(getActivity()));

        //set adapter
        mAdapter = new AdapterGroupExpenseIndividualSummaryMember(mMembers);
        rvMembers.setAdapter(mAdapter);


        getSummeryTotal();

        populateMemberSummery();

        return rootView;

    }

    private void populateMemberSummery() {


        Cursor cursor;
        if(mGroup.ismonthlytask == 0) {

            mDBHelper.getGroupMemberDetailsList(mGroup.id );

        }
        else {
            //mDBHelper.getGroupMemberDetailsList(mGroup.id , ((TextView)currentView.findViewById(R.id.dateChanger)).getText().toString()) ;
        }



    }

    private void getSummeryTotal() {


        Map<String, String> data = new HashMap<String, String>();

        if (mGroup.ismonthlytask == 0) {
            data = mDBHelper.getGroupEntryTotalPerHead(mGroup.id + "");
        } else {
            //data = mDBHelper.getGroupEntryTotalPerHead(groupId + "", ((TextView) currentView.findViewById(R.id.dateChanger)).getText().toString());
        }

        if(data.size()>0) {
            mToala.setText(data.get("total"));
            mPerHead.setText(data.get("perhead"));
        }

    }

    /*
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
    */


    @Override
    public void onEntryClicked(int position) {

    }

    @Override
    public void onEntryLongClicked(int position) {

    }
}



         /*
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

        Cursor cursor = myDb.getJointGroupbyId(groupId+"");

        if(isMonthlyRenewing == false) {
            data = myDb.getGroupEntryTotalPerHead(groupId + "");
        }
        else {
            data = myDb.getGroupEntryTotalPerHead(groupId+"", ((TextView)currentView.findViewById(R.id.dateChanger)).getText().toString()) ;
        }



        Cursor cursor;
        if(isMonthlyRenewing == false) {
            cursor = myDb.getGroupUsersData(groupId + "");
        }
        else {
            cursor = myDb.getGroupUsersData(groupId + "", ((TextView)currentView.findViewById(R.id.dateChanger)).getText().toString()) ;
        }

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

        mPerHead.setText(finalResult.get("amt_spent"));
        mToala.setText(finalResult.get("amt_toGive"));
        mTVGet.setText(finalResult.get("amt_toGet"));
    }


*/



