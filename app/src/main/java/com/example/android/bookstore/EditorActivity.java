package com.example.android.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookstore.Data.BookContract.BookEntry;

import java.util.ArrayList;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_BOOK_LOADER = 0;

    private boolean SAVE_OK = false;
    private Uri mCurrentBookUri;

    private EditText mProductNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mContactEditText;

    private Button increment;
    private Button decrement;
    private Button orderButton;

    private boolean mBookHasChanged = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_book));
            orderButton = (Button) findViewById(R.id.order_button);
            orderButton.setVisibility(View.GONE);
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_book));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        mProductNameEditText = (EditText) findViewById(R.id.edit_book_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier);
        mContactEditText = (EditText) findViewById(R.id.edit_phone);

        increment = (Button) findViewById(R.id.Increment);
        decrement = (Button) findViewById(R.id.decrement);

        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentQty = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(currentQty) || currentQty == null) {
                    currentQty = String.valueOf(0);
                }
                int qtyInt = Integer.parseInt(currentQty);
                qtyInt++;
                mQuantityEditText.setText(String.valueOf(qtyInt));
            }
        });
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentQty = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(currentQty) || currentQty == null) {
                    currentQty = String.valueOf(0);
                }

                int qtyInt = Integer.parseInt(currentQty);
                if (qtyInt == 0) {
                    Toast.makeText(getApplicationContext(), R.string.InvalidQuantity, Toast.LENGTH_SHORT).show();
                } else {
                    qtyInt--;
                    mQuantityEditText.setText(String.valueOf(qtyInt));
                }
            }
        });
        findViewById(R.id.order_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = mContactEditText.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.cannot_make_call, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mProductNameEditText.setOnTouchListener(touchListener);
        mPriceEditText.setOnTouchListener(touchListener);
        mQuantityEditText.setOnTouchListener(touchListener);
        mQuantityEditText.setOnTouchListener(touchListener);
        mQuantityEditText.setOnTouchListener(touchListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {BookEntry._ID,
                BookEntry.COLUMN_BOOK_PRODUCT_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};

        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            String currentProductName = cursor.getString(productNameColumnIndex);
            int currentPrice = cursor.getInt(priceColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            String currentSupplier = cursor.getString(supplierColumnIndex);
            int currentPhone = cursor.getInt(phoneColumnIndex);

            mProductNameEditText.setText(currentProductName);
            mPriceEditText.setText(Integer.toString(currentPrice));
            mQuantityEditText.setText(Integer.toString(currentQuantity));
            mSupplierNameEditText.setText(currentSupplier);
            mContactEditText.setText(Integer.toString(currentPhone));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mContactEditText.setText("");
    }

    private void saveBook() {
        String productNameString = mProductNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierNameEditText.getText().toString().trim();
        String contactString = mContactEditText.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_PRODUCT_NAME, productNameString);
        values.put(BookEntry.COLUMN_BOOK_PRICE, priceString);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantityString);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierString);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, contactString);

        if (mCurrentBookUri == null) {
            if (!validateInput(productNameString, supplierString, contactString, priceString, quantityString)) {
                return;
            }
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (newUri == null) {
                getResources().getString(R.string.editor_insert_book_failed);
            } else {
                getResources().getString(R.string.editor_insert_book_successful);
                finish();
            }
        } else {
            if (!validateInput(productNameString, supplierString, contactString, priceString, quantityString)) {
                return;
            }

            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new Book, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save Book to database
                saveBook();
                if (SAVE_OK) {
                    finish();
                    // Exit activity
                    return true;
                } else {
                    return false;
                }
                // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // If the Book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the Book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the Book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        // Only perform the delete if this is an existing Book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the Book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the Book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the Book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * This method is called inside save() to validate the inserted data
     */
    private boolean validateInput(String name, String supplierName, String contact, String price, String quantity) {

        ArrayList<String> resultList = new ArrayList<String>();
        if (TextUtils.isEmpty(name)) {
            resultList.add("name");
        }
        if (TextUtils.isEmpty(supplierName)) {
            resultList.add("supplier name");
        }
        int integerContact = checkNull(contact);
        if (integerContact <= 0) {
            resultList.add("contact");
        }
        int integerPrice = checkNull(price);
        if (integerPrice <= 0) {
            resultList.add("price");
        }
        int integerQuantity = checkNull(quantity);
        if (integerQuantity <= 0) {
            resultList.add("quantity");
        }

        String negativeToast = TextUtils.join(", ", resultList);

        if (resultList.size() > 0) {
            Toast.makeText(getApplicationContext(), "Invalid " + negativeToast, Toast.LENGTH_SHORT).show();
            return false;
        }
        SAVE_OK = true;
        // Otherwise the validations passed
        return true;
    }

    //check whether String null or equal number to avoid number exceptions
    public int checkNull(String testedString) {
        int num = 0;
        try {
            if (testedString != null) {
                num = Integer.parseInt(testedString);
                return num;
            }
        } catch (NumberFormatException e) {
            num = 0;
            return num;
        }
        return num;
    }
}
