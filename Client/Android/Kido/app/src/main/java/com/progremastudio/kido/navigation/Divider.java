/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.navigation;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.progremastudio.kido.R;

public class Divider extends Item {

    public Divider(String text) {
        this.setText(text);
        this.setLayout(R.layout.drawer_section);
    }

    @Override
    public View inflate(Context context, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = getLayoutInflater(context);
        view = layoutInflater.inflate(getLayout(), viewGroup, false);
        inflateText(context, view);
        return view;
    }

    private void inflateText(Context context, View view) {
        TextView actionTextView;
        actionTextView = (TextView) view.findViewById(R.id.text_title);
        actionTextView.setText(getText());
        actionTextView.setEnabled(false);
        actionTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_small));
        actionTextView.setTypeface(null, Typeface.BOLD);
    }

    private LayoutInflater getLayoutInflater(Context context) {
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}
