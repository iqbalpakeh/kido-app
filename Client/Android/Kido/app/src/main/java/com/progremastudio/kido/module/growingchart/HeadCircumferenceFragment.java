/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.growingchart;

import android.app.ActionBar;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.progremastudio.kido.R;
import com.progremastudio.kido.models.BaseActor;
import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.util.ActiveContext;
import com.progremastudio.kido.util.AgeCalculator;

import java.util.Calendar;

public class HeadCircumferenceFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private View root;
    private GrowingChart growingChart;
    private ChartData babyChart;

    public static HeadCircumferenceFragment getInstance() {
        return new HeadCircumferenceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prepareFragment(inflater, container);
        prepareLoaderManager();
        prepareGrowingChart();
        return root;
    }

    private void prepareFragment(LayoutInflater inflater, ViewGroup container) {
        setHasOptionsMenu(true);
        root = inflater.inflate(R.layout.fragment_head_circumference, null);
//        ActionBar actionBar = getActivity().getActionBar();
//        actionBar.setIcon(getResources().getDrawable(R.drawable.ic_chart_top));
    }

    private void prepareGrowingChart() {
        growingChart = new GrowingChart(root, getActivity());
        if (ActiveContext.getActiveBaby(getActivity()).getSex().getTitle().equals(BaseActor.Sex.FEMALE.getTitle())) {
            prepareGirZeroToFiveGrowingChart();
        } else {
            prepareBoysZeroToFiveGrowingChart();
        }
    }

    private void prepareGirZeroToFiveGrowingChart() {
        growingChart.clearReferenceData();
        growingChart.setMonths(5 * 12);
        growingChart.setStartMonth(0);
        growingChart.setUnit(" cm");
        growingChart.setDescription(getString(R.string.str_Percentiles_of_head_circumference_for_age_girls_aged_0_5_years));

        ChartData chartData3rd = new ChartData();
        chartData3rd.setYValues(getResources().getStringArray(R.array.head_growth_girl_zero_to_five_percentile_3rd));
        chartData3rd.setLegend("3rd     ");
        chartData3rd.setColor(getResources().getColor(R.color.red));
        growingChart.addChartData(chartData3rd);

        ChartData chartData15th = new ChartData();
        chartData15th.setYValues(getResources().getStringArray(R.array.head_growth_girl_zero_to_five_percentile_15th));
        chartData15th.setLegend("15th     ");
        chartData15th.setColor(getResources().getColor(R.color.orange));
        growingChart.addChartData(chartData15th);

        ChartData chartData50th = new ChartData();
        chartData50th.setYValues(getResources().getStringArray(R.array.head_growth_girl_zero_to_five_percentile_50th));
        chartData50th.setLegend("50th     ");
        chartData50th.setColor(getResources().getColor(R.color.green));
        growingChart.addChartData(chartData50th);

        ChartData chartData85th = new ChartData();
        chartData85th.setYValues(getResources().getStringArray(R.array.head_growth_girl_zero_to_five_percentile_85th));
        chartData85th.setLegend("85th     ");
        chartData85th.setColor(getResources().getColor(R.color.orange));
        growingChart.addChartData(chartData85th);

        ChartData chartData97th = new ChartData();
        chartData97th.setYValues(getResources().getStringArray(R.array.head_growth_girl_zero_to_five_percentile_97th));
        chartData97th.setLegend("97th     ");
        chartData97th.setColor(getResources().getColor(R.color.red));
        growingChart.addChartData(chartData97th);
    }

    private void prepareBoysZeroToFiveGrowingChart() {
        growingChart.clearReferenceData();
        growingChart.setMonths(5 * 12);
        growingChart.setStartMonth(0);
        growingChart.setUnit(" cm");
        growingChart.setDescription(getString(R.string.str_Percentiles_of_head_circumference_for_age_boys_aged_0_5_years));

        ChartData chartData3rd = new ChartData();
        chartData3rd.setYValues(getResources().getStringArray(R.array.head_growth_boys_zero_to_five_percentile_3rd));
        chartData3rd.setLegend("3rd     ");
        chartData3rd.setColor(getResources().getColor(R.color.red));
        growingChart.addChartData(chartData3rd);

        ChartData chartData15th = new ChartData();
        chartData15th.setYValues(getResources().getStringArray(R.array.head_growth_boys_zero_to_five_percentile_15th));
        chartData15th.setLegend("15th     ");
        chartData15th.setColor(getResources().getColor(R.color.orange));
        growingChart.addChartData(chartData15th);

        ChartData chartData50th = new ChartData();
        chartData50th.setYValues(getResources().getStringArray(R.array.head_growth_boys_zero_to_five_percentile_50th));
        chartData50th.setLegend("50th     ");
        chartData50th.setColor(getResources().getColor(R.color.green));
        growingChart.addChartData(chartData50th);

        ChartData chartData85th = new ChartData();
        chartData85th.setYValues(getResources().getStringArray(R.array.head_growth_boys_zero_to_five_percentile_85th));
        chartData85th.setLegend("85th     ");
        chartData85th.setColor(getResources().getColor(R.color.orange));
        growingChart.addChartData(chartData85th);

        ChartData chartData97th = new ChartData();
        chartData97th.setYValues(getResources().getStringArray(R.array.head_growth_boys_zero_to_five_percentile_97th));
        chartData97th.setLegend("97th     ");
        chartData97th.setColor(getResources().getColor(R.color.red));
        growingChart.addChartData(chartData97th);
    }

    private void prepareLoaderManager() {
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] babyIdArg = {
                String.valueOf(ActiveContext.getActiveBaby(getActivity()).getActivityId())
        };
        return new CursorLoader(getActivity(),
                Contract.Measurement.CONTENT_URI,
                Contract.Measurement.Query.PROJECTION,
                "baby_id = ?",
                babyIdArg,
                Contract.Measurement.Query.SORT_BY_TIMESTAMP_ASC);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.getCount() > 0) {
            String[] yValues = new String[cursor.getCount()];
            String[] xValues = new String[cursor.getCount()];
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                xValues[cursor.getPosition()] = prepareXValues(cursor);
                yValues[cursor.getPosition()] = prepareYValues(cursor);
            }
            babyChart = new ChartData();
            babyChart.setYValues(yValues);
            babyChart.setXValues(xValues);
            babyChart.setLegend("Baby     ");
            babyChart.setColor(getResources().getColor(R.color.default_font));
            growingChart.addBabyData(babyChart);
        }
        growingChart.show();
    }

    private String prepareXValues(Cursor cursor) {
        Long timeInMillis = Long.parseLong(cursor.getString(Contract.Measurement.Query.OFFSET_TIMESTAMP));
        Calendar timestampCalendar = Calendar.getInstance();
        timestampCalendar.setTimeInMillis(timeInMillis);
        AgeCalculator age = new AgeCalculator(ActiveContext.getActiveBaby(getActivity()).getBirthdayInCalendar(), timestampCalendar);
        return String.valueOf(age.getMonthDifferent());
    }

    private String prepareYValues(Cursor cursor) {
        return cursor.getString(Contract.Measurement.Query.OFFSET_HEAD);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
