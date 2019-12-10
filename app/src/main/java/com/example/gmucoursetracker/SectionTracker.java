package com.example.gmucoursetracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class SectionTracker extends Service {
    private static final String TAG = "+++++++++ SECTION TRACKER +++++++++";

    private HashMap<String, Timer> timers;
    private Databasehelper databasehelper;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // service is starting, restart all section trackersjkdjndsjn
        if(intent.getBooleanExtra("start", false)){
            Log.i(TAG, "Started");
            databasehelper = new Databasehelper(this);
            timers = new HashMap<>();

            restartTimers();

            return START_STICKY;
        }

        String crn = intent.getStringExtra("stopSectionTracker");
        if(crn != null){
            Log.i(TAG, "Stopped tracking crn " + crn);
            stopTrackingSection(crn);
            return START_STICKY;
        }

        // add section to track
        String[] info = intent.getStringArrayExtra("sectionInfo");
        if(info == null) {
            Log.e(TAG, "Section tracker needs to be passed a crn");
            return START_STICKY;
        }

        startTrackingSection(info);

        return START_STICKY;
    }

    public void restartTimers(){
        Log.i(TAG, "Restarting trackers");
        LinkedList<String> list = databasehelper.getAllTrackedCRN();

        Iterator<String> it = list.iterator();
        if(!it.hasNext()){
            Log.i(TAG, "There are no tracked sections");
            return;
        }
        while(it.hasNext()){
            createTimer(it.next());
        }
    }


    private void startTrackingSection(final String[] info){
        String[][] results = null;

        // get section data from gmu
        try {
            results = new GetSectionDataAsync().execute(info[4]).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(results == null){
            results = new String[2][3];
            // just pretend the class is not empty, for now.
            results[0][0] = "1";
            results[0][2] = "0";
        }
        HashMap<String, String> sectionInfo = new HashMap<>();
        sectionInfo.put("section", info[0]);
        sectionInfo.put("title", info[1]);
        sectionInfo.put("time", info[2]);
        sectionInfo.put("day", info[3]);
        sectionInfo.put("crn", info[4]);
        sectionInfo.put("instructor", info[5]);
        sectionInfo.put("name", info[6]);
        sectionInfo.put("available", "false");
        sectionInfo.put("capacity", results[0][0]);
        sectionInfo.put("remaining", results[0][2]);

        // create timer
        createTimer(info[4]);

        //save to database
        databasehelper.setSection(sectionInfo);
        //start timer
    }

    private void createTimer(final String crn){
        final Context context = this;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    String[][] results = new GetSectionDataAsync().execute(crn).get();
                    if(results == null){
                        Log.e(TAG, "Fetched failed for crn " + crn);
                        return;
                    }

                    // notify user if there is a spot
                    if(!results[0][2].equals("0")){
                        HashMap<String, String> info = databasehelper.getSectionInfo(crn);
                        String title = info.get("title");
                        String name = info.get("name");
                        String section = info.get("section");

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "section-tracker")
                                .setSmallIcon(R.drawable.ic_priority_high_black_24dp)
                                .setContentTitle("Course section has an open seat")
                                .setContentText(name + ", " + ", Section:" + section + ", CRN: " + crn)
                                .setPriority(NotificationCompat.PRIORITY_MAX);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                        notificationManager.notify(Integer.parseInt(crn), builder.build());

                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            r.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        stopTrackingSection(crn);
                        databasehelper.removeSection(crn);
                    }

                    Log.i(TAG, "Fetched crn " + crn);
                    // TODO: 12/8/2019 send notification when course has available seat

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5000);

        timers.put(crn, timer);
        Log.i(TAG, "Started tracking crn " + crn);
    }

    /**
     * Stops tracking for a course
     * @param crn
     */
    private void stopTrackingSection(String crn){
        Timer timer = timers.remove(crn);
        if(timer != null){
            timer.cancel();
            timer.purge();
        }else{
            Log.i(TAG, "Timer not found for crn "+ crn);
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "Destroyed");
        Intent broadcastIntent = new Intent(this , ServiceRestartBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
    }


    /**
     * Gets section data from GMU
     * Returns 2D array first row contains seats info, second row contains wait list seat info
     */
    private class GetSectionDataAsync extends AsyncTask<String, Void, String[][]> {
        private final String URL = "https://patriotweb.gmu.edu/pls/prod/bwckschd.p_disp_detail_sched?term_in=202010&crn_in=";
        @Override
        protected String[][] doInBackground(String... param) {

            String crn = param[0];

            String[][] results = new String[2][3];

            Document doc;
            try {
                doc = Jsoup.connect(this.URL + crn).get();
            } catch (IOException e) {
                Log.e(TAG, "Could not fetch section data for crn " + crn);
                e.printStackTrace();
                return null;
            }

            //parse data
            Elements body = doc.select(".datadisplaytable").get(1).select("tbody");
            Elements seatsData = body.select("tr").get(1).getAllElements();
            Elements waitListData = body.select("tr").get(2).getAllElements();

            //build seats array
            for(int i = 0; i < 3; i++){
                results[0][i] = seatsData.select("td").get(i).text();
                results[1][i] = waitListData.select("td").get(i).text();
            }
            return results;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}