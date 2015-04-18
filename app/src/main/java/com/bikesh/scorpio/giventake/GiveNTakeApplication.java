package com.bikesh.scorpio.giventake;

import android.app.Activity;
import android.app.Application;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Scorpio on 4/18/2015.
 */
public class GiveNTakeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }



    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see

    String TITLES[] = {"Dashboard","lends & Borrow","Personal Expense","Group" };

    int ICONS[] = {R.drawable.img,R.drawable.img,R.drawable.img,R.drawable.img };

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    String NAME = "Bikesh M";
    String EMAIL = "bikeshm@gmail.com";
    int PROFILE = R.drawable.img;

    //private Toolbar toolbar;                              // Declaring the Toolbar Object


    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle

    Activity activity;

    FragmentManager fragmentManager;

    public void setupDrawer(View v, Activity currentActivity, FragmentManager fragment,Toolbar toolbar){


        activity=currentActivity;
        fragmentManager=fragment;




        mRecyclerView = (RecyclerView) v.findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mAdapter = new DrawerDataAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager

        Drawer = (DrawerLayout) v.findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(activity,Drawer,toolbar,R.string.openDrawer,R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle

        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State


        final GestureDetector mGestureDetector = new GestureDetector(activity, new GestureDetector.SimpleOnGestureListener() {

            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(),e.getY());



                if(child!=null && mGestureDetector.onTouchEvent(e)){
                    Drawer.closeDrawers();
                    Toast.makeText(activity, "The Item Clicked is: " + rv.getChildPosition(child), Toast.LENGTH_SHORT).show();

                    switch(rv.getChildPosition(child)){
                        case 0:
                            //for login function execution
                            return false;
                        case 1:

                            /*fragmentManager.beginTransaction()
                                    .replace(R.id.mainFrame, new FragmentDashboard())
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .addToBackStack(null)
                                    .commit();
                            */
                            Toast.makeText(activity,"Dashboard: "+rv.getChildPosition(child),Toast.LENGTH_SHORT).show();
                            break;
                        case 2:

                            /*fragmentManager.beginTransaction()
                                    .replace(R.id.mainFrame, new FragmentLendAndBorrow())
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .addToBackStack(null)
                                    .commit();
                            */
                            Toast.makeText(activity,"FragmentLendsAndBorrow: "+rv.getChildPosition(child),Toast.LENGTH_SHORT).show();

                            break;





                    }

                    /*
                    //for login function execution
                    if(rv.getChildPosition(child)==0){
                        return false;
                    }
                    */
                    return true;

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }
        });


        //default load dashboard
        /*fragmentManager.beginTransaction()
                .replace(R.id.mainFrame, new FragmentDashboard())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
        */
    }





}
