package com.tricon.labs.giventake.libraries;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.tricon.labs.giventake.database.DBHelper;
import com.tricon.labs.giventake.models.Category;
import com.tricon.labs.giventake.models.Contact;

import java.io.IOException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.tricon.labs.giventake.libraries.parsePhone.parsePhone;
import static com.tricon.labs.giventake.libraries.parsePhone.parsePhoneGetAll;

/**
 * Created by bikesh on 6/5/2015.
 */
public class functions {

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }



    /*
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)  getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }
    */


    public static  boolean isInternetAvailablexx() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8"); //Google DNS (e.g. 8.8.8.8)
            int     exitValue = ipProcess.waitFor();
            Log.i("int ip ",exitValue+"");
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        Log.i("int ip ","fffffffffff");
        return false;
    }

    public static  boolean isInternetAvailablex() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name

            Log.i("int ip ",ipAddr.toString());

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            Log.i("int error ", e.toString());
            return false;
        }

    }





















    public static String getInternetType(Context appContext) {
        String networkType="?";

        ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (null != networkInfo) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                networkType = "Wifi";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (networkInfo.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        networkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        networkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        networkType = "4G";
                        break;
                    default:
                        networkType = "?";
                        break;
                }
            } else {
                networkType = "?";
            }
        } else {
            networkType = "?";
        }

        Log.i("int ip ",networkType);
        return networkType;
    }




    public static String getContactByPhone(String phone, ContentResolver ContentResolver) {


        Map<String, String> parsedPhone = parsePhoneGetAll(phone);

        if(parsedPhone.size()>0) {

            Cursor cursorPhone = ContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},

                    ContactsContract.CommonDataKinds.Phone.NUMBER + " = ? OR "+
                    ContactsContract.CommonDataKinds.Phone.NUMBER + " = ? OR "+
                    ContactsContract.CommonDataKinds.Phone.NUMBER + " = ? OR "+
                    ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",

                    new String[]{parsedPhone.get("E164").toString(), parsedPhone.get("NATIONAL").toString(),parsedPhone.get("INTERNATIONAL").toString(),parsedPhone.get("TENDIGIT").toString(),    },
                    null);

            if (cursorPhone.moveToFirst()) {
                return cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            }
        }

        return null;

    }




    public static ArrayList<Contact> getContactList(Context context){

        ArrayList<Contact> list = new ArrayList<>();
        Contact contact;

        String APP_SETTINGS_PREFERENCES = "APPSWTTINGSPREFERENCES" ;
        SharedPreferences sharedpreferences;
        sharedpreferences = context.getSharedPreferences(APP_SETTINGS_PREFERENCES, Context.MODE_PRIVATE);


        //hardcoding now
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("CountryCode", "IN");
        editor.commit();
        //---

        String[] projection    = new String[] {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE};

        Cursor cursor =  context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (cursor != null) {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                contact = new Contact();

                contact.id    = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                contact.name  = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                contact.phone = parsePhone(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)), sharedpreferences.getString("CountryCode", "IN") );

                list.add(contact);

                cursor.moveToNext();
            }
            cursor.close();
        }


        return list;
    }







}
