package com.tricon.labs.pepper.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.tricon.labs.pepper.R;
import com.tricon.labs.pepper.database.DBHelper;
import com.tricon.labs.pepper.models.Group;


public class FragmentGroupExpenseIndividualSummary extends Fragment {

    private DBHelper mDBHelper;

    private ProgressDialog mPDSaveData;

    private TextView mPerHead;
    private TextView mToala;

    Group mGroup;


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

        //setup views
        mPerHead = (TextView) rootView.findViewById(R.id.tv_per_head_amt);
        mToala = (TextView) rootView.findViewById(R.id.tv_total_amt);

        ListView LVMembers = (ListView) rootView.findViewById(R.id.lv_members);







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

        return rootView;

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

    }

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


}
