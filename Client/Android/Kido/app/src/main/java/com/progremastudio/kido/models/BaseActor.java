/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.models;

import android.os.Parcel;

abstract public class BaseActor extends BaseModel {

    protected String name;
    protected String familyId;
    protected Sex sex;

    public BaseActor() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        name = parcel.readString();
        sex = Sex.valueOf(parcel.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(name);
        parcel.writeString(sex.getTitle()); //time in milli second format
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public enum Sex {
        MALE("Boy"),
        FEMALE("Girl");
        private String title;

        Sex(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }
    }
}
