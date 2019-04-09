/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.timeline;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.progremastudio.kido.R;
import com.progremastudio.kido.module.diaper.DiaperModel;
import com.progremastudio.kido.module.disease.DiseaseModel;
import com.progremastudio.kido.module.feeding.FeedingModel;
import com.progremastudio.kido.module.measurement.MeasurementModel;
import com.progremastudio.kido.module.sleep.SleepModel;
import com.progremastudio.kido.module.vaccine.VaccineModel;
import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.util.TextFormation;

import java.text.DecimalFormat;

public class TimelineAdapter extends CursorAdapter {

    private Callback callback;

    public TimelineAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.adapter_timeline, parent, false);
        holder.infoTime = (TextView) view.findViewById(R.id.info_time);
        holder.infoFirst = (TextView) view.findViewById(R.id.widget_first);
        holder.infoSecond = (TextView) view.findViewById(R.id.widget_second);
        holder.infoThird = (TextView) view.findViewById(R.id.widget_third);
        holder.infoFourth = (TextView) view.findViewById(R.id.widget_fourth);
        holder.imageIcon = (ImageView) view.findViewById(R.id.info_type);
        holder.imageMenu = (ImageView) view.findViewById(R.id.menu_button);
        view.setTag(holder);
        return view;
    }

    public void setCallback(Callback listener) {
        callback = listener;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        Entry entry = new Entry(cursor);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.infoTime.setText(TextFormation.time(entry.getTimestamp()));
        holder.infoThird.setVisibility(View.GONE);
        holder.infoFourth.setVisibility(View.GONE);
        holder.imageMenu.setTag(R.id.widget_first, entry.getIdTag());
        holder.imageMenu.setTag(R.id.widget_second, entry.getTypeTag());
        holder.imageMenu.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ImageView menuHandler = (ImageView) v.findViewById(R.id.menu_button);
                        PopupMenu popup = new PopupMenu(context, menuHandler);
                        popup.setOnMenuItemClickListener(
                                new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        if (item.getTitle().equals("Edit")) {
                                            editEntry(menuHandler);
                                        } else if (item.getTitle().equals("Delete")) {
                                            deleteEntry(context, menuHandler);
                                        }
                                        return false;
                                    }
                                }
                        );
                        MenuInflater menuInflater = ((Activity) context).getMenuInflater();
                        menuInflater.inflate(R.menu.menu_entry, popup.getMenu());
                        popup.show();
                    }
                }
        );

        if (isEntryType(entry, Contract.Activity.TYPE_SLEEP)) {
            inflateSleepEntryLayout(context, view, holder, entry);
        } else if (isEntryType(entry, Contract.Activity.TYPE_DIAPER)) {
            inflateDiaperEntryLayout(context, view, holder, entry);
        } else if (isEntryType(entry, Contract.Activity.TYPE_FEEDING)) {
            inflateFeedingEntryLayout(context, view, holder, entry);
        } else if (isEntryType(entry, Contract.Activity.TYPE_MEASUREMENT)) {
            inflateMeasurementEntryLayout(context, view, holder, entry);
        } else if (isEntryType(entry, Contract.Activity.TYPE_DISEASE)) {
            inflateDiseaseEntryLayout(context, view, holder, entry);
        } else if (isEntryType(entry, Contract.Activity.TYPE_VACCINE)) {
            inflateVaccineEntryLayout(context, view, holder, entry);
        }
    }

    public void deleteEntry(Context context, View entry) {
        String id = (String) entry.getTag(R.id.widget_first);
        String type = (String) entry.getTag(R.id.widget_second);
        if (type.equals(Contract.Activity.TYPE_SLEEP)) {
            deleteSleepEntry(context, id);
        } else if (type.equals(Contract.Activity.TYPE_DIAPER)) {
            deleteDiaperEntry(context, id);
        } else if (type.equals(Contract.Activity.TYPE_FEEDING)) {
            deleteFeedingEntry(context, id);
        } else if (type.equals(Contract.Activity.TYPE_MEASUREMENT)) {
            deleteMeasurementEntry(context, id);
        } else if (type.equals(Contract.Activity.TYPE_DISEASE)) {
            deleteDiseaseEntry(context, id);
        } else if (type.equals(Contract.Activity.TYPE_VACCINE)) {
            deleteVaccineEntry(context, id);
        }
    }

    private void deleteSleepEntry(Context context, String id) {
        SleepModel sleep = new SleepModel();
        sleep.setActivityId(Long.valueOf(id));
        sleep.delete(context);
    }

    private void deleteDiaperEntry(Context context, String id) {
        DiaperModel diaper = new DiaperModel();
        diaper.setActivityId(Long.valueOf(id));
        diaper.delete(context);
    }

    private void deleteFeedingEntry(Context context, String id) {
        FeedingModel feeding = new FeedingModel();
        feeding.setActivityId(Long.valueOf(id));
        feeding.delete(context);
    }

    private void deleteMeasurementEntry(Context context, String id) {
        MeasurementModel measurementModel = MeasurementModel.getMeasurementModel(context, id);
        measurementModel.delete(context);
    }

    private void deleteDiseaseEntry(Context context, String id) {
        DiseaseModel disease = new DiseaseModel();
        disease.setActivityId(Long.valueOf(id));
        disease.delete(context);
    }

    private void deleteVaccineEntry(Context context, String id) {
        VaccineModel vaccine = new VaccineModel();
        vaccine.setActivityId(Long.valueOf(id));
        vaccine.delete(context);
    }

    public void editEntry(View entry) {
        callback.onTimelineEntryEditSelected(entry);
    }

    public boolean isEntryType(Entry entry, String type) {
        return entry.getActivityType().equals(type);
    }

    private void inflateSleepEntryLayout(Context context, View view, ViewHolder holder, Entry entry) {
        holder.infoFirst.setVisibility(View.VISIBLE);
        holder.infoFirst.setText(TextFormation.date(context, entry.getTimestamp()));
        holder.infoSecond.setVisibility(View.VISIBLE);
        holder.infoSecond.setText(TextFormation.timeBoundary(context, entry.getTimestamp(), entry.getSleepDuration()));
        holder.infoFourth.setVisibility(View.VISIBLE);
        holder.infoFourth.setText(TextFormation.duration(context, entry.getSleepDuration()));
        if (isNightSleep(entry)) {
            holder.infoThird.setVisibility(View.VISIBLE);
            holder.infoThird.setText(view.getResources().getString(R.string.str_Night_sleep));
            holder.imageIcon.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_sleep_night));
            holder.infoTime.setTextColor(view.getResources().getColor(R.color.transparent_blue));
            holder.infoSecond.setTextColor(view.getResources().getColor(R.color.transparent_blue));
            holder.infoThird.setTextColor(view.getResources().getColor(R.color.transparent_blue));
            holder.infoFourth.setTextColor(view.getResources().getColor(R.color.transparent_blue));
        } else {
            holder.infoThird.setVisibility(View.VISIBLE);
            holder.infoThird.setText(view.getResources().getString(R.string.str_Day_sleep));
            holder.imageIcon.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_sleep_nap));
            holder.infoTime.setTextColor(view.getResources().getColor(R.color.orange));
            holder.infoSecond.setTextColor(view.getResources().getColor(R.color.orange));
            holder.infoThird.setTextColor(view.getResources().getColor(R.color.orange));
            holder.infoFourth.setTextColor(view.getResources().getColor(R.color.orange));
        }
    }

    private void inflateDiaperEntryLayout(Context context, View view, ViewHolder holder, Entry entry) {
        holder.infoFirst.setVisibility(View.VISIBLE);
        holder.infoFirst.setText(TextFormation.date(context, entry.getTimestamp()));
        holder.infoSecond.setVisibility(View.VISIBLE);
        if (entry.getDiaperType().equals(DiaperModel.DiaperType.WET.getTitle())) {
            holder.infoSecond.setText(view.getResources().getString(R.string.str_Pee));
            holder.imageIcon.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_diaper_wet));
            holder.infoSecond.setTextColor(view.getResources().getColor(R.color.transparent_blue));
            holder.infoTime.setTextColor(view.getResources().getColor(R.color.transparent_blue));
        } else if (entry.getDiaperType().equals(DiaperModel.DiaperType.DRY.getTitle())) {
            holder.infoSecond.setText(view.getResources().getString(R.string.str_Poo));
            holder.imageIcon.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_diaper_dry));
            holder.infoSecond.setTextColor(view.getResources().getColor(R.color.orange));
            holder.infoTime.setTextColor(view.getResources().getColor(R.color.orange));
        } else if (entry.getDiaperType().equals(DiaperModel.DiaperType.MIXED.getTitle())) {
            holder.infoSecond.setText(view.getResources().getString(R.string.str_Pee_and_poo));
            holder.imageIcon.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_diaper_mixed));
            holder.infoSecond.setTextColor(view.getResources().getColor(R.color.purple));
            holder.infoTime.setTextColor(view.getResources().getColor(R.color.purple));
        }
    }

    private void inflateFeedingEntryLayout(Context context, View view, ViewHolder holder, Entry entry) {
        DecimalFormat format = new DecimalFormat("0.00");
        holder.infoFirst.setVisibility(View.VISIBLE);
        holder.infoFirst.setText(TextFormation.date(context, entry.getTimestamp()));
        holder.infoSecond.setVisibility(View.VISIBLE);
        holder.infoSecond.setText(TextFormation.duration(context, entry.getFeedingDuration()));
        if (entry.getFeedingType().compareTo(FeedingModel.FeedingType.FORMULA.getTitle()) == 0) {
            holder.infoThird.setVisibility(View.VISIBLE);
            holder.infoSecond.setVisibility(View.GONE);
            holder.infoThird.setText(format.format(Float.valueOf(entry.getFeedingVolume())) + " mL");
            holder.infoThird.setTextColor(view.getResources().getColor(R.color.red));
            holder.infoTime.setTextColor(view.getResources().getColor(R.color.red));
            holder.infoSecond.setTextColor(view.getResources().getColor(R.color.red));
            holder.imageIcon.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_formula_milk));
        } else if (entry.getFeedingType().compareTo(FeedingModel.FeedingType.RIGHT.getTitle()) == 0) {
            holder.infoThird.setVisibility(View.VISIBLE);
            holder.infoThird.setText(view.getResources().getString(R.string.str_Right_breastfeeding));
            holder.infoThird.setTextColor(view.getResources().getColor(R.color.green));
            holder.infoSecond.setTextColor(view.getResources().getColor(R.color.green));
            holder.infoTime.setTextColor(view.getResources().getColor(R.color.green));
            holder.imageIcon.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_right_breastfeeding));
        } else if (entry.getFeedingType().compareTo(FeedingModel.FeedingType.LEFT.getTitle()) == 0) {
            holder.infoThird.setVisibility(View.VISIBLE);
            holder.infoThird.setText(view.getResources().getString(R.string.str_Left_breastfeeding));
            holder.infoThird.setTextColor(view.getResources().getColor(R.color.orange));
            holder.infoSecond.setTextColor(view.getResources().getColor(R.color.orange));
            holder.infoTime.setTextColor(view.getResources().getColor(R.color.orange));
            holder.imageIcon.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_left_breastfeeding));
        } else if (entry.getFeedingType().compareTo(FeedingModel.FeedingType.SOLID.getTitle()) == 0) {
            holder.infoThird.setVisibility(View.VISIBLE);
            holder.infoSecond.setVisibility(View.GONE);
            holder.infoThird.setText(entry.getFeedingName());
            holder.infoThird.setTextColor(view.getResources().getColor(R.color.purple));
            holder.infoFourth.setVisibility(View.VISIBLE);
            holder.infoFourth.setText(format.format(Float.valueOf(entry.getFeedingVolume())) + " gr");
            holder.infoFourth.setTextColor(view.getResources().getColor(R.color.purple));
            holder.infoSecond.setTextColor(view.getResources().getColor(R.color.purple));
            holder.infoTime.setTextColor(view.getResources().getColor(R.color.purple));
            holder.imageIcon.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_solid_food));
        } else if (entry.getFeedingType().compareTo(FeedingModel.FeedingType.PUMP.getTitle()) == 0) {
            holder.infoThird.setVisibility(View.VISIBLE);
            holder.infoSecond.setVisibility(View.GONE);
            if (Float.valueOf(entry.getFeedingPump()) > 0) {
                holder.infoThird.setText(view.getResources().getString(R.string.str_Add_pumped_stock));
            } else {
                holder.infoThird.setText(view.getResources().getString(R.string.str_Use_pumped_stock));
            }
            holder.infoThird.setTextColor(view.getResources().getColor(R.color.teal));
            holder.infoFourth.setVisibility(View.VISIBLE);
            holder.infoFourth.setText(format.format(Float.valueOf(entry.getFeedingPump())) + " mL");
            holder.infoFourth.setTextColor(view.getResources().getColor(R.color.teal));
            holder.infoSecond.setTextColor(view.getResources().getColor(R.color.teal));
            holder.infoTime.setTextColor(view.getResources().getColor(R.color.teal));
            holder.imageIcon.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_breastpump));
        }
    }

    private void inflateMeasurementEntryLayout(Context context, View view, ViewHolder holder, Entry entry) {
        DecimalFormat format = new DecimalFormat("0.00");
        holder.imageIcon.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_measurement_navigation));
        holder.infoFirst.setVisibility(View.VISIBLE);
        holder.infoFirst.setText(TextFormation.date(context, entry.getTimestamp()));
        holder.infoSecond.setVisibility(View.VISIBLE);
        holder.infoSecond.setText(format.format(Float.valueOf(entry.getWeightMeasurement())) + " kg");
        holder.infoSecond.setTextColor(view.getResources().getColor(R.color.orange));
        holder.infoThird.setVisibility(View.VISIBLE);
        holder.infoThird.setText(view.getResources().getString(R.string.str_Height) + " "
                + format.format(Float.valueOf(entry.getHeightMeasurement())) + " cm");
        holder.infoThird.setTextColor(view.getResources().getColor(R.color.orange));
        holder.infoFourth.setVisibility(View.VISIBLE);
        holder.infoFourth.setText(view.getResources().getString(R.string.str_Head_circ) + " "
                + format.format(Float.valueOf(entry.getHeadCircMeasurement())) + " cm");
        holder.infoFourth.setTextColor(view.getResources().getColor(R.color.orange));
        holder.infoTime.setTextColor(view.getResources().getColor(R.color.orange));
    }

    private void inflateDiseaseEntryLayout(Context context, View view, ViewHolder holder, Entry entry) {
        holder.imageIcon.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_disease_navigation));
        holder.infoFirst.setVisibility(View.VISIBLE);
        holder.infoFirst.setText(TextFormation.date(context, entry.getTimestamp()));
        holder.infoSecond.setVisibility(View.VISIBLE);
        holder.infoSecond.setText(entry.getNameDisease());
        holder.infoSecond.setTextColor(view.getResources().getColor(R.color.blue_material));
        holder.infoTime.setTextColor(view.getResources().getColor(R.color.blue_material));
    }

    private void inflateVaccineEntryLayout(Context context, View view, ViewHolder holder, Entry entry) {
        holder.imageIcon.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_vaccine_navigation));
        holder.infoFirst.setVisibility(View.VISIBLE);
        holder.infoFirst.setText(TextFormation.date(context, entry.getTimestamp()));
        holder.infoSecond.setVisibility(View.VISIBLE);
        holder.infoSecond.setText(entry.getNameVaccine());
        holder.infoSecond.setTextColor(view.getResources().getColor(R.color.teal));
        holder.infoTime.setTextColor(view.getResources().getColor(R.color.teal));
    }

    private boolean isNightSleep(Entry entry) {
        return (entry.getSleepType().equals(SleepModel.SleepType.NIGHT.getTitle()));
    }

    public interface Callback {
        void onTimelineEntryEditSelected(View entry);
    }

    private static class ViewHolder {
        private TextView infoTime;
        private TextView infoFirst;
        private TextView infoSecond;
        private TextView infoThird;
        private TextView infoFourth;
        private ImageView imageIcon;
        private ImageView imageMenu;
    }

    private static class Entry {

        private String timestamp;
        private String activityType;
        private String sleepDuration;
        private String sleepType;
        private String diaperType;
        private String feedingType;
        private String feedingDuration;
        private String feedingVolume;
        private String feedingPump;
        private String feedingName;
        private String heightMeasurement;
        private String headCircMeasurement;
        private String weightMeasurement;
        private String nameDisease;
        private String nameVaccine;
        //private String reminderVaccine; //todo: add vaccine reminder data on timeline adapter
        private String idTag;
        private String typeTag;

        private Entry(Cursor cursor) {
            timestamp = cursor.getString(Contract.Activity.Query.OFFSET_TIMESTAMP);
            activityType = cursor.getString(Contract.Activity.Query.OFFSET_ACTIVITY_TYPE);
            sleepDuration = cursor.getString(Contract.Activity.Query.OFFSET_SLEEP_DURATION);
            sleepType = cursor.getString(Contract.Activity.Query.OFFSET_SLEEP_TYPE);
            diaperType = cursor.getString(Contract.Activity.Query.OFFSET_DIAPER_TYPE);
            feedingType = cursor.getString(Contract.Activity.Query.OFFSET_FEEDING_SIDES);
            feedingDuration = cursor.getString(Contract.Activity.Query.OFFSET_FEEDING_DURATION);
            feedingVolume = cursor.getString(Contract.Activity.Query.OFFSET_FEEDING_VOLUME);
            feedingPump = cursor.getString(Contract.Activity.Query.OFFSET_FEEDING_PUMP);
            feedingName = cursor.getString(Contract.Activity.Query.OFFSET_FEEDING_NAME);
            heightMeasurement = cursor.getString(Contract.Activity.Query.OFFSET_MEASUREMENT_HEIGHT);
            headCircMeasurement = cursor.getString(Contract.Activity.Query.OFFSET_MEASUREMENT_HEAD);
            weightMeasurement = cursor.getString(Contract.Activity.Query.OFFSET_MEASUREMENT_WEIGHT);
            nameDisease = cursor.getString(Contract.Activity.Query.OFFSET_DISEASE_NAME);
            nameVaccine = cursor.getString(Contract.Activity.Query.OFFSET_VACCINE_NAME);
            idTag = cursor.getString(Contract.Activity.Query.OFFSET_ID);
            typeTag = cursor.getString(Contract.Activity.Query.OFFSET_ACTIVITY_TYPE);
        }

        public String getActivityType() {
            return activityType;
        }

        public String getDiaperType() {
            return diaperType;
        }

        public String getFeedingDuration() {
            return feedingDuration;
        }

        public String getFeedingName() {
            return feedingName;
        }

        public String getFeedingPump() {
            return feedingPump;
        }

        public String getFeedingType() {
            return feedingType;
        }

        public String getFeedingVolume() {
            return feedingVolume;
        }

        public String getHeadCircMeasurement() {
            return headCircMeasurement;
        }

        public String getHeightMeasurement() {
            return heightMeasurement;
        }

        public String getIdTag() {
            return idTag;
        }

        public String getNameDisease() {
            return nameDisease;
        }

        public String getNameVaccine() {
            return nameVaccine;
        }

        public String getSleepDuration() {
            return sleepDuration;
        }

        public String getSleepType() {
            return sleepType;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getTypeTag() {
            return typeTag;
        }

        public String getWeightMeasurement() {
            return weightMeasurement;
        }
    }

}
