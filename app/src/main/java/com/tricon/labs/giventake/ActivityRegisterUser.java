package com.tricon.labs.giventake;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tricon.labs.giventake.libraries.functions.md5;
import static com.tricon.labs.giventake.libraries.parsePhone.parsePhone;

import com.tricon.labs.giventake.adapters.CountryAdapter;
import com.tricon.labs.giventake.models.Country;

public class ActivityRegisterUser extends ActivityBase {

    Intent backActivityIntent = null;
    Spinner countrySpinner;
    List<Country> countryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        getSupportActionBar().setTitle("Register");
        backActivityIntent = new Intent(ActivityRegisterUser.this, ActivityHome.class);

        ((Button) currentView.findViewById(R.id.saveBtn)).setOnClickListener(new saveData());
        countrySpinner= ((Spinner)findViewById(R.id.conttrycode));

        countryList = getCountries();
        CountryAdapter actionSpinnerArrayAdapter = new CountryAdapter(this,  getCountries());
        countrySpinner.setAdapter(actionSpinnerArrayAdapter);


    }


    private class saveData implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            String phoneNumber = ((EditText) currentView.findViewById(R.id.phone)).getText().toString().trim();

            if (((EditText) currentView.findViewById(R.id.name)).getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "Name required", Toast.LENGTH_LONG).show();
                return;
            }

            if (phoneNumber.equals("") ) {
                Toast.makeText(getApplicationContext(), "Phone number required", Toast.LENGTH_LONG).show();
                return;
            }

            Map<String, String> data = new HashMap<String, String>();

            //Todo:- validate and escape incomming data
            data.put("name", ((EditText) currentView.findViewById(R.id.name)).getText().toString());
            data.put("description", ((EditText) currentView.findViewById(R.id.description)).getText().toString());


            data.put("email", ((EditText) currentView.findViewById(R.id.email)).getText().toString());



            data.put("password", md5(((EditText) currentView.findViewById(R.id.password)).getText().toString()));

            data.put("country_code",countryList.get((int)countrySpinner.getSelectedItemId()).getCode().toUpperCase());

            data.put("phone", parsePhone(phoneNumber, data.get("country_code")) );

            //Toast.makeText(getApplicationContext(), "Selected country"+ countryList.get((int)countrySpinner.getSelectedItemId()).getName(), Toast.LENGTH_SHORT).show();

            Log.i("saving user", data+" "+countryList.get((int)countrySpinner.getSelectedItemId()).getCode() );



            if (myDb.insertUser(data) == 1) {
                Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();

                goBack();

            } else {
                Toast.makeText(getApplicationContext(), "Error while Saving data", Toast.LENGTH_SHORT).show();
            }





        }
    }

    private void goBack(){
        startActivity(backActivityIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myDb != null) {
            myDb.close();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_register_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
