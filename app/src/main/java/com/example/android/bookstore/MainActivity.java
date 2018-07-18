package com.example.android.bookstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.android.bookstore.Data.BookContract.BookEntry;
import com.example.android.bookstore.Data.BookDbHelper;

public class MainActivity extends AppCompatActivity {
    Cursor cursor;
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        insertBook();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        BookDbHelper bookDbHelper = new BookDbHelper(this);
        SQLiteDatabase db = bookDbHelper.getReadableDatabase();
        cursor = db.query(BookEntry.TABLE_NAME, null, null, null, null, null, null);
        TextView displayView = findViewById(R.id.text_view_book);
        try {
            displayView.setText("The books table contains " + cursor.getCount() + " book.\n\n");
            displayView.append(BookEntry._ID + " - " +
                    BookEntry.COLUMN_BOOK_PRODUCT_NAME + " - " +
                    BookEntry.COLUMN_BOOK_PRICE + " - " +
                    BookEntry.COLUMN_BOOK_QUANTITY + " - " +
                    BookEntry.COLUMN_BOOK_SUPPLIER_NAME + " - " +
                    BookEntry.COLUMN_BOOK_SUPPLIER_PHONE
                    + "\n");

            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentProductName = cursor.getString(productNameColumnIndex);
                String currentPrice = cursor.getString(priceColumnIndex);
                String currentQuantity = cursor.getString(quantityColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                String currentPhone = cursor.getString(phoneColumnIndex);

                displayView.append(("\n" + currentID + " - " +
                        currentProductName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplier + " - " +
                        currentPhone));
            }
        } finally {
            cursor.close();
        }
        Log.i(LOG_TAG, "Display the table");
    }

    private void insertBook() {
        BookDbHelper bookDbHelper = new BookDbHelper(this);
        SQLiteDatabase db = bookDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_PRODUCT_NAME, getString(R.string.COLUMN_BOOK_PRODUCT_NAME));
        values.put(BookEntry.COLUMN_BOOK_PRICE, getString(R.string.COLUMN_BOOK_PRICE));
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, getString(R.string.COLUMN_BOOK_QUANTITY));
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, getString(R.string.COLUMN_BOOK_SUPPLIER_NAME));
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, getString(R.string.COLUMN_BOOK_SUPPLIER_PHONE));
        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);

        Log.v("MainActivity", "CheckRowId" + newRowId);

        Log.i(LOG_TAG, "New Row has inserted ");
    }
}
