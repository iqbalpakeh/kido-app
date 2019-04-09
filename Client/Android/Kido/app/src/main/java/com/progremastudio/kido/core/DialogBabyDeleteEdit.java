/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.core;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.progremastudio.kido.R;
import com.progremastudio.kido.models.Baby;
import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.util.ActiveContext;

import java.io.File;

public class DialogBabyDeleteEdit extends DialogFragment {

    public static DialogBabyDeleteEdit getInstance() {
        return new DialogBabyDeleteEdit();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle.getString("DELETE_OR_EDIT").equals("EDIT")) return buildEditDialog();
        else return buildDeleteDialog();
    }

    private AlertDialog buildEditDialog() {
        final Cursor cursor = queryBabyToEdit(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (cursor.getCount() > 0) {
            builder.setCursor(cursor, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // delete old baby picture thumbnail
                    cursor.moveToPosition(which);
                    String thumbnailName = cursor.getString(Contract.Baby.Query.OFFSET_PICTURE).toString();
                    File tempFile = new File(thumbnailName.substring(6)); // to remove "file://" from string
                    if (tempFile.exists()) tempFile.delete();
                    // go to baby input fragment to edit
                    ActiveContext.clearCurrentFragment(getActivity());
                    Intent intent = new Intent(getActivity(), ActivityLogin.class);
                    intent.putExtra(ActivityLogin.INTENT_EDIT_BABY_REQUEST, true);
                    startActivity(intent);
                }
            }, Contract.Baby.NAME);
        }
        builder.setTitle("Edit baby");
        return builder.create();
    }

    private AlertDialog buildDeleteDialog() {
        final Cursor cursor = queryBabyToDelete(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (cursor.getCount() > 0) {
            builder.setCursor(cursor, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cursor.moveToPosition(which);
                    String name = cursor.getString(Contract.Baby.Query.OFFSET_NAME);
                    String picture = cursor.getString(Contract.Baby.Query.OFFSET_PICTURE);
                    String id = cursor.getString(Contract.Baby.Query.OFFSET_ID);
                    deleteBabyRelatedThumbnail(id);
                    deleteBabyDb(name, picture);
                }
            }, Contract.Baby.NAME);
        } else {
            builder.setMessage(getString(R.string.str_At_least_one_baby_must_be_created));
        }
        builder.setTitle("Delete baby");
        return builder.create();
    }

    private Cursor queryBabyToDelete(Context context) {
        String[] selectionArg = {ActiveContext.getActiveBaby(context).getName()};
        return context.getContentResolver().query(Contract.Baby.CONTENT_URI,
                Contract.Baby.Query.PROJECTION,
                "name <> ?",
                selectionArg,
                Contract.Baby.NAME);
    }

    private Cursor queryBabyToEdit(Context context) {
        return context.getContentResolver().query(Contract.Baby.CONTENT_URI,
                Contract.Baby.Query.PROJECTION,
                null,
                null,
                Contract.Baby.NAME);
    }

    private Cursor queryMeasurementThumbnailToDelete(Context context, String id) {
        String[] selectionArg = {id};
        return context.getContentResolver().query(Contract.Measurement.CONTENT_URI,
                Contract.Measurement.Query.PROJECTION,
                "baby_id = ?",
                selectionArg,
                Contract.Baby.ACTIVITY_ID);
    }

    private void deleteBabyDb(String name, String picture) {
        Baby baby = new Baby();
        baby.setName(name);
        baby.setPicture(Uri.parse(picture));
        baby.delete(getActivity());
    }

    private void deleteBabyRelatedThumbnail(String id) {
        /**
         * delete all thumbnail related. For now, there's only thumbnail from measurement fragment.
         */
        Cursor cursor = queryMeasurementThumbnailToDelete(getActivity(), id);
        if (cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String thumbnailName = cursor.getString(Contract.Measurement.Query.OFFSET_PICTURE);
                File tempFile = new File(thumbnailName.substring(6)); // to remove "file://" from string
                if (tempFile.exists()) tempFile.delete();
            }
        }
    }
}
