package com.bikesh.scorpio.giventake;

/**
 * Author : bikesh on 5/8/2015.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "GivnTake.db";

    public static final String USER_TABLE_NAME = "usertable";
    public static final String USER_TABLE_FIELDS[]={"name","email","phone","description","photo"};

    public static final String LENDANDBORROW_TABLE_NAME = "lendandborrowtable";

    public static final String PERSONAL_TABLE_NAME = "personaltable";
    public static final String PERSONAL_TABLE_FIELDS[]={"category_id","created_date","description","amt"};

    public static final String COLLECTION_TABLE_NAME = "collectiontable";

    public static final String JOINTENTRY_TABLE_NAME = "joint_entrytable";
    public static final String JOINTGROUP_TABLE_NAME = "joint_grouptable";
    public static final String JOINTSPLITE_TABLE_NAME = "joint_splittable";
    public static final String JOINT_USER_GROUP_RELATION_TABLE_NAME="joint_usergrouprelationtable";

    public static final String ONLINEJOINTGROUP_TABLE_NAME = "onlinejoint_grouptable";




    //private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table "+USER_TABLE_NAME+"  (_id INTEGER primary key autoincrement, onlineid INTEGER, name text, email text, phone text, description text, photo BLOB )"
        );

        db.execSQL(
                "create table "+LENDANDBORROW_TABLE_NAME+"  (_id INTEGER primary key autoincrement, created_date DATE, description text, from_user INTEGER, to_user INTEGER, amt FLOAT )"
        );



        db.execSQL(
                "create table "+PERSONAL_TABLE_NAME+"  (_id INTEGER primary key autoincrement, collection_id INTEGER, created_date DATE, description text, amt FLOAT )"
        );
        db.execSQL(
                "create table "+COLLECTION_TABLE_NAME+"  (_id INTEGER primary key autoincrement, name text, description text, photo BLOB )"
        );



        db.execSQL(
                "create table "+JOINTGROUP_TABLE_NAME+"  (_id INTEGER primary key autoincrement, name text,  members_count INTEGER, description text, totalamt FLOAT DEFAULT 0,photo BLOB )"
        );
        db.execSQL(
                "create table "+JOINTENTRY_TABLE_NAME+"  (_id INTEGER primary key autoincrement, joint_group_id INTEGER, created_date DATE, description text, owner_id INTEGER, user_id INTEGER, amt FLOAT, is_split INTEGER DEFAULT 0, is_month_wise INTEGER DEFAULT 0 )"
        );
        db.execSQL(
                "create table "+JOINT_USER_GROUP_RELATION_TABLE_NAME+"  (_id INTEGER primary key autoincrement, user_id INTEGER, joint_group_id INTEGER  )"
        );

        db.execSQL(
                "create table "+JOINTSPLITE_TABLE_NAME+"  (_id INTEGER primary key autoincrement, jointtable_id INTEGER, user_id INTEGER, amt FLOAT )"
        );

        //table for storing online shard joint groups for offine use (backup database)
        //online_id:- this is the row id in parse, after inseting to parse this field will update
        db.execSQL(
                "create table " + ONLINEJOINTGROUP_TABLE_NAME + "  (_id INTEGER primary key autoincrement, online_id text, name text, member_count INTEGER, owner text, description text, totalamt FLOAT DEFAULT 0, photo BLOB )"
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

        for (Map.Entry<String, String> entry : data.entrySet())
        {
            contentValues.put(entry.getKey(), entry.getValue() );
        }

        db.insert("usertable", null, contentValues);

        db.close();
        return 1;
    }

    public Map getUser(long id) {
        Map<String, String> data = new HashMap<String, String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from usertable where _id="+id+"", null );

        data = fetchUserData(res );

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
        Cursor res =  db.rawQuery("select * from usertable where email='" + email.trim() + "'", null);
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

        for (Map.Entry<String, String> entry : data.entrySet())
        {
            contentValues.put(entry.getKey(), entry.getValue() );
        }

        db.insert("lendandborrowtable", null, contentValues);
        return 1;
    }

    //createdDate = > "Month-Year" eg:- 5-2015
    public Cursor getUserEntrys(long userId, String createdDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from lendandborrowtable where (from_user = " + userId + " or to_user = " + userId + ") and STRFTIME('%m-%Y', created_date) = '" + createdDate + "' order by created_date ", null);
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
        data.put("amt_toGive", "0.0");
        if (res != null) {
            res.moveToFirst();
            data.put("amt_toGet", "" + res.getFloat(0));
            data.put("amt_toGive", ""+ ((res.getFloat(1)<0) ? (res.getFloat(1)*-1) : 0 ) );
        }

        res.close();

        return data;
    }

    //===========================================================================================================================

    public int insertCollection (Map<String, String> data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        for (Map.Entry<String, String> entry : data.entrySet())
        {
            contentValues.put(entry.getKey(), entry.getValue() );
        }

        db.insert("collectiontable", null, contentValues);
        db.close();
        return 1;
    }

    public Cursor getAllCollection() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from collectiontable", null);
        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }

    public Cursor getAllCollectionByMonth(String month) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from collectiontable  where _id in ( select collection_id from personaltable where  STRFTIME('%m-%Y', created_date) = '"+month+"') ", null );
        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }

    public float getCollectionTotalBalance(long ColId) {
        SQLiteDatabase db = this.getReadableDatabase();
        float balance=0;
        Cursor res =  db.rawQuery( "select ((select TOTAL(amt) from personaltable where category_id = "+ColId+"  )" , null );
        if (res != null) {
            res.moveToFirst();
            balance =  res.getFloat(0);
        }

        res.close();
        db.close();
        return balance;
    }
    //===========================================================================================================================

    public int insertPersonalExpense (Map<String, String> data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        for (Map.Entry<String, String> entry : data.entrySet())
        {
            contentValues.put(entry.getKey(), entry.getValue() );
        }

        db.insert("personaltable", null, contentValues);
        db.close();
        return 1;
    }

    public Cursor getPersonalExpense(long collectionId, String month) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.i("bm info", "select * from personaltable where collection_id = " + collectionId + "  and STRFTIME('%m-%Y', created_date) = '" + month + "'");
        Cursor res =  db.rawQuery( "select * from personaltable where collection_id = "+collectionId+"  and STRFTIME('%m-%Y', created_date) = '"+month+"'", null );
        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }


    public float getMonthTotalOfPersonalExpenseIndividual(long collectionId, String createdDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        float amt=0;
        Cursor res =  db.rawQuery( "select TOTAL(amt) from personaltable where collection_id = "+collectionId+" and STRFTIME('%m-%Y', created_date) = '"+createdDate+"'", null );

        if (res != null) {
            res.moveToFirst();
            amt =  res.getFloat(0);
        }
        else{
            amt=0;
        }

        res.close();
        db.close();
        return amt;
    }

    public float getMonthTotalOfPersonalExpense( String createdDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        float amt=0;
        Cursor res =  db.rawQuery( "select TOTAL(amt) from personaltable where  STRFTIME('%m-%Y', created_date) = '"+createdDate+"'", null );

        if (res != null) {
            res.moveToFirst();
            amt =  res.getFloat(0);
        }
        else{
            amt=0;
        }

        res.close();
        db.close();
        return amt;
    }


    //===========================================================================================================================


    public int insertJointGroup (Map<String, String> data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        for (Map.Entry<String, String> entry : data.entrySet())
        {
            contentValues.put(entry.getKey(), entry.getValue() );
        }

        db.insert(JOINTGROUP_TABLE_NAME, null, contentValues);
        db.close();
        return 1;
    }

    public  Map<String, String> getJointGroup(Map<String, String> data) {
        SQLiteDatabase db = this.getReadableDatabase();

        Map<String, String> result = new HashMap<String, String>();


        //data.put("name",  ((EditText) addGroupView.findViewById(R.id.name) ).getText().toString() );

        String where="";
        for (Map.Entry<String, String> entry : data.entrySet())
        {
            where = where + " " + entry.getKey() +" = '"+ entry.getValue()+"' and ";
        }

        if(where.equals("")){
            return result;
        }
        else{
            where = where.substring(0,where.length()-5); // removing last 'and'
        }

        Cursor res =  db.rawQuery( "select * from "+JOINTGROUP_TABLE_NAME+" where "+where+" " , null );

        data.put("_id", "0" ); // just adding id fied for fetching

        if (res != null) {
            res.moveToFirst();

            for (Map.Entry<String, String> entry : data.entrySet())
            {
                result.put(entry.getKey(), res.getString(res.getColumnIndex(entry.getKey())) );

            }

        }



        res.close();
        db.close();
        return result;
    }





    //===========================================================================================================================


//    "create table "+JOINT_USER_GROUP_RELATION_TABLE_NAME+"  (_id INTEGER primary key autoincrement, user_id INTEGER, joint_group_id INTEGER  )"

    public int insertUserGroupRelation (int groupId, ArrayList<Integer> members) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues;

        for(int i=0;i<members.size();i++)
        {
            contentValues = new ContentValues();
            contentValues.put("joint_group_id",groupId );
            contentValues.put("user_id",members.get(i) );

            db.insert(JOINT_USER_GROUP_RELATION_TABLE_NAME, null, contentValues);
        }


        db.close();
        return 1;
    }


}