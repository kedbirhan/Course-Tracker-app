package com.example.gmucoursetracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.Arrays;

public class classTrackingActivity extends AppCompatActivity {
    Databasehelper dbHelper;
    SQLiteDatabase db;
    Cursor c;
    customCursorAdaptor classAdaptor=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_tracking);

        dbHelper= new Databasehelper(this);
        db= dbHelper.getReadableDatabase();

         c= db.rawQuery("SELECT  * FROM "+ Databasehelper.table_name, null);
        TextView view= findViewById(R.id.textView5);
        if(c.getCount()==0){
            view.setText("there are No classes currently tracking");
        }else{
            view.setText("Click on items to stop notifications");

        }
        ListView listView= findViewById(R.id.trackingClassListView);

         classAdaptor= new customCursorAdaptor(this, c, false);
        listView.setAdapter(classAdaptor);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // show dialog if user wants to stop notifications.hh
                TextView crn= view.findViewById(R.id.crn);
                String crnval=crn.getText().toString().substring(5);

                TextView title = view.findViewById(R.id.titles);
                String titleval = title.getText().toString();

                alertDialogDemo(titleval, crnval);

            }
        });

    }

    //// 002, intro to java, time, day, crn, instructor,CS475
    private void alertDialogDemo(String title, final String crn) {
        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stop notifications");
        builder.setMessage("Are you sure you want to stop notifications for " + title);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               Intent intent = new Intent(context, SectionTracker.class);
               intent.putExtra("stopSectionTracker", crn);
               startService(intent);
               dbHelper.removeSection(crn);

               SQLiteDatabase dbTemp = dbHelper.getReadableDatabase();
               Cursor cr = dbTemp.rawQuery("SELECT  * FROM "+ Databasehelper.table_name, null);
               classAdaptor.changeCursor(cr);
               classAdaptor.notifyDataSetChanged();
               dbTemp.close();
            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               // user clicked cancel
            }
        });
        builder.show();
    }

}
