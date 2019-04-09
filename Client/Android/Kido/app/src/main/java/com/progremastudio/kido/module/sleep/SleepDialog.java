/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.sleep;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.progremastudio.kido.R;
import com.progremastudio.kido.core.ActivityChild;
import com.progremastudio.kido.util.ActiveContext;
import com.progremastudio.kido.widget.DurationPicker;

import java.util.Calendar;

public class SleepDialog extends DialogFragment implements DurationPicker.Callback {

    private final int selectedNight = 0;
    private final int selectedDay = 1;
    private DurationPicker durationPicker;
    private Button nightButton;
    private Button dayButton;
    private long sleepDuration;
    private int selectedButton;

    public static SleepDialog getInstance() {
        return new SleepDialog();
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View root = inflater.inflate(R.layout.dialog_sleep, null);
        createDurationPicker(root);
        createHandler(root);
        builder.setView(root);
        builder.setNegativeButton(R.string.str_Cancel, null);
        return builder.create();
    }

    private void createHandler(View root) {
        createNightHandler(root);
        createDayHandler(root);
    }

    private void createDurationPicker(View root) {
        durationPicker = new DurationPicker(getActivity(), root);
        durationPicker.setCallback(this);
        durationPicker.setTitle(getString(R.string.str_Sleep_duration));
    }

    private void createNightHandler(View root) {
        nightButton = (Button) root.findViewById(R.id.night_sleep);
        nightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedButton = selectedNight;
                switchLayout();
            }
        });
    }

    private void createDayHandler(View root) {
        dayButton = (Button) root.findViewById(R.id.day_sleep);
        dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedButton = selectedDay;
                switchLayout();
            }
        });
    }

    private void switchLayout() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String keyValue = sharedPref.getString(getString(R.string.var_KEY_COUNTER_METHOD_SLEEP), getString(R.string.str_Quick));
        if (keyValue.equals(getString(R.string.str_Quick))) {
            LinearLayout feedingLayout = (LinearLayout) getDialog().findViewById(R.id.sleep_layout);
            LinearLayout durationLayout = (LinearLayout) getDialog().findViewById(R.id.layout_duration);
            feedingLayout.setVisibility(View.GONE);
            durationLayout.setVisibility(View.VISIBLE);
        } else {
            getDialog().dismiss();
            openStopwatchLayout();
        }
    }

    private void openStopwatchLayout() {
        Intent intent = new Intent(getActivity(), ActivityChild.class);
        intent.putExtra("FRAGMENT", "SLEEPING_STOPWATCH");
        intent.putExtra("CREATE_OR_EDIT", getArguments().getString("CREATE_OR_EDIT"));
        if (selectedButton == selectedNight) intent.putExtra("TYPE", "NIGHT");
        else if (selectedButton == selectedDay) intent.putExtra("TYPE", "DAY");
        startActivity(intent);
    }

    @Override
    public void onDurationPickerOkClicked(Intent intent) {
        sleepDuration = intent.getLongExtra("duration", 0); // duration is in milliseconds
        storeSleepEntry();
        getDialog().dismiss();
        openSleepFragment();
    }

    private void openSleepFragment() {
        if (!ActiveContext.getCurrentFragment(getActivity()).equals(getString(R.string.str_Sleep))) {
            ActiveContext.setCurrentFragment(getActivity(), getString(R.string.str_Sleep));
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            ActiveContext.setCurrentFragment(getActivity(), getString(R.string.str_Sleep));
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.home_activity_container, SleepFragment.getInstance(), getString(R.string.str_Sleep))
                    .setTransition(FragmentTransaction.TRANSIT_NONE)
                    .commit();
        }
    }

    private void storeSleepEntry() {
        switch (selectedButton) {
            case selectedDay:
                storeDaySleepEntry();
                break;
            case selectedNight:
                storeNightSleepEntry();
                break;
        }
    }

    private void storeDaySleepEntry() {
        Bundle bundle = getArguments();
        if (bundle.getString("CREATE_OR_EDIT").equals("EDIT")) {
            SleepModel model = new SleepModel();
            model.setActivityId(Long.valueOf(bundle.getString("TAG_ACTIVITY")));
            model.setDuration(sleepDuration);
            model.setType(SleepModel.SleepType.DAY);
            model.edit(getActivity());
        } else if (bundle.getString("CREATE_OR_EDIT").equals("CREATE")) {
            SleepModel model = new SleepModel();
            model.setTimeStamp(String.valueOf(String.valueOf(Calendar.getInstance().getTimeInMillis())));
            model.setBabyID(ActiveContext.getActiveBaby(getActivity()).getActivityId());
            model.setFamilyId(ActiveContext.getActiveBaby(getActivity()).getFamilyId());
            model.setDuration(sleepDuration);
            model.setType(SleepModel.SleepType.DAY);
            model.insert(getActivity());
        }
    }

    private void storeNightSleepEntry() {
        Bundle bundle = getArguments();
        if (bundle.getString("CREATE_OR_EDIT").equals("EDIT")) {
            SleepModel model = new SleepModel();
            model.setActivityId(Long.valueOf(bundle.getString("TAG_ACTIVITY")));
            model.setDuration(sleepDuration);
            model.setType(SleepModel.SleepType.NIGHT);
            model.edit(getActivity());
        } else if (bundle.getString("CREATE_OR_EDIT").equals("CREATE")) {
            SleepModel model = new SleepModel();
            model.setTimeStamp(String.valueOf(String.valueOf(Calendar.getInstance().getTimeInMillis())));
            model.setBabyID(ActiveContext.getActiveBaby(getActivity()).getActivityId());
            model.setFamilyId(ActiveContext.getActiveBaby(getActivity()).getFamilyId());
            model.setDuration(sleepDuration);
            model.setType(SleepModel.SleepType.NIGHT);
            model.insert(getActivity());
        }
    }
}
