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

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


//Todo :- need to implement pagination
//Todo : - implement search option


public class FragmentPersonalExpense extends Fragment {

    private ListView mLVCategories;
    private DBHelper mDBHelper;
    private View mRootView;

    String mSelectedDate;

    List<Category> mCategoriesList;
    AdapterPersonalExpense mAdapter;

    Button mBtnDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_personal_expense, container, false);

        mDBHelper = DBHelper.getInstance(getActivity());

        mLVCategories = (ListView) mRootView.findViewById(R.id.lv_categories);

        Calendar calenderInstance = Calendar.getInstance();

        String dayString = ((calenderInstance.get(Calendar.MONTH) + 1) < 10 ? "0" : "") + (calenderInstance.get(Calendar.MONTH) + 1);

        mSelectedDate = dayString + "-" + calenderInstance.get(Calendar.YEAR);


        mLVCategories.setOnItemClickListener(new listItemClicked());
        mLVCategories.setOnItemLongClickListener(new listItemLongClicked());

        mBtnDate = ((Button) mRootView.findViewById(R.id.btn_date));

        mBtnDate.setText(calenderInstance.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " - " + calenderInstance.get(Calendar.YEAR));

        mBtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMonthPicker();
            }
        });

        return mRootView;

    }


    private void openMonthPicker() {

        final MonthYearPicker monthPicker = new MonthYearPicker(getActivity());

        monthPicker.build(new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                mBtnDate.setText(monthPicker.getSelectedMonthName() + "-" + monthPicker.getSelectedYear());

                String dayString = ((monthPicker.getSelectedMonth() + 1) < 10 ? "0" : "") + (monthPicker.getSelectedMonth() + 1);

                mSelectedDate = dayString + "-" + monthPicker.getSelectedYear();

                mCategoriesList.clear();
                mCategoriesList.addAll(mDBHelper.getCategoriesListsByMonth(mSelectedDate));

                populateListViewFromDB();

            }
        }, null);


        monthPicker.show();
    }


    @Override
    public void onResume() {
        super.onResume();

        mCategoriesList = mDBHelper.getCategoriesListsByMonth(mSelectedDate);
        mAdapter = new AdapterPersonalExpense(mCategoriesList);
        mLVCategories.setAdapter(mAdapter);

        populateListViewFromDB();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Saving data while orientation changes
        super.onSaveInstanceState(outState);
    }

    private void populateListViewFromDB() {

        mAdapter.notifyDataSetChanged();

        ((TextView) mRootView.findViewById(R.id.tv_monthly_total)).setText(": " + mDBHelper.getMonthTotalOfPersonalExpense(mSelectedDate));

    }


    private class listItemClicked implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Category selectedCategory = mCategoriesList.get(position);

            Intent i = new Intent(getActivity(), ActivityPersonalExpenseIndividual.class);

            i.putExtra("CATEGORYID", selectedCategory.id + "");
            i.putExtra("CATEGORYNAME", selectedCategory.name);
            startActivity(i);
        }
    }


    private class listItemLongClicked implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            generatePopupMenu(mCategoriesList.get(position));
            return true;
        }
    }


    public void generatePopupMenu(final Category category) {

        final CharSequence[] options = {"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Delete")) {


                    new AlertDialog.Builder(getActivity())
                            .setTitle("Delete")
                            .setMessage("Do you really want to delete?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {

                                    Toast.makeText(getActivity(), "id" + category.id, Toast.LENGTH_LONG).show();

                                    //delete collection
                                    if (mDBHelper.deleteCollection(category.id + "") == 1) {
                                        Log.i("delete collection", "collection deleted");

                                        //delete all the entrys in that collection
                                        if (mDBHelper.deleteCollectionEntrys(category.id + "") == 1) {
                                            Log.i("delete collection", "deleted all the collection entrys");
                                        } else {
                                            Log.i("delete collection", "Not deleted collection entrys");
                                        }

                                        //reloading the data
                                        mCategoriesList.clear();
                                        mCategoriesList.addAll(mDBHelper.getCategoriesListsByMonth(mSelectedDate));

                                        populateListViewFromDB();

                                    } else {
                                        Log.i("delete collection", "collection Not deleted ");
                                    }


                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();


                } else if (options[item].equals("Edit")) {
                    //dialog.dismiss();

                    Toast.makeText(getActivity(), "id" + category.id, Toast.LENGTH_LONG).show();

                    Intent i = new Intent(getActivity(), ActivityEditCategory.class);

                    i.putExtra("CATEGORYID", category.id + "");
                    i.putExtra("CATEGORYNAME", category.name);
                    startActivity(i);

                }
            }
        });

        builder.show();
    }

}


