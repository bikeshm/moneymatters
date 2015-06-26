package com.bikesh.scorpio.giventake.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.bikesh.scorpio.giventake.R;
import com.bikesh.scorpio.giventake.database.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.bikesh.scorpio.giventake.libraries.parsePhone.parsePhone;

/**
 * Created by bikesh on 6/25/2015.
 */
public class UserCheckBoxRecycler extends RecyclerView.Adapter<UserCheckBoxRecycler.viewHolder> {

    private Context context;

    Cursor mCursor;

    ArrayList<String> existingMembersphoneList = new ArrayList<String>();

    public ArrayList<String> CheckBoxSelected = new ArrayList<String>();

    Map<String, String> selectedUsers = new HashMap<String, String>();

    public HashMap<String, Map<String, String>> selectedUsers1 = new HashMap<String,  Map<String, String>>();


    public UserCheckBoxRecycler(Cursor c, Context con, ArrayList<String> existingMembersPhone) {
        this.context = con;
        mCursor=c;
        existingMembersphoneList=existingMembersPhone;
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

            String name = mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            holder.item_name.setText(name );

            DBHelper myDb = new DBHelper(context);

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

            holder.item_name.setOnClickListener(new checkboxClicked(   contact_id, name, phoneNumber  ) );
            holder.item_name.clearFocus();
            holder.item_name.setFocusable(false);

            //for edit
            if(existingMembersphoneList.contains(phoneNumber)){
                holder.item_name.setChecked(true);
                //holder.item_name.performClick();

                Map<String, String> temp = new HashMap<String, String>();
                temp.put("name", name);
                temp.put("phone", phoneNumber);
                selectedUsers1.put(contact_id, temp);

            }
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

                if (!selectedUsers.containsKey(rid) ) {

                    Map<String, String> temp = new HashMap<String, String>();
                    temp.put("name", name);
                    temp.put("phone", number);

                    selectedUsers1.put(rid, temp);
                }
            }
            else{
                selectedUsers1.remove(rid);
            }
        }
    }

    //holder
    public static class viewHolder extends RecyclerView.ViewHolder {

        public CheckBox item_name;
        public viewHolder(View itemView) {
            super(itemView);
            item_name = (CheckBox) itemView.findViewById(R.id.item_name);
        }
    }
}

