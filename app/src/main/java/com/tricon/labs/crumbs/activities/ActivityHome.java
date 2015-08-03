package com.tricon.labs.crumbs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.tricon.labs.crumbs.Fragments.FragmentGroupExpense;
import com.tricon.labs.crumbs.Fragments.FragmentLendAndBorrow;
import com.tricon.labs.crumbs.Fragments.FragmentPersonalExpense;
import com.tricon.labs.crumbs.R;
import com.tricon.labs.crumbs.activities.groupexpense.ActivityAddOrEditGroup;
import com.tricon.labs.crumbs.activities.lendandborrow.ActivityLendAndBorrowAddEntry;
import com.tricon.labs.crumbs.activities.personalexpense.ActivityPersonalExpenseAddEntry;
import com.tricon.labs.crumbs.adapters.AdapterViewPager;


public class ActivityHome extends AppCompatActivity {

    private boolean backPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.widget_toolbar);
        setSupportActionBar(toolbar);

        ViewPager vpExpenseModule = (ViewPager) findViewById(R.id.vp_expense_module);
        setupViewPager(vpExpenseModule);

        vpExpenseModule.addOnPageChangeListener(new pageChangeListener());

        final TabLayout tlExpenseModule = (TabLayout) findViewById(R.id.tl_expense_module);
        tlExpenseModule.setupWithViewPager(vpExpenseModule);

        //tlExpenseModule.setTabMode(TabLayout.MODE_SCROLLABLE);

        findViewById(R.id.btn_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                switch (tlExpenseModule.getSelectedTabPosition()) {
                    case 0:
                        i = new Intent(ActivityHome.this, ActivityPersonalExpenseAddEntry.class);
                        startActivity(i);
                        break;
                    case 1:
                        i = new Intent(ActivityHome.this, ActivityLendAndBorrowAddEntry.class);
                        startActivity(i);
                        break;
                    case 2:
                        i = new Intent(ActivityHome.this, ActivityAddOrEditGroup.class);
                        startActivity(i);
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        backPressed = false;
    }

    @Override
    public void onBackPressed() {
        if (backPressed) {
            super.onBackPressed();
        } else {
            backPressed = true;
            Toast.makeText(this, "Press back again to close the app", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        AdapterViewPager adapter = new AdapterViewPager(getSupportFragmentManager());

        adapter.addFrag(new FragmentPersonalExpense(), "Personal exp");
        adapter.addFrag(new FragmentLendAndBorrow(), "Lend and borrow");
        adapter.addFrag(new FragmentGroupExpense(), "Group exp"); //next face

        viewPager.setAdapter(adapter);
    }

    private class pageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

            switch (position) {
                case 0:
                    ((FloatingActionButton) findViewById(R.id.btn_create)).setImageResource(R.drawable.icon_personal_stuff);
                    break;
                case 1:
                    ((FloatingActionButton) findViewById(R.id.btn_create)).setImageResource(R.drawable.add_user_24);
                    break;
                case 2:
                    ((FloatingActionButton) findViewById(R.id.btn_create)).setImageResource(R.drawable.add_user_group);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}

