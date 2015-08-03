package com.tricon.labs.pepper.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tricon.labs.pepper.R;
import com.tricon.labs.pepper.adapters.AdapterGroupExpense;
import com.tricon.labs.pepper.database.DBHelper;
import com.tricon.labs.pepper.models.Group;

import java.util.ArrayList;
import java.util.List;


public class FragmentGroupExpenseIndividualExpenses extends Fragment {

    private DBHelper mDBHelper;

    private TextView mTVSpent;
    private TextView mTVGive;
    private TextView mTVGet;

    private List<Group> mGroupList = new ArrayList<>();


    private AdapterGroupExpense mAdapter;


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
        //return super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_group_expense_individual_expenses,container,false);

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
        return rootView;

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

        mTVSpent.setText(finalResult.get("amt_spent"));
        mTVGive.setText(finalResult.get("amt_toGive"));
        mTVGet.setText(finalResult.get("amt_toGet"));
    }

*/


}
