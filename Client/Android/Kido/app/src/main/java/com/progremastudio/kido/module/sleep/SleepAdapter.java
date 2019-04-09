/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.sleep;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.progremastudio.kido.R;
import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.util.ActiveContext;
import com.progremastudio.kido.util.TextFormation;
import com.progremastudio.kido.widget.RecyclerViewCursorAdapter;

public class SleepAdapter extends RecyclerViewCursorAdapter<SleepAdapter.SleepViewHolder> {

    private static final int HEADER = 0;
    private static final int ENTRY = 1;

    private LayoutInflater inflater;
    private SleepViewHolder entryHolder;
    private SleepViewHolder headerHolder;
    private SleepFragment fragment;
    private Context context;

    public SleepAdapter(final Context context) {
        super();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setCallBack(SleepFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onBindViewHolder(SleepViewHolder holder, Cursor cursor) {
        if (holder.getType() == HEADER) {
            holder.bindHeaderData();
        } else if (holder.getType() == ENTRY) {
            holder.bindEntryData(cursor);
        }
    }

    @Override
    public SleepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View view = inflater.inflate(R.layout.tile_sleep_header, parent, false);
            headerHolder = new SleepViewHolder(view, context, viewType);
            return headerHolder;
        } else if (viewType == ENTRY) {
            View view = inflater.inflate(R.layout.tile_sleep_entry, parent, false);
            entryHolder = new SleepViewHolder(view, context, viewType);
            entryHolder.setCallback(fragment);
            return entryHolder;
        }
        throw new RuntimeException("there is no type that matches the type " + viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return HEADER;
        }
        return ENTRY;
    }

    @org.jetbrains.annotations.Contract(pure = true)
    private boolean isHeader(int position) {
        return position == 0;
    }

    public static class SleepViewHolder extends RecyclerView.ViewHolder {

        private int type;
        private Context context;
        private Callback callback;
        private LinearLayout subHeaderLayout;
        private LinearLayout entryLayout;
        private ImageView sleepImage;
        private ImageView menu;
        private TextView sleepType;
        private TextView duration;
        private TextView timestamp;
        private TextView subHeaderDay;
        private TextView sleepActivitySummary;
        private TextView totalNight;
        private TextView totalDay;
        private TextView lastNight;
        private TextView lastDay;
        private TextView todaySubHeader;
        //--debug
        private ImageButton debugEdit;
        private ImageButton debugDelete;

        public SleepViewHolder(final View view, Context context, int viewType) {
            super(view);
            this.context = context;
            this.type = viewType;
            if (type == HEADER) {
                sleepActivitySummary = (TextView) view.findViewById(R.id.sleep_activity_summary);
                totalNight = (TextView) view.findViewById(R.id.night_sleep_total);
                totalDay = (TextView) view.findViewById(R.id.day_sleep_total);
                lastNight = (TextView) view.findViewById(R.id.night_sleep_last);
                lastDay = (TextView) view.findViewById(R.id.day_sleep_last);
                todaySubHeader = (TextView) view.findViewById(R.id.sub_header_today);
            } else if(type == ENTRY) {
                subHeaderLayout = (LinearLayout) view.findViewById(R.id.sub_header_layout);
                subHeaderDay = (TextView) view.findViewById(R.id.sub_header_day);
                entryLayout = (LinearLayout) view.findViewById(R.id.entry_layout);
                sleepImage = (ImageView) view.findViewById(R.id.sleep_type_image);
                sleepType = (TextView) view.findViewById(R.id.sleep_type);
                duration = (TextView) view.findViewById(R.id.sleep_duration);
                timestamp = (TextView) view.findViewById(R.id.sleep_time_stamp);
                menu = (ImageView) view.findViewById(R.id.menu_button);
                //--debug
                debugEdit = (ImageButton) view.findViewById(R.id.sleep_entry_edit_debug);
                debugDelete = (ImageButton) view.findViewById(R.id.sleep_entry_delete_debug);
            }
        }

        public void setCallback(Callback listener) {
            callback = listener;
        }

        public int getType() {
            return type;
        }

