package com.example.gmucoursetracker;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class customCursorAdaptor extends CursorAdapter {


    public customCursorAdaptor(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.class_tracking_row, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
       // get views
        TextView title= view.findViewById(R.id.titles);
        TextView sec= view.findViewById(R.id.sec);
        TextView ins= view.findViewById(R.id.instructor);
        TextView crn= view.findViewById(R.id.crn);
        TextView day= view.findViewById(R.id.days);
        TextView time= view.findViewById(R.id.times);
        //TextView isSeatAvailable= view.findViewById(R.id.isSeatAvailable);

        //get value from cursor for each view hh
        String title1=cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.name));
        String sec1=cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.section));
        String crn1=cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.crn));
        String time1=cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.time));
        String day1 =cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.day));
        String ins1 =cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.instructor));
        //String isSeatAvailable1=cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.available));

        // update the views selected
        title.setText(title1);
        sec.setText("Section: "+sec1);
        ins.setText("Instructor: "+ ins1);
        crn.setText("CRN: "+ crn1);
        day.setText("Days: " + day1);
        time.setText("Time: " + time1);
    }
}
