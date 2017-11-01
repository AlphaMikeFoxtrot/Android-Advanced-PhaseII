package com.example.anonymous.bookstwo;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ANONYMOUS on 31-Oct-17.
 */

public class GoogleQueryUtils {

    public static String mBookDescription;

    public static String getBookDescriptionFromJson(String json, String bookTitle) throws JSONException {

        JSONObject root = new JSONObject(json);
        JSONArray items = root.getJSONArray("items");

        JSONObject firstItem = items.getJSONObject(0).getJSONObject("volumeInfo");
        String title = firstItem.getString("title");

        if(title.toLowerCase().contains(bookTitle.toLowerCase())){
            mBookDescription = firstItem.getString("description");
        } else {
            mBookDescription = "Description unavailable";
        }

        return mBookDescription;

    }

}
