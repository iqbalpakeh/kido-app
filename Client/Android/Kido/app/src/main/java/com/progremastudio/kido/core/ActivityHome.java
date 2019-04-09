package com.progremastudio.kido.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.progremastudio.kido.R;
import com.progremastudio.kido.module.diaper.DiaperDialog;
import com.progremastudio.kido.module.diaper.DiaperFragment;
import com.progremastudio.kido.module.disease.DiseaseDialog;
import com.progremastudio.kido.module.disease.DiseaseFragment;
import com.progremastudio.kido.module.feeding.FeedingDialog;
import com.progremastudio.kido.module.feeding.FeedingFragment;
import com.progremastudio.kido.module.growingchart.BodyHeightFragment;
import com.progremastudio.kido.module.growingchart.BodyWeightFragment;
import com.progremastudio.kido.module.growingchart.HeadCircumferenceFragment;
import com.progremastudio.kido.module.measurement.MeasurementDialog;
import com.progremastudio.kido.module.measurement.MeasurementFragment;
import com.progremastudio.kido.module.sleep.SleepDialog;
import com.progremastudio.kido.module.sleep.SleepFragment;
import com.progremastudio.kido.module.vaccine.VaccineDialog;
import com.progremastudio.kido.module.vaccine.VaccineFragment;
import com.progremastudio.kido.util.ActiveContext;
import com.progremastudio.kido.util.TextFormation;

import java.util.Calendar;

