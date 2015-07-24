package com.tricon.labs.pepper.activities.personalexpense;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.tricon.labs.pepper.R;
import com.tricon.labs.pepper.adapters.AdapterCategoryList;
import com.tricon.labs.pepper.database.DBHelper;
import com.tricon.labs.pepper.models.PersonalExpenseEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

public class ActivityPersonalExpenseAddEntry extends AppCompatActivity {

    private Button mBtnDate;
    private TextInputLayout mTILCategory;
    private TextInputLayout mTILAmount;
    private TextInputLayout mTILDescription;
    private AutoCompleteTextView mACTVCategory;
    private EditText mETAmount;
    private EditText mETDescription;
    private ProgressDialog mPDSaveData;

    //set to store unique categories for autocomplete text view
    private TreeSet<String> mCategories;

    private DBHelper mDBHelper;

    private PersonalExpenseEntry mPersonalExpenseEntry;

    private boolean creatingEntryForSpecificCategory = false;
    private static final int CREATE_ENTRY = 1;
    private static final int EDIT_ENTRY = 2;
    private static int ENTRY_TYPE = CREATE_ENTRY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_expense_add_entry);

        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top);

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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }

        //setup views
        mBtnDate = (Button) findViewById(R.id.btn_date);
        mTILCategory = (TextInputLayout) findViewById(R.id.til_category);
        mTILAmount = (TextInputLayout) findViewById(R.id.til_amount);
        mTILDescription = (TextInputLayout) findViewById(R.id.til_description);
        mACTVCategory = (AutoCompleteTextView) findViewById(R.id.actv_category);
        mETAmount = (EditText) findViewById(R.id.et_amount);
        mETDescription = (EditText) findViewById(R.id.et_description);

        //set progress dialog
        mPDSaveData = new ProgressDialog(this);
        mPDSaveData.setCancelable(false);
        mPDSaveData.setIndeterminate(true);
        mPDSaveData.setMessage("Saving Data...");

        //set autocomplete threshold
        mACTVCategory.setThreshold(1);

        //get data from intent
        Bundle extras = getIntent().getExtras();
        ENTRY_TYPE = CREATE_ENTRY;

        //if extras is not null, that means user is either creating entry under specific category or editing previous entry.
        if (extras != null) {
            mPersonalExpenseEntry = extras.getParcelable("ENTRY");
            creatingEntryForSpecificCategory = extras.getBoolean("SPECIFICENTRY", false);

            //if creating Entry For a Specific Category then disable autocomplete text view
            mACTVCategory.setEnabled(!creatingEntryForSpecificCategory);

            if (!creatingEntryForSpecificCategory) {
                ENTRY_TYPE = EDIT_ENTRY;
                getSupportActionBar().setTitle("Edit Personal Expense");
            }
        } else {
            mPersonalExpenseEntry = new PersonalExpenseEntry();
        }

        //set data in views
        mBtnDate.setText(mPersonalExpenseEntry.date);
        mACTVCategory.setText(mPersonalExpenseEntry.category);
        mACTVCategory.setSelection(mPersonalExpenseEntry.category.length());
        if (mPersonalExpenseEntry.amount == 0) {
            mETAmount.setText("");
        } else {
            mETAmount.setText(mPersonalExpenseEntry.amount + "");
        }
        mETDescription.setText(mPersonalExpenseEntry.description);

        //get database instance
        mDBHelper = DBHelper.getInstance(this);

        if (!creatingEntryForSpecificCategory) {
            //fetch unique categories from database
            new FetchCategoriesTask().execute();
        }

        //bind listeners
        mBtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        if (!creatingEntryForSpecificCategory) {
            mACTVCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String category = parent.getAdapter().getItem(position).toString();
                    mACTVCategory.setText(category);
                    mACTVCategory.setSelection(category.length());
                }
            });
            /*mACTVCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        showAutoCompleteDropDown();
                    }
                }
            });*/
            mACTVCategory.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mTILCategory.setError(null);
                    return false;
                }
            });
        }

        mETAmount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTILAmount.setError(null);
                return false;
            }
        });

        mETDescription.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTILDescription.setError(null);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_personal_expense_add_entry, menu);
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_top, R.anim.slide_out_to_bottom);
    }

    public void openDatePicker() {
        //To show current date in the datepicker
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                selectedmonth++;
                String actualMonth = "" + selectedmonth;
                if (selectedmonth < 10) {
                    actualMonth = "0" + actualMonth;
                }

                String actualDay = "" + selectedday;
                if (selectedday < 10) {
                    actualDay = "0" + actualDay;
                }

                mBtnDate.setText(actualDay + "-" + actualMonth + "-" + selectedyear);
            }
        }, year, month, day).show();
    }


    private void saveData() {
        String newCategory = mACTVCategory.getText().toString().trim();

        if (TextUtils.isEmpty(newCategory)) {
            mTILCategory.setError("Category Required");
            mACTVCategory.setText("");
            return;
        }

        if (TextUtils.isEmpty(mETAmount.getText().toString().trim())) {
            mTILAmount.setError("Amount Required");
            mETAmount.setText("");
            return;
        }

        if (TextUtils.isEmpty(mETDescription.getText().toString().trim())) {
            mTILDescription.setError("Description Required");
            mETDescription.setText("");
            return;
        }

        // if user is creating entry for specific category then there will not be a new category. so no need to save category.
        // if category is not present in database then create new category in database and then save entry, otherwise save entry
        if (!creatingEntryForSpecificCategory && !mCategories.contains(newCategory.toLowerCase())) {
            new SaveCategoryTask(newCategory).execute();
        } else {
            saveEntry(newCategory);
        }
    }

    private void saveEntry(String newCategory) {
        String newDate = mBtnDate.getText().toString().trim();
        double newAmount = Double.parseDouble(mETAmount.getText().toString().trim());
        String newDescription = mETDescription.getText().toString().trim();

        //if there is any change in any field then save the entry otherwise finish the activity
        if ((!mPersonalExpenseEntry.category.equals(newCategory))
                || (!mPersonalExpenseEntry.date.equals(newDate))
                || (mPersonalExpenseEntry.amount != newAmount)
                || (!mPersonalExpenseEntry.description.equals(newDescription))) {

            new SaveEntryTask(newCategory, newDescription, newDate, newAmount).execute();

        } else {
            finish();
        }
    }

    /*private void showAutoCompleteDropDown() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mACTVCategory.showDropDown();
            }
        }, 500);
    }*/

    private class FetchCategoriesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mCategories = mDBHelper.getAllCategories();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mACTVCategory.setAdapter(new AdapterCategoryList(ActivityPersonalExpenseAddEntry.this, mCategories));
        }
    }

    private class SaveCategoryTask extends AsyncTask<Void, Void, Boolean> {

        private String mCategory;

        SaveCategoryTask(String category) {
            this.mCategory = category;
        }

        @Override
        protected void onPreExecute() {
            mPDSaveData.setMessage("Saving Category");
            mPDSaveData.show();
        }

        @Override
        protected Boolean doInBackground(Void... category) {
            Map<String, String> categoryData = new HashMap<>();
            categoryData.put("name", mCategory);
            categoryData.put("description", "");

            return mDBHelper.insertCollection(categoryData) > 0;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mPDSaveData.dismiss();
            if (success) {
                //put category in category map after saving in db
                mCategories.add(mCategory);

                //start save entry task
                saveEntry(mCategory);
            } else {
                Toast.makeText(ActivityPersonalExpenseAddEntry.this,
                        "Something went wrong while saving category.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class SaveEntryTask extends AsyncTask<Void, Void, Boolean> {

        private String mCategoryName;
        private String mDescription;
        private String mDate;
        private double mAmount;

        SaveEntryTask(String categoryName, String description, String date, double amount) {
            this.mCategoryName = categoryName;
            this.mDescription = description;
            this.mDate = date;
            this.mAmount = amount;
        }

        @Override
        protected void onPreExecute() {
            mPDSaveData.setMessage("Saving Entry");
            mPDSaveData.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Map<String, String> entryData = new HashMap<>();

            //get category id from category name
            int categoryId = mDBHelper.getCategoryIdFromCategoryName(mCategoryName);

            if (categoryId != -1) {
                //convert date into "yyyy MM dd" format
                SimpleDateFormat localDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                try {
                    mDate = dbDateFormat.format(localDateFormat.parse(mDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                entryData.put("collection_id", categoryId + "");
                entryData.put("created_date", mDate);
                entryData.put("description", mDescription);
                entryData.put("amt", mAmount + "");

                if (ENTRY_TYPE == CREATE_ENTRY) {
                    return mDBHelper.insertPersonalExpense(entryData) > 0;
                } else {
                    entryData.put("_id", mPersonalExpenseEntry.entryId + "");
                    return mDBHelper.updatePersonalExpense(entryData) > 0;
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mPDSaveData.dismiss();
            if (success) {
                Toast.makeText(ActivityPersonalExpenseAddEntry.this,
                        "Data saved successfully",
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ActivityPersonalExpenseAddEntry.this,
                        "Something went wrong while saving entry.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}