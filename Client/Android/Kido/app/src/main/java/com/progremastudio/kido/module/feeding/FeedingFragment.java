/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.feeding;

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

public class FeedingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        FeedingAdapter.FeedingViewHolder.Callback {

    private FeedingAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private LoaderManager loaderManager;
    private View view;

    public static FeedingFragment getInstance() {
        return new FeedingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feeding, container, false);
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
        adapter = new FeedingAdapter(getActivity());
        adapter.setCallback(this);
    }

    private void prepareRecyclerView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.feeding_recycler_view);
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
                Contract.Feeding.CONTENT_URI,
                Contract.Feeding.Query.PROJECTION,
                "baby_id = ? AND timestamp >= ? AND timestamp <= ?",
                arguments,
                Contract.Feeding.Query.SORT_BY_ACTIVITY_ID_DESC);
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
    public void onFeedingEntryEditSelected(View entry) {
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "EDIT");
        bundle.putString("TAG_ACTIVITY", entry.getTag().toString());
        FeedingDialog dialog = FeedingDialog.getInstance();
        dialog.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        dialog.show(fragmentTransaction, "SLEEP_DIALOG");
    }
}
