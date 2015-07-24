package com.tricon.labs.pepper.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.tricon.labs.pepper.R;
import com.tricon.labs.pepper.models.Country;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gautam
 *         <p/>
 *         Jul 29, 2014
 */
public class CountryAdapter extends BaseAdapter implements Filterable {

    private List<Country> mAllCountries;
    private List<Country> mFilteredCountries;
    private LayoutInflater mInflater;
    private Filter mCountryFilter = null;

    public CountryAdapter(Context ctxt, List<Country> countries) {
        this.mAllCountries = countries;
        this.mFilteredCountries = new ArrayList<Country>(countries);
        this.mInflater = LayoutInflater.from(ctxt);



    }

    @Override
    public int getCount() {
        return mFilteredCountries.size();
    }

    @Override
    public Filter getFilter() {
        if (mCountryFilter == null) {
            mCountryFilter = new CountryFilter();
        }
        return mCountryFilter;
    }

    @Override
    public Object getItem(int position) {
        return mFilteredCountries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_country_list_item, parent, false);
            holder = new ViewHolder();
            holder.countryName = (TextView) convertView.findViewById(R.id.country_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Country country = (Country) getItem(position);

        holder.countryName.setText(country.getName() + " (+"+ country.getPhoneCode()+")" );

        return convertView;
    }

    private static class ViewHolder {
        TextView countryName;
    }

    private class CountryFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                filterResults.values = new ArrayList<Country>(mAllCountries);
                filterResults.count = mAllCountries.size();
            } else {
                String filterString = constraint.toString().toLowerCase().replace("+", "");
                List<Country> filteredCountries = new ArrayList<Country>();
                for (Country country : mAllCountries) {
                    if ((country.getName().toLowerCase().startsWith(filterString) || country.getPhoneCode().replace("+", "").startsWith(filterString) || country.getName().replace("+", "").toLowerCase().startsWith(filterString))) {
                        filteredCountries.add(country);
                    }
                }
                filterResults.values = filteredCountries;
                filterResults.count = filteredCountries.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            if (results != null && results.count > 0) {
                mFilteredCountries = (List<Country>) results.values;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

}
