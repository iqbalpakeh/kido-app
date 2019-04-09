/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.measurement;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.progremastudio.kido.R;
import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.util.ActiveContext;
import com.progremastudio.kido.util.AgeCalculator;
import com.progremastudio.kido.util.TextFormation;
import com.progremastudio.kido.widget.RecyclerViewCursorAdapter;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Calendar;

public class MeasurementAdapter extends RecyclerViewCursorAdapter<MeasurementAdapter.MeasurementViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ENTRY = 1;

    private LayoutInflater layoutInflater;
    private MeasurementViewHolder viewHolder;
    private MeasurementFragment measurementFragment;
    private Context context;

    public MeasurementAdapter(final Context context) {
        super();
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setCallBack(MeasurementFragment fragment) {
        this.measurementFragment = fragment;
    }

    @Override
    public void onBindViewHolder(MeasurementViewHolder holder, Cursor cursor) {
        if (holder.getType() == TYPE_HEADER) {
            // No header in this fragment
        } else if (holder.getType() == TYPE_ENTRY) {
            if (cursor.getCount() > 0) {
                (new MeasurementAdapter.ThumbnailTasks(context, holder, cursor))
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ENTRY;
    }

    @org.jetbrains.annotations.Contract(pure = true)
    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public MeasurementAdapter.MeasurementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = layoutInflater.inflate(R.layout.tile_measurement_header, parent, false);
            viewHolder = new MeasurementViewHolder(view, context, viewType);
            return viewHolder;
        } else if (viewType == TYPE_ENTRY) {
            View view = layoutInflater.inflate(R.layout.tile_measurement_entry, parent, false);
            viewHolder = new MeasurementViewHolder(view, context, viewType);
            viewHolder.setCallBack(measurementFragment);
            return viewHolder;
        }
        throw new RuntimeException("there is no type that matches the type " + viewType);
    }

    public static class MeasurementViewHolder extends RecyclerView.ViewHolder {

        public int type;
        public Context context;
        public ImageView image;
        public TextView age;
        public TextView height;
        public TextView weight;
        public TextView head;
        public ImageView menu;
        public Callback callback;

        DecimalFormat twoSignificantDigit = new DecimalFormat("0.00");

        public MeasurementViewHolder (final View view, Context context, int type) {
            super(view);
            this.context = context;
            this.type = type;
            if (this.type == TYPE_HEADER) {
                // No Header on this fragment
            } else if (this.type == TYPE_ENTRY) {
                age = (TextView) view.findViewById(R.id.info_age);
                height = (TextView) view.findViewById(R.id.history_item_height);
                weight = (TextView) view.findViewById(R.id.history_item_weight);
                head = (TextView) view.findViewById(R.id.history_item_head);
                menu = (ImageView) view.findViewById(R.id.menu_button);
                image = (ImageView) view.findViewById(R.id.image_content);
            }
        }

        public interface Callback {
            void onMeasurementEntryEditSelected(View entry);
        }

        public void deleteEntry(Context context, View entry) {
            MeasurementModel measurementModel = MeasurementModel.getMeasurementModel(context, (String) entry.getTag());
            measurementModel.delete(context);
        }

        public void editEntry(View entry) {
            callback.onMeasurementEntryEditSelected(entry);
        }

        public void setCallBack(MeasurementAdapter.MeasurementViewHolder.Callback listener) {
            callback = listener;
        }

        public int getType() {
            return type;
        }

        public void bindEntryData(Cursor cursor) {
            age.setText(getAge(cursor));
            height.setText(getHeight(cursor));
            weight.setText(getWeight(cursor));
            head.setText(getHead(cursor));
            menu.setTag(getTag(cursor));
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ImageView menuHandler = (ImageView) v.findViewById(R.id.menu_button);
                    PopupMenu popupMenu = new PopupMenu(context, menuHandler);
                    popupMenu.setOnMenuItemClickListener(
                            new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    if (item.getTitle().equals(context.getResources().getString(R.string.str_Edit))) {
                                        editEntry(menuHandler);
                                    } else if (item.getTitle().equals(context.getResources().getString(R.string.str_Delete))) {
                                        deleteEntry(context, menuHandler);
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

        private String getAge(Cursor cursor) {
            String timestamp = cursor.getString(Contract.Measurement.Query.OFFSET_TIMESTAMP);
            Calendar timestampCalendar = Calendar.getInstance();
            timestampCalendar.setTimeInMillis(Long.parseLong(timestamp));

            Calendar birthdayCalendar = ActiveContext.getActiveBaby(context).getBirthdayInCalendar();
            AgeCalculator age = new AgeCalculator(birthdayCalendar, timestampCalendar);

            return age.getMonthDifferent() + " "
                    + context.getResources().getString(R.string.str_Months_on) + " "
                    + TextFormation.date(context, timestamp);
        }

        private String getHeight(Cursor cursor) {
            String height = cursor.getString(Contract.Measurement.Query.OFFSET_HEIGHT);
            return context.getResources().getString(R.string.str_Body_Height) + " "
                    + twoSignificantDigit.format(Float.valueOf(height)) + " cm";
        }

        private String getWeight(Cursor cursor) {
            String weight = cursor.getString(Contract.Measurement.Query.OFFSET_WEIGHT);
            return context.getResources().getString(R.string.str_Body_Weight) + " "
                    + twoSignificantDigit.format(Float.valueOf(weight)) + " kg";
        }

        private String getHead(Cursor cursor) {
            String head = cursor.getString((Contract.Measurement.Query.OFFSET_HEAD));
            return context.getResources().getString(R.string.str_Head_Circumference) + " "
                    + twoSignificantDigit.format(Float.valueOf(head)) + " cm";
        }

        private String getTag(Cursor cursor) {
            return cursor.getString(Contract.Measurement.Query.OFFSET_ACTIVITY_ID);
        }

        private void setImage(Bitmap bitmap) {
            image.setImageBitmap(bitmap);
        }

    }

    private class ThumbnailTasks extends AsyncTask {

        private Context context;
        private MeasurementViewHolder viewHolder;
        private Cursor cursor;
        private Bitmap bitmap;

        public ThumbnailTasks(Context context, MeasurementViewHolder holder, Cursor cursor) {
            this.context = context;
            this.viewHolder = holder;
            this.cursor = cursor;
            this.viewHolder.bindEntryData(cursor);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Uri image;
            InputStream inputStream ;
            try {
                image = Uri.parse(cursor.getString(Contract.Measurement.Query.OFFSET_PICTURE));
                inputStream = context.getContentResolver().openInputStream(image);
                bitmap = BitmapFactory.decodeStream(inputStream, null, null);
            } catch (Exception error) {
                bitmap = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (viewHolder.getTag(cursor).equals(cursor.getString(Contract.Measurement.Query.OFFSET_ACTIVITY_ID))) {
                viewHolder.setImage(bitmap);
            }
        }
    }

}
