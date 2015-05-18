package com.bikesh.scorpio.giventake;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by bikesh on 5/12/2015.
 */
public class CustomDatePicker implements View.OnClickListener {

    Context context;
    EditText editTextdatePicker;
    EditText editTexttempDatePicker;

    TextView textViewdatePicker;
    TextView textViewtempDatePicker;

    boolean removeDay=false;

    public CustomDatePicker(Context context, EditText datePicker, EditText tempDatePicker,boolean removeDay) {
        this.context=context;
        editTextdatePicker=datePicker;
        editTexttempDatePicker=tempDatePicker;
        this.removeDay=removeDay;
    }

    public CustomDatePicker(Context context, TextView datePicker, TextView tempDatePicker,boolean removeDay) {
        this.context=context;
        textViewdatePicker=datePicker;
        textViewtempDatePicker=tempDatePicker;
        this.removeDay=removeDay;
        Log.i("datepicker", "cccccccccc");
    }

    @Override
    public void onClick(View v) {


        //To show current date in the datepicker
        Calendar mcurrentDate=Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker=new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                selectedmonth++;
                String actualMonth=""+selectedmonth;
                if(selectedmonth<10){
                    actualMonth="0"+actualMonth;
                }

                String actualDay=""+selectedday;
                if(selectedday<10){
                    actualDay="0"+actualDay;
                }

                String selectedDate,selectedDateForDb;

                if(removeDay) {
                    selectedDate = actualMonth + "-" + selectedyear;
                    selectedDateForDb =selectedyear + "-" + actualMonth ;
                }
                else{

                    selectedDate =actualDay + "-" + actualMonth + "-" + selectedyear;
                    selectedDateForDb =selectedyear + "-" + actualMonth + "-" + actualDay;
                }

                if(editTextdatePicker!=null) {
                    editTextdatePicker.setText(selectedDate);
                    editTexttempDatePicker.setText(selectedDateForDb);
                }


                if(textViewdatePicker!=null) {
                    textViewdatePicker.setText(selectedDate);
                    textViewtempDatePicker.setText(selectedDateForDb);
                }

            }
        },mYear, mMonth, mDay);
        mDatePicker.setTitle("Select date");

        if(removeDay) {
            ((ViewGroup) mDatePicker.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        }

        mDatePicker.show();

    }
}