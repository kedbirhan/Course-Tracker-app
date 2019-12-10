package com.example.gmucoursetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Used to restart the Section Tracker Service
 */
public class ServiceRestartBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "+++++++++ BROADCAST RECEIVER ++++++";


    @Override
    public void onReceive(Context context, Intent intent) {
        intent = new Intent(context, SectionTracker.class);
        intent.putExtra("start", true);
        Log.e(TAG, "Restarting Section Tracker Service hh");
        context.startService(intent);
    }
}
