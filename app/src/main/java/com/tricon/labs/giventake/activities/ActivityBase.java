package com.tricon.labs.giventake.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tricon.labs.giventake.R;
import com.tricon.labs.giventake.database.DBHelper;
import com.tricon.labs.giventake.models.Country;

import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bikesh on 5/29/2015.
 */
public class ActivityBase extends AppCompatActivity {

    DrawerLayout Drawer;
    ActionBarDrawerToggle mDrawerToggle;

    Toolbar toolbar;

    View currentView;

    DBHelper myDb;

    ProgressDialog progressDialog;

    @Override
    public void setContentView(int layoutResID) {

        super.setContentView(R.layout.main_template);
        if(layoutResID==R.layout.main_template){
            return;
        }

        FrameLayout frame = (FrameLayout) findViewById(R.id.mainFrame);
        frame.removeAllViews();

        Context darkTheme = new ContextThemeWrapper(this, R.style.AppTheme);
        LayoutInflater inflater = (LayoutInflater) darkTheme.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        currentView=  inflater.inflate(layoutResID, null);
        //currentView=  LayoutInflater.from(getApplicationContext()).inflate(layoutResID, null);
        frame.addView(currentView);

        //setting up toolbar
        toolbar = (Toolbar) findViewById(R.id.widget_toolbar);
        setSupportActionBar(toolbar);


        setupDrawerLayout();
    }

    private void setupDrawerLayout() {

        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view

        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.openDrawer,R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        }; // Drawer Toggle Object Made

        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle

        mDrawerToggle.syncState();               // set the drawer toggle sync State


        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
                Toast.makeText(getApplicationContext(), menuItem.getTitle() + " pressed"+ menuItem.getItemId(), Toast.LENGTH_LONG).show();

                menuItem.setChecked(true);
                Drawer.closeDrawers();
/*
                if(menuItem.getTitle().equals("Home")){
                    startActivity(new Intent(ActivityBase.this, ActivityHome.class));
                    finish();
                }
                else if(menuItem.getTitle().equals("Lend & Borrow")){
                    startActivity(new Intent(ActivityBase.this, LendAndBorrow.class));
                    finish();
                }
                else if(menuItem.getTitle().equals("Personal Expense")){
                    startActivity(new Intent(ActivityBase.this, ActivityPersonalExpense.class));
                    finish();
                }
                else if(menuItem.getTitle().equals("Joint Group Expense")){
                    startActivity(new Intent(ActivityBase.this, ActivityJointExpense.class));
                    finish();
                }

                else if(menuItem.getTitle().equals("Settings")){
                    startActivity(new Intent(ActivityBase.this, ActivitySettings.class));
                    finish();
                }
                */

                return true;
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DBHelper(this);
    }





    //-------- Global functions --------//





    //// TODO: 6/8/2015  make format working
    public String formatDate(String dateString,String format){
        String dmy=null;
        try {

            Date myDate = new Date();
            System.out.println(myDate);

            SimpleDateFormat mdyFormat = new SimpleDateFormat("MM-dd-yyyy");
            SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dmyFormat = new SimpleDateFormat("dd-MM-yyyy");

            //SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            //String dateInString = "7-Jun-2013";
            Date date = ymdFormat.parse(dateString);

            // Format the date to Strings
            String mdy = mdyFormat.format(date);
            String ymd = ymdFormat.format(date);
            dmy = dmyFormat.format(date);

            // Results...
            //Log.i("daat n", "" + mdy);
            //Log.i("daat n", "" + ymd);
            //Log.i("daat n", "" + dmy);

            // Parse the Strings back to dates
            // Note, the formats don't "stick" with the Date value
            //Log.i("daat n", "" + mdyFormat.parse(mdy));
            //Log.i("daat n", "" + ymdFormat.parse(ymd));
            //Log.i("daat n", "" + ymdFormat.parse(dmy));
        } catch (ParseException exp) {
            exp.printStackTrace();
        }

        return dmy;
    }


    public void showProgress(){
        showProgress("Loading..");
    }

    public void showProgress(String msg){
        progressDialog = new ProgressDialog(ActivityBase.this);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void closeProgress(){
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDb != null) {
            myDb.close();
        }
    }




    public List<Country> getCountries() {
        try {
            InputStream is = getAssets().open("country_phone_code.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String countriesJsonString = new String(buffer, "UTF-8");

            JSONObject countriesJsonObject = new JSONObject(countriesJsonString);

            Gson gson = new GsonBuilder().create();
            Type listType = new TypeToken<ArrayList<Country>>() { }.getType();

            List<Country> countries = gson.fromJson(countriesJsonObject.getJSONArray("country").toString(), listType);
            return countries;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}