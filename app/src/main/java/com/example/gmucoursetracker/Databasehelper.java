package com.example.gmucoursetracker;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class Databasehelper  extends SQLiteOpenHelper {
    private static final String TAG = "Databasehelper++++++++++++++++++++++++++++++++++++++";

//    crate a table named todo, which as ID and todo_col columns
public SQLiteDatabase db=null;
private Databasehelper dbHelper =null;
    static String table_name="reg_table";
    static String _id="_id";
    static String crn="crn";
    static String title="title"; // inntroduction to cs
    static String name="name"; // cs100
    static String section="section";
    static  String isFound="setFound";
    static  String instructor="instructor";
    static String time= "time";
    static String day="day";
    static String remaining="remaining";
    static String capacity="capacity";
    static String[] columns = new String[]{_id,crn,title,name,section,isFound,instructor,time,day,remaining,capacity};
// table colums are
        //_id, crn, className, section, jonNum, instructor, time,day
     String CREATE_CMD =
            "CREATE TABLE reg_table (" + _id +
                    " INTEGER PRIMARY KEY, " + crn + " TEXT NOT NULL, " + name
                    + " TEXT, " + title + " TEXT, "+ section + " TEXT, " + isFound + " TEXT, " + instructor + " TEXT, "
                    + time + " TEXT, " + day + " TEXT, "+ remaining + " TEXT, "+ capacity + " TEXT"
                    +")";

    final private static String db_name = "reg_db";
    final private static Integer VERSION = 1;
    final private Context context;

    public Databasehelper(Context context) {
        super(context, db_name, null, VERSION);
        this.context = context;
    }
    //Called when the database is created for the first time. This is where the creation of tables
    // and the initial population of the tables should happen.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD); // this method is only create the database when the database is created the first time
    } // this runs on when the database is created

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS registeration_table");
        onCreate(db);
    }

    void deleteDatabase ( ) {
        context.deleteDatabase(db_name);
    }

    public String[] getAllCRN(){

        dbHelper= new Databasehelper(context);
        db=dbHelper.getWritableDatabase();
        Cursor cr= db.query(table_name, new String[]{"crn"}, null, new String[]{}, null, null, null);
        int rowSize= cr.getCount();
        String [] crn= new String[rowSize];
        int i=0;
        while(cr.moveToNext()){
            int index;
            index=cr.getColumnIndexOrThrow("crn");
            crn[i++]=cr.getString(index);
        }
        cr.close();
        dbHelper.close();
    return crn;


    }
    void setSection(HashMap<String,String> info){

        ContentValues val = new ContentValues();
        val.put(crn,info.get(crn)); val.put(title,info.get(title));val.put(name, info.get(name));
        val.put(section, info.get(section));val.put(isFound, info.get(isFound));val.put(instructor, info.get(instructor));
        val.put(time, info.get(time));val.put(day, info.get(day));val.put(remaining, info.get(remaining));
        val.put(capacity, info.get(capacity));
                                // open the DB if not open
        if(dbHelper==null)
            dbHelper= new Databasehelper(context);
        if(db==null)
            db=dbHelper.getWritableDatabase();

                                    // inserting
        db.insert(table_name,null,val);
        Log.i(TAG, "setSection: ");
        
    }

   void  removeSection(Context context,String crn){
        if(dbHelper==null)
        dbHelper= new Databasehelper(context);
        if(db==null)
        db=dbHelper.getWritableDatabase();

       String[] del={String.valueOf(crn)};
       Log.i(TAG, "removeSection: ");
       db.delete(table_name, "crn" +"=?", del);
       Log.i(TAG, "removeSection: successfully delted");


   }

    public  boolean contains (String arg) {
        if(dbHelper==null)
            dbHelper= new Databasehelper(context);
        if(db==null)
            db=dbHelper.getWritableDatabase();
        String Query = "Select * from " + table_name + " where " + crn + " = " + arg;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }






}

