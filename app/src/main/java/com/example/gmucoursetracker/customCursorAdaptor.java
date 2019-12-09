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
        TextView isSeatAvailable= view.findViewById(R.id.isSeatAvailable);

        //get value from cursor for each view
        String title1=cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.title));
        String sec1=cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.section));
        String crn1=cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.crn));
        String time1=cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.time));
        String day1 =cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.day));
        String isSeatAvailable1=cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.available));

        // update the views selected
        title.setText(title1);
        sec.setText(sec1);
        ins.setText(sec1);
        crn.setText(crn1);
        day.setText(day1);
        time.setText(time1);
        isSeatAvailable.setText(isSeatAvailable1);








    }
}
