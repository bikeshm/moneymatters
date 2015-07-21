package com.tricon.labs.giventake.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LendAndBorrowEntry implements Parcelable {
    public int entryId;
    public int fromUser;
    public int toUser;
    public String date;
    public String description;
    public double amount;
    public String status;

    public String toUserName;

    public LendAndBorrowEntry() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        this.entryId = -1;
        this.fromUser = -1;
        this.toUser = -1;
        this.date = simpleDateFormat.format(new Date());
        this.description = "";
        this.amount = 0;
        this.status = "";
        this.toUserName = "";
    }

    public LendAndBorrowEntry(int entryId, int fromUser, int toUser, String date, String description, double amount, String status, String toUserName) {
        this.entryId = entryId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.status = status;
        this.toUserName=toUserName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(entryId);
        dest.writeInt(fromUser);
        dest.writeInt(toUser);
        dest.writeString(date);
        dest.writeString(description);
        dest.writeDouble(amount);
        dest.writeString(status);
        dest.writeString(toUserName);

    }

    public LendAndBorrowEntry(Parcel in) {
        entryId = in.readInt();
        fromUser = in.readInt();
        toUser = in.readInt();
        date = in.readString();
        description = in.readString();
        amount = in.readDouble();
        status = in.readString();
        toUserName = in.readString();
    }

    public static final Creator<LendAndBorrowEntry> CREATOR = new Creator<LendAndBorrowEntry>() {
        @Override
        public LendAndBorrowEntry createFromParcel(Parcel in) {
            return new LendAndBorrowEntry(in);
        }

        @Override
        public LendAndBorrowEntry[] newArray(int size) {
            return new LendAndBorrowEntry[size];
        }
    };
}
