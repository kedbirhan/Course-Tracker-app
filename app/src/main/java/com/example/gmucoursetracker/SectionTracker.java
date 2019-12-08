package com.example.gmucoursetracker;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
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
    private final long INTERVAL = 5000 * 60 *100; // one minute

    private HashMap<String, Timer> timers;
    private Databasehelper databasehelper;

    /**
     * Stop the schedule
     */
    public void stopSchedule(){
    }

    /**
     * Start the schedule
     */
    public void startSchedule(final String crn){
        System.out.println("Starting schedule for crn " + crn);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               // getData(crn);
            }
        }, 0, this.INTERVAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // service is starting, restart all section trackers
        if(intent.getBooleanExtra("start", false)){
            System.out.println("Starting Section Service Tracker");
            databasehelper = new Databasehelper(this);
            timers = new HashMap<>();

            restartTimers();

            return START_STICKY;
        }

        // add section to track
        String[] info = intent.getStringArrayExtra("sectionInfo");
        if(info == null) {
            System.out.println("Error: service needs to be passed info about the section.");
            return START_STICKY;
        }

        startTrackingSection(info);

        return START_STICKY;
    }

    public void restartTimers(){
        LinkedList<String> list = databasehelper.getAllTrackedCRN();

        Iterator<String> it = list.iterator();
        while(it.hasNext()){
            createTimer(it.next());
        }
    }


    private void startTrackingSection(final String[] info){
        String[][] results = new String[0][];

        // get section data from gmu
        try {
            results = new GetSectionDataAsync().execute(info[4]).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(Arrays.toString(results[0]));

        if(results == null){
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
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    String[][] results = new GetSectionDataAsync().execute(crn).get();
                    System.out.println(Arrays.toString(results[0]));

                    // TODO: 12/8/2019 send notification when course has available seat 
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 2 * 60 * 1000);
        timers.put(crn, timer);
    }

    /**
     * Sto
     * @param crn
     */
    private void stopTrackingSection(String crn){
        Timer timer = timers.remove(crn);
        if(timer != null){
            timer.cancel();
            timer.purge();
        }
        databasehelper.removeSection(crn);
    }

    @Override
    public void onDestroy() {
        //stopSchedule();
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
                System.out.println("Getting new data for crn " + crn);
                doc = Jsoup.connect(this.URL + crn).get();
            } catch (IOException e) {
                System.out.println("Could not fetch section data for crn " + crn);
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