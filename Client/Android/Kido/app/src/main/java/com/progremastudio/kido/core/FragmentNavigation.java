/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.core;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

//import com.facebook.Session;
import com.progremastudio.kido.R;
import com.progremastudio.kido.navigation.Baby;
import com.progremastudio.kido.navigation.Divider;
import com.progremastudio.kido.navigation.Item;
import com.progremastudio.kido.navigation.NavigationAdapter;
import com.progremastudio.kido.navigation.StandardItem;
import com.progremastudio.kido.models.BaseActor;
import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.util.ActiveContext;

import java.util.ArrayList;

public class FragmentNavigation extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final int LOADER_BABY_LIST = 0;
    private NavigationDrawerCallbacks callbacks;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ListView root;
    private View fragmentContainerView;
    private NavigationAdapter adapter;
    private ArrayList<Item> items;
    private int currentPosition = 0;
    private int actionPositionOffset = 0;
    private int lastActivityClicked = 0;
    private boolean fromSavedInstanceState;
    private boolean userLearnedDrawer;

    public FragmentNavigation() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        userLearnedDrawer = sharedPreferences.getBoolean(PREF_USER_LEARNED_DRAWER, false);
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            fromSavedInstanceState = true;
        }
        ActiveContext.clearCurrentFragment(getActivity());
        selectItem(currentPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = (ListView) inflater.inflate(R.layout.fragment_drawer, container, false);
        root.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_BABY_LIST, getArguments(), this);
        return root;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_BABY_LIST:
                return new CursorLoader(getActivity(),
                        Contract.Baby.CONTENT_URI,
                        Contract.Baby.Query.PROJECTION,
                        null,
                        null,
                        Contract.Baby.NAME);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            switch (loader.getId()) {
                case LOADER_BABY_LIST:
                    inflateNavigationList(cursor);
                    break;
            }
        }
    }

    private void inflateNavigationList(Cursor cursor) {
        items = new ArrayList<Item>();
        items.add(new Divider(getString(R.string.str_Parent)));

        //TODO: Comment for temporary before multi user can be used!!
        //StandardItem userEntry = new StandardItem(ActiveContext.getActiveUser(getActivity()).getName());
        StandardItem userEntry = new StandardItem("You");
        userEntry.setThumbnail(getResources().getDrawable(R.drawable.ic_user));
        userEntry.setTextColor(getResources().getColor(R.color.default_font));

        items.add(userEntry);
        items.add(new Divider(getString(R.string.str_Baby)));
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String type = cursor.getString(Contract.Baby.Query.OFFSET_SEX);
            Baby baby = new Baby(cursor.getString(Contract.Baby.Query.OFFSET_NAME));
            if (type.equals(BaseActor.Sex.FEMALE.getTitle())) {
                baby.setThumbnail(getResources().getDrawable(R.drawable.ic_baby_girl));
            } else if (type.equals(BaseActor.Sex.MALE.getTitle())) {
                baby.setThumbnail(getResources().getDrawable(R.drawable.ic_baby_boy));
            }
            baby.setTextColor(getResources().getColor(R.color.default_font));
            items.add(baby);
        }
        items.add(new Divider(getString(R.string.str_Activity_Log)));

        actionPositionOffset = items.size();
        if (lastActivityClicked == 0) {
            lastActivityClicked = actionPositionOffset;
        }

        StandardItem timeline = new StandardItem(getString(R.string.str_Timeline));
        timeline.setThumbnail(getResources().getDrawable(R.drawable.ic_timeline_navigation));
        timeline.setTextColor(getResources().getColor(R.color.default_font));
        items.add(timeline);

        StandardItem feeding = new StandardItem(getString(R.string.str_Feeding));
        feeding.setThumbnail(getResources().getDrawable(R.drawable.ic_feeding_navigation));
        feeding.setTextColor(getResources().getColor(R.color.default_font));
        items.add(feeding);

        StandardItem diaper = new StandardItem(getString(R.string.str_Diaper));
        diaper.setThumbnail(getResources().getDrawable(R.drawable.ic_diaper_navigation));
        diaper.setTextColor(getResources().getColor(R.color.default_font));
        items.add(diaper);

        StandardItem sleep = new StandardItem(getString(R.string.str_Sleep));
        sleep.setThumbnail(getResources().getDrawable(R.drawable.ic_sleep_navigation));
        sleep.setTextColor(getResources().getColor(R.color.default_font));
        items.add(sleep);

        StandardItem measurement = new StandardItem(getString(R.string.str_Growth));
        measurement.setThumbnail(getResources().getDrawable(R.drawable.ic_measurement_navigation));
        measurement.setTextColor(getResources().getColor(R.color.default_font));
        items.add(measurement);

        StandardItem disease = new StandardItem(getString(R.string.str_Disease));
        disease.setThumbnail(getResources().getDrawable(R.drawable.ic_disease_navigation));
        disease.setTextColor(getResources().getColor(R.color.default_font));
        items.add(disease);

        StandardItem vaccine = new StandardItem(getString(R.string.str_Vaccine));
        vaccine.setThumbnail(getResources().getDrawable(R.drawable.ic_vaccine_navigation));
        vaccine.setTextColor(getResources().getColor(R.color.default_font));
        items.add(vaccine);

        items.add(new Divider(getString(R.string.str_Growing_Chart)));

        StandardItem bodyHeightForAge = new StandardItem(getString(R.string.str_Body_Height));
        bodyHeightForAge.setThumbnail(getResources().getDrawable(R.drawable.ic_body_height_chart));
        bodyHeightForAge.setTextColor(getResources().getColor(R.color.default_font));
        items.add(bodyHeightForAge);

        StandardItem bodyWeightForAge = new StandardItem(getString(R.string.str_Body_Weight));
        bodyWeightForAge.setThumbnail(getResources().getDrawable(R.drawable.ic_body_weight_chart));
        bodyWeightForAge.setTextColor(getResources().getColor(R.color.default_font));
        items.add(bodyWeightForAge);

        StandardItem headCircumferenceForAge = new StandardItem(getString(R.string.str_Head_Circumference));
        headCircumferenceForAge.setThumbnail(getResources().getDrawable(R.drawable.ic_head_circumference_chart));
        headCircumferenceForAge.setTextColor(getResources().getColor(R.color.default_font));
        items.add(headCircumferenceForAge);

        adapter = new NavigationAdapter(getActivity(), items);
        root.setAdapter(adapter);
        root.setItemChecked(currentPosition, true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        fragmentContainerView = getActivity().findViewById(fragmentId);
        this.drawerLayout = drawerLayout;
        this.drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                FragmentNavigation.this.drawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.str_Open_navigation_drawer,  /* "open drawer" description for accessibility */
                R.string.str_Close_navigation_drawer  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;
                getActionBar().setTitle(ActiveContext.getCurrentFragment(getActivity()));
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;
                if (!userLearnedDrawer) {
                    userLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }
                getActivity().invalidateOptionsMenu();
            }
        };
        if (!userLearnedDrawer && !fromSavedInstanceState) this.drawerLayout.openDrawer(fragmentContainerView);
        this.drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });
        this.drawerLayout.setDrawerListener(drawerToggle);
    }

    private void selectItem(int position) {
        currentPosition = position;
        if (root != null) root.setItemChecked(position, true);
        if (drawerLayout != null) drawerLayout.closeDrawer(fragmentContainerView);
        if (callbacks != null) {
            if ((position < actionPositionOffset) && (items != null) && (items.get(position) instanceof Baby)) {
                ActiveContext.setActiveBaby(getActivity(), items.get(position).getText());
                ActiveContext.clearCurrentFragment(getActivity());
                adapter.notifyDataSetChanged();
                callbacks.onNavigationDrawerItemSelected(lastActivityClicked, actionPositionOffset);
            } else if (position >= actionPositionOffset) {
                lastActivityClicked = position;
                callbacks.onNavigationDrawerItemSelected(position, actionPositionOffset);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, currentPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (drawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.menu_global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {

            case R.id.action_new_baby:
                handleCreateBabyEntry();
                return true;

            case R.id.action_edit_baby:
                handleEditBabyEntry();
                return true;

            case R.id.action_delete_baby:
                handleDeleteBabyEntry();
                return true;

            case R.id.action_settings:
                handleShowSetting();
                return true;

            case R.id.action_about:
                handleShowAbout();
                return true;

            /*
            case R.id.action_logout:
                //todo: activate logout action!
                logout();
                return true;
            */
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleShowAbout() {
        Intent intent = new Intent(getActivity(), ActivityChild.class);
        intent.putExtra("FRAGMENT", "ABOUT");
        startActivity(intent);
    }

    private void handleShowSetting() {
        Intent intent = new Intent(getActivity(), ActivityChild.class);
        intent.putExtra("FRAGMENT", "SETTING");
        startActivity(intent);
    }

    private void handleCreateBabyEntry() {
        ActiveContext.clearCurrentFragment(getActivity());
        Intent intent = new Intent(getActivity(), ActivityLogin.class);
        intent.putExtra(ActivityLogin.INTENT_NEW_BABY_REQUEST, true);
        startActivity(intent);
    }

    private void handleDeleteBabyEntry() {
        Bundle bundle = new Bundle();
        bundle.putString("DELETE_OR_EDIT", "DELETE");
        DialogBabyDeleteEdit dialogBabyDeleteEdit = DialogBabyDeleteEdit.getInstance();
        dialogBabyDeleteEdit.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        dialogBabyDeleteEdit.show(fragmentTransaction, "BABY_DELETE_EDIT_DIALOG");
    }

    private void handleEditBabyEntry() {
        Bundle bundle = new Bundle();
        bundle.putString("DELETE_OR_EDIT", "EDIT");
        DialogBabyDeleteEdit dialogBabyDeleteEdit = DialogBabyDeleteEdit.getInstance();
        dialogBabyDeleteEdit.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        dialogBabyDeleteEdit.show(fragmentTransaction, "BABY_DELETE_EDIT_DIALOG");
    }

    /*
    private void logout() {
        Session session = Session.getActiveSession();
        if (session != null) {
            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
            }
        } else {
            session = new Session(getActivity());
            Session.setActiveSession(session);
            session.closeAndClearTokenInformation();
        }
        clearSharedPreference();
        getActivity().finish();
    }
    */

    private void clearSharedPreference() {
        SharedPreferences setting = getActivity().getSharedPreferences(ActivityLogin.FLAG_LOGIN, 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putBoolean(ActivityLogin.FLAG_SKIP_LOGIN, false);
        editor.commit();
    }

    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    public static interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(int position, int calibration);
    }
}
