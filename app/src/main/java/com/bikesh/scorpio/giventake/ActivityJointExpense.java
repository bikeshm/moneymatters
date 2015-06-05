package com.bikesh.scorpio.giventake;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bikesh.scorpio.giventake.adapters.Adapter_CustomSimpleCursor;
import com.bikesh.scorpio.giventake.model.DBHelper;
import com.bikesh.scorpio.giventake.model.ParseHelper;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Map;

import static com.bikesh.scorpio.giventake.libraries.functions.getInternetType;


public class ActivityJointExpense extends ActivityBase {

    //View currentView;
    ListView listView;
    DBHelper myDb;

    ParseUser pUser;

    ParseHelper parseHelper;

    boolean registerUserFlag= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loading templet xml
        setContentView(R.layout.activity_joint_expense);


        listView = (ListView) currentView.findViewById(R.id.listViewFromDB);
        listView.setOnItemClickListener(new listItemClicked());

        ((ImageButton)currentView.findViewById(R.id.addUser)).setOnClickListener(new openAddnewGroup());


        myDb = new DBHelper(this);

        parseHelper=new ParseHelper();

        ParseUser.logOut();
        populateListViewFromDB();





    }

    private void populateListViewFromDB() {



        //chk inter net available
        if (!getInternetType(getApplicationContext()).equals("?")) {

            pUser = ParseUser.getCurrentUser();

            Log.i("track", "online");

            //check parse user loeged in
            if (pUser == null) { //user not loged in

                Map rootuser = myDb.getUser(1);

                Log.i("track", "user not signed in ");
                Log.i("track login", rootuser.get("phone").toString() + " " +  rootuser.get("password").toString());


                //loginin user
                ParseUser.logInInBackground(rootuser.get("phone").toString(),"GNT" + rootuser.get("password").toString(), new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {

                            //todo :- update local usder online id
                            if(registerUserFlag==true){

                            }


                            Log.i("track", "user loged in" + user.getObjectId());
                            // Hooray! The user is logged in.
                            populateListViewFromDB(); //recrussive for insert datata to local dm

                        } else {
                            // Signup failed. Look at the ParseException to see what happened.

                            Log.i("track", "user not exsist registring");
                            //create user and login
                            //parseHelper.createParseUserfromDB(myDb.getUser(1));
                            Map DBUser = myDb.getUser(1);
                            ParseUser newuser = new ParseUser();
                            newuser.put("name", DBUser.get("name").toString());
                            newuser.setUsername(DBUser.get("phone").toString());
                            newuser.setPassword("GNT" + DBUser.get("password").toString()); // simly adding GNT
                            newuser.setEmail(DBUser.get("email").toString());

                            Log.i("track registring", DBUser.get("phone").toString() + " " +  DBUser.get("password").toString());

                            //creating user in bg
                            newuser.signUpInBackground(new SignUpCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        // Hooray! Let them use the app now.
                                        Log.i("track", "user registerd" );


                                        //todo :- update local usder online id
                                        registerUserFlag=true;

                                        populateListViewFromDB(); //recrussive for sign in


                                    } else {
                                        Log.i("track", "error while registring user" + e.toString());
                                        // Sign up didn't succeed. Look at the ParseException
                                        // to figure out what went wrong
                                    }
                                }
                            });
                        }
                    }
                });

            }
            else{
                //user loged in
                Log.i("track", "user signed in  :)");
                // insert data to local db

                //then
                populateListViewFromDB_populate();
            }


        } else {
            Toast.makeText(getApplicationContext(), "Internet not available", Toast.LENGTH_LONG).show();
            populateListViewFromDB_populate();
        }

    }

    private void populateListViewFromDB_populate() {
        //if ineternet avialable user login
        // fetch data to local db
        //else if internet avilable user not loged in
        //return;




        //Todo :- 1. insted of listing all user just list the user who all are having amt balance
        //Todo :- need to implement pagination
        //Cursor cursor = myDb.getAllJointGroups();
        Cursor cursor =myDb.getAllJointGroupsWithData();



        listView.setAdapter(new Adapter_CustomSimpleCursor(this,        // Context
                R.layout.listview_item_template,    // Row layout template
                cursor                    // cursor (set of DB records to map)
        ));

        Map<String, String> finalResult = myDb.getAllGroupTotalSpendGiveGet();

        ((TextView)currentView.findViewById(R.id.totalspend)).setText(": "+finalResult.get("total"));
        ((TextView)currentView.findViewById(R.id.amtToGive)).setText(": " + finalResult.get("togive"));
        ((TextView)currentView.findViewById(R.id.amtToGet)).setText(": " + finalResult.get("toget"));


    }


    @Override
    public void onResume() {
        super.onResume();
        populateListViewFromDB();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ActivityJointExpense.this,ActivityHome.class));
    }


    private class listItemClicked implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(ActivityJointExpense.this, ActivityJointExpenseIndividual.class);
            i.putExtra("fromActivity", "ActivityJointExpense");
            i.putExtra("groupId", ""+id);
            startActivity(i);

            Toast.makeText(getApplicationContext(),"groupId : "+id, Toast.LENGTH_LONG).show();
        }
    }




    private class openAddnewGroup implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ActivityJointExpense.this, ActivityAddGroup.class);
            i.putExtra("fromActivity", "ActivityJointExpense");
            startActivity(i);

        }
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
}
