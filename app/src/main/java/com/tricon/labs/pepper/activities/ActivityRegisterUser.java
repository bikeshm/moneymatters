package com.tricon.labs.pepper.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.tricon.labs.pepper.R;
import com.tricon.labs.pepper.adapters.CountryAdapter;
import com.tricon.labs.pepper.common.Constants;
import com.tricon.labs.pepper.database.DBHelper;
import com.tricon.labs.pepper.models.Country;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tricon.labs.pepper.libraries.functions.getCountries;
import static com.tricon.labs.pepper.libraries.functions.isValidEmail;
import static com.tricon.labs.pepper.libraries.parsePhone.parsePhone;

public class ActivityRegisterUser extends AppCompatActivity {

    private List<Country> countryList;
    private Country mSelectedCountry = null;

    private EditText mName;
    private AutoCompleteTextView mACTVCountryCode;
    private EditText mPhone;
    private EditText mEmail;

    private ProgressDialog mPDSaveData;

    private static final int CREATE_USER = 1;
    private static final int EDIT_USER = 2;
    private static int ENTRY_TYPE = CREATE_USER;

    private DBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.widget_toolbar);
        setSupportActionBar(toolbar);

        mName = (EditText) findViewById(R.id.et_name);
        mACTVCountryCode = (AutoCompleteTextView) findViewById(R.id.actv_country);
        mPhone = (EditText) findViewById(R.id.et_phone);
        mEmail = (EditText) findViewById(R.id.et_email);

        //initialising DB
        mDBHelper = DBHelper.getInstance(this);

        //set progress dialog
        mPDSaveData = new ProgressDialog(this);
        mPDSaveData.setCancelable(false);
        mPDSaveData.setIndeterminate(true);
        mPDSaveData.setMessage("Saving Data...");

        //set autocomplete threshold
        mACTVCountryCode.setThreshold(1);

        countryList = getCountries(getApplicationContext());
        mACTVCountryCode.setAdapter(new CountryAdapter(this, countryList));

        mACTVCountryCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedCountry = (Country) parent.getAdapter().getItem(position);
                mACTVCountryCode.setText("+" + mSelectedCountry.getPhoneCode());
                mACTVCountryCode.setSelection(mSelectedCountry.getPhoneCode().length() + 1);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_register_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                validateAndSaveData();
                break;

            default:
                break;
        }
        return true;
    }

    private void validateAndSaveData() {

        String name = mName.getText().toString().trim();
        String countryCode = mACTVCountryCode.getText().toString().trim();
        String phoneNumber = mPhone.getText().toString().trim();
        String emailId = mEmail.getText().toString().trim();


        if (TextUtils.isEmpty(name)) {
            mName.setError("Name required");
            return;
        }

        if (TextUtils.isEmpty(countryCode)) {
            mACTVCountryCode.setError("Country code required");
            return;
        }

        if (mSelectedCountry == null) {

            boolean validCountryCode = false;
            for (int i = 0; i < countryList.size(); i++) {

                if (("+" + countryList.get(i).getPhoneCode()).equals(countryCode)) {
                    validCountryCode = true;
                    mSelectedCountry = countryList.get(i);
                    break;
                }

                if ((countryList.get(i).getPhoneCode()).equals(countryCode)) {
                    validCountryCode = true;
                    mACTVCountryCode.setText("+" + countryCode);
                    mSelectedCountry = countryList.get(i);
                    break;
                }
            }

            if (validCountryCode == false) {
                mACTVCountryCode.setError("invalid country code");
                return;
            }
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            mPhone.setError("Phone number required");
            return;
        }

        if (isValidEmail(emailId) != true) {
            mEmail.setError("Invalid email id");
            return;
        }

        new SaveEntryTask(name, mSelectedCountry.getCode(), phoneNumber, emailId).execute();
    }

    private class SaveEntryTask extends AsyncTask<Void, Void, Boolean> {

        private String mName;
        private String mCountryCode;
        private String mPhone;
        private String mEmail;

        SaveEntryTask(String name, String countryCode, String phone, String email) {
            this.mName = name;
            this.mCountryCode = countryCode;
            this.mPhone = phone;
            this.mEmail = email;
        }

        @Override
        protected void onPreExecute() {
            mPDSaveData.setMessage("Saving Entry");
            mPDSaveData.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Map<String, String> entryData = new HashMap<>();

            String phoneNumber = parsePhone(mPhone, mCountryCode);

            if (phoneNumber.equals("")) {
                return false;
            }

            entryData.put("name", mName);
            entryData.put("country_code", mCountryCode);
            entryData.put("phone", phoneNumber);
            entryData.put("email", mEmail);

            if (ENTRY_TYPE == 1) {

                if (mDBHelper.insertUser(entryData) == 1) {

                    SharedPreferences sharedpreferences;
                    sharedpreferences = getSharedPreferences(Constants.APP_SETTINGS_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("CountryCode", mCountryCode);
                    editor.commit();

                    return true;
                } else {
                    return false;
                }
            } else {
                //update
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mPDSaveData.dismiss();
            if (success) {
                Toast.makeText(ActivityRegisterUser.this,
                        "Data saved successfully",
                        Toast.LENGTH_SHORT).show();

                Intent i = new Intent(ActivityRegisterUser.this, ActivityHome.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(ActivityRegisterUser.this,
                        "Something went wrong while saving entry.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
