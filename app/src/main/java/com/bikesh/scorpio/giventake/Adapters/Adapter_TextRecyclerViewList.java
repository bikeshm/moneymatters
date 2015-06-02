package com.bikesh.scorpio.giventake.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bikesh.scorpio.giventake.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bikesh on 5/22/2015.
 */
public class Adapter_TextRecyclerViewList extends RecyclerView.Adapter<Adapter_TextRecyclerViewList.viewHolder> {

    private Context context;

    Cursor mCursor;

    Map<String, String> dataValues = new HashMap<String, String>();

    EditText rootTotalAmt;

    public Adapter_TextRecyclerViewList(Cursor c, Context con) {
        this.context = con;
        mCursor=c;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item_with_edittext_template, parent, false);
        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {

        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        } else {

            View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
            rootTotalAmt = ((EditText)rootView.findViewById(R.id.amount));


            holder.item_name.setText(mCursor.getString(mCursor.getColumnIndex("name")));
            String userID=mCursor.getString(mCursor.getColumnIndex("_id"));

            if (dataValues.containsKey(userID)) {
                holder.item_amt.setText(dataValues.get(userID));
            }

            else {
                holder.item_amt.setText("0.00");
            }

            /*for (Map.Entry<String, String> entry : dataValues.entrySet())
            {
                Log.i("Tch", entry.getKey()+" : "+entry.getValue() );
            }*/

            holder.item_amt.setOnKeyListener(new amtChaged(userID, holder.item_amt));

        }

    }


    private class amtChaged implements View.OnKeyListener {
        String uId;
        EditText et;
        public amtChaged(String userID, EditText tv) {
            uId=userID;
            et=tv;
        }
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if(et.getText().toString().equals("")){
                dataValues.put(uId,"0.00");
            }else {
                dataValues.put(uId, et.getText().toString());
            }


            float total=0;
            for (Map.Entry<String, String> entry : dataValues.entrySet())
            {
                total = total+ Float.parseFloat( entry.getValue() );
            }

            rootTotalAmt.setText(""+String.format("%.2f", total));
            return false;
        }
    }





    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }






    //holder
    public static class viewHolder extends RecyclerView.ViewHolder {

        //public ImageView imageMedicineType;
        public TextView item_name;
        public EditText item_amt;
       // public TextView txtNextTimeOfAlarm;

        public viewHolder(View itemView) {
            super(itemView);
            //imageMedicineType = (ImageView) itemView.findViewById(R.id.imageMedicineType);
            item_name = (TextView) itemView.findViewById(R.id.item_name);
            item_amt = (EditText) itemView.findViewById(R.id.item_amt);
            //txtNextTimeOfAlarm = (TextView) itemView.findViewById(R.id.txtNextTimeOfAlarm);
        }
    }



}