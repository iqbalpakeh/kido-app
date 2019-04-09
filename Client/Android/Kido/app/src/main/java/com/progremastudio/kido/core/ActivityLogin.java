/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.core;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.progremastudio.kido.R;

public class ActivityLogin extends FragmentActivity {

    public static final String FLAG_LOGIN = "FlagLogin";
    public static final String FLAG_SKIP_LOGIN = "FlagSkipLogin";
    public static final String INTENT_NEW_BABY_REQUEST = "IntentNewBabyRequest";
    public static final String INTENT_EDIT_BABY_REQUEST = "IntentEditBabyRequest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dispatcher();
    }

    private void dispatcher() {
        SharedPreferences sharedPreferences = getSharedPreferences(FLAG_LOGIN, 0);
        boolean isSkipLogin = sharedPreferences.getBoolean(FLAG_SKIP_LOGIN, false);
        boolean isNewBabyRequest = getIntent().getBooleanExtra(INTENT_NEW_BABY_REQUEST, false);
        boolean isEditBabyRequest = getIntent().getBooleanExtra(INTENT_EDIT_BABY_REQUEST, false);
        if (isSkipLogin && !isNewBabyRequest && !isEditBabyRequest) {
            goToHomeActivity();
        } else if (isSkipLogin && isNewBabyRequest) {
            goToBabyCreateFragment();
        } else if (isSkipLogin && isEditBabyRequest) {
            goToBabyEditFragment();
        } else {
            goToLoginFragment();
        }
    }

    private void goToHomeActivity() {
        startActivity(new Intent(this, ActivityHome.class));
        finish();
    }

    private void goToBabyCreateFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "CREATE");
        FragmentBabyInput fragment = FragmentBabyInput.getInstance();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.login_activity_container, fragment);
        fragmentTransaction.commit();
    }

    private void goToBabyEditFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "EDIT");
        FragmentBabyInput fragment = FragmentBabyInput.getInstance();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.login_activity_container, fragment);
        fragmentTransaction.commit();
    }

    private void goToLoginFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment module = FragmentIntroduction.getInstance();
        fragmentManager.beginTransaction().replace(R.id.login_activity_container, module).commit();
    }
}
