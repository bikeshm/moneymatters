package com.tricon.labs.giventake;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tricon.labs.giventake.adapters.AdapterContactList;
import com.tricon.labs.giventake.database.DBHelper;
import com.tricon.labs.giventake.models.Contact;
import com.tricon.labs.giventake.models.LendAndBorrowEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.tricon.labs.giventake.libraries.functions.getContactList;

public class ActivityLendAndBorrowAddEntry extends AppCompatActivity {

    private DBHelper mDBHelper;

    private AdapterContactList mAdapter;

    private LendAndBorrowEntry mLendAndBorrowEntry;

    private List<Contact> mContacts;

    private Contact mSelectedContact = null;

    private Button mBtnDate;
    private AutoCompleteTextView mACTVUserName;
    private EditText mETAmount;
    private EditText mETDescription;
    private RadioGroup mRadioGroup;
    private RadioButton mRBLend;

    private ProgressDialog mPDSaveData;

    private boolean isEditEntry = false;

    private static final int CREATE_ENTRY = 1;
    private static final int EDIT_ENTRY = 2;
    private static int ENTRY_TYPE = CREATE_ENTRY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_and_borrow_add_entry);

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

        mDBHelper = DBHelper.getInstance(this);

        mBtnDate = (Button) findViewById(R.id.btn_date);
        mRadioGroup = (RadioGroup) findViewById(R.id.rg_lend_and_borrow);
        mRBLend = (RadioButton) findViewById(R.id.rb_lend);

        TextInputLayout tilDescription = (TextInputLayout) findViewById(R.id.til_description);
        mACTVUserName = (AutoCompleteTextView) findViewById(R.id.actv_user_name);
        mETAmount = (EditText) findViewById(R.id.et_amount);
        mETDescription = (EditText) findViewById(R.id.et_description);

        //set autocomplete threshold
        mACTVUserName.setThreshold(1);
        new FetchUserFromContactTask().execute();

        //get data from intent
        Bundle extras = getIntent().getExtras();

        //if extras is not null, that means user is either creating entry under specific category or editing previous entry.
        if (extras != null) {
            mLendAndBorrowEntry = extras.getParcelable("ENTRY");
            isEditEntry = extras.getBoolean("EDITENTRY", false);

            //if creating Entry For a Specific Category then disable autocomplete text view
            mACTVUserName.setEnabled(isEditEntry);

            mACTVUserName.setText(mLendAndBorrowEntry.toUserName);

            if (isEditEntry) {
                ENTRY_TYPE = EDIT_ENTRY;
            }
        } else {
            mLendAndBorrowEntry = new LendAndBorrowEntry();


        }

        //initial date values
        SimpleDateFormat dmy = new SimpleDateFormat("dd-MM-yyyy");
        String dmyDate = dmy.format(new Date());

        mBtnDate.setText(dmyDate);

        mBtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        mACTVUserName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedContact = mAdapter.getItem(position);
                mACTVUserName.setText(mSelectedContact.name);
            }
        });

        mACTVUserName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (mSelectedContact != null && mSelectedContact.name.equals(mACTVUserName.getText().toString()) != true) {
                    mSelectedContact = null;
                }
                return false;
            }
        });

        //set progress dialog
        mPDSaveData = new ProgressDialog(this);
        mPDSaveData.setCancelable(false);
        mPDSaveData.setIndeterminate(true);
        mPDSaveData.setMessage("Saving Data...");

    }


    private class FetchUserFromContactTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            //getting user from contact
            mContacts = getContactList(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mAdapter = new AdapterContactList(ActivityLendAndBorrowAddEntry.this, mContacts);
            mACTVUserName.setAdapter(mAdapter);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_lend_and_borrow_add_entry, menu);
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

        TextInputLayout tilUserName = (TextInputLayout) findViewById(R.id.til_user_name);
        TextInputLayout tilAmount = (TextInputLayout) findViewById(R.id.til_amount);

        String userName = mACTVUserName.getText().toString().trim();

        // setting toUser from ActivityLendAndBorrowIndividual add new entry otherwise it will be -1
        int userId = mLendAndBorrowEntry.toUser;

        if (TextUtils.isEmpty(userName)) {
            tilUserName.setError("Nmae required");
            return;
        } else {
            tilUserName.setError(null);
        }

        if (TextUtils.isEmpty(mETAmount.getText().toString())) {
            tilAmount.setError("Amount required");
            return;
        } else {
            tilAmount.setError(null);
        }

        if (mSelectedContact == null && userId == -1 ) {
            tilUserName.setError("Invalid contact");
            return;
        } else {
            tilUserName.setError(null);
        }

        //if user from home screen it will be -1
        if(userId == -1) {
            userId = (int) mDBHelper.registerUserFromContact(mSelectedContact.phone, mSelectedContact.name);
        }

        if (userId == 0) {
            tilUserName.setError("invalid name");
            return;
        } else {
            tilUserName.setError(null);
        }

        saveEntry(userId);
    }


    private void saveEntry(int userId) {
        String newDate = mBtnDate.getText().toString().trim();

        int newFromUser, newToUser;

        if (mRadioGroup.getCheckedRadioButtonId() == mRBLend.getId()) {
            newFromUser = 1;
            newToUser = userId;
        } else {
            newFromUser = userId;
            newToUser = 1;
        }

        double newAmount = Double.parseDouble(mETAmount.getText().toString().trim());
        String newDescription = mETDescription.getText().toString().trim();

        //if there is any change in any field then save the entry otherwise finish the activity
        if ((mLendAndBorrowEntry.fromUser != newFromUser)
                || (mLendAndBorrowEntry.toUser != newToUser)
                || (!mLendAndBorrowEntry.date.equals(newDate))
                || (mLendAndBorrowEntry.amount != newAmount)
                || (!mLendAndBorrowEntry.description.equals(newDescription))) {

            new SaveEntryTask(newDate, newFromUser, newToUser, newAmount, newDescription).execute();

        } else {
            finish();
        }
    }


    private class SaveEntryTask extends AsyncTask<Void, Void, Boolean> {

        private String mDate;
        int mFromUser;
        int mToUser;
        private double mAmount;
        private String mDescription;


        SaveEntryTask(String date, int fromUser, int toUser, double amount, String description) {

            this.mDate = date;
            this.mFromUser = fromUser;
            this.mToUser = toUser;
            this.mAmount = amount;
            this.mDescription = description;
        }

        @Override
        protected void onPreExecute() {
            mPDSaveData.setMessage("Saving Entry");
            mPDSaveData.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Map<String, String> entryData = new HashMap<>();

            //convert date into "yyyy MM dd" format
            SimpleDateFormat localDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            try {
                mDate = dbDateFormat.format(localDateFormat.parse(mDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            entryData.put("created_date", mDate);
            entryData.put("description", mDescription);
            entryData.put("amt", mAmount + "");
            entryData.put("from_user", mFromUser + "");
            entryData.put("to_user", mToUser + "");

            if (ENTRY_TYPE == CREATE_ENTRY) {

                return mDBHelper.insertLendAndBorrowEntry(entryData) > 0;

            } else {

                entryData.put("_id", mLendAndBorrowEntry.entryId + "");

                return mDBHelper.updateLendAndBorrowEntry(entryData) > 0;
            }

        }

        @Override
        protected void onPostExecute(Boolean success) {
            mPDSaveData.dismiss();
            if (success) {
                Toast.makeText(ActivityLendAndBorrowAddEntry.this,
                        "Data saved successfully",
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ActivityLendAndBorrowAddEntry.this,
                        "Something went wrong while saving entry.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}