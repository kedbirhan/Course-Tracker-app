package com.example.tester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Used to restart the Section Tracker Service
 */
public class ServiceRestartBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Section tracker service terminated");
        context.startService(new Intent(context, SectionTracker.class));;
    }
}
