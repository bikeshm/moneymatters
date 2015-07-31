package com.tricon.labs.pepper.models;

public class Group {
    public int id;
    public String name;
    public Float totalAmount;
    public int membersCount;
    public Float balanceAmount;
    public Float amountPerHead;
    public Float amountSpentByMe;
    public Float amountSpentByMeCurrentMonth;

    public int status;

    public static final int STATUS_GIVE = 0;
    public static final int STATUS_GET = 1;
}
