package com.example.tester;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
    String debug=" +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild; // class name, sec follewed by its desciption


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepareListData();
        System.out.println("jfjf");

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

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
                String crn= listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition).split("##")[4];
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                +crn , Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

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
               String class_name= classes.next();
               listDataHeader.add(class_name); // add the name of the class
               JSONObject curr_class=obj.getJSONObject(class_name); // current class
               Iterator<String> sections=curr_class.keys(); // sections name
               System.out.println(class_name + debug);
               ArrayList<String> secInfo= new ArrayList<>(); // all the section info  will go to this list
               while (sections.hasNext()){
                   String info="";
                   String delimeter="##";
                   String sec_num= sections.next(); // section number
                   JSONObject _sec=curr_class.getJSONObject(sec_num);
                   System.out.println(debug);
                   info+=sec_num+delimeter+_sec.get("name") + delimeter+ _sec.get("time")+ delimeter+ _sec.get("days")
                           + delimeter+ _sec.get("crn") + delimeter+ _sec.get("instructor");
                  secInfo.add(info);
                   System.out.println(info + debug);
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
           System.out.println("error reading file" + debug);
       }
       return json;


   }

}