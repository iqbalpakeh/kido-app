/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.navigation;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

public abstract class Item {

    protected int layout;
    protected int textColor;
    protected Drawable thumbnail;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public Drawable getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Drawable dThumbnail) {
        this.thumbnail = dThumbnail;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int cTextColor) {
        this.textColor = cTextColor;
    }

    public abstract View inflate(Context context, View view, ViewGroup viewGroup);
}
