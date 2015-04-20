package com.bikesh.scorpio.giventake;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Scorpio on 4/8/2015.
 */
class FragmentDashboard extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //retrieving data from Savedinstance when orientation changes
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_dashboard,container,false);
    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Saving data while orientation changes
        super.onSaveInstanceState(outState);
    }
}