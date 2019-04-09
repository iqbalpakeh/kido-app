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
import com.progremastudio.kido.util.ActiveContext;

public class Baby extends Item {

    public Baby(String text) {
        this.setText(text);
        this.setLayout(R.layout.drawer_baby);
    }

    @Override
    public View inflate(Context context, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = getLayoutInflater(context);
        view = layoutInflater.inflate(getLayout(), viewGroup, false);
        inflateName(context, view);
        inflateThumbnail(view);
        inflateStatusFlag(context, view);
        return view;
    }

    private void inflateName(Context context, View view) {
        TextView name;
        name = (TextView) view.findViewById(R.id.baby_name_type_view);
        name.setText(getText());
        name.setTextColor(getTextColor());
        name.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_small));
        name.setTypeface(null, Typeface.BOLD);
    }

    private void inflateThumbnail(View view) {
        ImageView thumbnail;
        thumbnail = (ImageView) view.findViewById(R.id.thumbnail_section);
        thumbnail.setImageDrawable(getThumbnail());
    }

    private void inflateStatusFlag(Context context, View view) {
        ImageView statusFlag = (ImageView) view.findViewById(R.id.active_baby_flag);
        if (ActiveContext.getActiveBaby(context).getName().equals(getText())) {
            statusFlag.setVisibility(View.VISIBLE);
        } else {
            statusFlag.setVisibility(View.INVISIBLE);
        }
    }

    private LayoutInflater getLayoutInflater(Context context) {
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}
