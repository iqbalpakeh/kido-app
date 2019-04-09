/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.feeding;

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

import java.text.DecimalFormat;

public class FeedingAdapter extends RecyclerViewCursorAdapter<FeedingAdapter.FeedingViewHolder> {

    private static final int HEADER = 0;
    private static final int ENTRY = 1;

    private LayoutInflater inflater;
    private FeedingViewHolder entryHolder;
    private FeedingViewHolder headerHolder;
    private FeedingFragment fragment;
    private Context context;

    public FeedingAdapter(final Context context) {
        super();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setCallback(FeedingFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onBindViewHolder(FeedingViewHolder holder, Cursor cursor) {
        if (holder.getType() == HEADER) {
            holder.bindHeaderData();
        } else if (holder.getType() == ENTRY) {
            holder.bindEntryData(cursor);
        }
    }

    @Override
    public FeedingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View view = inflater.inflate(R.layout.tile_feeding_header, parent, false);
            headerHolder = new FeedingViewHolder(view, context, viewType);
            return headerHolder;
        } else if (viewType == ENTRY) {
            View view = inflater.inflate(R.layout.tile_feeding_entry, parent, false);
            entryHolder = new FeedingViewHolder(view, context, viewType);
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

    static class FeedingViewHolder extends RecyclerView.ViewHolder {

        private int type;
        private Context context;
        private Callback callback;
        private LinearLayout subHeaderLayout;
        private LinearLayout entryLayout;
        private ImageView feedingImage;
        private ImageView menu;
        private TextView feedingType;
        private TextView feedingDetails;
        private TextView timestamp;
        private TextView subHeaderDay;
        private TextView feedingActivitySummary;
        private TextView totalBreastfeeding;
        private TextView lastBreastfeeding;
        private TextView totalPumpedMilk;
        private TextView lastPumpedMilk;
        private TextView totalFormulaMilk;
        private TextView lastFormulaMilk;
        private TextView totalSolidFood;
        private TextView lastSolidFood;
        private TextView todaySubHeader;
        //--debug
        private ImageButton debugEdit;
        private ImageButton debugDelete;

        private DecimalFormat twoSignificantDigit = new DecimalFormat("0.00");

        public FeedingViewHolder(final View view, Context context, int type) {
            super(view);
            this.context = context;
            this.type = type;
            if (this.type == HEADER) {
                feedingActivitySummary = (TextView) view.findViewById(R.id.feeding_activity_summary);
                totalBreastfeeding = (TextView) view.findViewById(R.id.breastfeeding_total);
                totalPumpedMilk = (TextView) view.findViewById(R.id.pumped_milk_total);
                totalFormulaMilk = (TextView) view.findViewById(R.id.formula_milk_total);
                totalSolidFood = (TextView) view.findViewById(R.id.solid_food_total);
                lastBreastfeeding = (TextView) view.findViewById(R.id.breastfeeding_last);
                lastPumpedMilk = (TextView) view.findViewById(R.id.pumped_milk_last);
                lastFormulaMilk = (TextView) view.findViewById(R.id.formula_milk_last);
                lastSolidFood = (TextView) view.findViewById(R.id.solid_food_last);
                todaySubHeader = (TextView) view.findViewById(R.id.sub_header_today);
            } else if (this.type == ENTRY) {
                subHeaderLayout = (LinearLayout) view.findViewById(R.id.sub_header_layout);
                subHeaderDay = (TextView) view.findViewById(R.id.sub_header_day);
                entryLayout = (LinearLayout) view.findViewById(R.id.entry_layout);
                feedingImage = (ImageView) view.findViewById(R.id.feeding_type_image);
                feedingType = (TextView) view.findViewById(R.id.feeding_type_text);
                feedingDetails = (TextView) view.findViewById(R.id.feeding_details);
                timestamp = (TextView) view.findViewById(R.id.feeding_time_stamp);
                menu = (ImageView) view.findViewById(R.id.feeding_menu_button);
                //--debug
                debugEdit = (ImageButton) view.findViewById(R.id.feeding_entry_edit_debug);
                debugDelete = (ImageButton) view.findViewById(R.id.feeding_entry_delete_debug);
            }
        }

        public void deleteEntry(Context context, View entry) {
            FeedingModel model = new FeedingModel();
            model.setActivityId(Long.valueOf((String) entry.getTag()));
            model.delete(context);
        }

        public void editEntry(View entry) {
            callback.onFeedingEntryEditSelected(entry);
        }

        public void setCallback(Callback listener) {
            callback = listener;
        }

        public int getType() {
            return type;
        }

        public void bindHeaderData() {
            bindActivitySummary();
            bindLastSolidFood();
            bindLastPumpedMilk();
            bindLastFormulaMilk();
            bindLastBreastfeeding();
            bindTotalSolidFood();
            bindTotalPumpedMilk();
            bindTotalFormulaMilk();
            bindTotalBreastfeeding();
        }

        private void bindActivitySummary() {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String filter = sharedPref.getString(context.getString(R.string.var_KEY_DEF_TIME_FILTER_FEEDING), "");
            feedingActivitySummary.setText("Activities " + filter);
        }

        private void bindLastSolidFood() {
            Cursor cursor = requestLastSolidFoodCursor();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                lastSolidFood.setText(setLastSolidFoodText(cursor));
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                lastSolidFood.setText(setDefaultText());
            }
        }

        private Cursor requestLastSolidFoodCursor() {
            String[] arguments = {String.valueOf(ActiveContext.getActiveBaby(context).getActivityId())};
            return context.getContentResolver().query(
                    Contract.Feeding.CONTENT_URI,
                    Contract.Feeding.Query.PROJECTION,
                    "baby_id = ? AND sides = 'SOLID'",
                    arguments,
                    Contract.Feeding.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private String setLastSolidFoodText(Cursor cursor) {
            return context.getString(R.string.str_Last_meal) + " " +
                    TextFormation.last(context, cursor.getString(Contract.Diaper.Query.OFFSET_TIMESTAMP));
        }

        private String setDefaultText() {
            return context.getResources().getString(R.string.str_No_activity);
        }

        private void bindTotalSolidFood() {
            Cursor cursor = requestTotalSolidFoodCursor();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                totalSolidFood.setText(setTotalSolidFoodText(cursor));
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                totalSolidFood.setText(setDefaultText());
            }
        }

        private Cursor requestTotalSolidFoodCursor() {
            int timeFilter = ActiveContext.getPreferenceTimeFilter(context,
                    context.getString(R.string.var_KEY_DEF_TIME_FILTER_FEEDING));
            String[] arguments = ActiveContext.createTimeFilter(context, timeFilter);
            return context.getContentResolver().query(
                    Contract.Feeding.CONTENT_URI,
                    Contract.Feeding.Query.PROJECTION,
                    "baby_id = ? AND timestamp >= ? AND timestamp <= ? AND sides = 'SOLID'",
                    arguments,
                    Contract.Feeding.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private String setTotalSolidFoodText(Cursor cursor) {
            return context.getString(R.string.str_Total_meal) + " " +
                    TextFormation.total(context, String.valueOf(cursor.getCount()));
        }

        private void bindLastPumpedMilk() {
            Cursor cursor = requestLastPumpedMilkCursor();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                if(checkPumpingActivity(parsePumpingVolume(cursor))) {
                    lastPumpedMilk.setText(setLastPumpedMilkPumpingText(cursor));
                } else {
                    lastPumpedMilk.setText(setLastPumpedMilkFeedingText(cursor));
                }
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                lastPumpedMilk.setText(setDefaultText());
            }
        }

        private Cursor requestLastPumpedMilkCursor() {
            String[] arguments = {String.valueOf(ActiveContext.getActiveBaby(context).getActivityId())};
            return context.getContentResolver().query(
                    Contract.Feeding.CONTENT_URI,
                    Contract.Feeding.Query.PROJECTION,
                    "baby_id = ? AND sides = 'PUMP'",
                    arguments,
                    Contract.Feeding.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private float parsePumpingVolume(Cursor cursor) {
            return Float.valueOf(cursor.getString(Contract.Feeding.Query.OFFSET_PUMP));
        }

        private String setLastPumpedMilkPumpingText(Cursor cursor) {
            return context.getString(R.string.str_Last_pumping) + " " +
                    TextFormation.last(context, cursor.getString(Contract.Feeding.Query.OFFSET_TIMESTAMP));
        }

        private String setLastPumpedMilkFeedingText(Cursor cursor) {
            return context.getString(R.string.str_Last_feeding) + " " +
                    TextFormation.last(context, cursor.getString(Contract.Feeding.Query.OFFSET_TIMESTAMP));
        }

        private void bindTotalPumpedMilk() {
            Cursor cursor = requestTotalPumpedMilkCursor();
            if (cursor.getCount() > 0) {
                totalPumpedMilk.setText(setTotalPumpedMilkText(cursor));
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                totalPumpedMilk.setText(setDefaultText());
            }
        }

        private Cursor requestTotalPumpedMilkCursor() {
            int timeFilter = ActiveContext.getPreferenceTimeFilter(context,
                    context.getString(R.string.var_KEY_DEF_TIME_FILTER_FEEDING));
            String[] arguments = ActiveContext.createTimeFilter(context, timeFilter);
            return context.getContentResolver().query(
                    Contract.Feeding.CONTENT_URI,
                    Contract.Feeding.Query.PROJECTION,
                    "baby_id = ? AND timestamp >= ? AND timestamp <= ? AND sides = 'PUMP'",
                    arguments,
                    Contract.Feeding.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private String setTotalPumpedMilkText(Cursor cursor) {
            return context.getString(R.string.str_Remaining_stock) + " " +
                    TextFormation.volume(context, calculatePumpedMilkTotalVolume(cursor));
        }

        private String calculatePumpedMilkTotalVolume(Cursor cursor) {
            float pumpedMilkVolume = 0;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                pumpedMilkVolume += Float.valueOf(cursor.getString(Contract.Feeding.Query.OFFSET_PUMP));
            }
            return checkPumpedMilkExceedMaximum(pumpedMilkVolume);
        }

        private String checkPumpedMilkExceedMaximum(float pumpedMilkVolume) {
            if (pumpedMilkVolume > 99999.99f) {
                return context.getString(R.string.str_plentiful);
            } else {
                return twoSignificantDigit.format(pumpedMilkVolume);
            }
        }

        private void bindLastFormulaMilk() {
            Cursor cursor = requestLastFormulaMilkCursor();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                lastFormulaMilk.setText(setLastFormulaMilkText(cursor));
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                lastFormulaMilk.setText(setDefaultText());
            }
        }

        private Cursor requestLastFormulaMilkCursor() {
            String[] arguments = {String.valueOf(ActiveContext.getActiveBaby(context).getActivityId())};
            return context.getContentResolver().query(
                    Contract.Feeding.CONTENT_URI,
                    Contract.Feeding.Query.PROJECTION,
                    "baby_id = ? AND sides = 'FORMULA'",
                    arguments,
                    Contract.Feeding.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private String setLastFormulaMilkText(Cursor cursor) {
            return context.getString(R.string.str_Last_feeding) + " " +
                    TextFormation.last(context, cursor.getString(Contract.Feeding.Query.OFFSET_TIMESTAMP));
        }

        private void bindTotalFormulaMilk() {
            Cursor cursor = requestTotalFormulaMilkCursor();
            if (cursor.getCount() > 0) {
                totalFormulaMilk.setText(prepareTotalFormulaMilkText(cursor));
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                totalFormulaMilk.setText(setDefaultText());
            }
        }

        private Cursor requestTotalFormulaMilkCursor() {
            int timeFilter = ActiveContext.getPreferenceTimeFilter(context,
                    context.getString(R.string.var_KEY_DEF_TIME_FILTER_FEEDING));
            String[] arguments = ActiveContext.createTimeFilter(context, timeFilter);
            return context.getContentResolver().query(
                    Contract.Feeding.CONTENT_URI,
                    Contract.Feeding.Query.PROJECTION,
                    "baby_id = ? AND timestamp >= ? AND timestamp <= ? AND sides = 'FORMULA'",
                    arguments,
                    Contract.Feeding.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private String prepareTotalFormulaMilkText(Cursor cursor) {
            return context.getString(R.string.str_Total_feeding) + " " +
                    TextFormation.volume(context, calculateFormulaMilkTotalVolume(cursor));
        }

        private String calculateFormulaMilkTotalVolume(Cursor cursor) {
            float formulaMilkVolume = 0;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                formulaMilkVolume += Float.valueOf(cursor.getString(Contract.Feeding.Query.OFFSET_VOLUME));
            }
            return twoSignificantDigit.format(formulaMilkVolume);
        }

        private void bindLastBreastfeeding() {
            Cursor cursor = requestLastBreastfeedingCursor();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                lastBreastfeeding.setText(setLastBreastfeedingText(cursor));
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                lastBreastfeeding.setText(setDefaultText());
            }
        }

        private String setLastBreastfeedingText(Cursor cursor) {
            return context.getString(R.string.str_Last_feeding) + " " +
                    TextFormation.last(context, cursor.getString(Contract.Diaper.Query.OFFSET_TIMESTAMP));
        }

        private Cursor requestLastBreastfeedingCursor() {
            String[] arguments = {String.valueOf(ActiveContext.getActiveBaby(context).getActivityId())};
            return context.getContentResolver().query(
                    Contract.Feeding.CONTENT_URI,
                    Contract.Feeding.Query.PROJECTION,
                    "baby_id = ? AND (sides = 'LEFT' OR sides = 'RIGHT')",
                    arguments,
                    Contract.Feeding.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private void bindTotalBreastfeeding() {
            Cursor cursor = requestTotalBreastfeedingCursor();
            if (cursor.getCount() > 0) {
                totalBreastfeeding.setText(setTotalBreastfeedingText(cursor));
                todaySubHeader.setVisibility(View.VISIBLE);
                todaySubHeader.setText(ActiveContext.getDayFilter(context));
            } else {
                totalBreastfeeding.setText(setDefaultText());
            }
        }

        private String setTotalBreastfeedingText(Cursor cursor) {
            return context.getString(R.string.str_Total_feeding) + " " +
                    TextFormation.duration(context, String.valueOf(calculateTotalBreastfeedingDuration(cursor)));
        }

        private Cursor requestTotalBreastfeedingCursor() {
            int timeFilter = ActiveContext.getPreferenceTimeFilter(context,
                    context.getString(R.string.var_KEY_DEF_TIME_FILTER_FEEDING));
            String[] arguments = ActiveContext.createTimeFilter(context, timeFilter);
            return context.getContentResolver().query(
                    Contract.Feeding.CONTENT_URI,
                    Contract.Feeding.Query.PROJECTION,
                    "baby_id = ? AND timestamp >= ? AND timestamp <= ? AND (sides = 'LEFT' OR sides = 'RIGHT')",
                    arguments,
                    Contract.Feeding.Query.SORT_BY_ACTIVITY_ID_DESC);
        }

        private long calculateTotalBreastfeedingDuration(Cursor cursor) {
            long breastfeedingDuration = 0;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                breastfeedingDuration += Float.valueOf(cursor.getString(Contract.Feeding.Query.OFFSET_DURATION));
            }
            return breastfeedingDuration;
        }

        public void bindEntryData(Cursor cursor) {
            if (cursor.getCount() > 0) {
                String type = cursor.getString(Contract.Feeding.Query.OFFSET_TYPE);
                if (type.equals(FeedingModel.FeedingType.LEFT.getTitle())) {
                    bindLeft(cursor);
                } else if (type.equals(FeedingModel.FeedingType.RIGHT.getTitle())) {
                    bindRight(cursor);
                } else if (type.equals(FeedingModel.FeedingType.PUMP.getTitle())) {
                    bindPump(cursor);
                } else if (type.equals(FeedingModel.FeedingType.FORMULA.getTitle())) {
                    bindFormula(cursor);
                } else if (type.equals(FeedingModel.FeedingType.SOLID.getTitle())) {
                    bindSolid(cursor);
                } else if (type.equals(FeedingModel.FeedingType.SUB_HEADER.getTitle())) {
                    bindSubHeader(cursor);
                }
                bindMenuButton(cursor);
                //--debug
                bindDebugButton(cursor);
            }
        }

        private void bindLeft(Cursor cursor) {
            subHeaderLayout.setVisibility(View.GONE);
            entryLayout.setVisibility(View.VISIBLE);
            feedingType.setText(setLeftBreastfeedingTypeText());
            feedingImage.setImageResource(R.drawable.ic_left_breastfeeding);
            feedingDetails.setText(setLeftBreastfeedingDetailsText(cursor));
            timestamp.setText(setTimestamp(cursor));
        }

        private String setLeftBreastfeedingTypeText() {
            return context.getString(R.string.str_Left_breastfeeding);
        }

        private String setLeftBreastfeedingDetailsText(Cursor cursor) {
            return context.getString(R.string.str_Breastfeeding_for) + " " +
                    TextFormation.duration(context, cursor.getString(Contract.Feeding.Query.OFFSET_DURATION));
        }

        private void bindRight(Cursor cursor) {
            subHeaderLayout.setVisibility(View.GONE);
            entryLayout.setVisibility(View.VISIBLE);
            feedingType.setText(setRightBreastfeedingTypeTest());
            feedingImage.setImageResource(R.drawable.ic_right_breastfeeding);
            feedingDetails.setText(setRightBreastfeedingDetailsText(cursor));
            timestamp.setText(setTimestamp(cursor));
        }

        private String setRightBreastfeedingTypeTest() {
            return context.getString(R.string.str_Right_breastfeeding);
        }

        private String setRightBreastfeedingDetailsText(Cursor cursor) {
            return context.getString(R.string.str_Breastfeeding_for) + " " +
                    TextFormation.duration(context, cursor.getString(Contract.Feeding.Query.OFFSET_DURATION));
        }

        private void bindPump(Cursor cursor) {
            float pumpedMilkVolume = Float.valueOf(cursor.getString(Contract.Feeding.Query.OFFSET_PUMP));
            subHeaderLayout.setVisibility(View.GONE);
            entryLayout.setVisibility(View.VISIBLE);
            feedingImage.setImageResource(R.drawable.ic_breastpump);
            timestamp.setText(setTimestamp(cursor));
            if(checkPumpingActivity(pumpedMilkVolume)) {
                feedingType.setText(setPumpedMilkPumpingTypeText());
                feedingDetails.setText(setPumpedMilkPumpingDetailsText(pumpedMilkVolume));
            } else {
                feedingType.setText(setPumpedMilkFeedingTypeText());
                feedingDetails.setText(setPumpedMilkFeedingDetailsText(pumpedMilkVolume));
            }
        }

        private String setPumpedMilkFeedingDetailsText(float breastMilkPumpVolume) {
            return context.getString(R.string.str_Feeding) + " " + twoSignificantDigit.format(absoluteOf(breastMilkPumpVolume)) + " mL";
        }

        private String setPumpedMilkFeedingTypeText() {
            return context.getString(R.string.str_Pumped_milk_feeding);
        }

        private String setPumpedMilkPumpingTypeText() {
            return context.getString(R.string.str_Breast_milk_pumping);
        }

        private String setPumpedMilkPumpingDetailsText(float breastMilkPumpVolume) {
            return context.getString(R.string.str_Pumping) + " " + twoSignificantDigit.format(breastMilkPumpVolume) + " mL";
        }

        private boolean checkPumpingActivity(float volume) {
            return (volume >= 0.0f);
        }

        private float absoluteOf(float volume) {
            return (0.0f - volume);
        }

        private void bindFormula(Cursor cursor) {
            subHeaderLayout.setVisibility(View.GONE);
            entryLayout.setVisibility(View.VISIBLE);
            feedingType.setText(context.getString(R.string.str_Formula_milk_feeding));
            feedingImage.setImageResource(R.drawable.ic_formula);
            feedingDetails.setText(setFormulaMilkDetails(cursor));
            timestamp.setText(setTimestamp(cursor));
        }

        private String setFormulaMilkDetails(Cursor cursor) {
            return context.getString(R.string.str_Volume) + " " +
                    twoSignificantDigit.format(Float.valueOf(cursor.getString(Contract.Feeding.Query.OFFSET_VOLUME))) + " mL";
        }

        private void bindSolid(Cursor cursor) {
            subHeaderLayout.setVisibility(View.GONE);
            entryLayout.setVisibility(View.VISIBLE);
            feedingType.setText(setSolidFoodType(cursor));
            feedingImage.setImageResource(R.drawable.ic_solid_food);
            feedingDetails.setText(setSolidFoodDetails(cursor));
            timestamp.setText(setTimestamp(cursor));
        }

        private String setSolidFoodType(Cursor cursor) {
           return context.getString(R.string.str_Eating) + " " +
                   cursor.getString(Contract.Feeding.Query.OFFSET_NAME).toLowerCase();
        }

        private String setTimestamp(Cursor cursor) {
            return TextFormation.time(cursor.getString(Contract.Feeding.Query.OFFSET_TIMESTAMP));
        }

        private String setSolidFoodDetails(Cursor cursor) {
            return context.getString(R.string.str_Amount) + " " +
                    twoSignificantDigit.format(Float.valueOf(cursor.getString(Contract.Feeding.Query.OFFSET_VOLUME))) + " gr";
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
            menu.setTag(cursor.getString(Contract.Feeding.Query.OFFSET_ACTIVITY_ID));
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ImageView menu = (ImageView) view.findViewById(R.id.feeding_menu_button);
                    PopupMenu popupMenu = new PopupMenu(context, menu);
                    popupMenu.setOnMenuItemClickListener(
                            new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    if (item.getTitle()
                                            .equals(context.getResources().getString(R.string.str_Edit))) {
                                        editEntry(menu);
                                    } else if (item.getTitle()
                                            .equals(context.getResources().getString(R.string.str_Delete))) {
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

        //--debug
        private void bindDebugButton(Cursor cursor) {
            debugEdit.setTag(cursor.getString(Contract.Feeding.Query.OFFSET_ACTIVITY_ID));
            debugEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editEntry(debugEdit);
                }
            });
            debugDelete.setTag(cursor.getString(Contract.Feeding.Query.OFFSET_ACTIVITY_ID));
            debugDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteEntry(context, debugDelete);
                }
            });
        }

        public interface Callback {
            void onFeedingEntryEditSelected(View entry);
        }

    }

}
