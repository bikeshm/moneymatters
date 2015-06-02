package com.bikesh.scorpio.giventake.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.bikesh.scorpio.giventake.R;

import java.util.ArrayList;

/**
 * Created by bikesh on 5/22/2015.
 */
public class Adapter_RecyclerViewList extends RecyclerView.Adapter<Adapter_RecyclerViewList.viewHolder> {

    private Context context;

    Cursor mCursor;

    public ArrayList<String> CheckBoxSelected = new ArrayList<String>();


    public Adapter_RecyclerViewList(Cursor c, Context con) {
        this.context = con;
        mCursor=c;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item_with_checkbox_template, parent, false);
        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {

        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        } else {

           //holder.imageMedicineType.setImageResource(R.drawable.ic_launcher);
            holder.item_name.setText(mCursor.getString(mCursor.getColumnIndex("name")));

            if (CheckBoxSelected.contains(mCursor.getString(mCursor.getColumnIndex("_id")))) {
                holder.item_name.setChecked(true);
            } else {
                holder.item_name.setChecked(false);
            }
            holder.item_name.setOnClickListener(new checkboxClicked(mCursor.getString(mCursor.getColumnIndex("_id"))));
            holder.item_name.clearFocus();
            holder.item_name.setFocusable(false);
        }

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    private class checkboxClicked implements View.OnClickListener {
        String rid;
        public checkboxClicked(String id) {
            rid=id;
        }
        @Override
        public void onClick(View v) {
            CheckBox cb = (CheckBox) v;
            if(cb.isChecked()){
                if (!CheckBoxSelected.contains(rid) ) {
                    CheckBoxSelected.add(rid);
                }
            }
            else{
                CheckBoxSelected.remove(rid);
            }
        }
    }



    //holder
    public static class viewHolder extends RecyclerView.ViewHolder {

        //public ImageView imageMedicineType;
        public CheckBox item_name;
       // public TextView txtDosage;
       // public TextView txtNextTimeOfAlarm;

        public viewHolder(View itemView) {
            super(itemView);
            //imageMedicineType = (ImageView) itemView.findViewById(R.id.imageMedicineType);
            item_name = (CheckBox) itemView.findViewById(R.id.item_name);
            //txtDosage = (TextView) itemView.findViewById(R.id.txtDosage);
            //txtNextTimeOfAlarm = (TextView) itemView.findViewById(R.id.txtNextTimeOfAlarm);
        }
    }

}