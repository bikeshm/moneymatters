package com.tricon.labs.giventake.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tricon.labs.giventake.R;
import com.tricon.labs.giventake.models.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bikesh on 7/17/2015.
 */
public class AdapterPersonalExpense extends BaseAdapter {

    List<Category> categories;

    public AdapterPersonalExpense( List<Category> categoriesList) {
        categories = categoriesList;
    }


    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position

        Category category = (Category) getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_personal_expense_item, parent, false);

        }

        // Lookup view for data population

        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);

        TextView tvToalAmount = (TextView) convertView.findViewById(R.id.tv_amount);

        // Populate the data into the template view using the data object

        tvName.setText(category.name);

        tvToalAmount.setText(category.toalAmount + "");

        // Return the completed view to render on screen

        return convertView;

    }

}