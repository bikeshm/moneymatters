package com.tricon.labs.giventake.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tricon.labs.giventake.ActivityEditCategory;
import com.tricon.labs.giventake.ActivityPersonalExpenseIndividual;
import com.tricon.labs.giventake.R;
import com.tricon.labs.giventake.adapters.AdapterPersonalExpense;
import com.tricon.labs.giventake.database.DBHelper;
import com.tricon.labs.giventake.libraries.MonthYearPicker;
import com.tricon.labs.giventake.models.Category;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


//Todo :- need to implement pagination
//Todo : - implement search option


public class FragmentPersonalExpense extends Fragment {

    private Button mBtnDate;
    private TextView mTVMonthlyTotal;

    private DBHelper mDBHelper;

    private String mSelectedDate;
    private List<Category> mCategoriesList = new ArrayList<>();

    private AdapterPersonalExpense mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setup view
        View mRootView = inflater.inflate(R.layout.fragment_personal_expense, container, false);
        mBtnDate = ((Button) mRootView.findViewById(R.id.btn_date));
        mTVMonthlyTotal = (TextView) mRootView.findViewById(R.id.tv_monthly_total);
        ListView mLVCategories = (ListView) mRootView.findViewById(R.id.lv_categories);

        //set list view adapter
        mAdapter = new AdapterPersonalExpense(mCategoriesList);
        mLVCategories.setAdapter(mAdapter);

        //get db instance
        mDBHelper = DBHelper.getInstance(getActivity());

        //get current date
        Calendar calenderInstance = Calendar.getInstance();
        String dayString = ((calenderInstance.get(Calendar.MONTH) + 1) < 10 ? "0" : "") + (calenderInstance.get(Calendar.MONTH) + 1);
        mSelectedDate = dayString + "-" + calenderInstance.get(Calendar.YEAR);
        mBtnDate.setText(calenderInstance.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " - " + calenderInstance.get(Calendar.YEAR));

        //set listeners
        mLVCategories.setOnItemClickListener(new listItemClicked());
        mLVCategories.setOnItemLongClickListener(new listItemLongClicked());
        mBtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] monthAndYear = mSelectedDate.split("-");
                openDatePicker(Integer.parseInt(monthAndYear[0].trim()) - 1, Integer.parseInt(monthAndYear[1].trim()));
            }
        });

        return mRootView;
    }


    private void openDatePicker(int selectedMonth, int selectedYear) {
        final MonthYearPicker monthPicker = new MonthYearPicker(getActivity());

        monthPicker.build(selectedMonth, selectedYear, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBtnDate.setText(monthPicker.getSelectedMonthName() + "-" + monthPicker.getSelectedYear());

                String dayString = ((monthPicker.getSelectedMonth() + 1) < 10 ? "0" : "") + (monthPicker.getSelectedMonth() + 1);
                mSelectedDate = dayString + "-" + monthPicker.getSelectedYear();

                populateListViewFromDB();
            }
        }, null);
        monthPicker.show();
    }


    @Override
    public void onResume() {
        super.onResume();
        populateListViewFromDB();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Saving data while orientation changes
        super.onSaveInstanceState(outState);
    }

    private void populateListViewFromDB() {
        mCategoriesList.clear();
        mCategoriesList.addAll(mDBHelper.getCategoriesListsByMonth(mSelectedDate));

        mAdapter.notifyDataSetChanged();

        mTVMonthlyTotal.setText(mDBHelper.getMonthTotalOfPersonalExpense(mSelectedDate) + "");
    }


    private class listItemClicked implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Category selectedCategory = mCategoriesList.get(position);

            Intent i = new Intent(getActivity(), ActivityPersonalExpenseIndividual.class);

            i.putExtra("CATEGORYID", selectedCategory.id);
            i.putExtra("CATEGORYNAME", selectedCategory.name);
            i.putExtra("BTNDATE", mBtnDate.getText());
            i.putExtra("SELECTEDDATE", mSelectedDate);
            startActivity(i);
        }
    }


    private class listItemLongClicked implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            generatePopupMenu(position, mCategoriesList.get(position));
            return true;
        }
    }


    public void generatePopupMenu(final int position, final Category category) {
        final CharSequence[] options = {"Edit", "Delete"};

        new AlertDialog.Builder(getActivity())
                .setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Delete")) {
                            Toast.makeText(getActivity(), "id" + category.id, Toast.LENGTH_LONG).show();

                            //delete collection
                            if (mDBHelper.deleteCollection(category.id + "") == 1) {
                                Log.i("delete collection", "collection deleted");

                                //delete all entries in that collection
                                if (mDBHelper.deleteCollectionEntrys(category.id + "") == 1) {
                                    Log.i("delete collection", "deleted all the collection entrys");
                                } else {
                                    Log.i("delete collection", "Not deleted collection entrys");
                                }

                                //delete entry from collection
                                mCategoriesList.remove(position);
                                mAdapter.notifyDataSetChanged();

                                //subtract expense of this entry from total expense nd set that in "Total Expense Text view"
                                Double newExpense = Double.parseDouble(mTVMonthlyTotal.getText().toString()) - category.totalAmount;
                                mTVMonthlyTotal.setText(newExpense + "");

                            } else {
                                Log.i("delete collection", "collection Not deleted ");
                            }
                        } else if (options[item].equals("Edit")) {
                            Toast.makeText(getActivity(), "id" + category.id, Toast.LENGTH_LONG).show();

                            Intent i = new Intent(getActivity(), ActivityEditCategory.class);
                            i.putExtra("CATEGORYID", category.id + "");
                            i.putExtra("CATEGORYNAME", category.name);
                            startActivity(i);
                        }
                    }
                })
                .show();
    }

}


