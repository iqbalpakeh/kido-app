package com.progremastudio.kido.module.feeding;

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

public class FeedingStopwatch extends Fragment {

    public static FeedingStopwatch getInstance() {
        return new FeedingStopwatch();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getActionBar().setIcon(getResources().getDrawable(R.drawable.ic_about));
        getActivity().getActionBar().setTitle(getResources().getString(R.string.str_Stopwatch));
        View root =  inflater.inflate(R.layout.fragment_stopwatch, container, false);
        TextView title = (TextView) root.findViewById(R.id.title);
        title.setText(getString(R.string.str_Feeding_duration));
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
        FeedingModel entry = getActivity().getIntent().getParcelableExtra("ENTRY");
        entry.setDuration(timer.getDuration());
        if (getActivity().getIntent().getStringExtra("CREATE_OR_EDIT").equals("EDIT")) {
            entry.setActivityId(Long.valueOf(getActivity().getIntent().getStringExtra("TAG_ACTIVITY")));
            entry.edit(getActivity());
        } else if (getActivity().getIntent().getStringExtra("CREATE_OR_EDIT").equals("CREATE")) {
            entry.setTimeStamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            entry.setBabyID(ActiveContext.getActiveBaby(getActivity()).getActivityId());
            entry.setFamilyId(ActiveContext.getActiveBaby(getActivity()).getFamilyId());
            entry.insert(getActivity());
        }
        getActivity().finish();
    }
}
