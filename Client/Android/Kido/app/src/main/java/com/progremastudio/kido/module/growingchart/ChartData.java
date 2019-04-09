/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.growingchart;

public class ChartData {

    private String[] yValues;
    private String[] xValues;
    private String legend;
    private int color;

    public String[] getYValues() {
        return yValues;
    }

    public void setYValues(String[] yValues) {
        this.yValues = yValues;
    }

    public String[] getXValues() {
        return xValues;
    }

    public void setXValues(String[] xValues) {
        this.xValues = xValues;
    }

    public String getLegend() {
        return legend;
    }

    public void setLegend(String legend) {
        this.legend = legend;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
