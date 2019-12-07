package com.example.gmucoursetracker;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Databasehelper helper = new Databasehelper(appContext);


        HashMap<String, String> sectionInfo = new HashMap<>();

        sectionInfo.put("section", "001");
        sectionInfo.put("title", "Introduction to programming");
        sectionInfo.put("time", "2:30");
        sectionInfo.put("day", "MW");
        sectionInfo.put("crn", "12345");
        sectionInfo.put("instructor", "Bitch Maddox");
        sectionInfo.put("name", "CS 306");
        sectionInfo.put("isFound", "false");
        sectionInfo.put("capacity", "0");
        sectionInfo.put("remaining", "1");



        sectionInfo.put("section", "001");
        sectionInfo.put("title", "Introduction to programming");
        sectionInfo.put("time", "2:30");
        sectionInfo.put("day", "MW");
        sectionInfo.put("crn", "12325");
        sectionInfo.put("instructor", "Bitch Maddox");
        sectionInfo.put("name", "CS 306");
        sectionInfo.put("isFound", "false");
        sectionInfo.put("capacity", "0");
        sectionInfo.put("remaining", "1");


        helper.setSection(sectionInfo);
        helper.setSection(sectionInfo);
        System.out.println(Arrays.toString(helper.getAllCRN()));


    }
}
