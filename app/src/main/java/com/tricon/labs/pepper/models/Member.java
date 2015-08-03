package com.tricon.labs.pepper.models;

public class Member {
    public int id;
    public String name;
    public Float amountSpent;
    public Float amountBalance;

    public int status;

    public static final int STATUS_GIVE = 0;
    public static final int STATUS_GET = 1;
}
