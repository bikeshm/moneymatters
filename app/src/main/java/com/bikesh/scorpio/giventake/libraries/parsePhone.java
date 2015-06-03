package com.bikesh.scorpio.giventake.libraries;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

/**
 * Created by bikesh on 6/3/2015.
 */
public class parsePhone {


    public static String parsePhone(String phone) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phone, Locale.getDefault().getCountry());
            return  phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164) ;

        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        return "";
    }
}
