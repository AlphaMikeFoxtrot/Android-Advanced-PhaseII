package com.example.anonymous.bookstwo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by ANONYMOUS on 29-Oct-17.
 */

public class BookRecyclerViewAdapter extends RecyclerView.Adapter<BookRecyclerViewAdapter.BookViewHolder>{

    private ArrayList<Book> books;
    private Context context;

    public BookRecyclerViewAdapter(ArrayList<Book> books, Context context) {
        this.books = books;
        this.context = context;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View listItemView = inflater.inflate(R.layout.custom_card_view, parent, false);

        return new BookViewHolder(listItemView, this.context, this.books);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {

        Book currentBook = books.get(position);

        Picasso.with(context).load(currentBook.getmBookCoverImageUrl()).into(holder.bookCover);
        holder.bookTitle.setText(currentBook.getmBookTitle());
        holder.bookAuthor.setText(currentBook.getmBookAuthor());

    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView bookCover;
        TextView bookTitle, bookAuthor;
        ArrayList<Book> books = new ArrayList<Book>();
        Context context;

        public BookViewHolder(View itemView, Context context, ArrayList<Book> books) {
            super(itemView);

            this.books = books;
            this.context = context;

            itemView.setOnClickListener(this);

            bookCover = itemView.findViewById(R.id.book_cover);
            bookTitle = itemView.findViewById(R.id.book_title);
            bookAuthor = itemView.findViewById(R.id.book_author);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();

            Book book = this.books.get(position);
            Intent toBookDetails = new Intent(this.context, BookDetails.class);
            toBookDetails.putExtra("book_cover_url", book.getmBookCoverImageUrl());
            toBookDetails.putExtra("book_title", book.getmBookTitle());
            toBookDetails.putExtra("book_authors", book.getmBookAuthor());
            toBookDetails.putExtra("book_ratings", book.getmBookRatings());
            toBookDetails.putExtra("book_ratings_count", book.getmBookRatingsCount());
            toBookDetails.putExtra("book_page_count", book.getmBookPageCount());
            toBookDetails.putExtra("book_published_year", book.getmBookPublishedYear());
            toBookDetails.putExtra("amazon_link", book.getmBookAmazonLink());
            this.context.startActivity(toBookDetails);

        }
    }

}
