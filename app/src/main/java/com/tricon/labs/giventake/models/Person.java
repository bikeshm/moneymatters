package com.tricon.labs.giventake.models;

public class Person {
    public int id;
    public String name;
    public Float totalAmount;
    public int status;

    public static final int STATUS_GIVE = 0;
    public static final int STATUS_GET = 1;
}