public class ActivityHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ACTIVITY_HOME";

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // set today on active context
        String timestamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
        String day = TextFormation.date(this, timestamp);
        ActiveContext.setDayFilter(this, day);

        // prepare layout
        setContentView(R.layout.activity_home_pro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.str_Timeline));
        setSupportActionBar(toolbar);

        // prepare drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_navigation_drawer_open, R.string.app_navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // prepare navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        // prepare floating action button
        FloatingActionButton fabFeeding = (FloatingActionButton) findViewById(R.id.fab_feeding);
        fabFeeding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFeedingItem();
            }
        });
        FloatingActionButton fabDiaper = (FloatingActionButton) findViewById(R.id.fab_diaper);
        fabDiaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDiaperItem();
            }
        });
        FloatingActionButton fabSleep = (FloatingActionButton) findViewById(R.id.fab_sleep);
        fabSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSleepItem();
            }
        });
        FloatingActionButton fabMeasurement = (FloatingActionButton) findViewById(R.id.fab_measurement);
        fabMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMeasurementItem();
            }
        });
        FloatingActionButton fabDisease = (FloatingActionButton) findViewById(R.id.fab_disease);
        fabDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDiseaseItem();
            }
        });
        FloatingActionButton fabVaccine = (FloatingActionButton) findViewById(R.id.fab_vaccine);
        fabVaccine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addVaccineItem();
            }
        });

        ActiveContext.setCurrentFragment(this, "");
    }

    private void addFeedingItem() {
        logEventToFireBase(getString(R.string.FBLog_Press_feeding_button));
        ((FloatingActionsMenu) this.findViewById(R.id.fab_menu)).collapse();
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "CREATE");
        FeedingDialog dialog = FeedingDialog.getInstance();
        dialog.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        dialog.show(fragmentTransaction, "DIAPER_DIALOG");
    }

    private void addDiaperItem() {
        logEventToFireBase(getString(R.string.FBLog_Press_diaper_button));
        ((FloatingActionsMenu) this.findViewById(R.id.fab_menu)).collapse();
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "CREATE");
        DiaperDialog diaperChoiceBox = DiaperDialog.getInstance();
        diaperChoiceBox.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        diaperChoiceBox.show(fragmentTransaction, "DIAPER_DIALOG");
    }

    private void addSleepItem() {
        logEventToFireBase(getString(R.string.FBLog_Press_sleep_button));
        ((FloatingActionsMenu) this.findViewById(R.id.fab_menu)).collapse();
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "CREATE");
        SleepDialog sleepDialog = SleepDialog.getInstance();
        sleepDialog.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        sleepDialog.show(fragmentTransaction, "DIAPER_DIALOG");
    }

    private void addMeasurementItem() {
        ((FloatingActionsMenu) this.findViewById(R.id.fab_menu)).collapse();
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "CREATE");
        MeasurementDialog measurementDialog = MeasurementDialog.getInstance();
        measurementDialog.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        measurementDialog.show(fragmentTransaction, "MEASUREMENT_DIALOG");
    }

    private void addDiseaseItem() {
        ((FloatingActionsMenu) this.findViewById(R.id.fab_menu)).collapse();
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "CREATE");
        DiseaseDialog fragment = DiseaseDialog.getInstance();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .commit();
    }

    private void addVaccineItem() {
        ((FloatingActionsMenu) this.findViewById(R.id.fab_menu)).collapse();
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "CREATE");
        VaccineDialog fragment = VaccineDialog.getInstance();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ActivitySupport.class);
            intent.putExtra("FRAGMENT", "SETTING");
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(this, ActivitySupport.class);
            intent.putExtra("FRAGMENT", "ABOUT");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d(TAG, "@onNavigationItemSelected");
        int id = item.getItemId();
        if (id == R.id.nav_timeline) {
            inflateTimelineFragment();
        } else if (id == R.id.nav_feeding) {
            inflateFeedingFragment();
        } else if (id == R.id.nav_diaper) {
            inflateDiaperFragment();
        } else if (id == R.id.nav_sleep) {
            inflateSleepFragment();
        } else if (id == R.id.nav_measurement) {
            inflateGrowthFragment();
        } else if (id == R.id.nav_disease) {
            inflateDiseaseFragment();
        } else if (id == R.id.nav_vaccine) {
            inflateVaccineFragment();
        } else if (id == R.id.nav_body_height) {
            inflateBodyHeightFragment();
        } else if (id == R.id.nav_body_weight) {
            inflateBodyWeightFragment();
        } else if (id == R.id.nav_head_circumference) {
            inflateHeadCircumferenceFragment();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void inflateTimelineFragment() {
        getSupportActionBar().setTitle(getString(R.string.str_Timeline));
    }

    private void inflateFeedingFragment() {
        getSupportActionBar().setTitle(getString(R.string.str_Feeding));
        ActiveContext.setCurrentFragment(this, getString(R.string.str_Feeding));
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, FeedingFragment.getInstance(), getString(R.string.str_Feeding))
                .commit();
    }

    private void inflateDiaperFragment() {
        getSupportActionBar().setTitle(getString(R.string.str_Diaper));
        ActiveContext.setCurrentFragment(this, getString(R.string.str_Diaper));
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, DiaperFragment.getInstance(), getString(R.string.str_Diaper))
                .commit();
    }

    private void inflateSleepFragment() {
        getSupportActionBar().setTitle(getString(R.string.str_Sleep));
        ActiveContext.setCurrentFragment(this, getString(R.string.str_Sleep));
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, SleepFragment.getInstance(), getString(R.string.str_Sleep))
                .commit();
    }

    private void inflateGrowthFragment() {
        getSupportActionBar().setTitle(getString(R.string.str_Growth));
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, MeasurementFragment.getInstance(), getString(R.string.str_Growth))
                .commit();
    }

    private void inflateDiseaseFragment() {
        getSupportActionBar().setTitle(getString(R.string.str_Disease));
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, DiseaseFragment.getInstance(), getString(R.string.str_Disease))
                .commit();
    }

    private void inflateVaccineFragment() {
        getSupportActionBar().setTitle(getString(R.string.str_Vaccine));
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, VaccineFragment.getInstance(), getString(R.string.str_Vaccine))
                .commit();
    }

    private void inflateBodyHeightFragment() {
        getSupportActionBar().setTitle(getString(R.string.str_Body_Height));
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, BodyHeightFragment.getInstance(), getString(R.string.str_Body_Height))
                .commit();
    }

    private void inflateBodyWeightFragment() {
        getSupportActionBar().setTitle(getString(R.string.str_Body_Weight));
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, BodyWeightFragment.getInstance(), getString(R.string.str_Body_Weight))
                .commit();
    }

    private void inflateHeadCircumferenceFragment() {
        getSupportActionBar().setTitle(getString(R.string.str_Head_Circumference));
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, HeadCircumferenceFragment.getInstance(), getString(R.string.str_Head_Circumference))
                .commit();
    }

    private void logEventToFireBase(String eventName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}
