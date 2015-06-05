package com.bikesh.scorpio.giventake.model;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bikesh on 6/5/2015. giveandtake@myfriendsgroup.com
 */
public class ParseHelper {

    boolean boolReturn=true;
    Map<String, String> mapData = new HashMap<String, String>();


    public boolean login(String username, String password){

        ParseUser.logInInBackground(username, password, new LogInCallback() {

            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    boolReturn =true;
                } else {
                    boolReturn=false;
                }
            }
        });

        return boolReturn;
    }


    public boolean createParseUserfromDB(Map DBUser) {





        /*

        ParseUser user = new ParseUser();
        user.put("name", DBUser.get("name").toString() );
        user.setUsername(DBUser.get("phone").toString() );
        user.setPassword("GNT"+DBUser.get("password").toString() ); // simly adding GNT
        user.setEmail(DBUser.get("email").toString() );


        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.

                    boolReturn = login("name", "password" );
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    boolReturn=false;
                }
            }
        });
        */
        return boolReturn;
    }


    /*
    public Map<String, String> getParseUserByPhone(String phoneNumber){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("phone",  phoneNumber );

        //query.whereEqualTo("username", "93Znhb4byBnDiIJm9LfD6Tg8E");
        //query.whereEqualTo("password", userpassword);


        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {

                if (e == null) {

                    mapData.put("name", results.get(0).getString("name"));
                    mapData.put("onlineid", results.get(0).getObjectId() );



                }

            }
        });

        return "";
    }
    */


}
