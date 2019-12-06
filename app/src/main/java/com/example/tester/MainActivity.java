package com.example.tester;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;

public class MainActivity extends Activity {
    static String  table_name="reg_table";
    static String db_name="reg_db";
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild; // class name, sec follewed by its desciption

    private Intent sectionTrackerIntent;

    /**
     * Checks whether the section tracker is running
     */
    private boolean isSectionTrackerRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SectionTracker.class.equals(service.service.getClassName())) {
                System.out.println("Service Tracker is already running");
                return true;
            }
        }
        System.out.println("Service Tracker is starting...");
        return false;
    }


    /**
     * When the app is killed, the service must be killed so the BroadcastReceiver restarts it
     * and it doesn't die with the app.
     */
    @Override
    protected void onDestroy() {
        stopService(sectionTrackerIntent);
        super.onDestroy();
    }

    /**
     * Start the Section Tracker Service
     */
    public void startSectionTrackerService(){
        // Start Service Tracker service
        sectionTrackerIntent = new Intent(this, SectionTracker.class);
        if(!isSectionTrackerRunning()){
            startService(sectionTrackerIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startSectionTrackerService();

        prepareListData();

        // get the listview
        expListView = findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview on child click listener


        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });


        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String [] info=listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition).split("##");
                String crn= info[4];
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                +crn , Toast.LENGTH_SHORT)
                        .show();
                            alertDialogDemo(info);

                return false;

            }
        });

        }

    private void alertDialogDemo(String [] info) {
        System.out.println(Arrays.toString(info));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(info[6]);
        builder.setMessage("are you sure you want to be notified ?");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.show();
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader= new ArrayList<>();
        listDataChild = new HashMap<>();
        try {
            JSONObject obj = new JSONObject(loadData()); // get the whole json file
            Iterator<String> classes= obj.keys(); // gets the class names
            Iterator<String>  sec;
           while(classes.hasNext()){
               String class_name= classes.next(); // class name CS112
               listDataHeader.add(class_name); // add the name of the class
               JSONObject curr_class=obj.getJSONObject(class_name); // current class
               Iterator<String> sections=curr_class.keys(); // sections name
               ArrayList<String> secInfo= new ArrayList<>(); // all the section info  will go to this list
               while (sections.hasNext()){
                   String info="";
                   String delimeter="##";
                   String sec_num= sections.next(); // section number
                   JSONObject _sec=curr_class.getJSONObject(sec_num);
                   info+=sec_num+delimeter+_sec.get("name") + delimeter+ _sec.get("time")+ delimeter+ _sec.get("days")
                           + delimeter+ _sec.get("crn") + delimeter+ _sec.get("instructor")+ delimeter+class_name;
                  secInfo.add(info);
               }
               listDataChild.put(class_name, secInfo);
           }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
   public String loadData(){
       String json= null;
       try{
           InputStream is= MainActivity.this.getAssets().open("data.json");
           int size= is.available();
           byte[] buffer= new byte[size];
           is.read(buffer);
           is.close();
           json = new String(buffer,"UTF-8");
       }catch (IOException e) {
           e.printStackTrace();
       }
       return json;
   }

}