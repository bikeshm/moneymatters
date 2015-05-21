package com.bikesh.scorpio.giventake;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bikesh on 5/15/2015.
 * thia adapter userd to generate fist level listview (eg:- ActivityLendandBorrow, ActivityPersonalExpense, etc)
 */
public class Adapter_CustomSimpleCursor extends SimpleCursorAdapter {

    DBHelper myDb;
    private int layout;
    private final LayoutInflater inflater;

    Context cContext;

    Map<String, String> dataExtra;


    ArrayList<String> CheckBoxSelected = new ArrayList<String>();


    public Adapter_CustomSimpleCursor(Context context,int layout, Cursor c ) {
        super(context,layout,c,new String[]{},new int[]{},0);
        this.layout=layout;
        this.cContext = context;
        this.inflater=LayoutInflater.from(context);
        myDb = new DBHelper(context);
    }

    public Adapter_CustomSimpleCursor(Context context,int layout, Cursor c, Map<String, String> data ) {
        super(context,layout,c,new String[]{},new int[]{},0);
        this.layout=layout;
        this.cContext = context;
        this.inflater=LayoutInflater.from(context);
        myDb = new DBHelper(context);

        dataExtra=data;
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        Log.i("La:", cContext.getResources().getResourceEntryName(layout) );

        //common
        ((TextView) view.findViewById(R.id.item_name)).setText(cursor.getString(cursor.getColumnIndex("name")));

        //for first level display
        if(cContext.getResources().getResourceEntryName(layout).equals("listview_item_template")) {

            String amdGiveString = "Amount give", amdGetString = "Amount get";

            float balanceAmt = 0;

            if (cContext.getClass().getSimpleName().equals("ActivityLendAndBorrow")) {

                balanceAmt = myDb.getTotalBalance(Long.parseLong((cursor.getString(cursor.getColumnIndex("_id")))));
                amdGiveString = amdGiveString + "from him/her";
                amdGetString = amdGetString + "from him/her";

            }

            if (cContext.getClass().getSimpleName().equals("ActivityPersonalExpense")) {
                balanceAmt = myDb.getMonthTotalOfPersonalExpenseIndividual(Long.parseLong((cursor.getString(cursor.getColumnIndex("_id")))), dataExtra.get("selectedDate") );
                ((TextView) view.findViewById(R.id.item_description)).setVisibility(View.GONE);

            }

            ((TextView) view.findViewById(R.id.item_amt)).setText("" + balanceAmt);


            if (balanceAmt < 0) {
                ((TextView) view.findViewById(R.id.item_description)).setText(amdGetString);
                balanceAmt = balanceAmt * -1;
                ((TextView) view.findViewById(R.id.item_amt)).setText("" + balanceAmt);
            } else if (balanceAmt > 0) {
                ((TextView) view.findViewById(R.id.item_description)).setText(amdGiveString);
            } else {
                ((TextView) view.findViewById(R.id.item_description)).setText("");

            }
        }
        else if(cContext.getResources().getResourceEntryName(layout).equals("custom_spinner_item_template")){
            // this usess loading user and collection
            ((TextView)view.findViewById(R.id.item_name)).setText(cursor.getString(cursor.getColumnIndex("name")));


            // if cusersor having phone meanse this is loading user fields or loadding collection
            if(cursor.getColumnIndex("phone")!=-1) {
                ((TextView) view.findViewById(R.id.item_phone)).setText(cursor.getString(cursor.getColumnIndex("phone")));
            }
            else{
                ((TextView) view.findViewById(R.id.item_phone)).setVisibility(View.GONE);
            }
        }
        else if(cContext.getResources().getResourceEntryName(layout).equals("listview_item_with_checkbox_template")){

            String id= cursor.getString(cursor.getColumnIndex("_id"));
            ((TextView) view.findViewById(R.id.item_id)).setText(id);
            ((CheckBox) view.findViewById(R.id.item_name)).setOnCheckedChangeListener(new checkboxClicked(view));

            Log.i("checked id", "" + id);

            if (CheckBoxSelected.contains(id)) {
                ((CheckBox) view.findViewById(R.id.item_name)).setChecked(true);
            } else {
                ((CheckBox) view.findViewById(R.id.item_name)).setChecked(false);
            }
            ((CheckBox) view.findViewById(R.id.item_name)).setFocusable(false);
            ((CheckBox) view.findViewById(R.id.item_name)).setFocusableInTouchMode(false);

            //((CheckBox) view.findViewById(R.id.item_name)).clearFocus();

            //((CheckBox) view.findViewById(R.id.item_name)).setFocusable(false);
            //CheckBoxSelected

        }

    }


    private class checkboxClicked implements CompoundButton.OnCheckedChangeListener {
        View v;
        public checkboxClicked(View view) {
            v=view;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            //Log.i("checked",""+isChecked);
            //Log.i("checked", ((TextView) v.findViewById(R.id.item_id)).getText().toString());



            if(isChecked) {
                if (!CheckBoxSelected.contains(((TextView) v.findViewById(R.id.item_id)).getText().toString())) {

                    CheckBoxSelected.add("" + ((TextView) v.findViewById(R.id.item_id)).getText().toString());
                }

            } else {
                Log.i("checked index", "" + CheckBoxSelected.indexOf(((TextView) v.findViewById(R.id.item_id)).getText().toString()));
                Log.i("checked index", "" +  ((TextView) v.findViewById(R.id.item_id)).getText().toString() );

                        //if(CheckBoxSelected.indexOf( ((TextView) v.findViewById(R.id.item_id)).getText().toString())>0) {
                        CheckBoxSelected.remove(((TextView) v.findViewById(R.id.item_id)).getText().toString());
                //}
            }


            for (int i = 0; i < CheckBoxSelected.size(); i++) {
                Log.i("checked", "" + CheckBoxSelected.get(i));
            }

            //((CheckBox) v.findViewById(R.id.item_name)).clearFocus();

        }



    }
}