package com.bikesh.scorpio.giventake;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Scorpio on 4/8/2015.
 */
public class FragmentLendAndBorrow extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //retrieving data from Savedinstance when orientation changes
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_lend_and_borrow, container, false);

        ListView listView = (ListView) v.findViewById(R.id.listViewFromDB);

        String[] values = new String[] { "Android List View",
                 "Adapter implementation",
                 "Simple List View In Android",
                 "Create List View Android",
                 "Android Example",
                 "List View Source Code",
                 "List View Array Adapter",
                 "Android Example List View",

                "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View",
                 };


        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getActivity(), values);
        listView.setAdapter(adapter);


        return v;
    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Saving data while orientation changes
        super.onSaveInstanceState(outState);
    }




    DBAdapter myDb;

    private void populateListViewFromDB(View v) {
        Cursor cursor = myDb.getAllRows();

        // Allow activity to manage lifetime of the cursor.
        // DEPRECATED! Runs on the UI thread, OK for small/short queries.
        //startManagingCursor(cursor); // manually closing cursor


        // Setup mapping from cursor to view fields:
        String[] fromFieldNames = new String[] {DBAdapter.KEY_NAME, DBAdapter.KEY_STUDENTNUM, DBAdapter.KEY_FAVCOLOUR, DBAdapter.KEY_STUDENTNUM};
        int[] toViewIDs = new int[]            {R.id.item_name,     R.id.item_icon,           R.id.item_favcolour,     R.id.item_studentnum};

        // Create adapter to may columns of the DB onto elemesnt in the UI.
        SimpleCursorAdapter myCursorAdapter =
                new SimpleCursorAdapter(
                        getActivity(),		// Context
                        R.layout.item_lend_and_borrow,	// Row layout template
                        cursor,					// cursor (set of DB records to map)
                        fromFieldNames,			// DB Column names
                        toViewIDs, 				// View IDs to put information in
                        0
                );

        // Set the adapter for the list view
        ListView myList = (ListView) v.findViewById(R.id.listViewFromDB);
        myList.setAdapter(myCursorAdapter);

        cursor.close();
    }

}



class MySimpleArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public MySimpleArrayAdapter(Context context, String[] values) {
        super(context, R.layout.item_lend_and_borrow, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_lend_and_borrow, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.item_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.item_icon);
        textView.setText(values[position]);
        // change the icon for Windows and iPhone
        String s = values[position];
        //if (s.startsWith("iPhone")) {
        //    imageView.setImageResource(R.drawable.gear);
        //} else {
            imageView.setImageResource(R.drawable.marker);
        //}

        return rowView;
    }
}