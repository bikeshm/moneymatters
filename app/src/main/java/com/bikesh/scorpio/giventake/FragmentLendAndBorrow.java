package com.bikesh.scorpio.giventake;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Scorpio on 4/8/2015.
 */
public class FragmentLendAndBorrow extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //retrieving data from Savedinstance when orientation changes
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_lend_and_borrow, container, false);

        ListView listView = (ListView) v.findViewById(R.id.listViewFromDB);

        String[] values = new String[] { "Android List View",
                 "Adapter implementation",
                 "Simple List View In Android",
                 "Create List View Android",
                 "Android Example",
                 "List View Source Code",
                 "List View Array Adapter",
                 "Android Example List View",

                "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View",
                 };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, values);

        listView.setAdapter(adapter);

        return v;
    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Saving data while orientation changes
        super.onSaveInstanceState(outState);
    }
}