package com.bikesh.scorpio.giventake.model;

/**
 * Author : bikesh on 5/8/2015.
 */


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.bikesh.scorpio.giventake.libraries.functions.getContactbyphone;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "GivnTake.db";

    public static final String USER_TABLE_NAME = "usertable";
    public static final String USER_TABLE_FIELDS[]={"name","email","phone","description","photo"};

    public static final String LENDANDBORROW_TABLE_NAME = "lendandborrowtable";

    public static final String PERSONAL_TABLE_NAME = "personaltable";
    public static final String PERSONAL_TABLE_FIELDS[]={"category_id","created_date","description","amt"};

    public static final String COLLECTION_TABLE_NAME = "collectiontable";

    public static final String JOINTGROUP_TABLE_NAME = "joint_grouptable";
    public static final String JOINT_USER_GROUP_RELATION_TABLE_NAME="joint_usergrouprelationtable";
    public static final String JOINTENTRY_TABLE_NAME = "joint_entrytable";

    public static final String JOINTSPLITE_TABLE_NAME = "joint_splittable"; //face 2


    public static final String ONLINEJOINTGROUP_TABLE_NAME = "onlinejoint_grouptable";




    //private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table "+USER_TABLE_NAME+"  (_id INTEGER primary key autoincrement, onlineid text DEFAULT '0', name text, email text, phone text,password text, description text, photo BLOB )"
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
                "create table "+JOINTGROUP_TABLE_NAME+"  (_id INTEGER primary key autoincrement, onlineid text DEFAULT '0', isonline INTEGER DEFAULT 0, owner text, name text,  members_count INTEGER,ismonthlytask INTEGER , description text, totalamt FLOAT DEFAULT 0, balanceamt FLOAT DEFAULT 0, photo BLOB, last_updated DATE ,status text DEFAULT 'new')"  /* status=>new,udated (for knowing local changes)    */
        );
        db.execSQL(
                "create table "+JOINTENTRY_TABLE_NAME+"  (_id INTEGER primary key autoincrement, onlineid text DEFAULT '0', joint_group_id INTEGER, created_date DATE, description text, user_id INTEGER, amt FLOAT, is_split INTEGER DEFAULT 0, last_updated DATE, status text DEFAULT 'new' )"  /* status=>new,udated  (for knowing local changes)   */
        );
        db.execSQL(
                "create table "+JOINT_USER_GROUP_RELATION_TABLE_NAME+"  (_id INTEGER primary key autoincrement, user_id INTEGER, joint_group_id INTEGER  )"
        );

        db.execSQL(
                "create table "+JOINTSPLITE_TABLE_NAME+"  (_id INTEGER primary key autoincrement, jointtable_id INTEGER, user_id INTEGER, amt FLOAT )"
        );

        //table for storing online shard joint groups for offine use (backup database)
        //online_id:- this is the row id in parse, after inseting to parse this field will update
        //db.execSQL(
        //      "create table " + ONLINEJOINTGROUP_TABLE_NAME + "  (_id INTEGER primary key autoincrement, online_id text, name text, member_count INTEGER, owner text, description text, totalamt FLOAT DEFAULT 0, photo BLOB )"
        //);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS usertable");
        onCreate(db);
    }


    //-------------------------------------------------------------------------------------------------------
    //=======================================================================================================
    //common or global function

    // insert
    public int commonInsert  (Map<String, String> data, String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        for (Map.Entry<String, String> entry : data.entrySet())
        {
            contentValues.put(entry.getKey(), entry.getValue() );
        }

        db.insert(table, null, contentValues);
        db.close();
        return 1;
    }

    public int commonUpdate (Map<String, String> data, String table) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        for (Map.Entry<String, String> entry : data.entrySet())
        {
            if( !entry.getKey().equals("_id") ) {
                contentValues.put(entry.getKey(), entry.getValue());
            }
        }

        db.update(table, contentValues, "_id = ? ", new String[] { data.get("_id") } );
        db.close();
        return 1;
    }

    public int commonUpdateWhere (Map<String, String> data, String where,  String table) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        for (Map.Entry<String, String> entry : data.entrySet())
        {
            if( !entry.getKey().equals("_id") ) {
                contentValues.put(entry.getKey(), entry.getValue());
            }
        }

        db.update(table, contentValues, where+" = ? ", new String[] { data.get(where) } );
        db.close();
        return 1;
    }

    public Integer commonDelete (String id, String table)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(table,
                "_id = ? ",
                new String[]{id});

        db.close();
        return result;

    }

    //-------------------------------------------------------------------------------------------------------

    //Map<String, String> map = new HashMap<String, String>();
    //map.put("name", "demo");
    public int insertUser  (Map<String, String> data) {

        return commonInsert(data, "usertable");
    }

    public int updateUser(Map<String, String> data)
    {
        return commonUpdate(data,"usertable");
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


            while (res.isAfterLast() == false) {

                //Log.i("DB", res.getString(res.getColumnIndex("name")) );
                data.put("_id", res.getString(res.getColumnIndex("_id")));
                data.put("onlineid",  res.getString(res.getColumnIndex("onlineid")) );

                data.put("name", res.getString(res.getColumnIndex("name")));
                data.put("password", res.getString(res.getColumnIndex("password")));
                data.put("email", res.getString(res.getColumnIndex("email")));
                data.put("phone", res.getString(res.getColumnIndex("phone")));
                data.put("description", res.getString(res.getColumnIndex("description")));
                res.moveToNext();
            }
        }

        return data;
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from usertable where email='" + email.trim() + "'", null);
        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }


    public Map getUserbyOnlineId(String onlineId){

        Map<String, String> data = new HashMap<String, String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from usertable where onlineid="+onlineId+"", null );

        data = fetchUserData(res );

        res.close();
        return data;
    }

    public String getUserPhone(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from usertable where _id='"+userId+"'", null );
        if (res != null) {
            res.moveToFirst();

            if(res.getCount()>0) {
                return res.getString(res.getColumnIndex("phone"));
            }
        }
        return null;
    }

    public Cursor getUserByPhone(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from usertable where phone='"+phone.trim()+"'", null );
        if (res != null) {
            res.moveToFirst();
        }
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

    //get all users included me
    public Cursor getAllUsersIncludedMe() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from usertable ", null );
        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }


    //----------------------------------
    // todo :- need to work on this
    public Cursor getLendAndBorrowList() {
        /*SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from usertable where _id != 1", null );
        if (res != null) {
            res.moveToFirst();
        }
        return res;*/
        return null;
    }

    //------------------------------------------------------------------------------------------------------------


    public int insertEntry  (Map<String, String> data) {
        return commonInsert(data, "lendandborrowtable");
    }

    public int updateEntry (Map<String, String> data)
    {
        return commonUpdate(data,"lendandborrowtable");
    }



    public Integer deleteEntry (String id)
    {
        return commonDelete(id, "lendandborrowtable");
    }

    public Cursor getEntryById(String entryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from lendandborrowtable where _id = " + entryId, null);
        if (res != null) {
            res.moveToFirst();
        }
        return res;
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
        return commonInsert(data,"collectiontable");
    }


    public Cursor getCollectionById(String entryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from collectiontable where _id = " + entryId, null);
        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }

    public int updateCollection (Map<String, String> data)
    {
        return commonUpdate(data,"collectiontable");
    }

    public Integer deleteCollection (String id)
    {
        return commonDelete(id, "collectiontable");
    }

    public Integer deleteCollectionEntrys (String id)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("personaltable",
                "collection_id = ? ",
                new String[] { id });

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
        return commonInsert(data, "personaltable");
    }

    public Cursor getPersonalExpense(String entryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from personaltable where _id = " + entryId, null);
        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }

    public int updatePersonalExpense (Map<String, String> data)
    {
        return commonUpdate(data,"personaltable");
    }

    public Integer deletePersonalExpense (String id)
    {
        return commonDelete(id, "personaltable");
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
        return commonInsert(data, JOINTGROUP_TABLE_NAME);
    }

    public Cursor getJointGroupbyId(String groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+JOINTGROUP_TABLE_NAME+" where _id = "+ groupId, null );
        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }

    public Map fetchJointGroupbyId(String groupId){

        Cursor res = getJointGroupbyId(groupId);

        Map<String, String> data = new HashMap<String, String>();

        if(res!=null) {
            res.moveToFirst();

            while (res.isAfterLast() == false) {

                //Log.i("DB", res.getString(res.getColumnIndex("name")) );
                data.put("_id", res.getString(res.getColumnIndex("_id")));
                data.put("onlineid",  res.getString(res.getColumnIndex("onlineid")) );
                data.put("isonline",  res.getString(res.getColumnIndex("isonline")) );
                data.put("owner",  res.getString(res.getColumnIndex("owner")) );
                data.put("name", res.getString(res.getColumnIndex("name")));
                data.put("members_count", res.getString(res.getColumnIndex("members_count")));
                data.put("ismonthlytask", res.getString(res.getColumnIndex("ismonthlytask")));
                data.put("description", res.getString(res.getColumnIndex("description")));

                data.put("totalamt", res.getString(res.getColumnIndex("totalamt")));
                data.put("balanceamt", res.getString(res.getColumnIndex("balanceamt")));
                res.moveToNext();
            }
        }

        return data;
    }

    public Cursor getAllJointGroups() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+JOINTGROUP_TABLE_NAME, null );
        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }


    //---
    public Cursor getAllJointGroupsWithData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res=null;
        Map<String, String> data = new HashMap<String, String>();

        SimpleDateFormat dmy = new SimpleDateFormat("MM-yyyy");



        String sql=" select G._id,  G.name, G.totalamt,G.members_count,G.balanceamt," +
                " (G.totalamt / G.members_count  ) as perhead," +
                " (select Total(amt) from joint_entrytable where user_id = 1 and joint_group_id =  G._id  ) as i_spend, " +
                " (select Total(amt) from joint_entrytable where user_id = 1 and joint_group_id =  G._id and STRFTIME('%m-%Y', created_date) = '"+dmy.format(new Date())+"'  ) as i_spendinmonth" +
                " from joint_grouptable G";

        res = db.rawQuery(sql, null);





        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }
    //---

    public  Map<String, String> getJointGroup(Map<String, String> data) {
        SQLiteDatabase db = this.getReadableDatabase();

        Map<String, String> result = new HashMap<String, String>();


        //data.put("name",  ((EditText) addGroupView.findViewById(R.id.name) ).getText().toString() );

        String where="where";
        for (Map.Entry<String, String> entry : data.entrySet())
        {
            String value = entry.getValue();

            if(value.contains("'")){
                value=value.replace("'", "''").replace("\"", "\"\"");
            }


            where = where + " " + entry.getKey() +" = '"+ value +"' and ";
        }

        if(where.equals("where")){
            where="";
        }
        else{
            where = where.substring(0,where.length()-5); // removing last 'and'
        }

        Cursor res =  db.rawQuery( "select * from "+JOINTGROUP_TABLE_NAME+"  "+where+" " , null );

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

    public int insertUserGroupRelation (int groupId, ArrayList<String> members) {
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

    public int insertRelation (String userId,  String groupId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues;


        contentValues = new ContentValues();
        contentValues.put("joint_group_id",groupId );
        contentValues.put("user_id",userId );

        db.insert(JOINT_USER_GROUP_RELATION_TABLE_NAME, null, contentValues);



        db.close();
        return 1;
    }

    public int isRelationExist(String userId, String groupId){


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+JOINT_USER_GROUP_RELATION_TABLE_NAME+" where user_id='"+userId+"' and joint_group_id = '"+groupId+"'", null );

        if (res != null) {
            res.moveToFirst();

            return res.getCount();
        }


        res.close();


        return 0;
    }



    public Cursor getAllUsersInGroup(String groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from usertable where _id in ( select user_id from "+JOINT_USER_GROUP_RELATION_TABLE_NAME+" where joint_group_id = "+groupId+")", null );
        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }

    //for ongoing single exp
    public Cursor getGroupUsersData(String groupId) {
        /*
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select U.name, " +
                "(select Total(amt) from joint_entrytable where user_id = U._id and joint_group_id = "+groupId+" ), " +
                "((select Total(amt)/(select count(user_id) from joint_usergrouprelationtable where  joint_group_id = "+groupId+"  ) from joint_entrytable where joint_group_id = "+groupId+" )- (select Total(amt) from joint_entrytable where user_id = U._id and joint_group_id = "+groupId+"  ))  " +
                "from usertable U where u._id in ( select user_id from  joint_usergrouprelationtable  where  joint_group_id = "+groupId+" ) ", null );

        if (res != null) {
            res.moveToFirst();
        }
        return res;
        */
        return getGroupUsersData(groupId,null);
    }

    //for monthly renewing
    public Cursor getGroupUsersData(String groupId,String month) {
        SQLiteDatabase db = this.getReadableDatabase();

        if(month!=null){
            month= "  and STRFTIME('%m-%Y', created_date) = '"+month+"'";
        }else{
            month="";
        }

        Cursor res =  db.rawQuery( "select U.name, " +
                "(select Total(amt) from joint_entrytable where user_id = U._id and joint_group_id = "+groupId+ month +" ), " +
                "((select Total(amt)/(select count(user_id) from joint_usergrouprelationtable where  joint_group_id = "+groupId+"  ) from joint_entrytable where joint_group_id = "+groupId+ month +" )- (select Total(amt) from joint_entrytable where user_id = U._id and joint_group_id = "+groupId+ month +"  ))  " +
                "from usertable U where u._id in ( select user_id from  joint_usergrouprelationtable  where  joint_group_id = "+groupId+" ) ", null );

        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }




    //=======================================================
    public int insertGroupEntry(Map<String, String> data) {
        commonInsert(data, JOINTENTRY_TABLE_NAME);
        //calculating total balance
        updateGroupTotalAndBalance(data.get("joint_group_id"));
        return 1;
    }

    //--


    public Map<String, String> getAllGroupTotalSpendGiveGet() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res=null;
        Map<String, String> data = new HashMap<String, String>();

        data.put("total", "0");
        data.put("togive", "0");
        data.put("toget", "0");


        res = db.rawQuery(" select" +
                " (select Total(amt) from joint_entrytable where user_id = 1 ) as total," +
                " (select Total(balanceamt) from joint_grouptable where balanceamt >0  ) as togive," +
                " (select Total(balanceamt) from joint_grouptable where balanceamt <0  ) as toget", null);


        if (res != null) {
            res.moveToFirst();
            data.put("total", "" + String.format("%.2f", res.getFloat(0)));
            data.put("togive", ""+ String.format("%.2f", res.getFloat(1)) );
            data.put("toget", ""+ String.format("%.2f", (res.getFloat(2)*-1) ) );
        }



        res.close();
        db.close();
        return data;
    }

    //--


    public void updateGroupTotalAndBalance(String groupId){

        SQLiteDatabase db = this.getWritableDatabase();

        SimpleDateFormat dmy = new SimpleDateFormat("MM-yyyy");

        Cursor c=getJointGroupbyId(groupId);

        String month = "";
        if(c.getInt(c.getColumnIndex("ismonthlytask")) == 1){
            month= "  and STRFTIME('%m-%Y', created_date) = '"+dmy.format(new Date())+"'";
        }



        //isted of this send the new amount to this function and use that
        Cursor res =  db.rawQuery("UPDATE joint_grouptable " +
                " SET totalamt=(select Total(amt) from joint_entrytable where joint_group_id ="+groupId+ month +"), " +
                " balanceamt = (((select Total(amt) from joint_entrytable where joint_group_id ="+groupId+ month +")/members_count )- (select Total(amt) from joint_entrytable where user_id = 1 and joint_group_id =  "+groupId+ month +" )) "+
                " WHERE _id ="+groupId, null);

        res.moveToFirst();
        res.close();
    }



    public Map<String, String> getGroupEntry(Map<String, String> data){
        SQLiteDatabase db = this.getReadableDatabase();

        Map<String, String> result = new HashMap<String, String>();

        String where="where";
        for (Map.Entry<String, String> entry : data.entrySet())
        {
            String value = entry.getValue();

            if(value.contains("'")){
                value=value.replace("'", "''").replace("\"", "\"\"");
            }
            where = where + " " + entry.getKey() +" = '"+ value +"' and ";
        }

        if(where.equals("where")){
            where="";
        }
        else{
            where = where.substring(0,where.length()-5); // removing last 'and'
        }

        Cursor res =  db.rawQuery( "select * from "+JOINTENTRY_TABLE_NAME+"  "+where+" " , null );

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





    public Cursor getGroupEntrys(String groupId) {
        return getGroupEntrys(groupId,null);
    }

    public Cursor getGroupEntrys(String groupId, String month) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res=null;

        if(month!=null){
            month= "  and STRFTIME('%m-%Y', created_date) = '"+month+"'";
        }else{
            month="";
        }

        res = db.rawQuery("select E.created_date,E.description,U.name,E.amt,E.is_split from " + JOINTENTRY_TABLE_NAME +" E, usertable U where E.user_id = U._id and  joint_group_id = "+groupId+ month, null);

        if (res != null) {
            res.moveToFirst();
        }
        return res;
    }


    public Map<String, String> getGroupEntryTotalPerHead(String groupId) {
        return getGroupEntryTotalPerHead(groupId, null);
    }

    public Map<String, String> getGroupEntryTotalPerHead(String groupId, String month) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res=null;
        Map<String, String> data = new HashMap<String, String>();

        data.put("total", "0");
        data.put("perhead", "0");

        if(month!=null){
            month= "  and STRFTIME('%m-%Y', created_date) = '"+month+"'";
        }else{
            month="";
        }


        res = db.rawQuery("select Total(amt), "+
                " Total(amt)/(select count(user_id) from joint_usergrouprelationtable where  joint_group_id = "+groupId+"  ) " +
                " from " + JOINTENTRY_TABLE_NAME +" where  joint_group_id = " + groupId + month, null);


        if (res != null) {
            res.moveToFirst();
            data.put("total", "" + String.format("%.2f", res.getFloat(0)) );
            data.put("perhead", ""+ String.format("%.2f", res.getFloat(1)) );
        }



        res.close();
        db.close();
        return data;
    }


    //===========================Online Group===============================

    public int insertOnlineGroup(Map<String, String> data) {

        Map existingGroup = getOnlineGroup(data.get("group_id"));

        if( existingGroup.size()>0){
            //update
            Log.i("api call db","updating data");
            data.put("_id",existingGroup.get("_id").toString());
            data.remove("group_id");


            commonUpdate(data, JOINTGROUP_TABLE_NAME);
        }
        else {
            Log.i("api call db","inserting data");
            data.remove("group_id");
            data.remove("_id");
            commonInsert(data, JOINTGROUP_TABLE_NAME);
        }

        isOnlineGroupExist(data.get("group_id"));

        return 1;
    }

    public Map getOnlineGroup(String online_id) {
        Map<String, String> data = new HashMap<String, String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+JOINTGROUP_TABLE_NAME+" where onlineid='"+online_id+"'", null );

        if(res!=null) {
            res.moveToFirst();

            while (res.isAfterLast() == false) {

                //Log.i("DB", res.getString(res.getColumnIndex("name")) );
                data.put("_id", res.getString(res.getColumnIndex("_id")));
                data.put("onlineid",  res.getString(res.getColumnIndex("onlineid")) );
                data.put("owner", res.getString(res.getColumnIndex("owner")));
                data.put("name", res.getString(res.getColumnIndex("name")));
                data.put("members_count", res.getString(res.getColumnIndex("members_count")));
                data.put("ismonthlytask", res.getString(res.getColumnIndex("ismonthlytask")));
                data.put("description", res.getString(res.getColumnIndex("description")));
                data.put("totalamt", res.getString(res.getColumnIndex("totalamt")));
                data.put("balanceamt", res.getString(res.getColumnIndex("balanceamt")));
                res.moveToNext();
                break;
            }
        }

        res.close();
        return data;
    }

    public Boolean isOnlineGroupExist(String online_id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select _id from "+JOINTGROUP_TABLE_NAME+" where onlineid="+online_id+"", null );

        if(res!=null) {
            res.moveToFirst();
            if(res.getCount()>0){
                return true;
            }
        }

        res.close();
        return false;
    }



    public int updateOnlineEntrys (Map<String, String> dataCollection) {

        /*
        Map<String, String> data = new HashMap<String, String>();

        commonUpdateWhere (Map<String, String> data, String where,  String table)
        */

        return 1;
    }


    public void updateOnlineUserGroupRelation(Map<String, String> onlineUserdata, String group_id, ContentResolver ContentResolver) {

        //if(check phone already in user table){
        //get the user  _id
        //}
        //else{
        //  if(check user exsist in contact){
        //      get the data
        //      insert to user table
        //      get the user _id
        //  }
        //  else{
        //      inser data to user table
        //      get the user _id
        //  }
        //
        // }

        //  if(check same relation not exsist in relatoin)
        //      {
        //          update relation
        //      }



        String userId="0";
        Cursor userdata = getUserByPhone(onlineUserdata.get("phone").toString());
        if(userdata.getCount()>0){
            //user already in user table
            userId = userdata.getString(userdata.getColumnIndex("_id"));
        }
        else{
            Map<String, String> newUser = new HashMap<String, String>();

            //Log.i("api call", "checking contact "+ onlineUserdata.get("phone").toString());

            String name = getContactbyphone(onlineUserdata.get("phone").toString(), ContentResolver);
            if(name!=null) {
                newUser.put("name", name );
                //Log.i("api call", "exist in contact ");
            }
            else{
                newUser.put("name", onlineUserdata.get("name").toString() );
                //Log.i("api call", "Not exist in contact ");
            }

            newUser.put("onlineid", onlineUserdata.get("onlineid").toString());
            newUser.put("phone", onlineUserdata.get("phone").toString());

            //Log.i("api call", "inserting user in db ");
            insertUser(newUser);

            //Log.i("api call", "recursion ");
            updateOnlineUserGroupRelation(onlineUserdata, group_id, ContentResolver);
        }


        if(isRelationExist(userId, group_id )==0){
            insertRelation(userId,group_id);
        }

    }

    public void cleanupOnlineGroupRelation(ArrayList onlineGroupExistingUsers, String groupId) {
        /*
        * get all the users from relation who all are not existing in  onlineGroupExistingUsers
        * delete user from relation
        * delete user from entry
        // if(check anyone deleted from the grop from server)
        // update in local db
        * */

        //String[] exUserArray = new String[onlineGroupExistingUsers.size()];
        //String args = TextUtils.join(", ", exUserArray);

        //Log.i("api call","arg"+args);

        String args="";
        for (int i = 0; i < onlineGroupExistingUsers.size(); i++) {
           args= args+ onlineGroupExistingUsers.get(i)+", ";
        }

        if(!args.equals("")) {
            args = args.substring(0, args.length() - 2);
        }
        Log.i("api call","arg"+args);

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM "+JOINT_USER_GROUP_RELATION_TABLE_NAME+" WHERE joint_group_id = "+groupId+" and user_id NOT IN ("+args+");");
        db.close();

        db = this.getReadableDatabase();
        db.execSQL("DELETE FROM "+JOINTENTRY_TABLE_NAME+" WHERE joint_group_id = "+groupId+" and user_id NOT IN ("+args+");");
        db.close();
    }
}