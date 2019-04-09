/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.vaccine;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
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
import com.progremastudio.kido.util.TextFormation;
import com.progremastudio.kido.widget.RecyclerViewCursorAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class VaccineAdapter extends RecyclerViewCursorAdapter<VaccineAdapter.VaccineViewHolder> {

    public static final int HEADER = 0;
    public static final int ENTRY = 1;

    private LayoutInflater inflater;
    private VaccineViewHolder holder;
    private VaccineFragment fragment;
    private Context context;

    public VaccineAdapter(final Context context) {
        super();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setCallback(VaccineFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onBindViewHolder(VaccineViewHolder holder, Cursor cursor) {
        if (holder.getType() == HEADER) {
            // No header in this fragment
        } else if (holder.getType() == ENTRY) {
            holder.bindEntryData(cursor);
        }
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

    @Override
    public VaccineViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        if (type == HEADER) {
            View view = inflater.inflate(R.layout.tile_vaccine_header, parent, false);
            holder = new VaccineViewHolder(view, context, type);
            return holder;
        } else if (type == ENTRY) {
            View view = inflater.inflate(R.layout.tile_vaccine_entry, parent, false);
            holder = new VaccineViewHolder(view, context, type);
            holder.setCallback(fragment);
            return holder;
        }
        throw new RuntimeException("there is no type that matches the type " + type);
    }

    static class VaccineViewHolder extends RecyclerView.ViewHolder {

        private int type;
        private Context context;
        private Callback callback;
        private TextView name;
        private TextView timestamp;
        private TextView location;
        private TextView notes;
        private TextView dateReminder;
        private TextView timeReminder;
        private ImageView menu;

        public VaccineViewHolder(final View view, Context context, int type) {
            super(view);
            this.context = context;
            this.type = type;
            if (this.type == HEADER) {
                // No Header on this fragment
            } else if (this.type == ENTRY) {
                name = (TextView) view.findViewById(R.id.vaccine_name_content);
                timestamp = (TextView) view.findViewById(R.id.vaccine_timestamp);
                location = (TextView) view.findViewById(R.id.vaccine_location_content);
                notes = (TextView) view.findViewById(R.id.vaccine_notes_content);
                dateReminder = (TextView) view.findViewById(R.id.vaccine_reminder_date);
                timeReminder = (TextView) view.findViewById(R.id.vaccine_reminder_time);
                menu = (ImageView) view.findViewById(R.id.menu_button);
            }
        }

        public interface Callback {
            void onVaccineEntryEditSelected(View entry);
        }

        public void deleteEntry(Context context, View entry) {
            VaccineModel model = new VaccineModel();
            model.setActivityId(Long.valueOf((String) entry.getTag(R.id.vaccine_dialog)));
            model.delete(context);
        }

        public void editEntry(View entry) {
            callback.onVaccineEntryEditSelected(entry);
        }

        public void setCallback(Callback listener) {
            callback = listener;
        }

        public int getType() {
            return type;
        }

        private String getTimeFormat(Context context, String timeInMs) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(timeInMs));
            return (" " + context.getString(R.string.str_at) + " " + new SimpleDateFormat("HH:mm").format(calendar.getTime()));
        }

        public void bindEntryData(Cursor cursor) {
            if (cursor.getCount() > 0) {
                if (!cursor.getString(Contract.Vaccine.Query.OFFSET_REMINDER).equals("ALARM_NOT_SET")) {
                    dateReminder.setText(TextFormation.dateComplete(context, cursor.getString(Contract.Vaccine.Query.OFFSET_REMINDER)));
                    timeReminder.setText(getTimeFormat(context, cursor.getString(Contract.Vaccine.Query.OFFSET_REMINDER)));
                }
                timestamp.setText(TextFormation.dateComplete(context, cursor.getString(Contract.Vaccine.Query.OFFSET_TIMESTAMP)));
                name.setText(cursor.getString(Contract.Vaccine.Query.OFFSET_NAME));
                location.setText(cursor.getString(Contract.Vaccine.Query.OFFSET_LOCATION));
                notes.setText(cursor.getString(Contract.Vaccine.Query.OFFSET_NOTES));
                menu.setTag(cursor.getString(Contract.Vaccine.Query.OFFSET_ACTIVITY_ID));
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
        }

    }

}
