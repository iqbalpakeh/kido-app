/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.sleep;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.progremastudio.kido.R;
import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.util.ActiveContext;

public class SleepFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SleepAdapter.SleepViewHolder.Callback {

    private SleepAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private LoaderManager loaderManager;
    private View view;

    public static SleepFragment getInstance() {
        return new SleepFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sleep, container, false);
        prepareLayoutManager();
        prepareAdapter();
        prepareRecyclerView();
        prepareLoaderManager();
        return view;
    }

    private void prepareLayoutManager() {
        layoutManager = new LinearLayoutManager(getActivity());
    }

    private void prepareAdapter() {
        adapter = new SleepAdapter(getActivity());
        adapter.setCallBack(this);
    }

    private void prepareRecyclerView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.sleep_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void prepareLoaderManager() {
        loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int timeFilter = ActiveContext.getPreferenceTimeFilter(getActivity(),
                getString(R.string.var_KEY_DEF_TIME_FILTER_FEEDING));
        String[] arguments = ActiveContext.createTimeFilter(getActivity(), timeFilter);
        return new CursorLoader(getActivity(),
                Contract.Sleep.CONTENT_URI,
                Contract.Sleep.Query.PROJECTION,
                "baby_id = ? AND timestamp >= ? AND timestamp <= ?",
                arguments,
                Contract.Sleep.Query.SORT_BY_ACTIVITY_ID_DESC);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.getCount() >= 0) {
            cursor.moveToFirst();
            adapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onSleepEntryEditSelected(View entry) {
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "EDIT");
        bundle.putString("TAG_ACTIVITY", entry.getTag().toString());
        SleepDialog sleepDialog = SleepDialog.getInstance();
        sleepDialog.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        sleepDialog.show(fragmentTransaction, "SLEEP_DIALOG");
    }
}
