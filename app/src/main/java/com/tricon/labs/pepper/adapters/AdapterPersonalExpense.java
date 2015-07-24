package com.tricon.labs.pepper.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tricon.labs.pepper.R;
import com.tricon.labs.pepper.models.Category;

import java.util.List;

public class AdapterPersonalExpense extends BaseAdapter {

    private List<Category> categories;

    public AdapterPersonalExpense(List<Category> categoriesList) {
        categories = categoriesList;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Category getItem(int position) {
        return categories.get(position);
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

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_personal_expense_item, parent, false);

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
        Category category = getItem(position);

        viewHolder.tvName.setText(category.name);
        viewHolder.tvTotalAmount.setText(category.totalAmount + "");

        return convertView;
    }

    //View holder class
    private static class ViewHolderItem {
        TextView tvName;
        TextView tvTotalAmount;
    }
}