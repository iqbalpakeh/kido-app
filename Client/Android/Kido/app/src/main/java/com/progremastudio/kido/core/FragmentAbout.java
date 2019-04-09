/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.core;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.progremastudio.kido.R;

public class FragmentAbout extends Fragment{

    public static FragmentAbout getInstance() {
        return new FragmentAbout();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_introduction, container, false);
        try {
            String packageName = getActivity().getPackageName();
            String version = getActivity().getPackageManager().getPackageInfo(packageName, 0).versionName;
            TextView applicationVersion = (TextView) root.findViewById(R.id.application_version);
            applicationVersion.setText("Version " + version);
        } catch (PackageManager.NameNotFoundException error) {
            Log.e("ERROR", error.getStackTrace().toString());
        }
        return root;
    }
}
