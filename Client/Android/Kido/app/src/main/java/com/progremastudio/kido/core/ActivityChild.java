/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.core;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.progremastudio.kido.R;
import com.progremastudio.kido.module.sleep.SleepStopwatch;
import com.progremastudio.kido.module.feeding.FeedingStopwatch;
import com.progremastudio.kido.widget.AlarmFragment;

public class ActivityChild extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().getStringExtra("FRAGMENT").equals("ABOUT")) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.support_activity_container, FragmentAbout.getInstance())
                    .commit();
        } else if (getIntent().getStringExtra("FRAGMENT").equals("SETTING")) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.support_activity_container, FragmentPreference.getInstance())
                    .commit();
        } else if (getIntent().getStringExtra("FRAGMENT").equals("FEEDING_STOPWATCH")){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.support_activity_container, FeedingStopwatch.getInstance())
                    .commit();
        } else if (getIntent().getStringExtra("FRAGMENT").equals("SLEEPING_STOPWATCH")){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.support_activity_container, SleepStopwatch.getInstance())
                    .commit();
        } else if (getIntent().getStringExtra("FRAGMENT").equals("ALARM")){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.support_activity_container, AlarmFragment.getInstance())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }
}
