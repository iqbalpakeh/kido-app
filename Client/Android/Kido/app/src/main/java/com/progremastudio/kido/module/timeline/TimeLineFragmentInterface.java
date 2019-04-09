/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.timeline;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.progremastudio.kido.R;
import com.progremastudio.kido.core.InterfaceOnFragmentAttach;
import com.progremastudio.kido.models.Baby;
import com.progremastudio.kido.module.diaper.DiaperDialog;
import com.progremastudio.kido.module.disease.DiseaseDialog;
import com.progremastudio.kido.module.feeding.FeedingDialog;
import com.progremastudio.kido.module.measurement.MeasurementDialog;
import com.progremastudio.kido.module.sleep.SleepDialog;
import com.progremastudio.kido.module.vaccine.VaccineDialog;
import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.core.InterfaceHistoryFragment;
import com.progremastudio.kido.util.ActiveContext;
import com.progremastudio.kido.widget.HistoryFragment;
import com.progremastudio.kido.widget.ObserveableListView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class TimeLineFragmentInterface extends HistoryFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, InterfaceHistoryFragment, TimelineAdapter.Callback {

    private static final int LOADER_LIST_VIEW = 0;
    private TextView nameHandler;
    private TextView birthdayHandler;
    private TextView ageHandler;
    private TextView sexHandler;
    private TextView infoTimeFilter;
    private ImageView imageHandler;
    private TimelineAdapter adapter;
    private ObserveableListView timelineHistoryList;
    private View root;
    private View placeholder;
    private InterfaceOnFragmentAttach listener;

    public static TimeLineFragmentInterface getInstance() {
        return new TimeLineFragmentInterface();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        prepareFragment(inflater, container);
        attachView();
        prepareListView();
        prepareLoaderManager();
        inflateDefaultTimeFilter();
        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (InterfaceOnFragmentAttach) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement InterfaceOnFragmentAttach");
        }
    }

    @Override
    public void prepareFragment(LayoutInflater inflater, ViewGroup container) {
        root = inflater.inflate(R.layout.fragment_timeline, container, false);
        placeholder = inflater.inflate(R.layout.placeholder_header, null);
        super.attachQuickReturnView(root, R.id.header_container);
        super.attachPlaceHolderLayout(placeholder, R.id.placeholder_header);
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(getResources().getDrawable(R.drawable.ic_timeline_top));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listener.prepareQuickButton();
    }

    @Override
    public void attachView() {
        nameHandler = (TextView) root.findViewById(R.id.name_content);
        birthdayHandler = (TextView) root.findViewById(R.id.birthday_content);
        ageHandler = (TextView) root.findViewById(R.id.age_content);
        sexHandler = (TextView) root.findViewById(R.id.sex_content);
        imageHandler = (ImageView) root.findViewById(R.id.baby_image);
        infoTimeFilter = (TextView) root.findViewById(R.id.time_filter);
    }

    @Override
    public void prepareListView() {
        timelineHistoryList = (ObserveableListView) root.findViewById(R.id.activity_list);
        adapter = new TimelineAdapter(getActivity(), null, 0);
        adapter.setCallback(this);
        timelineHistoryList.addHeaderView(placeholder);
        timelineHistoryList.setAdapter(adapter);
        super.attachListView(timelineHistoryList);
    }

    @Override
    public void prepareLoaderManager() {
        Bundle bundle = new Bundle();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String keyValue = sharedPref.getString(getString(R.string.var_KEY_DEF_TIME_FILTER_TIMELINE), getString(R.string.str_This_week));
        bundle.putInt("FILTER_POSITION", getPosition(getActivity(), keyValue));
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_LIST_VIEW, bundle, this);
    }

    @Override
    public void onTimelineEntryEditSelected(View entry) {
        String id = (String) entry.getTag(R.id.widget_first);
        String type = (String) entry.getTag(R.id.widget_second);
        if (type.equals(Contract.Activity.TYPE_SLEEP)) {
            editSleepEntry(id);
        } else if (type.equals(Contract.Activity.TYPE_DIAPER)) {
            editDiaperEntry(id);
        } else if (type.equals(Contract.Activity.TYPE_FEEDING)) {
            editFeedingEntry(id);
        } else if (type.equals(Contract.Activity.TYPE_MEASUREMENT)) {
            editMeasurementEntry(id);
        } else if (type.equals(Contract.Activity.TYPE_DISEASE)) {
            editDiseaseEntry(id);
        } else if (type.equals(Contract.Activity.TYPE_VACCINE)) {
            editVaccineEntry(id);
        }
    }

    private void editSleepEntry(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "EDIT");
        bundle.putString("TAG_ACTIVITY", id);
        SleepDialog dialog = SleepDialog.getInstance();
        dialog.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        dialog.show(transaction, "SLEEP_DIALOG");
    }

    private void editDiaperEntry(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "EDIT");
        bundle.putString("TAG_ACTIVITY", id);
        DiaperDialog dialog = DiaperDialog.getInstance();
        dialog.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        dialog.show(transaction, "DIAPER_DIALOG");
    }

    private void editFeedingEntry(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "EDIT");
        bundle.putString("TAG_ACTIVITY", id);
        FeedingDialog dialog = FeedingDialog.getInstance();
        dialog.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        dialog.show(transaction, "FEEDING_DIALOG");
    }

    private void editMeasurementEntry(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "EDIT");
        bundle.putString("TAG_ACTIVITY", id);
        MeasurementDialog dialog = MeasurementDialog.getInstance();
        dialog.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        dialog.show(transaction, "MEASUREMENT_DIALOG");
    }

    private void editDiseaseEntry(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "EDIT");
        bundle.putString("TAG_ACTIVITY", id);
        DiseaseDialog fragment = DiseaseDialog.getInstance();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .commit();
    }

    private void editVaccineEntry(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "EDIT");
        bundle.putString("TAG_ACTIVITY", id);
        VaccineDialog fragment = VaccineDialog.getInstance();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        Baby baby = ActiveContext.getActiveBaby(getActivity());
        nameHandler.setText(getResources().getString(R.string.str_Name_is) + " " + baby.getName());
        birthdayHandler.setText(getResources().getString(R.string.str_Birthday_on) + " " + baby.getBirthdayInReadableFormat(getActivity()));
        ageHandler.setText(getResources().getString(R.string.str_Age_is) + " " + baby.getAgeInReadableFormat(getActivity()));
        sexHandler.setText(getResources().getString(R.string.str_Gender_is) + " " + baby.getSex().getTitle());
        showBabyImage(baby, getActivity());
    }

    private void showBabyImage(Baby baby, Context context) {
        Uri image;
        Bitmap bitmap;
        InputStream inputStream = null;
        try {
            image = baby.getPicture();
            inputStream = context.getContentResolver().openInputStream(image);
        } catch (FileNotFoundException ex) {
            Log.d("_DBG_IMAGE", "No image found for " + baby.getName());
        } catch (Exception error) {
            Log.e("_DBG_IMAGE", Log.getStackTraceString(error));
        }
        bitmap = BitmapFactory.decodeStream(inputStream, null, null);
        imageHandler.setImageBitmap(bitmap);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        int filterPosition = bundle.getInt("FILTER_POSITION");
        String[] timeFilterArg = createTimeFilter(getActivity(), filterPosition);
        switch (loaderId) {
            case LOADER_LIST_VIEW:
                return new CursorLoader(getActivity(),
                        Contract.Activity.CONTENT_URI,
                        Contract.Activity.Query.PROJECTION,
                        "baby_id = ? AND timestamp >= ? AND timestamp <= ?",
                        timeFilterArg,
                        Contract.Activity.Query.SORT_BY_TIMESTAMP_DESC);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor.getCount() >= 0) {
            cursor.moveToFirst();
            switch (cursorLoader.getId()) {
                case LOADER_LIST_VIEW:
                    adapter.swapCursor(cursor);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        if (cursorLoader.getId() == LOADER_LIST_VIEW) {
            adapter.swapCursor(null);
        }
    }

    @Override
    public void restartLoaderFilterToday() {
        Bundle bundle = new Bundle();
        bundle.putInt("FILTER_POSITION", TIME_FILTER_POSITION_TODAY);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(LOADER_LIST_VIEW, bundle, this);
        infoTimeFilter.setText(getString(R.string.str_for_today));
    }

    @Override
    public void restartLoaderFilterAll() {
        Bundle bundle = new Bundle();
        bundle.putInt("FILTER_POSITION", TIME_FILTER_POSITION_ALL);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(LOADER_LIST_VIEW, bundle, this);
        infoTimeFilter.setText(getString(R.string.str_all));
    }

    @Override
    public void restartLoaderFilterThisWeek() {
        Bundle bundle = new Bundle();
        bundle.putInt("FILTER_POSITION", TIME_FILTER_POSITION_THIS_WEEK);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(LOADER_LIST_VIEW, bundle, this);
        infoTimeFilter.setText(getString(R.string.str_for_this_week));
    }

    @Override
    public void restartLoaderFilterThisMonth() {
        Bundle bundle = new Bundle();
        bundle.putInt("FILTER_POSITION", TIME_FILTER_POSITION_THIS_MONTH);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(LOADER_LIST_VIEW, bundle, this);
        infoTimeFilter.setText(getString(R.string.str_for_this_month));
    }

    private void inflateDefaultTimeFilter() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String keyValue = sharedPref.getString(getString(R.string.var_KEY_DEF_TIME_FILTER_TIMELINE), getString(R.string.str_for_this_week));
        infoTimeFilter.setText(keyValue);
    }
}
