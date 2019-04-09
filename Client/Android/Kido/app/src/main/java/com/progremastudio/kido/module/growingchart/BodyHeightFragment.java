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

public class BodyHeightFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private View root;
    private GrowingChart growingChart;

    public static BodyHeightFragment getInstance() {
        return new BodyHeightFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prepareFragment(inflater, container);
        prepareLoaderManager();
        prepareGrowingChart();
        return root;
    }

    private void prepareFragment(LayoutInflater inflater, ViewGroup container) {
        root = inflater.inflate(R.layout.fragment_body_height, null);
    }

    public void restartLoader0To2Years() {
        if (ActiveContext.getActiveBaby(getActivity()).getSex() == BaseActor.Sex.FEMALE) {
            prepareGirlZeroToTwoGrowingChart();
        } else {
            prepareBoysZeroToTwoGrowingChart();
        }
        growingChart.show();
    }

    public void restartLoader2To5Years() {
        if (ActiveContext.getActiveBaby(getActivity()).getSex() == BaseActor.Sex.FEMALE) {
            prepareGirlTwoToFiveGrowingChart();
        } else {
            prepareBoysTwoToFiveGrowingChart();
        }
        growingChart.show();
    }

    private void prepareGrowingChart() {
        growingChart = new GrowingChart(root, getActivity());
        if (ActiveContext.getActiveBaby(getActivity()).getSex().getTitle().equals(BaseActor.Sex.FEMALE.getTitle())) {
            prepareGirlZeroToTwoGrowingChart();
        } else {
            prepareBoysZeroToTwoGrowingChart();
        }
    }

    private void prepareGirlZeroToTwoGrowingChart() {
        growingChart.clearReferenceData();
        growingChart.setMonths(2 * 12);
        growingChart.setStartMonth(0);
        growingChart.setUnit(" cm");
        growingChart.setDescription(getString(R.string.str_Percentiles_of_height_for_age_girls_aged_0_2_years));

        ChartData chartData3rd = new ChartData();
        chartData3rd.setYValues(getResources().getStringArray(R.array.height_growth_girl_zero_to_two_percentile_3rd));
        chartData3rd.setLegend("3rd     ");
        chartData3rd.setColor(getResources().getColor(R.color.red));
        growingChart.addChartData(chartData3rd);

        ChartData chartData15th = new ChartData();
        chartData15th.setYValues(getResources().getStringArray(R.array.height_growth_girl_zero_to_two_percentile_15th));
        chartData15th.setLegend("15th     ");
        chartData15th.setColor(getResources().getColor(R.color.orange));
        growingChart.addChartData(chartData15th);

        ChartData chartData50th = new ChartData();
        chartData50th.setYValues(getResources().getStringArray(R.array.height_growth_girl_zero_to_two_percentile_50th));
        chartData50th.setLegend("50th     ");
        chartData50th.setColor(getResources().getColor(R.color.green));
        growingChart.addChartData(chartData50th);

        ChartData chartData85th = new ChartData();
        chartData85th.setYValues(getResources().getStringArray(R.array.height_growth_girl_zero_to_two_percentile_85th));
        chartData85th.setLegend("85th     ");
        chartData85th.setColor(getResources().getColor(R.color.orange));
        growingChart.addChartData(chartData85th);

        ChartData chartData97th = new ChartData();
        chartData97th.setYValues(getResources().getStringArray(R.array.height_growth_girl_zero_to_two_percentile_97th));
        chartData97th.setLegend("97th     ");
        chartData97th.setColor(getResources().getColor(R.color.red));
        growingChart.addChartData(chartData97th);
    }

    private void prepareGirlTwoToFiveGrowingChart() {
		growingChart.clearReferenceData();
        growingChart.setMonths(3 * 12);
        growingChart.setStartMonth(24);
        growingChart.setUnit(" cm");
        growingChart.setDescription(getString(R.string.str_Percentiles_of_height_for_age_girls_aged_2_5_years));

        ChartData chartData3rd = new ChartData();
        chartData3rd.setYValues(getResources().getStringArray(R.array.height_growth_girl_two_to_five_percentile_3rd));
        chartData3rd.setLegend("3rd     ");
        chartData3rd.setColor(getResources().getColor(R.color.red));
        growingChart.addChartData(chartData3rd);

        ChartData chartData15th = new ChartData();
        chartData15th.setYValues(getResources().getStringArray(R.array.height_growth_girl_two_to_five_percentile_15th));
        chartData15th.setLegend("15th     ");
        chartData15th.setColor(getResources().getColor(R.color.orange));
        growingChart.addChartData(chartData15th);

        ChartData chartData50th = new ChartData();
        chartData50th.setYValues(getResources().getStringArray(R.array.height_growth_girl_two_to_five_percentile_50th));
        chartData50th.setLegend("50th     ");
        chartData50th.setColor(getResources().getColor(R.color.green));
        growingChart.addChartData(chartData50th);

        ChartData chartData85th = new ChartData();
        chartData85th.setYValues(getResources().getStringArray(R.array.height_growth_girl_two_to_five_percentile_85th));
        chartData85th.setLegend("85th     ");
        chartData85th.setColor(getResources().getColor(R.color.orange));
        growingChart.addChartData(chartData85th);

        ChartData chartData97th = new ChartData();
        chartData97th.setYValues(getResources().getStringArray(R.array.height_growth_girl_two_to_five_percentile_97th));
        chartData97th.setLegend("97th     ");
        chartData97th.setColor(getResources().getColor(R.color.red));
        growingChart.addChartData(chartData97th);
    }

    private void prepareBoysZeroToTwoGrowingChart() {
		growingChart.clearReferenceData();
        growingChart.setMonths(2 * 12);
        growingChart.setStartMonth(0);
        growingChart.setUnit(" cm");
        growingChart.setDescription(getString(R.string.str_Percentiles_of_height_for_age_boys_aged_0_2_years));

        ChartData chartData3rd = new ChartData();
        chartData3rd.setYValues(getResources().getStringArray(R.array.height_growth_boys_zero_to_two_percentile_3rd));
        chartData3rd.setLegend("3rd     ");
        chartData3rd.setColor(getResources().getColor(R.color.red));
        growingChart.addChartData(chartData3rd);

        ChartData chartData15th = new ChartData();
        chartData15th.setYValues(getResources().getStringArray(R.array.height_growth_boys_zero_to_two_percentile_15th));
        chartData15th.setLegend("15th     ");
        chartData15th.setColor(getResources().getColor(R.color.orange));
        growingChart.addChartData(chartData15th);

        ChartData chartData50th = new ChartData();
        chartData50th.setYValues(getResources().getStringArray(R.array.height_growth_boys_zero_to_two_percentile_50th));
        chartData50th.setLegend("50th     ");
        chartData50th.setColor(getResources().getColor(R.color.green));
        growingChart.addChartData(chartData50th);

        ChartData chartData85th = new ChartData();
        chartData85th.setYValues(getResources().getStringArray(R.array.height_growth_boys_zero_to_two_percentile_85th));
        chartData85th.setLegend("85th     ");
        chartData85th.setColor(getResources().getColor(R.color.orange));
        growingChart.addChartData(chartData85th);

        ChartData chartData97th = new ChartData();
        chartData97th.setYValues(getResources().getStringArray(R.array.height_growth_boys_zero_to_two_percentile_97th));
        chartData97th.setLegend("97th     ");
        chartData97th.setColor(getResources().getColor(R.color.red));
        growingChart.addChartData(chartData97th);
    }

    private void prepareBoysTwoToFiveGrowingChart() {
		growingChart.clearReferenceData();
        growingChart.setMonths(3 * 12);
        growingChart.setStartMonth(24);
        growingChart.setUnit(" cm");
        growingChart.setDescription(getString(R.string.str_Percentiles_of_height_for_age_boys_aged_2_5_years));

        ChartData chartData3rd = new ChartData();
        chartData3rd.setYValues(getResources().getStringArray(R.array.height_growth_boys_two_to_five_percentile_3rd));
        chartData3rd.setLegend("3rd     ");
        chartData3rd.setColor(getResources().getColor(R.color.red));
        growingChart.addChartData(chartData3rd);

        ChartData chartData15th = new ChartData();
        chartData15th.setYValues(getResources().getStringArray(R.array.height_growth_boys_two_to_five_percentile_15th));
        chartData15th.setLegend("15th     ");
        chartData15th.setColor(getResources().getColor(R.color.orange));
        growingChart.addChartData(chartData15th);

        ChartData chartData50th = new ChartData();
        chartData50th.setYValues(getResources().getStringArray(R.array.height_growth_boys_two_to_five_percentile_50th));
        chartData50th.setLegend("50th     ");
        chartData50th.setColor(getResources().getColor(R.color.green));
        growingChart.addChartData(chartData50th);

        ChartData chartData85th = new ChartData();
        chartData85th.setYValues(getResources().getStringArray(R.array.height_growth_boys_two_to_five_percentile_85th));
        chartData85th.setLegend("85th     ");
        chartData85th.setColor(getResources().getColor(R.color.orange));
        growingChart.addChartData(chartData85th);

        ChartData chartData97th = new ChartData();
        chartData97th.setYValues(getResources().getStringArray(R.array.height_growth_boys_two_to_five_percentile_97th));
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
            while (cursor.moveToNext()) {
				int curPos = cursor.getPosition();
                xValues[curPos] = prepareXValues(cursor);
                yValues[curPos] = prepareYValues(cursor);
            }
            ChartData babyChart = new ChartData();
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
        return cursor.getString(Contract.Measurement.Query.OFFSET_HEIGHT);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
