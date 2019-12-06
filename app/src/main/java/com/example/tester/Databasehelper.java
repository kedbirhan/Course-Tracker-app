package com.example.tester;



import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Databasehelper  extends SQLiteOpenHelper {
//    crate a table named todo, which as ID and todo_col columns
    static String _ID="_id";
    static String crn="crn";
    static String className="className";
    static String sec="section";
    static  String jobNum="jobNum";
    static  String instrctor="instructor";
    static String time= "time";
    static String day="day";
    static String table_name="reg_table";
// table colums are
        //_id, crn, className, section, jonNum, instructor, time,day
    final private static String CREATE_CMD =
            "CREATE TABLE reg_table (" + _ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " + crn + " TEXT NOT NULL, " + className
                    + " TEXT, " + sec + " TEXT, " + jobNum + " INTEGER, " + instrctor + " TEXT, "
                    + time + " TEXT, " + day + " TEXT)";

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

}

