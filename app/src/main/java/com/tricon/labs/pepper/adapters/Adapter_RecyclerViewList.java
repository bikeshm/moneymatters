package com.tricon.labs.pepper.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.tricon.labs.pepper.R;
import com.tricon.labs.pepper.database.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.tricon.labs.pepper.libraries.parsePhone.parsePhone;

/**
 * Created by bikesh on 5/22/2015.
 */

public class Adapter_RecyclerViewList{}
/*
public class Adapter_RecyclerViewList extends RecyclerView.Adapter<Adapter_RecyclerViewList.viewHolder> {

    private Context context;

    Cursor mCursor;

    public ArrayList<String> CheckBoxSelected = new ArrayList<String>();

    Map<String, String> selectedUsers = new HashMap<String, String>();
    //map.put("name", "demo");

    public HashMap<String, Map<String, String>> selectedUsers1 = new HashMap<String,  Map<String, String>>();


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

            //((TextView) view.findViewById(R.id.item_id)).setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)));
            String name = mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            holder.item_name.setText(name );

            DBHelper  myDb = new DBHelper(context);

            String phoneNumber = parsePhone( mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),myDb.getdefaultContryCode() );

            if (myDb != null) {
                myDb.close();
            }

            String contact_id = mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));




            if ( selectedUsers.containsKey(contact_id)) {
                holder.item_name.setChecked(true);
            } else {
                holder.item_name.setChecked(false);
            }
                    /*
            if (CheckBoxSelected.contains(   contact_id     )) {
                holder.item_name.setChecked(true);
            } else {
                holder.item_name.setChecked(false);
            }
            *./

            //holder.item_name.setOnClickListener(new checkboxClicked(   contact_id  ) );

            holder.item_name.setOnClickListener(new checkboxClicked(   contact_id, name, phoneNumber  ) );
            holder.item_name.clearFocus();
            holder.item_name.setFocusable(false);

            /*
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
            *.../
        }

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    private class checkboxClicked implements View.OnClickListener {
        String rid,name,number;


        public checkboxClicked(String contact_id, String name, String phone) {

            rid=contact_id;
            this.name= name;
            number=phone;
        }
        @Override
        public void onClick(View v) {
            CheckBox cb = (CheckBox) v;
            if(cb.isChecked()){

                //map.put("name", "demo");

                if (!selectedUsers.containsKey(rid) ) {

                    Map<String, String> temp = new HashMap<String, String>();
                    temp.put("name", name);
                    temp.put("phone", number);

                    selectedUsers1.put(rid, temp);
                }

                /*
                if (!CheckBoxSelected.contains(rid) ) {
                    CheckBoxSelected.add(rid);
                }*../
            }
            else{
                //CheckBoxSelected.remove(rid);
                selectedUsers1.remove(rid);
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
            */