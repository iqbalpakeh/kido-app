/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.models;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;

import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.util.SecurityUtils;
//import com.progremastudio.kido.util.VolleyService;

import java.util.HashMap;
import java.util.Map;


public class User extends BaseActor {

    public static final Creator CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel parcel) {
            return new User(parcel);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private String accessToken;
    private String loginType;

    public User() {
    }

    public User(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(familyId);
        parcel.writeString(accessToken);
        parcel.writeString(loginType);
    }

    public void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        familyId = parcel.readString();
        accessToken = parcel.readString();
        loginType = parcel.readString();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String inputPlainText) {
        this.loginType = SecurityUtils.computeSHA1(inputPlainText);
    }

    @Override
    public void insert(Context context) {
        ContentValues values = new ContentValues();
        values.put(Contract.User.USER_NAME, getName());
        values.put(Contract.User.FAMILY_ID, getFamilyId());
        values.put(Contract.User.ACCESS_TOKEN, getAccessToken());
        values.put(Contract.User.LOGIN_TYPE, getLoginType());
        context.getContentResolver().insert(Contract.User.CONTENT_URI, values);
    }

    @Override
    public void delete(Context context) {
    }

    @Override
    public void edit(Context context) {

    }

    @Override
    public void httpPost(Context context) {

        Map<Object, Object> small = new HashMap<Object, Object>();
        small.put("id", String.valueOf(getActivityId()));
        small.put("family_id", "XkfaDa7m");

        Map<Object, Object> param = new HashMap<Object, Object>();
        param.put("baby_id", "1");
        param.put("activity_id", "1");
        param.put("type", "sleep");
        param.put("timestamp", "2014-12-08 23:30:00");
        param.put("access_token", getAccessToken());
        param.put("user",small);

        //VolleyService volleyService = new VolleyService(context);
        //volleyService.volleyRequest(param);
    }

    public enum accountType {
        FACEBOOK("FACEBOOK"),
        GOOGLE("GOOGLE");

        private String title;

        accountType(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }
    }

}
