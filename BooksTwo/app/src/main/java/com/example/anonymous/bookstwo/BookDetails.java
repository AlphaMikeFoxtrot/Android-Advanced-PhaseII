package com.example.anonymous.bookstwo;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Locale;

public class BookDetails extends AppCompatActivity {

    private ImageView mBookCover, mAmazonLogo;
    private TextView mBookTitle, mBookAuthor, mBookRating, mBookRatingsCount, mBookPagesCount, mBookPublishedYear, mBookDescription;
    private RatingBar mBookRatingBar;
    private LinearLayout mLinearLayout;
    private Button mBookAmazon;
    private ProgressBar mProgressBar;
    private String bookTitle, bookAuthor, bookRatings, bookImageUrl, bookPageLength, bookRatingCount, bookPublishedYear, bookAmazonLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        mBookAmazon = findViewById(R.id.book_detail_amazon_link);
        mBookCover = findViewById(R.id.book_detail_cover);
        mBookTitle = findViewById(R.id.book_detail_title);
        mBookAuthor = findViewById(R.id.book_detail_author);
        mBookRating = findViewById(R.id.book_detail_rating_text);
        mBookRatingBar = findViewById(R.id.book_detail_rating_bar);
        mBookRatingsCount = findViewById(R.id.book_detail_rating_count);
        mBookPublishedYear = findViewById(R.id.book_detail_publish_date);
        mBookPagesCount = findViewById(R.id.book_detail_book_pages);
        mBookDescription = findViewById(R.id.book_detail_description);
        mLinearLayout = findViewById(R.id.linear_layout);
        mProgressBar = findViewById(R.id.book_detail_activity_progress_bar);
        mAmazonLogo = findViewById(R.id.amazon_logo);

        Intent intent = getIntent();

        bookTitle = intent.getStringExtra("book_title");
        bookAuthor = intent.getStringExtra("book_authors");
        bookRatings = intent.getStringExtra("book_ratings");
        bookImageUrl = intent.getStringExtra("book_cover_url");
        bookPageLength = intent.getStringExtra("book_page_count");
        bookRatingCount = intent.getStringExtra("book_ratings_count");
        bookPublishedYear = intent.getStringExtra("book_published_year");
        bookAmazonLink = intent.getStringExtra("amazon_link");

        Picasso.with(getApplicationContext()).load("http://www.golfsale.net/wp-content/uploads/2016/03/a_cart_icon.png").into(mAmazonLogo);

        DecimalFormat formatter = new DecimalFormat("#,###,###");
        if(bookRatingCount != null) {
            String formattedRatingCount = formatter.format(Integer.parseInt(bookRatingCount));
            mBookRatingsCount.setText("(" + formattedRatingCount + " ratings)");
        } else {
            mBookRatingsCount.setText(0 + " ratings");
        }

        DecimalFormat ratingFormatter = new DecimalFormat("#.#");
        if(bookRatings != null) {
            String formattedRating = ratingFormatter.format(Float.parseFloat(bookRatings));
            mBookRatingBar.setRating(Float.parseFloat(formattedRating));
            mBookRating.setText(bookRatings + "/5");
        } else {
            mBookRatingBar.setRating(0);
            mBookRating.setText("0/5");
        }

        if(bookPageLength != null && bookPublishedYear != null){
            mBookPagesCount.setText(bookPageLength + " pages");
            mBookPublishedYear.setText("Publication year :" + bookPublishedYear);
        } else {
            mBookPagesCount.setText("Page Length : Unknown");
            mBookPublishedYear.setText("Publication year : Unknown");
        }

        Picasso.with(getApplicationContext()).load(bookImageUrl).into(mBookCover);
        mBookTitle.setText(bookTitle);
        mBookAuthor.setText("By " + bookAuthor);

        // mBookDescription.setText(bookTitle);

        String url;
        if(bookTitle.contains(", ")){

            String urlBookTitle = bookTitle.replaceAll("\\(.*\\)", "").replace(", ", "+");
            GoogleBookAsyncTask googleBookAsyncTask = new GoogleBookAsyncTask();
            googleBookAsyncTask.execute("https://www.googleapis.com/books/v1/volumes?q=intitle:" + urlBookTitle.toString().toLowerCase());

        } else if(bookTitle.contains(",")){

            String urlBookTitle = bookTitle.replaceAll("\\(.*\\)", "").replace(",", "+");
            GoogleBookAsyncTask googleBookAsyncTask = new GoogleBookAsyncTask();
            googleBookAsyncTask.execute("https://www.googleapis.com/books/v1/volumes?q=intitle:" + urlBookTitle.toString().toLowerCase());

        } else if(bookTitle.contains(" ")){

            String urlBookTitle = bookTitle.replaceAll("\\(.*\\)", "").replace(" ", "+");
            GoogleBookAsyncTask googleBookAsyncTask = new GoogleBookAsyncTask();
            googleBookAsyncTask.execute("https://www.googleapis.com/books/v1/volumes?q=intitle:" + urlBookTitle.toString().toLowerCase());

        } else if(bookTitle.contains("\n")){

            String urlBookTitle = bookTitle.replaceAll("\\(.*\\)", "").replace("\n", "+");
            GoogleBookAsyncTask googleBookAsyncTask = new GoogleBookAsyncTask();
            googleBookAsyncTask.execute("https://www.googleapis.com/books/v1/volumes?q=intitle:" + urlBookTitle.toString().toLowerCase());

        }

        Toast.makeText(this, "" + bookTitle, Toast.LENGTH_SHORT).show();

        mBookAmazon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bookAmazonLink != null){

                    Intent toBrowser = new Intent(Intent.ACTION_VIEW);
                    toBrowser.setData(Uri.parse(bookAmazonLink));
                    startActivity(toBrowser);

                } else {

                    String url = "http://www.google.com/#q=" + bookTitle + " by " + bookAuthor + " amazon india";
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);

                }
            }
        });

    }

    private class GoogleBookAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {

                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();

                if(inputStream == null){

                    // if the returned inputstream is empty
                    // noting to do
                    return null;

                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null){

                    stringBuffer.append(line + "\n");

                }

                if(stringBuffer.length() == 0){

                    return null;

                }
                String bookJson = stringBuffer.toString();
                return bookJson;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if(reader != null){
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
            super.onPostExecute(s);
            mProgressBar.setVisibility(View.GONE);
            try {
                String bookDescription = GoogleQueryUtils.getBookDescriptionFromJson(s, bookTitle);
                mBookDescription.setText(bookDescription);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(BookDetails.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                mBookDescription.setText("description not available".toUpperCase());
            }
        }

    }
}
