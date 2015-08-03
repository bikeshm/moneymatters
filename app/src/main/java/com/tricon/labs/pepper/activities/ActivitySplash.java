package com.tricon.labs.pepper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.tricon.labs.pepper.R;
import com.tricon.labs.pepper.database.DBHelper;


public class ActivitySplash extends AppCompatActivity {

    private DBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //get db instance
        mDBHelper =DBHelper.getInstance(ActivitySplash.this);

        new Handler().postDelayed(new Runnable() {

            // Using handler with postDelayed called runnable run method
            @Override
            public void run() {

                Intent i = new Intent(ActivitySplash.this, ActivityHome.class);

                // if first time opening the app , need to register root user (me user)
                if (mDBHelper.getNumRowsUsertable() == 0) {

                    //registering root user
                    //Map<String, String> data = new HashMap<String, String>();
                    //data.put("_id", "1");
                    //data.put("name", "Me");
                    //mDBHelper.insertUser(data);

                    //register user
                    i = new Intent(ActivitySplash.this, ActivityRegisterUser.class);


                }
                startActivity(i);
                finish();
            }
        }, 2 * 1000); // wait for 3 seconds

    } // onreate end

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}

