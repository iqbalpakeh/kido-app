/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.core;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.progremastudio.kido.R;
import com.progremastudio.kido.util.ActiveContext;

public class FragmentPreference extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static FragmentPreference getInstance() {
        return new FragmentPreference();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        ActiveContext.clearCurrentFragment(getActivity());
        Preference connectionPref = findPreference(key);
        connectionPref.setSummary(showSummaryValue(sharedPreferences.getString(key, "")));
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        resetSummary(getString(R.string.var_KEY_COUNTER_METHOD_BREASTFEEDING));
        resetSummary(getString(R.string.var_KEY_COUNTER_METHOD_SLEEP));
        resetSummary(getString(R.string.var_KEY_DEF_TIME_FILTER_TIMELINE));
        resetSummary(getString(R.string.var_KEY_DEF_TIME_FILTER_FEEDING));
        resetSummary(getString(R.string.var_KEY_DEF_TIME_FILTER_DIAPER));
        resetSummary(getString(R.string.var_KEY_DEF_TIME_FILTER_SLEEP));
        resetSummary(getString(R.string.var_KEY_DEF_TIME_FILTER_GROWTH));
        resetSummary(getString(R.string.var_KEY_DEF_TIME_FILTER_DISEASE));
        resetSummary(getString(R.string.var_KEY_DEF_TIME_FILTER_VACCINE));
    }

    private void resetSummary(String key) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String keyValue = sharedPref.getString(key, "");
        Preference preference = findPreference(key);
        preference.setSummary(showSummaryValue(keyValue));
    }

    private String showSummaryValue(String keyValue) {
        if (keyValue.equals(getString(R.string.str_Quick))) {
            return keyValue;
        } else if (keyValue.equals(getString(R.string.str_Stopwatch))) {
            return keyValue;
        } else if (keyValue.equals(getString(R.string.str_for_today))) {
            return getString(R.string.str_Today);
        } else if (keyValue.equals(getString(R.string.str_for_this_week))) {
            return getString(R.string.str_This_week);
        } else if (keyValue.equals(getString(R.string.str_for_this_month))) {
            return getString(R.string.str_This_month);
        } else {
            return getString(R.string.str_All);
        }
    }

}
