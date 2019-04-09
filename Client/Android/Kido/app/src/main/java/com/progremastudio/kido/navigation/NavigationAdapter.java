/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.navigation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class NavigationAdapter extends ArrayAdapter<Item> {

    private final int PARENT_POSITION = 1;
    private ArrayList<Item> items;
    private Context context;

    public NavigationAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
    }

    @Override
    public boolean isEnabled(int position) {
        return !((items.get(position) instanceof Divider) || (position == PARENT_POSITION));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return items.get(position).inflate(context, convertView, parent);
    }
}
