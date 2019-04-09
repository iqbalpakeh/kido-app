/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.disease;

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

public class DiseaseAdapter extends RecyclerViewCursorAdapter<DiseaseAdapter.DiseaseViewHolder>{

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ENTRY = 1;

    private LayoutInflater layoutInflater;
    private DiseaseViewHolder viewHolder;
    private DiseaseFragment diseaseFragment;
    private Context context;

    public DiseaseAdapter(final Context context) {
        super();
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setCallBack(DiseaseFragment fragment) {
        this.diseaseFragment = fragment;
    }

    @Override
    public void onBindViewHolder(DiseaseViewHolder holder, Cursor cursor) {
        if(holder.getType() == TYPE_HEADER) {
            // No header in this fragment
        } else if(holder.getType() == TYPE_ENTRY) {
            holder.bindEntryData(cursor);
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
    public DiseaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) {
            View view = layoutInflater.inflate(R.layout.tile_disease_header, parent, false);
            viewHolder = new DiseaseViewHolder(view, context, viewType);
            return viewHolder;
        } else if(viewType == TYPE_ENTRY) {
            View view = layoutInflater.inflate(R.layout.tile_disease_entry, parent, false);
            viewHolder = new DiseaseViewHolder(view, context, viewType);
            viewHolder.setCallback(diseaseFragment);
            return viewHolder;
        }
        throw new RuntimeException("there is no type that matches the type " + viewType);
    }

    public static class DiseaseViewHolder extends RecyclerView.ViewHolder {

        private int type;
        private Context context;
        private Callback callback;
        private TextView timeHandler;
        private TextView nameHandler;
        private TextView symptomHandler;
        private TextView treatmentHandler;
        private TextView notesHandler;
        private ImageView menuHandler;

        public DiseaseViewHolder (final View view, Context context, int type) {
            super(view);
            this.context = context;
            this.type = type;
            if(this.type == TYPE_HEADER) {
                // No Header on this fragment
            } else if(this.type == TYPE_ENTRY) {
                timeHandler = (TextView) view.findViewById(R.id.disease_timestamp);
                nameHandler = (TextView) view.findViewById(R.id.disease_name_content);
                symptomHandler = (TextView) view.findViewById(R.id.disease_symptom_content);
                treatmentHandler = (TextView) view.findViewById(R.id.disease_treatment_content);
                notesHandler = (TextView) view.findViewById(R.id.disease_notes_content);
                menuHandler = (ImageView) view.findViewById(R.id.menu_button);
            }
        }

        public interface Callback {
            void onDiseaseEntryEditSelected(View entry);
        }

        public void deleteEntry(Context context, View entry) {
            DiseaseModel diseaseModel = new DiseaseModel();
            diseaseModel.setActivityId(Long.valueOf((String) entry.getTag(R.id.disease_dialog)));
            diseaseModel.delete(context);
        }

        public void editEntry(View entry) {
            callback.onDiseaseEntryEditSelected(entry);
        }

        public void setCallback(Callback listener) {
            callback = listener;
        }

        public int getType() {
            return type;
        }

        public void bindEntryData(Cursor cursor) {
            if(cursor.getCount() > 0) {
                timeHandler.setText(TextFormation.dateComplete(context, cursor.getString(Contract.Disease.Query.OFFSET_TIMESTAMP)));
                nameHandler.setText(cursor.getString(Contract.Disease.Query.OFFSET_NAME));
                symptomHandler.setText(cursor.getString(Contract.Disease.Query.OFFSET_SYMPTOM));
                treatmentHandler.setText(cursor.getString(Contract.Disease.Query.OFFSET_TREATMENT));
                notesHandler.setText(cursor.getString(Contract.Disease.Query.OFFSET_NOTES));
                menuHandler.setTag(cursor.getString(Contract.Disease.Query.OFFSET_ACTIVITY_ID));
                menuHandler.setOnClickListener(new View.OnClickListener() {
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
