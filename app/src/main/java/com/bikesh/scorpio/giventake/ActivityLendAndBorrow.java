package com.bikesh.scorpio.giventake;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class ActivityLendAndBorrow extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_lend_and_borrow);

        super.onCreate(savedInstanceState);
        //loading templet xml
        setContentView(R.layout.main_template);


        //setting up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //setting up navigation drawer
        GiveNTakeApplication AC = (GiveNTakeApplication)getApplicationContext();
        View view = getWindow().getDecorView().findViewById(android.R.id.content);
        AC.setupDrawer(view, ActivityLendAndBorrow.this, getFragmentManager(), toolbar );

        //loading home activity templet in to template frame
        FrameLayout frame = (FrameLayout) findViewById(R.id.mainFrame);
        frame.removeAllViews();
        View lendAndBorrowView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_lend_and_borrow, null);




        ListView listView = (ListView) lendAndBorrowView.findViewById(R.id.listViewFromDB);

        String[] values = new String[] {
                "Manoj",
                "Vyshakh",
                "Bikesh",
                "Anjane",
                "MSR",
                "Riju",
                "ANsar",
                "Suneesh",

                "Anees",
                "Gokul",
                "Lakshmi",
                "Savi",
                "Sanju",
                "Prasadh",
                "Luttan",
                "Sapna",
        };


        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(ActivityLendAndBorrow.this, values);
        listView.setAdapter(adapter);



        frame.addView(lendAndBorrowView);
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

        // inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View rowView = inflater.inflate(R.layout.item_lend_and_borrow, parent, false);

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_lend_and_borrow, null);

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

        Random r = new Random();
        //rand.nextInt((max - min) + 1) + min;
        int amt = r.nextInt((500 - 80) + 1) + 80;
        TextView textPrice = (TextView) rowView.findViewById(R.id.item_studentnum);
        textPrice.setText(""+amt);

        Log.d("Log", "=============" + position);

        return rowView;
    }


}
