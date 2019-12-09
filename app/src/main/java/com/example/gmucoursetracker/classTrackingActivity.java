package com.example.gmucoursetracker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class classTrackingActivity extends AppCompatActivity {
    Databasehelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_tracking);

        dbHelper= new Databasehelper(this);
        db= dbHelper.getReadableDatabase();

        Cursor c= db.rawQuery("SELECT  * FROM "+ Databasehelper.table_name, null);
        ListView listView= findViewById(R.id.trackingClassListView);

        customCursorAdaptor classAdaptor= new customCursorAdaptor(this, c, false);
        listView.setAdapter(classAdaptor);

    }

}
