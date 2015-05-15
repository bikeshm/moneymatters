package com.bikesh.scorpio.giventake;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by bikesh on 5/15/2015.
 */
public class Adapter_CustomSimpleCursor extends SimpleCursorAdapter {

    DBHelper myDb;
    private int layout;
    private final LayoutInflater inflater;

    Context cContext;

    public Adapter_CustomSimpleCursor(Context context,int layout, Cursor c ) {
        super(context,layout,c,new String[]{},new int[]{},0);
        this.layout=layout;
        this.cContext = context;
        this.inflater=LayoutInflater.from(context);
        myDb = new DBHelper(context);
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        String amdGiveString="Amount give",amdGetString="Amount get";

        ((TextView) view.findViewById(R.id.item_name)).setText(cursor.getString(cursor.getColumnIndex("name")));


        float balanceAmt=0;

        if(cContext.getClass().getSimpleName().equals("ActivityLendAndBorrow")) {

            balanceAmt = myDb.getTotalBalance(Long.parseLong((cursor.getString(cursor.getColumnIndex("_id")))));
            amdGiveString=amdGiveString+"from him/her";
            amdGetString=amdGetString+"from him/her";

        }
        else if(cContext.getClass().getSimpleName().equals("ActivityPersonalExpense")) {

            balanceAmt = myDb.getTotalBalance(Long.parseLong((cursor.getString(cursor.getColumnIndex("_id")))));
        }

        ((TextView)view.findViewById(R.id.item_amt)).setText("" + balanceAmt);



        if(balanceAmt<0){
            ((TextView)view.findViewById(R.id.item_description)).setText(amdGetString);
            balanceAmt=balanceAmt*-1;
            ((TextView)view.findViewById(R.id.item_amt)).setText(""+balanceAmt);
        }
        else if (balanceAmt>0){
            ((TextView)view.findViewById(R.id.item_description)).setText(amdGiveString);
        }
        else{
            ((TextView)view.findViewById(R.id.item_description)).setText("");

        }

    }

}