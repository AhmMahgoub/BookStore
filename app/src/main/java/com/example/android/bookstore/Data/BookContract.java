package com.example.android.bookstore.Data;

import android.provider.BaseColumns;

public final class BookContract {

    private BookContract() {
    }
    public static final class BookEntry implements BaseColumns {
        public final static String TABLE_NAME = "book";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOK_PRODUCT_NAME = "name";
        public final static String COLUMN_BOOK_PRICE = "price";
        public final static String COLUMN_BOOK_QUANTITY = "quantity";
        public final static String COLUMN_BOOK_SUPPLIER_NAME = "supplier";
        public final static String COLUMN_BOOK_SUPPLIER_PHONE = "phone";
    }
}
