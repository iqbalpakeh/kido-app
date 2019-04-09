/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.sleep;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.progremastudio.kido.R;
import com.progremastudio.kido.util.ActiveContext;
import com.progremastudio.kido.widget.Stopwatch;

import java.util.Calendar;

public class SleepStopwatch extends Fragment {

    public static SleepStopwatch getInstance() {
        return new SleepStopwatch();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getActionBar().setIcon(getResources().getDrawable(R.drawable.ic_about));
        getActivity().getActionBar().setTitle(getResources().getString(R.string.str_Stopwatch));
        View root =  inflater.inflate(R.layout.fragment_stopwatch, container, false);
        TextView title = (TextView) root.findViewById(R.id.title);
        title.setText(getString(R.string.str_Sleep_duration));
        final Stopwatch timer = (Stopwatch) root.findViewById(R.id.timer);
        timer.start();
        Button done = (Button) root.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDone(timer);
            }
        });
        Button cancel = (Button) root.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return root;
    }

    private void handleDone(Stopwatch timer) {
        if (getActivity().getIntent().getStringExtra("TYPE").equals("NIGHT")) storeNightEntry(timer);
        else if (getActivity().getIntent().getStringExtra("TYPE").equals("DAY")) storeDayEntry(timer);
        getActivity().finish();
    }

    private void storeNightEntry(Stopwatch timer) {
        if (getActivity().getIntent().getStringExtra("CREATE_OR_EDIT").equals("EDIT")) {
            SleepModel sleepModel = new SleepModel();
            sleepModel.setActivityId(Long.valueOf(getActivity().getIntent().getStringExtra("TAG_ACTIVITY")));
            sleepModel.setDuration(timer.getDuration());
            sleepModel.setType(SleepModel.SleepType.NIGHT);
            sleepModel.edit(getActivity());
        } else if (getActivity().getIntent().getStringExtra("CREATE_OR_EDIT").equals("CREATE")) {
            SleepModel sleepModel = new SleepModel();
            sleepModel.setTimeStamp(String.valueOf(String.valueOf(Calendar.getInstance().getTimeInMillis())));
            sleepModel.setBabyID(ActiveContext.getActiveBaby(getActivity()).getActivityId());
            sleepModel.setFamilyId(ActiveContext.getActiveBaby(getActivity()).getFamilyId());
            sleepModel.setDuration(timer.getDuration());
            sleepModel.setType(SleepModel.SleepType.NIGHT);
            sleepModel.insert(getActivity());
        }
    }

    private void storeDayEntry(Stopwatch timer) {
        if (getActivity().getIntent().getStringExtra("CREATE_OR_EDIT").equals("EDIT")) {
            SleepModel sleepModel = new SleepModel();
            sleepModel.setActivityId(Long.valueOf(getActivity().getIntent().getStringExtra("TAG_ACTIVITY")));
            sleepModel.setDuration(timer.getDuration());
            sleepModel.setType(SleepModel.SleepType.DAY);
            sleepModel.edit(getActivity());
        } else if (getActivity().getIntent().getStringExtra("CREATE_OR_EDIT").equals("CREATE")) {
            SleepModel sleepModel = new SleepModel();
            sleepModel.setTimeStamp(String.valueOf(String.valueOf(Calendar.getInstance().getTimeInMillis())));
            sleepModel.setBabyID(ActiveContext.getActiveBaby(getActivity()).getActivityId());
            sleepModel.setFamilyId(ActiveContext.getActiveBaby(getActivity()).getFamilyId());
            sleepModel.setDuration(timer.getDuration());
            sleepModel.setType(SleepModel.SleepType.DAY);
            sleepModel.insert(getActivity());
        }
    }
}
