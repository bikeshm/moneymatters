package com.bikesh.scorpio.giventake;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bikesh.scorpio.giventake.adapters.DrawerDataAdapter;
import com.bikesh.scorpio.giventake.model.DBHelper;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.bikesh.scorpio.giventake.libraries.parsePhone.parsePhone;

/**
 * Created by bikesh on 5/29/2015.
 */
public class ActivityBase extends ActionBarActivity {

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

    //Activity activity;



    Toolbar toolbar;
    View currentView;

    DBHelper myDb;

    @Override
    public void setContentView(int layoutResID) {
        //super.setContentView(view);
        super.setContentView(R.layout.main_template);
        if(layoutResID==R.layout.main_template){
            return;
        }

        //loading home activity templet in to template frame
        FrameLayout frame = (FrameLayout) findViewById(R.id.mainFrame);
        frame.removeAllViews();
        Context darkTheme = new ContextThemeWrapper(this, R.style.AppTheme);
        LayoutInflater inflater = (LayoutInflater) darkTheme.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        currentView=  inflater.inflate(layoutResID, null);

        frame.addView(currentView);

        //setting up toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);


        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mAdapter = new DrawerDataAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager

        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view


        //mDrawerToggle = new ActionBarDrawerToggle(activity,Drawer,toolbar,R.string.openDrawer,R.string.closeDrawer){
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.openDrawer,R.string.closeDrawer){

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



        //final GestureDetector mGestureDetector = new GestureDetector(activity, new GestureDetector.SimpleOnGestureListener() {
        final GestureDetector mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

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
                    Toast.makeText(ActivityBase.this, "The Item Clicked is: " + rv.getChildPosition(child), Toast.LENGTH_SHORT).show();

                    Intent i;
                    switch(rv.getChildPosition(child)){
                        case 0:
                            //for login function execution
                            return false;
                        case 1:
                            Toast.makeText(ActivityBase.this,"Dashboard: "+rv.getChildPosition(child),Toast.LENGTH_SHORT).show();
                            i = new Intent(getApplicationContext(), ActivityHome.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            break;
                        case 2:
                            Toast.makeText(ActivityBase.this,"FragmentLendsAndBorrow: "+rv.getChildPosition(child),Toast.LENGTH_SHORT).show();
                            i = new Intent(getApplicationContext(), ActivityLendAndBorrow.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);

                            break;

                    }


                    //for login function execution
                    //if(rv.getChildPosition(child)==0){
                    //    return false;
                    //}

                    return true;

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DBHelper(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_joint_expense, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    public String registreUserFromContact(String phone, String name) {

        //Locale.getDefault().getCountry()
        String user_id=null;

        phone=parsePhone(phone);

        Log.i("Phone n", phone);

        Cursor cursorDbuser = myDb.getUserByPhone(phone);

        Log.i("Phone is exsist", cursorDbuser.getCount() + "");

        if(cursorDbuser.getCount()==0){

            Map<String, String> data = new HashMap<String, String>();
            data.put("name",  name );
            data.put("phone", phone );


            if (myDb.insertUser(data)==1) {
                cursorDbuser = myDb.getUserByPhone(phone);
                user_id= cursorDbuser.getString(cursorDbuser.getColumnIndex("_id"));
            }
        }
        else{
            //return user id
            user_id= cursorDbuser.getString(cursorDbuser.getColumnIndex("_id"));
        }

        return user_id;
    }










    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDb != null) {
            myDb.close();
        }
    }

}