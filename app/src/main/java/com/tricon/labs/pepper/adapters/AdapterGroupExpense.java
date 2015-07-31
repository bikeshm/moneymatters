package com.tricon.labs.pepper.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tricon.labs.pepper.R;
import com.tricon.labs.pepper.models.Group;

import java.util.List;

public class AdapterGroupExpense extends BaseAdapter {

    private List<Group> groups;

    public AdapterGroupExpense(List<Group> categoriesList) {
        groups = categoriesList;
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public Group getItem(int position) {
        return groups.get(position);
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

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_group_expense_item, parent, false);

            // setting up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tvBalanceAmount = (TextView) convertView.findViewById(R.id.tv_balance_amount);

            // storing the holder with the view.
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        // Get the data item for this position
        Group group = getItem(position);

        viewHolder.tvName.setText(group.name);
        viewHolder.tvBalanceAmount.setText(group.balanceAmount + "");

        if (group.status == Group.STATUS_GIVE) {
            viewHolder.tvBalanceAmount.setTextColor(parent.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            viewHolder.tvBalanceAmount.setTextColor(parent.getContext().getResources().getColor(android.R.color.holo_green_dark));
        }

        return convertView;
    }

    //View holder class
    private static class ViewHolderItem {
        TextView tvName;
        TextView tvBalanceAmount;
    }
}