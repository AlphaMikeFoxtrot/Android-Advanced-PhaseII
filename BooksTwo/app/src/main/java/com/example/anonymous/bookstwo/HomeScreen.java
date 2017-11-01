package com.example.anonymous.bookstwo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {

    public static EditText searchQuery;
    ImageButton searchButton;

    public ArrayList<Book> books = new ArrayList<Book>();

    RecyclerView bestSellersList;

    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        searchQuery = findViewById(R.id.search_query);
        searchButton = findViewById(R.id.search_button);

        bar = findViewById(R.id.home_screen_activity_progress_bar);

        bestSellersList = (RecyclerView) findViewById(R.id.ny_times_list);
        bestSellersList.setHasFixedSize(true);
        bestSellersList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        NyTimesAsyncTask nyTimesAsyncTask = new NyTimesAsyncTask();
        nyTimesAsyncTask.execute("https://api.nytimes.com/svc/books/v3/lists.json?list=hardcover-fiction&api-key=0e0bdc62a8a34068a04c91fc38eda500");

        final String BASE_URL = "http://www.goodreads.com/search/index.xml?key=PD86Pp9gK5x9cTiskvdRQ&q=";

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMainAct = new Intent(HomeScreen.this, MainActivity.class);
                String url = BASE_URL + searchQuery.getText().toString();
                toMainAct.putExtra("url", url);
                startActivity(toMainAct);
            }
        });

    }

    public class NyTimesAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            // The following two need to be declared outside try and catch
            // block, so that they can be used in the "finally" statement
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Contains the raw xml response :
            String nyTimesJsonResponse = null;

            try {

                // construct url using 'strings' arg
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // reading input stream
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.delete(0, stringBuffer.length());

                if(inputStream == null){

                    // since there was no possible response from the server
                    // nothing needs to be done
                    return null;

                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine()) != null){

                    stringBuffer.append(line);

                }

                if (stringBuffer.length() == 0){

                    // return stream was empty
                    // no point in parsing
                    return null;

                }

                nyTimesJsonResponse = stringBuffer.toString();
                return nyTimesJsonResponse;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {

                if(urlConnection != null){
                    urlConnection.disconnect();
                } if(reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            // add code to set book array list-----
            try {
                books = NewYorkTimesQueryUtils.readFromNyTimesJson(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            bar.setVisibility(View.GONE);
            BookRecyclerViewAdapter rv_adapter = new BookRecyclerViewAdapter(books, getApplicationContext());
            bestSellersList.setAdapter(rv_adapter);
        }
    }
}
