package com.tricon.labs.pepper.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Group implements Parcelable {
    public int id;
    public int onlineid;
    public int isonline;
    public int owner;
    public String name;
    public Float totalAmount;
    public int membersCount;
    public int ismonthlytask;
    public Float balanceAmount;
    public Float amountPerHead;
    public Float amountSpentByMe;
    public Float amountSpentByMeCurrentMonth;

    public int status;

    public static final int STATUS_GIVE = 0;
    public static final int STATUS_GET = 1;

    public Group() {

    }

    protected Group(Parcel in) {
        id = in.readInt();
        onlineid = in.readInt();
        isonline = in.readInt();
        owner = in.readInt();
        name = in.readString();
        totalAmount = in.readFloat();
        membersCount = in.readInt();
        ismonthlytask = in.readInt();
        balanceAmount = in.readFloat();
        amountPerHead = in.readFloat();
        amountSpentByMe = in.readFloat();
        amountSpentByMeCurrentMonth = in.readFloat();
        status = in.readInt();

    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeInt(onlineid);
        dest.writeInt(isonline);
        dest.writeInt(owner);
        dest.writeString(name);
        dest.writeFloat(totalAmount);
        dest.writeInt(membersCount);
        dest.writeInt(ismonthlytask);
        dest.writeFloat(balanceAmount);
        dest.writeFloat(amountPerHead);
        dest.writeFloat(amountSpentByMe);
        dest.writeFloat(amountSpentByMeCurrentMonth);
        dest.writeInt(status);


    }
}
