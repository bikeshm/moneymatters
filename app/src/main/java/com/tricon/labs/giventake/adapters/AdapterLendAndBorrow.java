package com.tricon.labs.giventake.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tricon.labs.giventake.R;
import com.tricon.labs.giventake.models.Category;
import com.tricon.labs.giventake.models.Person;

import java.util.List;

/**
 * Created by bikesh on 7/17/2015.
 */
public class AdapterLendAndBorrow extends BaseAdapter {

    List<Person> persons;

    public AdapterLendAndBorrow(List<Person> categoriesList) {
        persons = categoriesList;
    }


    @Override
    public int getCount() {
        return persons.size();
    }

    @Override
    public Object getItem(int position) {
        return persons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolderItem viewHolder;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lend_and_borrow_item, parent, false);

            // setting up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tvTotalAmount = (TextView) convertView.findViewById(R.id.tv_amount);

            // storing the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        // Get the data item for this position
        Person person = (Person) getItem(position);

        viewHolder.tvName.setText(person.name);
        viewHolder.tvTotalAmount.setText(person.totalAmount + " ["+person.status+"]");

        return convertView;
    }

    //View holder class
    static class ViewHolderItem {
        TextView tvName;
        TextView tvTotalAmount;

    }

}