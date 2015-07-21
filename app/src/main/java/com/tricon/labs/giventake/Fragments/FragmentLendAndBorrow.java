package com.tricon.labs.giventake.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tricon.labs.giventake.ActivityLendAndBorrowIndividual;
import com.tricon.labs.giventake.R;
import com.tricon.labs.giventake.adapters.AdapterLendAndBorrow;
import com.tricon.labs.giventake.database.DBHelper;
import com.tricon.labs.giventake.models.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FragmentLendAndBorrow extends Fragment {

    private TextView mTVGive;
    private TextView mTVGet;

    private DBHelper mDBHelper;
    private List<Person> mPersonList = new ArrayList<>();
    private AdapterLendAndBorrow mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setup views
        View mRootView = inflater.inflate(R.layout.fragment_lend_and_borrow, container, false);
        mTVGive = (TextView) mRootView.findViewById(R.id.amt_togive);
        mTVGet = (TextView) mRootView.findViewById(R.id.amt_toget);
        ListView mLVPersons = (ListView) mRootView.findViewById(R.id.lv_persons);

        //get db instance
        mDBHelper = new DBHelper(getActivity());

        //set list view adapter
        mAdapter = new AdapterLendAndBorrow(mPersonList);
        mLVPersons.setAdapter(mAdapter);

        //set listeners
        mLVPersons.setOnItemClickListener(new listItemClicked());

        return mRootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        populateListViewFromDB();
    }

    private class listItemClicked implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Person Person = mPersonList.get(position);
            Intent i = new Intent(getActivity(), ActivityLendAndBorrowIndividual.class);
            i.putExtra("ID", "" + Person.id);
            i.putExtra("NAME", "" + Person.name);
            startActivity(i);
        }
    }

    private void populateListViewFromDB() {
        //Todo :- need to implement pagination
        mPersonList.clear();
        mPersonList.addAll(mDBHelper.getLendAndBorrowList());
        mAdapter.notifyDataSetChanged();

        Map<String, String> finalResult = mDBHelper.getFinalResult();
        mTVGive.setText(finalResult.get("amt_toGive"));
        mTVGet.setText(finalResult.get("amt_toGet"));
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Saving data while orientation changes
        super.onSaveInstanceState(outState);
    }
}
