package com.example.gmucoursetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Used to restart the Section Tracker Service
 */
public class ServiceRestartBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        intent = new Intent(context, SectionTracker.class);
        intent.putExtra("start", true);
        context.startService(intent);
    }
}
