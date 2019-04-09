/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.vaccine;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

public class VaccineFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        VaccineAdapter.VaccineViewHolder.Callback {

    private VaccineAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private LoaderManager loaderManager;
    private View view;

    public static VaccineFragment getInstance() {
        return new VaccineFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_vaccine, container, false);
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
        adapter = new VaccineAdapter(getActivity());
        adapter.setCallback(this);
    }

    private void prepareRecyclerView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
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
                Contract.Vaccine.CONTENT_URI,
                Contract.Vaccine.Query.PROJECTION,
                "baby_id = ? AND timestamp >= ? AND timestamp <= ?",
                arguments,
                Contract.Vaccine.Query.SORT_BY_ACTIVITY_ID_DESC);
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
    public void onVaccineEntryEditSelected(View entry) {
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "EDIT");
        bundle.putString("TAG_ACTIVITY", entry.getTag(R.id.vaccine_dialog).toString());
        bundle.putString("TAG_TIMESTAMP", entry.getTag(R.id.vaccine_timestamp_button).toString());
        bundle.putString("TAG_NAME", entry.getTag(R.id.vaccine_name_content).toString());
        bundle.putString("TAG_LOCATION", entry.getTag(R.id.vaccine_location_content).toString());
        bundle.putString("TAG_NOTES", entry.getTag(R.id.vaccine_notes_content).toString());
        VaccineDialog fragment = VaccineDialog.getInstance();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .commit();
    }
}
