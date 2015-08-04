package com.tricon.labs.crumbs.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
    public int id;
    public String name;
    public String phone;

    public Contact(int id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    protected Contact(Parcel in) {
        id = in.readInt();
        name = in.readString();
        phone = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public boolean equals(Object contact) {
        return contact instanceof Contact && this.phone.equals(((Contact) contact).phone);
    }

    @Override
    public int hashCode() {
        return this.phone.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(phone);
    }
}
