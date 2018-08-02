package com.example.android.bookstore;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookstore.Data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {
    private Context context;

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.summary);

        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);

        String productName = cursor.getString(nameColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);
        String productprice = cursor.getString(priceColumnIndex);

        final long id = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry._ID));
        final int qty = Integer.parseInt(quantity);

        nameTextView.setText(productName);
        quantityTextView.setText(quantity);
        priceTextView.setText(productprice);

        view.findViewById(R.id.sale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.decreaseCount(id, qty);
            }
        });
    }
}
