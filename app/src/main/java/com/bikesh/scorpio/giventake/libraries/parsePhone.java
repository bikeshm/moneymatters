package com.bikesh.scorpio.giventake.libraries;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by bikesh on 6/3/2015.
 */
public class parsePhone {


    public static String parsePhone(String phone, String contryCode ) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phone,  contryCode.toUpperCase() ); //Locale.getDefault().getCountry()
            return  phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164) ;

        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        return "";
    }


    public static Map<String, String> parsePhoneGetAll(String phone) {

        Map<String, String> data = new HashMap<String, String>();

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phone, Locale.getDefault().getCountry());
            //return  phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164) ;

            data.put("E164", phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164));
            data.put("NATIONAL", phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL));
            data.put("INTERNATIONAL", phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL));
            data.put("TENDIGIT", phone.substring(phone.length() - 10) );



        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        return data;
    }
}
