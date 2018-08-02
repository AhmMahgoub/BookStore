package com.example.android.bookstore.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.bookstore.R;

import static com.example.android.bookstore.Data.BookContract.BookEntry;

/**
 * Created by Ahm on 7/28/2018.
 */

public class BookProvider extends ContentProvider {

    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    private static final int Books = 100;

    private static final int Book_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_Books, Books);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_Books + "/#", Book_ID);
    }

    private BookDbHelper bookDbHelper;

    @Override
    public boolean onCreate() {
        bookDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = bookDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case Books:
                cursor = database.query(BookEntry.TABLE_NAME, projection, null, null,
                        null, null, null);

                break;

            case Book_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case Books:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {

        String productName = values.getAsString(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
        if (TextUtils.isEmpty(productName)) {
            throw new IllegalArgumentException(getContext().getResources().getString(R.string.err_invalid_product_name));
        }
        Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
        if (quantity < 0) {
            throw new IllegalArgumentException(getContext().getResources().getString(R.string.err_invalid_quantity));
        }
        String phoneNumber = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);
        if (TextUtils.isEmpty(phoneNumber)) {
            throw new IllegalArgumentException(getContext().getResources().getString(R.string.err_invalid_supplier_phone_number));
        }
        Integer price = values.getAsInteger(BookEntry.COLUMN_BOOK_PRICE);
        if (price <= 0) {
            throw new IllegalArgumentException(getContext().getResources().getString(R.string.err_invalid_price));
        }
        String supplierName = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
        if (TextUtils.isEmpty(supplierName)) {
            throw new IllegalArgumentException(getContext().getResources().getString(R.string.err_invalid_supplier_name));
        }
        SQLiteDatabase database = bookDbHelper.getWritableDatabase();

        long id = database.insert(BookEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case Books:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case Book_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    public int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(BookEntry.COLUMN_BOOK_PRODUCT_NAME)) {
            String productName = values.getAsString(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
            if (TextUtils.isEmpty(productName)) {
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.err_invalid_product_name));
            }
        }
        if (values.containsKey(BookEntry.COLUMN_BOOK_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
            if (quantity < 0) {
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.err_invalid_quantity));
            }
        }
        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE)) {
            String phoneNumber = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);
            if (TextUtils.isEmpty(phoneNumber)) {
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.err_invalid_supplier_phone_number));
            }
        }
        if (values.containsKey(BookEntry.COLUMN_BOOK_PRICE)) {
            Integer price = values.getAsInteger(BookEntry.COLUMN_BOOK_PRICE);
            if (price <= 0) {
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.err_invalid_price));
            }
        }
        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            if (TextUtils.isEmpty(supplierName)) {
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.err_invalid_supplier_name));
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = bookDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = bookDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case Books:
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case Book_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);

        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case Books:
                return BookEntry.CONTENT_LIST_TYPE;
            case Book_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
