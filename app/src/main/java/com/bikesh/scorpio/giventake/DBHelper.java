package com.bikesh.scorpio.giventake;

/**
 * Created by bikesh on 5/8/2015.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "GivnTake.db";

    public static final String USER_TABLE_NAME = "usertable";
    public static final String USER_COLUMN_ID = "_id";
    public static final String  USER_COLUMN_NAME = "name";
    public static final String  USER_COLUMN_EMAIL = "email";
    public static final String  USER_COLUMN_PHONE = "phone";
    public static final String  USER_COLUMN_CITY = "description";
    public static final String  USER_COLUMN_PHOTO = "photo";

    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table usertable  (_id integer primary key autoincrement, name text, email text, phone text, description text, photo BLOB )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS usertable");
        onCreate(db);
    }

    //Map<String, String> map = new HashMap<String, String>();
    //map.put("name", "demo");
    public int insertUser  (Map<String, String> data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //if(getUserByEmail(data.get("email")).getCount()>0 || getUserByPhone(data.get("phone")).getCount()>0 ){
        //    return 2; //user already exsist
        //}

        contentValues.put("name", data.get("name").trim());
        contentValues.put("email", data.get("email").trim());
        contentValues.put("phone", data.get("phone").trim());
        contentValues.put("description", data.get("description"));

        db.insert("usertable", null, contentValues);
        return 1;
    }
    public Cursor getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from usertable where id="+id+"", null );
        return res;
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from usertable where email='"+email.trim()+"'", null );
        return res;
    }

    public Cursor getUserByPhone(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from usertable where phone='"+phone.trim()+"'", null );
        return res;
    }

    public int getNumRowsUsertable(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, USER_TABLE_NAME);
        return numRows;
    }
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from usertable", null );
        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }

    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place)
    {
        /*
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        */
        return true;
    }

    public Integer deleteContact (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

}