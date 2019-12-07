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
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
    private static final String TAG = "MainActivity";
    public  Databasehelper dbHelper=null;
    public  SQLiteDatabase db=null;

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
        dbHelper = new Databasehelper(this);
        db=dbHelper.getWritableDatabase();// get writable database


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
//// 002, intro to java, time, day, crn, instrctor,CS475
    private void alertDialogDemo(final String [] info) {
        System.out.println(Arrays.toString(info));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(info[6]);
        builder.setMessage("are you sure you want to be notified ?");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button;
                // notify service user wants to track a section
                sectionTrackerIntent.putExtra("newCrn", info[4]);
                startService(sectionTrackerIntent);
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

    void setSection(HashMap<String,String> info){
        ContentValues val = new ContentValues();
        val.put(Databasehelper.crn,info.get(Databasehelper.crn)); val.put(Databasehelper.title,info.get(Databasehelper.title));val.put(Databasehelper.name, info.get(Databasehelper.name));
        val.put(Databasehelper.section, info.get(Databasehelper.section));val.put(Databasehelper.isFound, info.get(Databasehelper.isFound));val.put(Databasehelper.instructor, info.get(Databasehelper.instructor));
        val.put(Databasehelper.time, info.get(Databasehelper.time));val.put(Databasehelper.day, info.get(Databasehelper.day));val.put(Databasehelper.remaining, info.get(Databasehelper.remaining));
        val.put(Databasehelper.capacity, info.get(Databasehelper.capacity));
        // open the DB if not open
        if(dbHelper==null)
            dbHelper= new Databasehelper(this);
        if(db==null)
            db=dbHelper.getWritableDatabase();

        // inserting
        db.insert(table_name,null,val);
        Log.i(TAG, "setSection: ");
    }
    void  removeSection(Context context,String crn){
        if(dbHelper==null)
            dbHelper= new Databasehelper(context);
        if(db==null)
            db=dbHelper.getWritableDatabase();

        String[] del={String.valueOf(crn)};
        Log.i(TAG, "removeSection: ");
        db.delete(table_name, "crn" +"=?", del);
        Log.i(TAG, "removeSection: successfully delted");
    }

    private Cursor readDb() {

        return db.query(Databasehelper.table_name, Databasehelper.columns, null, new String[]{}, null, null, null);

    }





}