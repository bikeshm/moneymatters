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
import android.widget.EditText;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "GivnTake.db";

    public static final String USER_TABLE_NAME = "usertable";
    //public static final String USER_COLUMN_ID = "_id";
    //public static final String  USER_COLUMN_NAME = "name";
    //public static final String  USER_COLUMN_EMAIL = "email";
    //public static final String  USER_COLUMN_PHONE = "phone";
    //public static final String  USER_COLUMN_CITY = "description";
    //public static final String  USER_COLUMN_PHOTO = "photo";

    public static final String LENDANDBORROW_TABLE_NAME = "lendandborrowtable";
    public static final String PERSONAL_TABLE_NAME = "personaltable";
    public static final String JOINT_TABLE_NAME = "jointtable";



    //private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table usertable  (_id INTEGER primary key autoincrement, name text, email text, phone text, description text, photo BLOB )"
        );
        // getting error is set _id numeric  (error: AUTOINCREMENT is only allowed on an INTEGER PRIMARY KEY:)

        db.execSQL(
                "create table lendandborrowtable  (_id INTEGER primary key autoincrement, created_date DATETIME, description text, from_user INTEGER, to_user INTEGER, amt FLOAT )"
        );

        db.execSQL(
                "create table personaltable  (_id INTEGER primary key autoincrement, category_id INTEGER, created_date DATETIME, description text, amt FLOAT )"
        );
        db.execSQL(
                "create table category  (_id INTEGER primary key autoincrement, name text, description text, photo BLOB )"
        );


        db.execSQL(
                "create table jointtable  (_id INTEGER primary key autoincrement, joint_group_id INTEGER, created_date DATETIME, description text, owner_id INTEGER, user_id INTEGER, amt FLOAT )"
        );
        db.execSQL(
                "create table joint_group  (_id INTEGER primary key autoincrement, name text, owner INTEGER, description text, photo BLOB )"
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

    public Map getUser(long id) {
        Map<String, String> data = new HashMap<String, String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from usertable where _id="+id+"", null );

        data = fetchUserData(res );
        /*if(res!=null) {
            res.moveToFirst();
        }

        while(res.isAfterLast() == false){

            //Log.i("DB", res.getString(res.getColumnIndex("name")) );
            data.put("_id",  res.getString(res.getColumnIndex("_id")) );
            data.put("name",  res.getString(res.getColumnIndex("name")) );
            data.put("email",  res.getString(res.getColumnIndex("email")) );
            data.put("phone", res.getString(res.getColumnIndex("phone")) );
            data.put("description", res.getString(res.getColumnIndex("description")));
            res.moveToNext();
        }*/
        res.close();
        return data;
    }

    public Map fetchUserData(Cursor res ){
        Map<String, String> data = new HashMap<String, String>();

        if(res!=null) {
            res.moveToFirst();
        }

        while(res.isAfterLast() == false){

            //Log.i("DB", res.getString(res.getColumnIndex("name")) );
            data.put("_id",  res.getString(res.getColumnIndex("_id")) );
            data.put("name",  res.getString(res.getColumnIndex("name")) );
            data.put("email",  res.getString(res.getColumnIndex("email")) );
            data.put("phone", res.getString(res.getColumnIndex("phone")) );
            data.put("description", res.getString(res.getColumnIndex("description")));
            res.moveToNext();
        }

        return data;
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