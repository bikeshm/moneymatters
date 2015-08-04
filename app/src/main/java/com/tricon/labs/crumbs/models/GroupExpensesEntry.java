package com.tricon.labs.crumbs.models;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupExpensesEntry implements Parcelable {
    public int expenseId;
    public int groupId;
    public String expenseDate;
    public String description;
    public Contact spentBy;
    public double amount;

    public int status;

    public static final int STATUS_GIVE = 0;
    public static final int STATUS_GET = 1;

    protected GroupExpensesEntry(Parcel in) {
        expenseId = in.readInt();
        groupId = in.readInt();
        expenseDate = in.readString();
        description = in.readString();
        spentBy = in.readParcelable(Contact.class.getClassLoader());
        amount = in.readDouble();
        status = in.readInt();
    }

    public static final Creator<GroupExpensesEntry> CREATOR = new Creator<GroupExpensesEntry>() {
        @Override
        public GroupExpensesEntry createFromParcel(Parcel in) {
            return new GroupExpensesEntry(in);
        }

        @Override
        public GroupExpensesEntry[] newArray(int size) {
            return new GroupExpensesEntry[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(expenseId);
        dest.writeInt(groupId);
        dest.writeString(expenseDate);
        dest.writeString(description);
        dest.writeParcelable(spentBy,flags);
        dest.writeDouble(amount);
        dest.writeInt(status);
    }
}
