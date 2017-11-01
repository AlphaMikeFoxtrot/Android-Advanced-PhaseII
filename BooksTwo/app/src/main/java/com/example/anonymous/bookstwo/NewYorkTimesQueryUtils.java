package com.example.anonymous.bookstwo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by ANONYMOUS on 31-Oct-17.
 */

public class NewYorkTimesQueryUtils {

    public static ArrayList<Book> readFromNyTimesJson(String json) throws JSONException {

        ArrayList<Book> books = new ArrayList<>();
        // Book book = new Book();
        String bookTitle, bookAuthor, bookDescription, bookAmazonLink;

        JSONObject root = new JSONObject(json);
        JSONArray results = root.getJSONArray("results");

        for(int i = 0; i < 15; i++){

            JSONObject nthObject = results.getJSONObject(i);
            JSONArray bookDetails = nthObject.getJSONArray("book_details");
            JSONObject bookDetail = bookDetails.getJSONObject(0);

            bookAmazonLink = nthObject.getString("amazon_product_url");
            bookTitle = bookDetail.getString("title");
            bookAuthor = bookDetail.getString("author");
            bookDescription = bookDetail.getString("description");

            Book book = new Book();

            book.setmBookTitle(bookTitle);
            book.setmBookAuthor(bookAuthor);
            book.setmBookDescription(bookDescription);
            book.setmBookCoverImageUrl("https://s.gr-assets.com/assets/nophoto/book/111x148-bcc042a9c91a29c1d680899eff700a03.png");
            book.setmBookAmazonLink(bookAmazonLink);

            books.add(book);

        }


        return books;

    }

}
