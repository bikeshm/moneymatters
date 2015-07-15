package com.tricon.labs.giventake.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bikesh on 6/24/2015.
 *
 * Pojo class
 */
public class Country {

    private String name;

    //the code is the actual value from json just maping to code2
    @SerializedName("code")
    private String code2;

    private String phoneCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code2;
    }

    public void setCode(String code) {
        this.code2 = code;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }
}
