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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class AdapterCategoryList extends BaseAdapter implements Filterable {

    private List<String> mAllCategories;
    private List<String> mFilteredCategories;
    private LayoutInflater mInflater;
    private Filter mCategoryFilter = null;

    public AdapterCategoryList(Context context, TreeSet<String> categories) {
        this.mAllCategories = new ArrayList<>(categories);
        this.mFilteredCategories = new ArrayList<>(categories);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public Filter getFilter() {
        if (mCategoryFilter == null) {
            mCategoryFilter = new CategoryFilter();
        }
        return mCategoryFilter;
    }

    @Override
    public int getCount() {
        return mFilteredCategories.size();
    }

    @Override
    public String getItem(int position) {
        return mFilteredCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_category_list_item, parent, false);
            holder = new ViewHolder();
            holder.tvCategory = (TextView) convertView.findViewById(R.id.tv_category);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvCategory.setText(getItem(position));

        return convertView;
    }

    private static class ViewHolder {
        TextView tvCategory;
    }

    private class CategoryFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().trim().toLowerCase();
            FilterResults filterResults = new FilterResults();

            if (TextUtils.isEmpty(filterString)) {
                filterResults.values = new ArrayList<>(mAllCategories);
                filterResults.count = mAllCategories.size();
            } else {
                List<String> filteredCategories = new ArrayList<>();
                for (String category : mAllCategories) {
                    if (category.toLowerCase().startsWith(filterString)) {
                        filteredCategories.add(category);
                    }
                }
                filterResults.values = filteredCategories;
                filterResults.count = filteredCategories.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                mFilteredCategories = (List<String>) results.values;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}


