package com.bikesh.scorpio.giventake;

/**
 * Created by bikesh on 5/8/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

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

        db.execSQL(
                "create table usertable  (_id INTEGER primary key autoincrement, name text, email text, phone text, description text, photo BLOB )"
        );
        // getting error is set _id numeric  (error: AUTOINCREMENT is only allowed on an INTEGER PRIMARY KEY:)

        db.execSQL(
                "create table lendandborrowtable  (_id INTEGER primary key autoincrement, created_date DATE, description text, from_user INTEGER, to_user INTEGER, amt FLOAT )"
        );



        db.execSQL(
                "create table personaltable  (_id INTEGER primary key autoincrement, category_id INTEGER, created_date DATE, description text, amt FLOAT )"
        );
        db.execSQL(
                "create table collectiontable  (_id INTEGER primary key autoincrement, name text, description text, photo BLOB )"
        );



        db.execSQL(
                "create table joint_grouptable  (_id INTEGER primary key autoincrement, name text, is_online INTEGER DEFAULT 0, owner INTEGER, description text, photo BLOB )"
        );
        db.execSQL(
                "create table jointtable  (_id INTEGER primary key autoincrement, joint_group_id INTEGER, created_date DATE, description text, owner_id INTEGER, user_id INTEGER, amt FLOAT, is_split INTEGER DEFAULT 0, is_month_wise INTEGER DEFAULT 0 )"
        );
        db.execSQL(
                "create table splittable  (_id INTEGER primary key autoincrement, jointtable_id INTEGER, user_id INTEGER, amt FLOAT )"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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

        //TODO:- move this to loop
        contentValues.put("name", data.get("name").trim());
        contentValues.put("email", data.get("email").trim());
        contentValues.put("phone", data.get("phone").trim());
        contentValues.put("description", data.get("description"));

        db.insert("usertable", null, contentValues);

        db.close();
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

    //get all users except me
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from usertable where _id != 1", null );
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


    //------------------------------------------------------------------------------------------------------------


    public int insertEntry  (Map<String, String> data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //TODO:- move this to loop
        contentValues.put("created_date", data.get("created_date").trim());
        contentValues.put("description", data.get("description").trim());
        contentValues.put("from_user", data.get("from_user").trim());
        contentValues.put("to_user", data.get("to_user"));
        contentValues.put("amt", data.get("amt"));

        db.insert("lendandborrowtable", null, contentValues);
        return 1;
    }

    //createdDate = > "Month-Year" eg:- 5-2015
    public Cursor getUserEntrys(long userId, String createdDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from lendandborrowtable where (from_user = "+userId+" or to_user = "+userId+") and STRFTIME('%m-%Y', created_date) = '"+createdDate+"'", null );
        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }

    public float getMonthTotalOfGive(long userId, String createdDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        float amt=0;
        Cursor res =  db.rawQuery( "select TOTAL(amt) from lendandborrowtable where from_user = "+userId+" and STRFTIME('%m-%Y', created_date) = '"+createdDate+"'", null );

        if (res != null) {
            res.moveToFirst();
            amt =  res.getFloat(0);
        }
        else{
            amt=0;
        }

        res.close();

        return amt;
    }

    public float getMonthTotalOfGet(long userId, String createdDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        float amt=0;
        Cursor res =  db.rawQuery( "select TOTAL(amt) from lendandborrowtable where to_user = "+userId+" and STRFTIME('%m-%Y', created_date) = '"+createdDate+"'", null );

        if (res != null) {
            res.moveToFirst();
            amt =  res.getFloat(0);
        }
        else{
            amt=0;
        }

        res.close();

        return amt;
    }


    //<0 get or give
    public float getTotalBalance(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        float balance=0;
        //Cursor res =  db.rawQuery( "select sum((select sum(amt) from lendandborrowtable where from_user = "+userId+")-(select sum(amt) from lendandborrowtable where from_user = "+userId+"))") , null );

        Cursor res =  db.rawQuery( "select ((select TOTAL(amt) from lendandborrowtable where from_user = "+userId+")-(select TOTAL(amt) from lendandborrowtable where to_user = "+userId+"))" , null );
        if (res != null) {
            res.moveToFirst();
            balance =  res.getFloat(0);
        }



        res.close();
        db.close();

        return balance;
    }

    public float getPrevBalance(long userId, String CurrentDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        float prevBalance=0;
        //Cursor res =  db.rawQuery( "select sum((select sum(amt) from lendandborrowtable where from_user = "+userId+")-(select sum(amt) from lendandborrowtable where from_user = "+userId+"))") , null );

        Cursor res =  db.rawQuery( "select ((select TOTAL(amt) from lendandborrowtable where from_user = "+userId+" and  STRFTIME('%m-%Y', created_date) < '"+CurrentDate+"'  )-(select TOTAL(amt) from lendandborrowtable where to_user = "+userId+" and  STRFTIME('%m-%Y', created_date) < '"+CurrentDate+"' ))" , null );


        if (res != null) {
            res.moveToFirst();
            prevBalance =  res.getFloat(0);
        }



        res.close();

        return prevBalance;
    }


    public Map<String, String> getFinalResult() {
        Map<String, String> data = new HashMap<String, String>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery(
                "select" +
                " TOTAL ( ( select (select TOTAL(amt) from lendandborrowtable where from_user = 1 and to_user = u._id  )  -(select TOTAL(amt) from lendandborrowtable where from_user =u._id and to_user = 1   ) as amt where amt>0      ) )," +
                " TOTAL ( ( select (select TOTAL(amt) from lendandborrowtable where from_user = 1 and to_user = u._id  )  -(select TOTAL(amt) from lendandborrowtable where from_user =u._id and to_user = 1   ) as amt where amt<0      ) )" +
                "from usertable u where _id !=1" , null );

        data.put("amt_toGet", "0.0");
        data.put("amt_toGive", "0.0" );
        if (res != null) {
            res.moveToFirst();
            data.put("amt_toGet", "" + res.getFloat(0));
            data.put("amt_toGive", ""+ ((res.getFloat(1)<0) ? (res.getFloat(1)*-1) : 0 ) );
        }

        res.close();

        return data;
    }

    //===========================================================================================================================

    public int insertCategory (Map<String, String> data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put("name", data.get("name").trim());
        contentValues.put("description", data.get("description"));

        db.insert("collectiontable", null, contentValues);
        return 1;
    }

    public Cursor getAllCategory() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from collectiontable", null );
        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }

    public float getCategoryTotalBalance(long ColId) {
        SQLiteDatabase db = this.getReadableDatabase();
        float balance=0;
        Cursor res =  db.rawQuery( "select ((select TOTAL(amt) from personaltable where category_id = "+ColId+"  )" , null );
        if (res != null) {
            res.moveToFirst();
            balance =  res.getFloat(0);
        }

        res.close();

        return balance;
    }


}