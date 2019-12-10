package com.example.gmucoursetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;

public class Databasehelper  extends SQLiteOpenHelper {
    private static final String TAG = "+++++++++ DATABASE HELPER ++++++++++";

    private SQLiteDatabase db = null;

    final private static String db_name = "SectionTracker6";
    final private static Integer VERSION = 1;
    final private Context context;

    public  static String table_name = "sections";
    private String _id = "_id";
    public static  String crn="crn";
    public static  String title="title";
    public static  String name="name";
    public static  String section="section";
    public static  String available = "available";
    public static  String instructor="instructor";
    public static  String time= "time";
    public static  String day="day";
    public static  String remaining="remaining";
    public static  String capacity="capacity";

    //private String[] columns = new String[]{_id,crn,title,name,section,available,instructor,time,day,remaining,capacity};

    private String CREATE_CMD =  "CREATE TABLE " + table_name + " " +
            "(" + _id + " INTEGER PRIMARY KEY AUTOINCREMENT, "+ crn + " TEXT NOT NULL, " + name +
            " TEXT, " + title + " TEXT, "+ section + " TEXT, " + available + " TEXT, " + instructor + " TEXT, " + time +
            " TEXT, " + day + " TEXT, "+ remaining + " TEXT, "+ capacity + " TEXT" +")";

    public Databasehelper(Context context) {
        super(context, db_name, null, VERSION);
        this.context = context;
    }

    /**
     * Inserts or updates a section
     * @param info
     */
    public void setSection(HashMap<String,String> info){
        db = this.getWritableDatabase();

        ContentValues val = new ContentValues();
        val.put(crn,info.get(crn));
        val.put(title,info.get(title));
        val.put(name, info.get(name));
        val.put(section, info.get(section));
        val.put(available, info.get(available));
        val.put(instructor, info.get(instructor));
        val.put(time, info.get(time));
        val.put(day, info.get(day));
        val.put(remaining, info.get(remaining));
        val.put(capacity, info.get(capacity));

        // inserting
        db.insert(table_name,null,val);
        db.close();
        Log.i(TAG, "Set/Update on CRN " + crn);
    }

    /**
     * Removes a section
     * @param crn
     */
    void removeSection(String crn){
        db = this.getWritableDatabase();

        String[] del = {crn};


        db.delete(table_name, "crn" +"=?", del);
        db.close();

        Log.i(TAG, "Removed CRN " + crn);    }

    public boolean contains (String arg) {
        db = this.getReadableDatabase();

        String Query = "Select * from " + table_name + " where " + crn + " = " + arg;
        Cursor cursor = db.rawQuery(Query, null);

        boolean contains = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return contains;
    }
    public HashMap<String,String> getSectionInfo(String crn){
        db = this.getReadableDatabase();
        HashMap<String,String> map=new HashMap<>();
        String Query = "Select * from " + table_name + " where " + this.crn + " = " + crn;
        Cursor cursor = db.rawQuery(Query, null);
        cursor.moveToFirst();
        map.put("name",cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.name)));
        map.put("title", cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.title)));
        map.put("section", cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.section)));
        db.close();
       return map;
    }


    /**
     * Returns all the CRN that are currently being tracked (not available)
     * @return crn being tracked
     */
    public LinkedList<String> getAllTrackedCRN(){
        db = this.getReadableDatabase();

        Cursor cr = db.rawQuery("select * from " + table_name,null);

        LinkedList<String> list = new LinkedList<>();

        while(cr.moveToNext()){
            int index = cr.getColumnIndexOrThrow("crn");
            String crn  = cr.getString(index);

            index = cr.getColumnIndexOrThrow("available");
            String available = cr.getString(index);

            // check if the section has available seats or not
            if(available.equals("true")){
                continue;
            }

            list.add(crn);
        }

        cr.close();
        db.close();

        return list;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS registeration_table");
        onCreate(db);
    }

    void deleteDatabase ( ) {
        context.deleteDatabase(db_name);
    }

}

