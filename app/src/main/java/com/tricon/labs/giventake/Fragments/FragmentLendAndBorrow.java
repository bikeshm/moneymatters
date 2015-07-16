package com.tricon.labs.giventake.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tricon.labs.giventake.R;


public class FragmentLendAndBorrow extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //retrieving data from Savedinstance when orientation changes
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View v =  inflater.inflate(R.layout.fragment_lend_and_borrow,container,false);
        return v;

    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Saving data while orientation changes
        super.onSaveInstanceState(outState);
    }
}

/*
public class ActivityLendAndBorrow extends ActivityBase {

    DBHelper mDBHelper;

    ListView mLVCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_lend_and_borrow);

        //setting up toolbar
        //Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //setSupportActionBar(toolbar);

        mLVCategories = (ListView) currentView.findViewById(R.id.listViewFromDB);

        mLVCategories.setOnItemClickListener(new listItemClicked());

        mDBHelper = new DBHelper(this);
        populateListViewFromDB();

        ((FloatingActionButton) currentView.findViewById(R.id.addEntry)).setOnClickListener(new openAddnewEntrry());
        //((ImageButton)currentView.findViewById(R.id.addEntry)).setOnClickListener(new openAddnewEntrry());

    }

    @Override
    public void onResume() {
        super.onResume();

        populateListViewFromDB();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityLendAndBorrow.this,ActivityHome.class));
    }

    private class listItemClicked implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //id == table id
            Intent i = new Intent(ActivityLendAndBorrow.this, ActivityLendAndBorrowIndividual.class);
            i.putExtra("fromActivity", "ActivityLendAndBorrow");
            i.putExtra("fromUserId", ""+id);
            i.putExtra("userName", ""+ ((TextView) view.findViewById(R.id.item_name)).getText().toString() );
            startActivity(i);
            //Toast.makeText(ActivityLendAndBorrow.this, "Id "+ id , Toast.LENGTH_LONG).show();
        }
    }





    private class openAddnewEntrry implements View.OnClickListener {
        @Override
        public void onClick(View mRootView) {

            Intent i = new Intent(ActivityLendAndBorrow.this, ActivityLendAndBorrowAddEntry.class);
            i.putExtra("fromActivity", "ActivityLendAndBorrow");
            startActivity(i);

        }
    }




    private void populateListViewFromDB() {

        //Todo :- 1. insted of listing all user just list the user who all are having amt balance
        //Todo :- need to implement pagination

         Cursor cursor = mDBHelper.getLendAndBorrowList();
        //Cursor cursor = mDBHelper.getAllUsers();



        mLVCategories.setAdapter(new Adapter_CustomSimpleCursor(this,		// Context
                R.layout.listview_item_template,	// Row layout template
                cursor					// cursor (set of DB records to map)
        ));



        Map<String, String> finalResult = mDBHelper.getFinalResult();

        ((TextView)currentView.findViewById(R.id.amt_togive)).setText(": "+finalResult.get("amt_toGive"));
        ((TextView)currentView.findViewById(R.id.amt_toget)).setText(": " + finalResult.get("amt_toGet"));


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDBHelper != null) {
            mDBHelper.close();
        }
    }

}
*/