        public void bindHeaderData() {
            bindActivitySummary();
            bindLastNight();
            bindLastDay();
            bindTotalNight();
            bindTotalDay();
        }

        private void bindActivitySummary() {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String filter = sharedPref.getString(context.getString(R.string.var_KEY_DEF_TIME_FILTER_FEEDING), "");
            sleepActivitySummary.setText("Activities " + filter);
        }

        private void bindLastNight() {
            Cursor cursor = requestLastNightCursor();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                lastNight.setText(setLastNightText(cursor));
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                lastNight.setText(setDefaultText());
            }
        }

        private Cursor requestLastNightCursor() {
            String[] arguments = {String.valueOf(ActiveContext.getActiveBaby(context).getActivityId())};
            return context.getContentResolver().query(
                    Contract.Sleep.CONTENT_URI,
                    Contract.Sleep.Query.PROJECTION,
                    "baby_id = ? AND type = 'NIGHT'",
                    arguments,
                    Contract.Sleep.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private String setLastNightText(Cursor cursor) {
            return TextFormation.last(context, cursor.getString(Contract.Sleep.Query.OFFSET_TIMESTAMP));
        }

        private String setDefaultText() {
            return context.getResources().getString(R.string.str_No_activity);
        }

        private void bindLastDay() {
            Cursor cursor = requestLastDayCursor();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                lastDay.setText(setLastDayText(cursor));
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                lastDay.setText(setDefaultText());
            }
        }

        private Cursor requestLastDayCursor() {
            String[] arguments = {String.valueOf(ActiveContext.getActiveBaby(context).getActivityId())};
            return context.getContentResolver().query(
                    Contract.Sleep.CONTENT_URI,
                    Contract.Sleep.Query.PROJECTION,
                    "baby_id = ? AND type = 'DAY'",
                    arguments,
                    Contract.Sleep.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private String setLastDayText(Cursor cursor) {
            return TextFormation.last(context, cursor.getString(Contract.Sleep.Query.OFFSET_TIMESTAMP));
        }

        private void bindTotalNight() {
            Cursor cursor = requestTotalNightCursor();
            if (cursor.getCount() > 0) {
                totalNight.setText(setTotalNightText(cursor));
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                totalNight.setText(setDefaultText());
            }
        }

        private Cursor requestTotalNightCursor() {
            String[] arguments = ActiveContext.createTimeFilter(context, ActiveContext.FOR_THIS_WEEK);
            return context.getContentResolver().query(
                    Contract.Sleep.CONTENT_URI,
                    Contract.Sleep.Query.PROJECTION,
                    "baby_id = ? AND timestamp >= ? AND timestamp <= ? AND type = 'NIGHT'",
                    arguments,
                    Contract.Sleep.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private String setTotalNightText(Cursor cursor) {
            return context.getResources().getString(R.string.str_Total_duration) + " " +
                    TextFormation.sleepDuration(context, String.valueOf(getTotalSleep(cursor)));
        }

        private void bindTotalDay() {
            Cursor cursor = requestTotalDayCursor();
            if (cursor.getCount() > 0) {
                totalDay.setText(setTotalDayText(cursor));
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                totalDay.setText(setDefaultText());
            }
        }

        private Cursor requestTotalDayCursor() {
            String[] arguments = ActiveContext.createTimeFilter(context, ActiveContext.FOR_THIS_WEEK);
            return context.getContentResolver().query(
                    Contract.Sleep.CONTENT_URI,
                    Contract.Sleep.Query.PROJECTION,
                    "baby_id = ? AND timestamp >= ? AND timestamp <= ? AND type = 'DAY'",
                    arguments,
                    Contract.Sleep.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private String setTotalDayText(Cursor cursor) {
            return context.getResources().getString(R.string.str_Total_duration) + " " +
                    TextFormation.sleepDuration(context, String.valueOf(getTotalSleep(cursor)));
        }

        private long getTotalSleep(Cursor cursor) {
            long total = 0;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                total += Long.valueOf(cursor.getString(Contract.Sleep.Query.OFFSET_DURATION));
            }
            return total;
        }

        public void bindEntryData(Cursor cursor) {
            if (cursor.getCount() > 0) {
                String type = cursor.getString(Contract.Sleep.Query.OFFSET_TYPE);
                if (type.equals(SleepModel.SleepType.DAY.getTitle())) {
                    bindDaySleep(cursor);
                } else if (type.equals(SleepModel.SleepType.NIGHT.getTitle())) {
                    bindNightSleep(cursor);
                } else if (type.equals(SleepModel.SleepType.SUB_HEADER.getTitle())) {
                    bindSubHeader(cursor);
                }
            }
            bindMenuButton(cursor);
            //--debug
            bindDebugButton(cursor);
        }

        //--debug
        private void bindDebugButton(Cursor cursor) {
            debugEdit.setTag(cursor.getString(Contract.Sleep.Query.OFFSET_ACTIVITY_ID));
            debugEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editEntry(debugEdit);
                }
            });
            debugDelete.setTag(cursor.getString(Contract.Sleep.Query.OFFSET_ACTIVITY_ID));
            debugDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteEntry(context, debugDelete);
                }
            });
        }

