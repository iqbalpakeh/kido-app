/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.measurement;

import android.database.Cursor;
import android.os.Bundle;
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

public class MeasurementFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        MeasurementAdapter.MeasurementViewHolder.Callback{

    private MeasurementAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private LoaderManager loaderManager;
    private View view;

    public static MeasurementFragment getInstance() {
        return new MeasurementFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_growth, container, false);
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
        adapter = new MeasurementAdapter(getActivity());
        adapter.setCallBack(this);
    }

    private void prepareRecyclerView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.growth_recycler_view);
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
        String[] arguments = ActiveContext.createTimeFilter(getActivity(), ActiveContext.FOR_THIS_WEEK);
        return new CursorLoader(getActivity(),
                Contract.Measurement.CONTENT_URI,
                Contract.Measurement.Query.PROJECTION,
                "baby_id = ? AND timestamp >= ? AND timestamp <= ?",
                arguments,
                Contract.Measurement.Query.SORT_BY_ACTIVITY_ID_DESC);
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
    public void onMeasurementEntryEditSelected(View entry) {
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "EDIT");
        bundle.putString("TAG_ACTIVITY", entry.getTag().toString());
        MeasurementDialog measurementDialog = MeasurementDialog.getInstance();
        measurementDialog.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        measurementDialog.show(fragmentTransaction, "MEASUREMENT_DIALOG");
    }

}
