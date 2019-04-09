/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.core;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.progremastudio.kido.R;

public class FragmentIntroduction extends Fragment {

    private final String TAG = "SplashActivity";
    private final int SPLASH_TIME = 4000;
    private boolean isActive = true;

    public static FragmentIntroduction getInstance() {
        return new FragmentIntroduction();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_introduction, container, false);
        try {
            String packageName = getActivity().getPackageName();
            String version = getActivity().getPackageManager().getPackageInfo(packageName, 0).versionName;
            TextView applicationVersion = (TextView) root.findViewById(R.id.application_version);
            applicationVersion.setText("Version " + version);
        } catch (PackageManager.NameNotFoundException error) {
            Log.e("ERROR", error.getStackTrace().toString());
        }

        final Thread splashScreenThread = new Thread() {
            public void run() {
                int wait = 0;
                try {
                    while (isActive && (SPLASH_TIME > wait)) {
                        sleep(100);
                        if (isActive) {
                            wait += 100;
                        }
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, e.getMessage());
                } finally {
                    Bundle bundle = new Bundle();
                    bundle.putString("CREATE_OR_EDIT", "CREATE");
                    FragmentBabyInput fragment = FragmentBabyInput.getInstance();
                    fragment.setArguments(bundle);

                    // debug code for skipping authentication login
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.login_activity_container, fragment);
                    fragmentTransaction.commit();

                    /*
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.login_activity_container, FragmentLogIn.getInstance());
                    fragmentTransaction.commit();
                    */
                }
            }
        };

        // Skip splash screen when running ui testing
        //splashScreenThread.start();

        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "CREATE");
        FragmentBabyInput fragment = FragmentBabyInput.getInstance();
        fragment.setArguments(bundle);

        // debug code for skipping authentication login
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.login_activity_container, fragment);
        fragmentTransaction.commit();

        return root;
    }
}
