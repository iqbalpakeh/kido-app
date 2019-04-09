/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.widget;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.progremastudio.kido.R;
import com.progremastudio.kido.module.vaccine.VaccineModel;

public class AlarmFragment extends Fragment {

    private int WAKELOCK_TIMEOUT = 60 * 1000;
    private String TAG = this.getClass().getSimpleName();
    private PowerManager.WakeLock mWakeLock;
    private MediaPlayer mPlayer;
    private View root;
    private Button done;
    private TextView name;
    private TextView location;
    private TextView notes;

    public static AlarmFragment getInstance() {
        return new AlarmFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playAlarmTone();
        ensureWakelockRelease();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root =  inflater.inflate(R.layout.fragment_alarm, container, false);
        prepareActionBar();
        prepareHandler();
        inflateHandler();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        addActivityWindowFlag();
        acquireWakelock();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWakeLock != null && mWakeLock.isHeld()) mWakeLock.release();
    }

    private void prepareHandler() {
        done = (Button) root.findViewById(R.id.done);
        name = (TextView) root.findViewById(R.id.vaccine_name_content);
        location = (TextView) root.findViewById(R.id.vaccine_location_content);
        notes = (TextView) root.findViewById(R.id.vaccine_notes_content);
    }

    private void inflateHandler() {
        VaccineModel entry = getActivity().getIntent().getParcelableExtra("ENTRY");
        name.setText(entry.getName());
        location.setText(entry.getLocation());
        notes.setText(entry.getNotes());
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.stop();
                if (mWakeLock != null && mWakeLock.isHeld()) mWakeLock.release();
                getActivity().finish();
            }
        });
    }

    private void prepareActionBar() {
        getActivity().getActionBar().setIcon(getResources().getDrawable(R.drawable.ic_about));
        getActivity().getActionBar().setTitle(getResources().getString(R.string.str_Alarm));
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void playAlarmTone() {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mPlayer = new MediaPlayer();
        try {
            if (alert != null) {
                mPlayer.setDataSource(getActivity(), alert);
                mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mPlayer.setLooping(true);
                mPlayer.prepare();
                mPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ensureWakelockRelease() {
        Runnable releaseWakelock = new Runnable() {
            @Override
            public void run() {
                try {
                    clearActivityWindowFlag();
                    if (mWakeLock != null && mWakeLock.isHeld()) mWakeLock.release();
                } catch (Exception error) {
                    //do nothing
                }
            }
        };
        new Handler().postDelayed(releaseWakelock, WAKELOCK_TIMEOUT);
    }

    private void clearActivityWindowFlag() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    private void addActivityWindowFlag() {
        // Set the window to keep screen on
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    private void acquireWakelock() {
        PowerManager pm = (PowerManager) getActivity().getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (mWakeLock == null) {
            mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
        }
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
            Log.i(TAG, "Wakelock acquired!!");
        }
    }
}
