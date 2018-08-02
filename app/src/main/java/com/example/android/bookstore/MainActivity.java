package com.example.android.bookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bookstore.Data.BookContract.BookEntry;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    BookCursorAdapter mCursorAdapter;
    private static final int Book_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addNewBook = (Button) findViewById(R.id.add_book);
        addNewBook.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        ListView BookListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        BookListView.setEmptyView(emptyView);

        mCursorAdapter = new BookCursorAdapter(this, null);
        BookListView.setAdapter(mCursorAdapter);

        BookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(Book_LOADER, null, this);
    }

    private void insertBook() {

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_PRODUCT_NAME, getString(R.string.COLUMN_BOOK_PRODUCT_NAME));
        values.put(BookEntry.COLUMN_BOOK_PRICE, getString(R.string.COLUMN_BOOK_PRICE));
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, getString(R.string.COLUMN_BOOK_QUANTITY));
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, getString(R.string.COLUMN_BOOK_SUPPLIER_NAME));
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, getString(R.string.COLUMN_BOOK_SUPPLIER_PHONE));

        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

        Log.v("MainActivity", "CheckRowId" + newUri);
        Log.i(LOG_TAG, "New Row has inserted ");
    }

    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {BookEntry._ID,
                BookEntry.COLUMN_BOOK_PRODUCT_NAME,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_PRICE};

        return new CursorLoader(this,   // Parent activity context
                BookEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    public void decreaseCount(long columnId, int quantity) {

        if (quantity >= 1) {
            quantity--;

            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);

            Uri updateUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, columnId);

            int rowsAffected = getContentResolver().update(updateUri, values, null, null);
            if (rowsAffected == 1) {
                Toast.makeText(this, R.string.valid_sale, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.invalid_sale, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.out_of_stock, Toast.LENGTH_LONG).show();
        }
    }
}