        private void bindDaySleep(Cursor cursor) {
            subHeaderLayout.setVisibility(View.GONE);
            entryLayout.setVisibility(View.VISIBLE);
            sleepImage.setImageResource(R.drawable.ic_sleep_nap);
            sleepType.setText(setDaySleepText());
            duration.setText(setDurationText(cursor));
            timestamp.setText(setTimestampText(cursor));
        }

        private String setDaySleepText() {
            return context.getResources().getString(R.string.str_Day_sleep);
        }

        private String setDurationText(Cursor cursor) {
            return context.getResources().getString(R.string.str_Duration) + " " +
                    TextFormation.duration(context, cursor.getString(Contract.Sleep.Query.OFFSET_DURATION));
        }

        private String setTimestampText(Cursor cursor) {
            return TextFormation.time(cursor.getString(Contract.Sleep.Query.OFFSET_TIMESTAMP));
        }

        private void bindNightSleep(Cursor cursor) {
            subHeaderLayout.setVisibility(View.GONE);
            entryLayout.setVisibility(View.VISIBLE);
            sleepImage.setImageResource(R.drawable.ic_sleep_night);
            sleepType.setText(setNightSleepText());
            duration.setText(setDurationText(cursor));
            timestamp.setText(setTimestampText(cursor));
        }

        private String setNightSleepText() {
            return context.getResources().getString(R.string.str_Night_sleep);
        }

        private void bindSubHeader(Cursor cursor) {
            subHeaderLayout.setVisibility(View.VISIBLE);
            entryLayout.setVisibility(View.GONE);
            subHeaderDay.setText(setSubHeaderDayText(cursor));
        }

        private String setSubHeaderDayText(Cursor cursor) {
            return TextFormation.date(context, cursor.getString(Contract.Sleep.Query.OFFSET_TIMESTAMP));
        }

        private void bindMenuButton(Cursor cursor) {
            menu.setTag(cursor.getString(Contract.Sleep.Query.OFFSET_ACTIVITY_ID));
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ImageView menu = (ImageView) view.findViewById(R.id.menu_button);
                    PopupMenu popupMenu = new PopupMenu(context, menu);
                    popupMenu.setOnMenuItemClickListener(
                            new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    if (item.getTitle().equals(
                                            context.getResources().getString(R.string.str_Edit))) {
                                        editEntry(menu);
                                    } else if (item.getTitle().equals(
                                            context.getResources().getString(R.string.str_Delete))) {
                                        deleteEntry(context, menu);
                                    }
                                    return false;
                                }
                            }
                    );
                    MenuInflater menuInflater = ((Activity) context).getMenuInflater();
                    menuInflater.inflate(R.menu.menu_entry, popupMenu.getMenu());
                    popupMenu.show();
                }
            });
        }

        public void deleteEntry(Context context, View entry) {
            SleepModel model = new SleepModel();
            model.setActivityId(Long.valueOf((String) entry.getTag()));
            model.delete(context);
        }

        public void editEntry(View entry) {
            callback.onSleepEntryEditSelected(entry);
        }

        public interface Callback {
            void onSleepEntryEditSelected(View entry);
        }

    }

}
