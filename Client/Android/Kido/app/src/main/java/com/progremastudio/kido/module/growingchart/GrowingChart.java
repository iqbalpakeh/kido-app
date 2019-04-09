/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.growingchart;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;
import com.progremastudio.kido.R;

import java.util.ArrayList;

public class GrowingChart {

    private int months;
    private int startMonth;
    private View root;
    private Context context;
    private LineChart chart;
    private ArrayList<ChartData> referenceDatas; // data from WHO
    private ChartData babyData; // individual baby data
    private String description;
    private String unit;

    public GrowingChart(View root, Context context) {
        this.root = root;
        this.context = context;
        this.months = 2 * 12; // default chart used is zero to two type
        this.referenceDatas = new ArrayList<ChartData>();

    }

    public void show() {
        preProcessChart();
        exeProcessChart();
        postProcessChart();
    }

    public void clearReferenceData() {
        referenceDatas.clear();
    }

    public void addBabyData(ChartData babyData) {
        this.babyData = babyData;
    }

    public void addChartData(ChartData chartData) {
        referenceDatas.add(chartData);
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    private void preProcessChart() {
        chart = (LineChart) root.findViewById(R.id.growing_chart);
        chart.clear();
        chart.setUnit(this.unit);
        chart.setDrawUnitsInChart(true);
        chart.setStartAtZero(false); // if enabled, the chart will always start at zero on the y-axis
        chart.setDrawYValues(true); // disable the drawing of values into the chart
        chart.setDrawBorder(true);
        chart.setDescription(description);
        chart.setNoDataTextDescription(context.getString(R.string.str_You_need_to_provide_data_for_the_chart));
        chart.setHighlightEnabled(true);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true); // enable scaling and dragging
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setDrawVerticalGrid(false);
        chart.setDrawHorizontalGrid(false);
        chart.setPinchZoom(true); // if disabled, scaling can be done on x- and y-axis separately
        chart.setBackgroundColor(context.getResources().getColor(R.color.pink_transparent));
        chart.setBorderPositions(new BarLineChartBase.BorderPosition[]{
                BarLineChartBase.BorderPosition.BOTTOM
        });
    }


    private void exeProcessChart() {
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        ArrayList<String> xValues = new ArrayList<String>();

        // set x range
        for (int i = startMonth; i <= (startMonth + months); i++) {
            xValues.add(String.valueOf(i));
        }

        for (int i = 0; i < referenceDatas.size(); i++) {
            dataSets.add(createLineDataSet(
                            referenceDatas.get(i).getYValues(),
                            referenceDatas.get(i).getLegend(),
                            referenceDatas.get(i).getColor())
            );
        }

        if (babyData != null) {
            ChartDataAdapter chartDataAdapter = new ChartDataAdapter(babyData, startMonth, (startMonth + months));
            LineDataSet babyDataSet = createLineDataSet(chartDataAdapter.getYValues(), chartDataAdapter.getLegend(), chartDataAdapter.getColor());
            babyDataSet.setCircleSize(5.0f);
            dataSets.add(babyDataSet);
        }

        LineData data = new LineData(xValues, dataSets);
        chart.setData(data);
    }

    private LineDataSet createLineDataSet(String[] percentile, String legend, int color) {
        ArrayList<Entry> percentileValues = new ArrayList<Entry>();
        for (int i = 0; i < percentile.length; i++) {
            if (percentile[i] == "") continue;
            percentileValues.add(new Entry(Float.valueOf(percentile[i]), i));
        }
        LineDataSet dataSet = new LineDataSet(percentileValues, legend);
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setLineWidth(1f);
        dataSet.setCircleSize(1f);
        dataSet.setFillAlpha(65);
        dataSet.setFillColor(color);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        return dataSet;
    }

    private void postProcessChart() {
        chart.animateX(1500);
        Legend legend = chart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(context.getResources().getColor(R.color.default_font));
        XLabels xLabels = chart.getXLabels();
        xLabels.setTextColor(context.getResources().getColor(R.color.default_font));
        YLabels yLabels = chart.getYLabels();
        yLabels.setPosition(YLabels.YLabelPosition.LEFT);
        yLabels.setTextColor(context.getResources().getColor(R.color.default_font));
    }
}
