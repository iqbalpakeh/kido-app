/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.diaper;

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

public class DiaperAdapter extends RecyclerViewCursorAdapter<DiaperAdapter.DiaperViewHolder> {

    private static final int HEADER = 0;
    private static final int ENTRY = 1;

    private LayoutInflater inflater;
    private DiaperViewHolder entryHolder;
    private DiaperViewHolder headerHolder;
    private DiaperFragment fragment;
    private Context context;

    public DiaperAdapter(final Context context) {
        super();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setCallback(DiaperFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onBindViewHolder(DiaperViewHolder viewHolder, Cursor cursor) {
        if (viewHolder.getType() == HEADER) {
            viewHolder.bindHeaderData();
        } else if (viewHolder.getType() == ENTRY) {
            viewHolder.bindEntryData(cursor);
        }
    }

    @Override
    public DiaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View view = inflater.inflate(R.layout.tile_diaper_header, parent, false);
            headerHolder = new DiaperViewHolder(view, context, viewType);
            return headerHolder;
        } else if (viewType == ENTRY) {
            View view = inflater.inflate(R.layout.tile_diaper_entry, parent, false);
            entryHolder = new DiaperViewHolder(view, context, viewType);
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

    public static class DiaperViewHolder extends RecyclerView.ViewHolder {

        private int type;
        private Context context;
        private Callback callback;
        private LinearLayout subHeaderLayout;
        private LinearLayout entryLayout;
        private ImageView diaperImage;
        private ImageView menu;
        private TextView typeText;
        private TextView timestamp;
        private TextView subHeaderDay;
        private TextView diaperActivitySummary;
        private TextView totalDry;
        private TextView lastDry;
        private TextView totalWet;
        private TextView lastWet;
        private TextView totalMix;
        private TextView lastMix;
        private TextView todaySubHeader;
        //--debug
        private ImageButton debugEdit;
        private ImageButton debugDelete;

        public DiaperViewHolder(final View view, Context context, int type) {
            super(view);
            this.context = context;
            this.type = type;
            if (this.type == HEADER) {
                diaperActivitySummary = (TextView) view.findViewById(R.id.diaper_activity_summary);
                totalDry = (TextView) view.findViewById(R.id.dry_total);
                totalWet = (TextView) view.findViewById(R.id.wet_total);
                totalMix = (TextView) view.findViewById(R.id.mix_total);
                lastDry = (TextView) view.findViewById(R.id.dry_last);
                lastWet = (TextView) view.findViewById(R.id.wet_last);
                lastMix = (TextView) view.findViewById(R.id.mix_last);
                todaySubHeader = (TextView) view.findViewById(R.id.sub_header_today);
            } else if (this.type == ENTRY) {
                subHeaderLayout = (LinearLayout) view.findViewById(R.id.sub_header_layout);
                subHeaderDay = (TextView) view.findViewById(R.id.sub_header_day);
                entryLayout = (LinearLayout) view.findViewById(R.id.entry_layout);
                diaperImage = (ImageView) view.findViewById(R.id.diaper_type_image);
                typeText = (TextView) view.findViewById(R.id.diaper_type_text);
                timestamp = (TextView) view.findViewById(R.id.diaper_time_stamp);
                menu = (ImageView) view.findViewById(R.id.menu_button);
                //--debug
                debugEdit = (ImageButton) view.findViewById(R.id.diaper_entry_edit_debug);
                debugDelete = (ImageButton) view.findViewById(R.id.diaper_entry_delete_debug);
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
            bindLastWetData();
            bindLastDryData();
            bindLastMixData();
            bindTotalWetData();
            bindTotalDryData();
            bindTotalMixData();
        }

        private void bindActivitySummary() {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String filter = sharedPref.getString(context.getString(R.string.var_KEY_DEF_TIME_FILTER_FEEDING), "");
            diaperActivitySummary.setText("Activities " + filter);
        }

        private void bindLastWetData() {
            Cursor cursor = requestLastWetData();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                lastWet.setText(setLastWetDataText(cursor));
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                lastWet.setText(setDefaultText());
            }
        }

        private Cursor requestLastWetData() {
            String[] arguments = {String.valueOf(ActiveContext.getActiveBaby(context).getActivityId())};
            return context.getContentResolver().query(
                    Contract.Diaper.CONTENT_URI,
                    Contract.Diaper.Query.PROJECTION,
                    "baby_id = ? AND type = 'WET'",
                    arguments,
                    Contract.Diaper.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private String setLastWetDataText(Cursor cursor) {
            return TextFormation.last(context, cursor.getString(Contract.Diaper.Query.OFFSET_TIMESTAMP));
        }

        private void bindLastDryData() {
            Cursor cursor = requestLastDryData();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                lastDry.setText(setLastDryData(cursor));
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                lastDry.setText(setDefaultText());
            }
        }

        private Cursor requestLastDryData() {
            String[] arguments = {String.valueOf(ActiveContext.getActiveBaby(context).getActivityId())};
            return context.getContentResolver().query(
                    Contract.Diaper.CONTENT_URI,
                    Contract.Diaper.Query.PROJECTION,
                    "baby_id = ? AND type = 'DRY'",
                    arguments,
                    Contract.Diaper.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private String setLastDryData(Cursor cursor) {
            return TextFormation.last(context, cursor.getString(Contract.Diaper.Query.OFFSET_TIMESTAMP));
        }

        private void bindLastMixData() {
            Cursor cursor = requestLastMixData();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                lastMix.setText(setLastMixData(cursor));
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                lastMix.setText(setDefaultText());
            }
        }

        private Cursor requestLastMixData() {
            String[] arguments = {String.valueOf(ActiveContext.getActiveBaby(context).getActivityId())};
            return context.getContentResolver().query(
                    Contract.Diaper.CONTENT_URI,
                    Contract.Diaper.Query.PROJECTION,
                    "baby_id = ? AND type = 'MIXED'",
                    arguments,
                    Contract.Diaper.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private String setLastMixData(Cursor cursor) {
            return TextFormation.last(context, cursor.getString(Contract.Diaper.Query.OFFSET_TIMESTAMP));
        }

        private void bindTotalDryData() {
            Cursor cursor = requestTotalDryData();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                totalDry.setText(setTotalDryDataText(cursor));
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                totalDry.setText(setDefaultText());
            }
        }

        private Cursor requestTotalDryData() {
            String[] arguments = ActiveContext.createTimeFilter(context, ActiveContext.FOR_THIS_WEEK);
            return context.getContentResolver().query(
                    Contract.Diaper.CONTENT_URI,
                    Contract.Diaper.Query.PROJECTION,
                    "baby_id = ? AND type = 'DRY' AND timestamp >= ? AND timestamp <= ?",
                    arguments,
                    Contract.Diaper.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private String setTotalDryDataText(Cursor cursor) {
            return TextFormation.totalDiaperUsed(context, String.valueOf(cursor.getCount()));
        }

        private void bindTotalWetData() {
            Cursor cursor = requestTotalWetData();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                totalWet.setText(setTotalWetDataText(cursor));
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                totalWet.setText(setDefaultText());
            }
        }

        private Cursor requestTotalWetData() {
            String[] arguments = ActiveContext.createTimeFilter(context, ActiveContext.FOR_THIS_WEEK);
            return context.getContentResolver().query(
                    Contract.Diaper.CONTENT_URI,
                    Contract.Diaper.Query.PROJECTION,
                    "baby_id = ? AND type = 'WET' AND timestamp >= ? AND timestamp <= ?",
                    arguments,
                    Contract.Diaper.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private String setTotalWetDataText(Cursor cursor) {
            return TextFormation.totalDiaperUsed(context, String.valueOf(cursor.getCount()));
        }

        private void bindTotalMixData() {
            Cursor cursor = requestTotalMixData();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                totalMix.setText(setTotalMixDataText(cursor));
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                totalMix.setText((context.getResources().getString(R.string.str_No_activity)));
            }
        }

        private Cursor requestTotalMixData() {
            String[] arguments = ActiveContext.createTimeFilter(context, ActiveContext.FOR_THIS_WEEK);
            return context.getContentResolver().query(
                    Contract.Diaper.CONTENT_URI,
                    Contract.Diaper.Query.PROJECTION,
                    "baby_id = ? AND type = 'MIXED' AND timestamp >= ? AND timestamp <= ?",
                    arguments,
                    Contract.Diaper.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private String setTotalMixDataText(Cursor cursor) {
            return TextFormation.totalDiaperUsed(context, String.valueOf(cursor.getCount()));
        }

        private String setDefaultText() {
            return context.getResources().getString(R.string.str_No_activity);
        }

        public void bindEntryData(final Cursor cursor) {
            if (cursor.getCount() > 0) {
                String type = cursor.getString(Contract.Diaper.Query.OFFSET_TYPE);
                if (type.equals(DiaperModel.DiaperType.WET.getTitle())) {
                    bindDryData(cursor);
                } else if (type.equals(DiaperModel.DiaperType.DRY.getTitle())) {
                    bindWetData(cursor);
                } else if (type.equals(DiaperModel.DiaperType.MIXED.getTitle())) {
                    bindMixData(type, cursor);
                } else if (type.equals(DiaperModel.DiaperType.SUB_HEADER.getTitle())) {
                    bindSubHeaderData(cursor);
                }
            }
            bindMenuButton(cursor);
            //--debug
            bindDebugButton(cursor);
        }

        private String setTimeStampText(Cursor cursor) {
            return TextFormation.time(cursor.getString(Contract.Diaper.Query.OFFSET_TIMESTAMP));
        }

        private void bindDryData(Cursor cursor) {
            subHeaderLayout.setVisibility(View.GONE);
            entryLayout.setVisibility(View.VISIBLE);
            diaperImage.setImageResource(R.drawable.ic_diaper_wet);
            typeText.setText(context.getResources().getString(R.string.str_Pee));
            timestamp.setText(setTimeStampText(cursor));
        }

        private void bindWetData(Cursor cursor) {
            subHeaderLayout.setVisibility(View.GONE);
            entryLayout.setVisibility(View.VISIBLE);
            diaperImage.setImageResource(R.drawable.ic_diaper_dry);
            typeText.setText(context.getResources().getString(R.string.str_Poo));
            timestamp.setText(setTimeStampText(cursor));
        }

        private void bindMixData(String type, Cursor cursor) {
            subHeaderLayout.setVisibility(View.GONE);
            entryLayout.setVisibility(View.VISIBLE);
            diaperImage.setImageResource(R.drawable.ic_diaper_mix);
            typeText.setText(context.getResources().getString(R.string.str_Pee_and_poo));
            timestamp.setText(setTimeStampText(cursor));
        }

        private void bindSubHeaderData(Cursor cursor) {
            subHeaderLayout.setVisibility(View.VISIBLE);
            entryLayout.setVisibility(View.GONE);
            subHeaderDay.setText(setSubHeaderText(cursor));
        }

        private String setSubHeaderText(Cursor cursor) {
            return TextFormation.date(context, cursor.getString(Contract.Diaper.Query.OFFSET_TIMESTAMP));
        }

        private void bindMenuButton(Cursor cursor) {
            menu.setTag(cursor.getString(Contract.Diaper.Query.OFFSET_ACTIVITY_ID));
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
            DiaperModel model = new DiaperModel();
            model.setActivityId(Long.valueOf((String) entry.getTag()));
            model.delete(context);
        }

        public void editEntry(View entry) {
            callback.onDiaperEntryEditSelected(entry);
        }

        //--debug
        private void bindDebugButton(Cursor cursor) {
            debugEdit.setTag(cursor.getString(Contract.Diaper.Query.OFFSET_ACTIVITY_ID));
            debugEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editEntry(debugEdit);
                }
            });
            debugDelete.setTag(cursor.getString(Contract.Diaper.Query.OFFSET_ACTIVITY_ID));
            debugDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteEntry(context, debugDelete);
                }
            });
        }

        public interface Callback {
            void onDiaperEntryEditSelected(View entry);
        }

    }

}
