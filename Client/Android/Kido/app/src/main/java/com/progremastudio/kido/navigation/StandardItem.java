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
import android.widget.ImageView;
import android.widget.TextView;

import com.progremastudio.kido.R;

public class StandardItem extends Item {

    public StandardItem(String text) {
        this.setText(text);
        this.setLayout(R.layout.drawer_text);
    }

    @Override
    public View inflate(Context context, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = getLayoutInflater(context);
        view = layoutInflater.inflate(getLayout(), viewGroup, false);
        inflateName(context, view);
        inflateThumbnail(view);
        return view;
    }

    private LayoutInflater getLayoutInflater(Context context) {
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void inflateName(Context context, View view) {
        TextView itemName;
        itemName = (TextView) view.findViewById(R.id.text_title);
        itemName.setText(getText());
        itemName.setTextColor(getTextColor());
        itemName.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_small));
        itemName.setTypeface(null, Typeface.BOLD);
    }

    private void inflateThumbnail(View view) {
        ImageView thumbnail;
        thumbnail = (ImageView) view.findViewById(R.id.thumbnail_section);
        thumbnail.setImageDrawable(getThumbnail());
    }
}
