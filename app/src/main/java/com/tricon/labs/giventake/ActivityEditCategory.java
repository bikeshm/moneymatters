package com.tricon.labs.giventake;

import android.os.AsyncTask;
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
import java.util.HashSet;

public class ActivityEditCategory extends AppCompatActivity {

    private TextInputLayout mTILCategory;
    private EditText mETCategory;

    private HashSet<String> mCategories = new HashSet<>();

    private DBHelper mDBHelper;

    private String CATEGORY_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

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

        //get data from intent
        Bundle extras = getIntent().getExtras();
        CATEGORY_ID = extras.getString("CATEGORYID", "0");
        mETCategory.setText(extras.getString("CATEGORYNAME", ""));

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

        //get all categories from db
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mCategories = mDBHelper.getAllCategories();
                return null;
            }
        }.execute();
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
        final String category = mETCategory.getText().toString().trim();

        if (TextUtils.isEmpty(category)) {
            mTILCategory.setError("Category Required");
            mETCategory.setText("");
            return;
        }

        if (mCategories.contains(category.toLowerCase())) {
            Toast.makeText(this, "Category Already Exist", Toast.LENGTH_SHORT).show();
            return;
        }

        //save data in database
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                HashMap<String, String> data = new HashMap<>();
                data.put("name", category);
                data.put("description", "");
                data.put("_id", CATEGORY_ID);
                if (mDBHelper.updateCollection(data) > 0) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
