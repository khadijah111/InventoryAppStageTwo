package com.example.android.inventoryappstagetwo;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventoryappstagetwo.data.ProductsContract;

/**
 * Allows user to create a new product or edit an existing one.
 */
public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int LOADER_ID = 0;

    /**
     * EditText field to enter the product's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the product's breed
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the product's weight
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the product's gender
     */
    private Spinner mSupplierNameSpinner;

    /**
     * EditText field to enter the product's breed
     */
    private EditText mEmailEditText;
    private EditText mCallEditText;
    private Integer quantityHolder;

    /**
     * Content URI for the existing product (null if it's a new product)
     */
    private Uri mCurrentProductUri;

    private boolean mProductHasChanged = false;

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
// the view, and we change the mproductHasChanged boolean to true.

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };
    /**
     * Gender of the product. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mSupplierName = ProductsContract.ProductsEntry.SUPPLIER_NAME_UNKNOWN; // mean= 0;

    //Buttons
    private Button mSaveButton;
    private Button mOrderButton;
    private Button mDeleteButton;
    private Button mCallButton;
    private Button mPlusButton;
    private Button mLessButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new product or editing an existing one.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        // If the intent DOES NOT contain a product content URI, then we know that we are
        // creating a new product.
        if (mCurrentProductUri == null) {
            // This is a new product, so change the app bar to say "Add a product"
            setTitle(getString(R.string.editor_activity_title_new_product));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a product that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit product"
            setTitle(getString(R.string.editor_activity_title_edit_mobile_product));

            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mSupplierNameSpinner = (Spinner) findViewById(R.id.spinner_supplier_name);
        mEmailEditText = (EditText) findViewById(R.id.edit_product_email);
        mCallEditText = (EditText) findViewById(R.id.phoneEditText);

        mSaveButton = (Button) findViewById(R.id.saveButton);
        mOrderButton = (Button) findViewById(R.id.orderButton);
        mDeleteButton = (Button) findViewById(R.id.deleteButton);
        mCallButton = (Button) findViewById(R.id.callButton);
        mPlusButton = (Button) findViewById(R.id.plusButton);
        mLessButton = (Button) findViewById(R.id.lessButton);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameSpinner.setOnTouchListener(mTouchListener);
        mEmailEditText.setOnTouchListener(mTouchListener);
        mCallEditText.setOnTouchListener(mTouchListener);
        setupSpinner();

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // save product or update product to DB
                saveProduct();
                //exit the current Activity then go to Main catalog activity
                //finish();
            }
        });

        mOrderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // order
                sendEmail();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
            }
        });

        mCallButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                call();
            }
        });

        mPlusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mQuantityEditText.getText().toString().trim().equals("")) {
                    quantityHolder = 0;
                    mQuantityEditText.setText(String.valueOf(quantityHolder));
                } else {
                    quantityHolder = Integer.parseInt(mQuantityEditText.getText().toString().trim());
                    quantityHolder++;
                    mQuantityEditText.setText(String.valueOf(quantityHolder));
                }
            }
        });

        mLessButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mQuantityEditText.getText().toString().trim().equals("") || Integer.parseInt(mQuantityEditText.getText().toString().trim()) == 0) {
                    quantityHolder = 0;
                    mQuantityEditText.setText(String.valueOf(quantityHolder));
                } else {
                    quantityHolder = Integer.parseInt(mQuantityEditText.getText().toString().trim());
                    quantityHolder--;
                    mQuantityEditText.setText(String.valueOf(quantityHolder));
                }
            }
        });
    }

    private void call() {
        if (mCallEditText.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.phone_number_cannont_be_empty),
                    Toast.LENGTH_LONG).show();
            return;
        } else {
            String callNumString = mCallEditText.getText().toString().trim();
            String uri = "tel:" + callNumString.trim();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        }
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the product.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mSupplierNameSpinner.setAdapter(supplierSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mSupplierNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supplier_techno_company))) {
                        mSupplierName = ProductsContract.ProductsEntry.SUPPLIER_NAME_TECH_COMPANY; // tech co
                    } else if (selection.equals(getString(R.string.supplier_phone_company))) {
                        mSupplierName = ProductsContract.ProductsEntry.SUPPLIER_NAME_PHONE_COMPANY; // phone co
                    } else {
                        mSupplierName = ProductsContract.ProductsEntry.SUPPLIER_NAME_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupplierName = 0; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
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
                // save product or update product to DB
                saveProduct();
                //exit the current Activity then go to Main catalog activity
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar

            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get user input from editor and save product into database.
     */
    private void saveProduct() {
        ContentValues productValues = new ContentValues();
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        Integer supplierNameInteger = mSupplierName;
        String supplierEmailString = mEmailEditText.getText().toString().trim();
        String supplierPhoneString = mCallEditText.getText().toString().trim();

        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank
        //So return without any action needed
        if (mCurrentProductUri == null && //insert mode
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && mSupplierName == ProductsContract.ProductsEntry.SUPPLIER_NAME_UNKNOWN
                && TextUtils.isEmpty(supplierEmailString) && TextUtils.isEmpty(supplierPhoneString)) {
            // Since no fields were modified, we can return early without creating a new product.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(this, getString(R.string.all_field_cannot_be_null),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.name_cannot_be_null),
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            //Create a ContentValues object where column names are the keys
            productValues.put(ProductsContract.ProductsEntry.COLUMN_MOBILE_NAME, nameString);
        }

        // If the Price is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int price = 0;
        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.price_cannot_be_null),
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            price = Integer.parseInt(priceString);
        }
        productValues.put(ProductsContract.ProductsEntry.COLUMN_MOBILE_PRICE, price);

        // If the weight is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        productValues.put(ProductsContract.ProductsEntry.COLUMN_MOBILE_QUANTITY, quantity);

        //Create a ContentValues object where column names are the keys
        productValues.put(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_NAME, supplierNameInteger);


        if (TextUtils.isEmpty(supplierEmailString)) {
            Toast.makeText(this, getString(R.string.email_cannot_be_null),
                    Toast.LENGTH_SHORT).show();

        } else {
            //Create a ContentValues object where column names are the keys
            productValues.put(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_EMAIL, supplierEmailString);
        }

        if (TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(this, getString(R.string.phone_number_cannont_be_empty),
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            //Create a ContentValues object where column names are the keys
            productValues.put(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_PHONE, Integer.parseInt(supplierPhoneString));
        }
        // If the intent DOES NOT contain a product content URI, then we know that we are
        // creating a new product -----------INSERT MODE-------------.
        if (mCurrentProductUri == null) {//INSERT new product
            // Insert a new row for Toto into the provider using the ContentResolver.
            // Use the {@link productEntry#CONTENT_URI} to indicate that we want to insert
            // into the products database table.
            // Receive the new content URI that will allow us to access Toto's data in the future.
            Uri uri = getContentResolver().insert(ProductsContract.ProductsEntry.CONTENT_URI,  // the user dictionary content URI
                    productValues); // the values to insert

            // Show a toast message depending on whether or not the insertion was successful
            if (uri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else { // Otherwise this is an EXISTING product, so update the product with content URI: mCurrentproductUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentproductUri will already identify the correct row in the database that
            // we want to modify.
            //Update an existing product
            //------------------UPDATE MODE -------------------------
            int rowsAffected = getContentResolver().update(mCurrentProductUri, productValues, null, null);

            // Show a toast message depending on whether or not the insertion was successful
            if (rowsAffected == 0) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.


        // These are the products rows that we will retrieve.
        //Set the projection ..SELECT name, breed, weight, gender
        String[] projection = {
                ProductsContract.ProductsEntry._ID,
                ProductsContract.ProductsEntry.COLUMN_MOBILE_NAME,
                ProductsContract.ProductsEntry.COLUMN_MOBILE_PRICE,
                ProductsContract.ProductsEntry.COLUMN_MOBILE_QUANTITY,
                ProductsContract.ProductsEntry.COLUMN_SUPPLIER_NAME,
                ProductsContract.ProductsEntry.COLUMN_SUPPLIER_EMAIL,
                ProductsContract.ProductsEntry.COLUMN_SUPPLIER_PHONE
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) { // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_MOBILE_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_MOBILE_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_MOBILE_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_NAME);
            int supplierEmailColumnIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_EMAIL);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int supplierName = cursor.getInt(supplierNameColumnIndex);
            String supplierEmail = cursor.getString(supplierEmailColumnIndex);
            int phone = cursor.getInt(supplierPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mEmailEditText.setText(supplierEmail);
            mCallEditText.setText(Integer.toString(phone));

            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (supplierName) {
                case ProductsContract.ProductsEntry.SUPPLIER_NAME_TECH_COMPANY:
                    mSupplierNameSpinner.setSelection(1);
                    break;
                case ProductsContract.ProductsEntry.SUPPLIER_NAME_PHONE_COMPANY:
                    mSupplierNameSpinner.setSelection(2);
                    break;
                default:
                    mSupplierNameSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameSpinner.setSelection(0); // Select "Unknown" gender
        mEmailEditText.setText("");
        mCallEditText.setText("");
    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteproduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
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
     * Perform the deletion of the product in the database.
     */
    private void deleteproduct() {

// Deletes the words that match the selection criteria
        // Only perform the delete if this is an existing product.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentproductUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }

    protected void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);

        if (mEmailEditText.getText().toString().trim().equals("")) {
            Toast.makeText(this, "The email cannot be empty value", Toast.LENGTH_LONG).show();
            return;
        } else if (!mEmailEditText.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_LONG).show();
            return;
        }
        //RECEIVER
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:" + mEmailEditText.getText().toString().trim()));

        //SUBJECT
        intent.putExtra(Intent.EXTRA_SUBJECT, "New order: " + mNameEditText.getText().toString().trim());

        //MESSAGE
        String message = "I want to request a new order from: " + mNameEditText.getText().toString().trim() +
                " With quantity of: " + mQuantityEditText.getText().toString().trim() + " Pcs, " + "\n" +
                "Please confirm if you can send them to us." + "\n\n" + "Best regards," + "\n";

        intent.putExtra(android.content.Intent.EXTRA_TEXT, message);

        try {
            startActivity(intent);
            Log.i("Order Button: ", "Finished sending email");
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(DetailsActivity.this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
