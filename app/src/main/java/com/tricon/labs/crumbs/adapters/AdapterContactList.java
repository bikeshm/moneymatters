package com.tricon.labs.crumbs.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.tricon.labs.crumbs.R;
import com.tricon.labs.crumbs.models.Contact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AdapterContactList extends BaseAdapter implements Filterable {

    private List<Contact> mContacts;
    private List<Contact> mFilteredContacts;
    private LayoutInflater mInflater;
    private Filter mContactFilter = null;

    public AdapterContactList(Context context, List<Contact> contacts) {
        mContacts = contacts;
        mFilteredContacts = new ArrayList<>(contacts);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public Filter getFilter() {
        if (mContactFilter == null) {
            mContactFilter = new ContactFilter();
        }
        return mContactFilter;
    }

    @Override
    public int getCount() {
        return mFilteredContacts.size();
    }

    @Override
    public Contact getItem(int position) {
        return mFilteredContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Contact contact = getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_contact_list_item, parent, false);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvPhone = (TextView) convertView.findViewById(R.id.tv_phone);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvName.setText(contact.name);
        holder.tvPhone.setText(contact.phone);

        return convertView;
    }

    public void addMember(Contact contact) {
        mContacts.add(contact);
    }

    public void addMembers(Collection<Contact> members) {
        mContacts.addAll(members);
    }

    public void removeMember(Contact contact) {
        mContacts.remove(contact);
    }

    private static class ViewHolder {
        TextView tvName;
        TextView tvPhone;
    }

    private class ContactFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().trim().toLowerCase();
            FilterResults filterResults = new FilterResults();

            if (TextUtils.isEmpty(filterString)) {
                filterResults.values = new ArrayList<>(mContacts);
                filterResults.count = mContacts.size();
            } else {
                List<Contact> filteredContacts = new ArrayList<>();
                for (Contact contact : mContacts) {
                    if (contact.name.toLowerCase().startsWith(filterString)) {
                        filteredContacts.add(contact);
                    }
                }
                filterResults.values = filteredContacts;
                filterResults.count = filteredContacts.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                mFilteredContacts = (List<Contact>) results.values;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}


