package com.tricon.labs.giventake;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tricon.labs.giventake.database.DBHelper;

import java.util.HashMap;

public class ActivityAddCategory extends AppCompatActivity {

    private TextInputLayout mTILCategory;
    private EditText mETCategory;
    private EditText mETDescription;

    private DBHelper mDBHelper;

    private static int GROUP_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.widget_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_clear_mtrl_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportActionBar().setHomeButtonEnabled(true);

        //setup views
        mTILCategory = (TextInputLayout) findViewById(R.id.til_category);
        mETCategory = (EditText) findViewById(R.id.et_category);
        mETDescription = (EditText) findViewById(R.id.et_description);

        //get data from intent
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            GROUP_ID = extras.getInt("GROUPID", 0);
            mETCategory.setText(extras.getString("NAME", ""));
            mETDescription.setText(extras.getString("DESCRIPTION", ""));
        }

        //get database instance
        mDBHelper = DBHelper.getInstance(this);

        //set listeners
        mETCategory.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTILCategory.setError(null);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_add_category, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                saveData();
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDBHelper != null) {
            mDBHelper.close();
        }
    }

    private void saveData() {
        String category = mETCategory.getText().toString().trim();
        String description = mETDescription.getText().toString().trim();

        if (TextUtils.isEmpty(category)) {
            mTILCategory.setError("Category Required");
            return;
        }

        HashMap<String, String> data = new HashMap<>();

        data.put("name", category);
        data.put("description", description);

        if (GROUP_ID == 0) {
            if (mDBHelper.insertCollection(data) == 1) {
                Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
            }
        } else {
            data.put("_id", "1");
            if (mDBHelper.updateCollection(data) == 1) {
                Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
