package com.example.anonymous.bookstwo;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

/**
 * Created by ANONYMOUS on 29-Oct-17.
 */

public class QueryUtils {

    public static Book book;
    public static ArrayList<Book> books = new ArrayList<>();

    public static ArrayList<Book> readFromXml(StringReader stringReader) throws XmlPullParserException, IOException {

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();

        parser.setInput(stringReader);

        int event = parser.getEventType();

        String currentTag = "", currentTagValue = "";

        while (event != XmlPullParser.END_DOCUMENT){

            currentTag = parser.getName();

            switch (event){

                case XmlPullParser.START_TAG:
                    if(currentTag.equalsIgnoreCase("work")){
                        book = new Book();
                        books.add(book);
                    }
                    break;

                case XmlPullParser.TEXT:
                    currentTagValue = parser.getText();
                    break;

                case XmlPullParser.END_TAG:

                    switch (currentTag){

                        case "title":   // book title
                            book.setmBookTitle(currentTagValue);
                            break;
                        case "name":    // book author
                            book.setmBookAuthor(currentTagValue);
                            break;
                        case "image_url":   // book image url
                            book.setmBookCoverImageUrl(currentTagValue);
                            break;
                        case "average_rating":  // book ratings out of 5
                            book.setmBookRatings(currentTagValue);
                            break;
                        case "books_count":     //book page length
                            book.setmBookPageCount(currentTagValue);
                            break;
                        case "ratings_count":   // total number of ratings
                            book.setmBookRatingsCount(currentTagValue);
                            break;
                        case "original_publication_year":
                            book.setmBookPublishedYear(currentTagValue);
                            break;

                    }

                    break;

            }

            event = parser.next();

        }

        return books;
    }

}
