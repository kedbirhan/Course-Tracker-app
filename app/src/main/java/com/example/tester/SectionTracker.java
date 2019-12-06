package com.example.tester;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class SectionTracker extends Service {
    private String crn = "19881";
    private final long INTERVAL = 5000 * 60 *100; // one minute
    private final String URL = "https://patriotweb.gmu.edu/pls/prod/bwckschd.p_disp_detail_sched?term_in=202010&crn_in=";

    private String[] seats = new String[3];
    private String[] waitList = new String[3];
    private Timer timer = null;

    /**
     * Get seats
     * @return ['capacity', 'actual', 'Remaining']
     */
    public String[] getSeats(){
        return this.seats;
    }

    /**
     * Get waitList Seats
     * @return ['capacity', 'actual', 'Remaining']
     */
    public String[] getWaitList(){
        return this.waitList;
    }

    /**
     * Stop the schedule
     */
    public void stopSchedule(){
        if(this.timer == null){
            System.out.println("There is no schedule running for crn " + this.crn);
            return;
        }
        timer.cancel();
        timer.purge();
    }

    /**
     * Start the schedule
     */
    public void startSchedule(){
        if(this.timer != null){
            System.out.println("There is already a schedule running for crn " + this.crn);
            return;
        }

        System.out.println("Starting schedule for crn " + this.crn);
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getData();
            }
        }, 0, this.INTERVAL);
    }

    /**
     * Get Section data from GMU
     */
    private void getData(){
        Document doc;
        try {
            System.out.println("Getting new data for crn " + this.crn);
            doc = Jsoup.connect(this.URL + this.crn).get();
        } catch (IOException e) {
            System.out.println("Could not fetch section data for crn " + this.crn);
            e.printStackTrace();
            return;
        }

        //parse data
        Elements body = doc.select(".datadisplaytable").get(1).select("tbody");
        Elements seatsData = body.select("tr").get(1).getAllElements();
        Elements waitListData = body.select("tr").get(2).getAllElements();

        //build seats array
        for(int i = 0; i < 3; i++){
            this.seats[i] = seatsData.select("td").get(i).text();
            this.waitList[i] = waitListData.select("td").get(i).text();
        }
        System.out.println(Arrays.toString(seats));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Section tracker service started");
        startSchedule();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopSchedule();
        Intent broadcastIntent = new Intent(this , ServiceRestartBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
    }
}