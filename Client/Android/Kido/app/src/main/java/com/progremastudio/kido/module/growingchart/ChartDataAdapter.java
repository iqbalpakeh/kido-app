/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.growingchart;

public class ChartDataAdapter {

    private String[] yValues;
    private ChartData chartData;
    private int minRange;
    private int maxRange;

    public ChartDataAdapter(ChartData chartData, int minRange, int maxRange) {
        this.chartData = chartData;
        this.minRange = minRange;
        this.maxRange = maxRange;
        yValues = new String[maxRange - minRange + 1];
    }

    public String[] getYValues() {
        for (int i = 0; i <= maxRange - minRange; i++) {
            yValues[i] = "";
        }
        for (int i = 0; i < chartData.getXValues().length; i++) {
            try {
                yValues[Integer.valueOf(chartData.getXValues()[i]) - minRange] = chartData.getYValues()[i];
            } catch (Exception exception) {
                /***
                 * This means that the data is out of range from the plot! For example,
                 * the chart is for 24 to 60 month but the data is for 20th month.
                 ***/
            }
        }
        return yValues;
    }

    public int getColor() {
        return chartData.getColor();
    }

    public String getLegend() {
        return chartData.getLegend();
    }

}
