/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.diaper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.progremastudio.kido.R;
import com.progremastudio.kido.util.ActiveContext;

import java.util.Calendar;

public class DiaperDialog extends DialogFragment {

    private Button dryHandler;
    private Button wetHandler;
    private Button mixHandler;

    public static DiaperDialog getInstance() {
        return new DiaperDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View root = inflater.inflate(R.layout.dialog_diaper, null);
        createDryHandler(root);
        createWetHandler(root);
        createMixHandler(root);
        builder.setView(root);
        builder.setNegativeButton(R.string.str_Cancel, null);
        return builder.create();
    }

    private void createDryHandler(View root) {
        dryHandler = (Button) root.findViewById(R.id.dialog_choice_dry);
        dryHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeDryEntry();
                getDialog().dismiss();
                openDiaperFragment();
            }
        });
    }

    private void createWetHandler(View root) {
        wetHandler = (Button) root.findViewById(R.id.dialog_choice_wet);
        wetHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeWetEntry();
                getDialog().dismiss();
                openDiaperFragment();
            }
        });
    }

    private void createMixHandler(View root) {
        mixHandler = (Button) root.findViewById(R.id.dialog_choice_mixed);
        mixHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeMixEntry();
                getDialog().dismiss();
                openDiaperFragment();
            }
        });
    }

    private void openDiaperFragment() {
        if (!ActiveContext.getCurrentFragment(getActivity()).equals(getString(R.string.str_Diaper))) {
            ActiveContext.setCurrentFragment(getActivity(), getString(R.string.str_Diaper));
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            ActiveContext.setCurrentFragment(getActivity(), getString(R.string.str_Diaper));
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.home_activity_container, DiaperFragment.getInstance(), getString(R.string.str_Diaper))
                    .setTransition(FragmentTransaction.TRANSIT_NONE)
                    .commit();
        }
    }

    private void storeDryEntry() {
        Bundle bundle = getArguments();
        if (bundle.getString("CREATE_OR_EDIT").equals("EDIT")) {
            DiaperModel model = new DiaperModel();
            model.setActivityId(Long.valueOf(bundle.getString("TAG_ACTIVITY")));
            model.setType(DiaperModel.DiaperType.DRY);
            model.edit(getActivity());
        } else if (bundle.getString("CREATE_OR_EDIT").equals("CREATE")) {
            DiaperModel model = new DiaperModel();
            model.setBabyID(ActiveContext.getActiveBaby(getActivity()).getActivityId());
            model.setFamilyId(ActiveContext.getActiveBaby(getActivity()).getFamilyId());
            model.setTimeStamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            model.setType(DiaperModel.DiaperType.DRY);
            model.insert(getActivity());
            model.httpPost(getActivity());
        }
    }

    private void storeWetEntry() {
        Bundle bundle = getArguments();
        if (bundle.getString("CREATE_OR_EDIT").equals("EDIT")) {
            DiaperModel model = new DiaperModel();
            model.setActivityId(Long.valueOf(bundle.getString("TAG_ACTIVITY")));
            model.setType(DiaperModel.DiaperType.WET);
            model.edit(getActivity());
        } else if (bundle.getString("CREATE_OR_EDIT").equals("CREATE")) {
            DiaperModel model = new DiaperModel();
            model.setBabyID(ActiveContext.getActiveBaby(getActivity()).getActivityId());
            model.setFamilyId(ActiveContext.getActiveBaby(getActivity()).getFamilyId());
            model.setTimeStamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            model.setType(DiaperModel.DiaperType.WET);
            model.insert(getActivity());
        }
    }

    private void storeMixEntry() {
        Bundle bundle = getArguments();
        if (bundle.getString("CREATE_OR_EDIT").equals("EDIT")) {
            DiaperModel model = new DiaperModel();
            model.setActivityId(Long.valueOf(bundle.getString("TAG_ACTIVITY")));
            model.setType(DiaperModel.DiaperType.MIXED);
            model.edit(getActivity());
        } else if (bundle.getString("CREATE_OR_EDIT").equals("CREATE")) {
            DiaperModel diaperModel = new DiaperModel();
            diaperModel.setBabyID(ActiveContext.getActiveBaby(getActivity()).getActivityId());
            diaperModel.setFamilyId(ActiveContext.getActiveBaby(getActivity()).getFamilyId());
            diaperModel.setTimeStamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            diaperModel.setType(DiaperModel.DiaperType.MIXED);
            diaperModel.insert(getActivity());
        }
    }
}
