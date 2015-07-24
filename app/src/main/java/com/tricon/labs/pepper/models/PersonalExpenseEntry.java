package com.tricon.labs.pepper.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PersonalExpenseEntry implements Parcelable {
    public int entryId;
    public int categoryId;
    public String category;
    public String date;
    public String description;
    public double amount;

    public PersonalExpenseEntry() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        this.entryId = -1;
        this.categoryId = -1;
        this.category = "";
        this.date = simpleDateFormat.format(new Date());
        this.description = "";
        this.amount = 0;
    }

    public PersonalExpenseEntry(int entryId, int categoryId, String category, String date, String description, double amount) {
        this.entryId = entryId;
        this.categoryId = categoryId;
        this.category = category;
        this.date = date;
        this.description = description;
        this.amount = amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(entryId);
        dest.writeInt(categoryId);
        dest.writeString(category);
        dest.writeString(date);
        dest.writeString(description);
        dest.writeDouble(amount);
    }

    public PersonalExpenseEntry(Parcel in) {
        entryId = in.readInt();
        categoryId = in.readInt();
        category = in.readString();
        date = in.readString();
        description = in.readString();
        amount = in.readDouble();
    }

    public static final Creator<PersonalExpenseEntry> CREATOR = new Creator<PersonalExpenseEntry>() {
        @Override
        public PersonalExpenseEntry createFromParcel(Parcel in) {
            return new PersonalExpenseEntry(in);
        }

        @Override
        public PersonalExpenseEntry[] newArray(int size) {
            return new PersonalExpenseEntry[size];
        }
    };
}
