/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.core;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.progremastudio.kido.R;
import com.progremastudio.kido.models.User;
import com.progremastudio.kido.util.ActiveContext;

public class FragmentLogIn extends Fragment {

    private static final String TAG = "LoginFragment";

    private User user;
    private EditText emailHandler;
    private EditText passwordHandler;
    private EditText confirmPasswordHandler;
    private Button loginHandler;
    private Button newUserHandler;
    private ImageButton cancelRegisterHandler;
    private ImageButton acceptRegisterHandler;
    private LinearLayout registerControlLayout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static FragmentLogIn getInstance() {
        return new FragmentLogIn();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = new User();
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        preparePasswordLogin(root, savedInstanceState);
        checkCurrentUserStatus();
        return root;
    }

    private void preparePasswordLogin(View rootView, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    userSignIn(user);
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    userSignOut(user);
                }
            }
        };
        emailHandler = (EditText) rootView.findViewById(R.id.email_handler);
        passwordHandler = (EditText) rootView.findViewById(R.id.password_handler);
        confirmPasswordHandler = (EditText) rootView.findViewById(R.id.confirm_password_handler);
        loginHandler = (Button) rootView.findViewById(R.id.login_handler);
        loginHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });
        newUserHandler = (Button) rootView.findViewById(R.id.register_handler);
        newUserHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNewUser();
            }
        });
        registerControlLayout = (LinearLayout) rootView.findViewById(R.id.register_control_layout);
        cancelRegisterHandler = (ImageButton) rootView.findViewById(R.id.cancel_register_handler);
        cancelRegisterHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCancelRegister();
            }
        });
        acceptRegisterHandler = (ImageButton) rootView.findViewById(R.id.accept_register_handler);
        acceptRegisterHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAcceptRegister();
            }
        });
    }

    private void checkCurrentUserStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userSignIn(user);
        }
    }

    private void userSignIn(FirebaseUser user) {
        setActiveUser(user);
        //todo: sync database??
        if (isBabyExist()) {
            skipLoginNextStartup();
            goToHomeActivity();
        } else {
            goToBabyInputFragment();
        }
    }

    private void userSignOut(FirebaseUser user) {
        //todo: sync database??
    }

    private void handleNewUser() {
        confirmPasswordHandler.setVisibility(View.VISIBLE);
        loginHandler.setVisibility(View.GONE);
        newUserHandler.setVisibility(View.GONE);
        registerControlLayout.setVisibility(View.VISIBLE);
    }

    private void handleLogin() {
        //todo : add progress dialog
        mAuth.signInWithEmailAndPassword(emailHandler.getText().toString(), passwordHandler.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleCancelRegister() {
        confirmPasswordHandler.setVisibility(View.GONE);
        loginHandler.setVisibility(View.VISIBLE);
        newUserHandler.setVisibility(View.VISIBLE);
        registerControlLayout.setVisibility(View.GONE);
    }

    private void handleAcceptRegister() {
        //todo : add progress dialog
        mAuth.createUserWithEmailAndPassword(emailHandler.getText().toString(), passwordHandler.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private boolean isBabyExist() {
        return !ActiveContext.getActiveBaby(getActivity()).getName().equals("");
    }

    private void setActiveUser(FirebaseUser firebaseUser) {
        (Toast.makeText(getActivity(), getString(R.string.str_Log_in_with_facebook_account)
                + " " + firebaseUser.getEmail(), Toast.LENGTH_LONG)).show();
        user.setName(firebaseUser.getDisplayName());
        user.setFamilyId(firebaseUser.getDisplayName()); // default family name is similar to the user name
        //user.setAccessToken(firebaseUser.getToken());
        user.setLoginType(User.accountType.FACEBOOK.getTitle());
        user.insert(getActivity());
        ActiveContext.setActiveUser(getActivity(), user);
    }

    private void goToBabyInputFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("CREATE_OR_EDIT", "CREATE");
        FragmentBabyInput fragment = FragmentBabyInput.getInstance();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.login_activity_container, fragment);
        fragmentTransaction.commit();
    }

    private void skipLoginNextStartup() {
        SharedPreferences setting = getActivity().getSharedPreferences(ActivityLogin.FLAG_LOGIN, 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putBoolean(ActivityLogin.FLAG_SKIP_LOGIN, true);
        editor.commit();
    }

    private void goToHomeActivity() {
        startActivity(new Intent(getActivity(), ActivityHome.class));
        getActivity().finish();
    }
}
