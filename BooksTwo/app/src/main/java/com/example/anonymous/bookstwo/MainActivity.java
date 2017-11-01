package com.example.anonymous.bookstwo;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Xml;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookRecyclerViewAdapter adapter;

    private ProgressBar progressBar;

    public ArrayList<Book> books = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.book_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);

        progressBar = findViewById(R.id.main_activity_progress_bar);

        Intent intent = getIntent();

        BookAsyncTask bookAsyncTask = new BookAsyncTask();
        bookAsyncTask.execute(intent.getStringExtra("url"));

    }

    @Override
    protected void onDestroy() {
        HomeScreen.searchQuery.setText("");
        books.clear();
        super.onDestroy();
    }

    private class BookAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            // The following two need to be declared outside try and catch
            // block, so that they can be used in the "finally" statement
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Contains the raw xml response :
            String raw_xml_response = null;

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

                raw_xml_response = stringBuffer.toString();
                return raw_xml_response;

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
                books = QueryUtils.readFromXml(new StringReader(s));
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            progressBar.setVisibility(View.GONE);
            BookRecyclerViewAdapter rv_adapter = new BookRecyclerViewAdapter(books, getApplicationContext());
            recyclerView.setAdapter(rv_adapter);
        }
    }
}
