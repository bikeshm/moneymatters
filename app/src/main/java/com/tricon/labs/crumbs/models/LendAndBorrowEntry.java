package com.tricon.labs.crumbs.models;

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
    public int status;

    public static final int STATUS_GIVE = 0;
    public static final int STATUS_GET = 1;


    public LendAndBorrowEntry() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        this.entryId = -1;
        this.fromUser = -1;
        this.toUser = -1;
        this.date = simpleDateFormat.format(new Date());
        this.description = "";
        this.amount = 0;
        this.status = STATUS_GIVE;

    }

    public LendAndBorrowEntry(int entryId, int fromUser, int toUser, String date, String description, double amount, int status) {
        this.entryId = entryId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.status = status;
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
        dest.writeInt(status);

    }

    public LendAndBorrowEntry(Parcel in) {
        entryId = in.readInt();
        fromUser = in.readInt();
        toUser = in.readInt();
        date = in.readString();
        description = in.readString();
        amount = in.readDouble();
        status = in.readInt();
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
