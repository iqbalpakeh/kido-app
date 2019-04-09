package com.progremastudio.kido.widget;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

public abstract class RecyclerViewCursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private Cursor cursor;
    private int count;

    public abstract void onBindViewHolder(final VH holder, final Cursor cursor);

    @Override
    public final void onBindViewHolder(final VH holder, final int position) {
        final Cursor cursor = this.getItem(position);
        this.onBindViewHolder(holder, cursor);
    }

    @Override
    public int getItemCount() {
        // Total number of items in the data set hold by the adapter
        return count;
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
        if (cursor != null) {
            count = cursor.getCount() + 1; // = all entry + 1 header
        }
    }

    public Cursor getCursor() {
        return cursor;
    }

    public Cursor getItem(int position) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.moveToFirst();
            cursor.moveToPosition(position-1); // -1 is used to exclude header
        }
        return this.cursor;
    }

}