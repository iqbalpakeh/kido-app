/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.core;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.progremastudio.kido.R;

public class ActivitySupport extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().getStringExtra("FRAGMENT").equals("ABOUT")) {
            getSupportActionBar().setTitle(getString(R.string.str_About));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.support_activity_container, FragmentAbout.getInstance())
                    .commit();
        } else if (getIntent().getStringExtra("FRAGMENT").equals("SETTING")) {
            getSupportActionBar().setTitle(getString(R.string.str_Settings));
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.support_activity_container, FragmentPreference.getInstance())
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
