package com.example.android.inventoryappstagetwo.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * {@link ContentProvider} for Pets app.
 */
public class ProductsProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = ProductsProvider.class.getSimpleName();

    //Database Helper object
    private ProductDBHelper productDbHelper;
    /**
     * Initialize the provider and the database helper object.
     */

    /**
     * URI matcher code for the content URI for the pets table
     */
    private static final int PRODUCTS = 100;

    /**
     * URI matcher code for the content URI for a single pet in the pets table
     */
    private static final int PRODUCT_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        //HERE bind each URI with specific integer code
        sUriMatcher.addURI(ProductsContract.CONTENT_AUTHORITY, ProductsContract.PATH_PETS, PRODUCTS);
        sUriMatcher.addURI(ProductsContract.CONTENT_AUTHORITY, ProductsContract.PATH_PETS + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        productDbHelper = new ProductDBHelper(getContext());
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        //1- ACCESS db USING mdbHelper
        SQLiteDatabase dataBase = productDbHelper.getReadableDatabase();

        Cursor cursor;

        //2- Using URiMatcher to find the kind of input URI 100 for hole table and 101 for specific row in table
        int match = sUriMatcher.match(uri);

        switch (match)
        {
            case PRODUCTS:
                //perform DB query in the hole Pets Table
                cursor = dataBase.query(ProductsContract.ProductsEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PRODUCT_ID:
                //perform DB query in Pets Table based on product_ID column to retrieve selected ROW/ROWS
                selection = ProductsContract.ProductsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                //SELECT * FROM products WHERE _ID = 3;
                // This will perform a query on the products table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = dataBase.query(ProductsContract.ProductsEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("CANNOT query unknown URI: "+ uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        //Using URiMatcher to find the kind of input URI
        final int match = sUriMatcher.match(uri);//here will be 100 or 101
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct (Uri uri, ContentValues values)
    {
        /**---------------VALIDATION PROCESS---------------------**/
        // Check that the name is not null
        String name = values.getAsString(ProductsContract.ProductsEntry.COLUMN_MOBILE_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        //Check for price
        // If the price is provided, check that it's greater than or equal to 0 $
        Integer price = values.getAsInteger(ProductsContract.ProductsEntry.COLUMN_MOBILE_PRICE);

        if (price == null && price < 0) {
            throw new IllegalArgumentException("Pet requires valid price");
        }

        //Check that the supplier_name is valid and not null
        Integer supplierName = values.getAsInteger(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_NAME);

        if (supplierName == null || ! ProductsContract.ProductsEntry.isValidSupplierName(supplierName)) {
            throw new IllegalArgumentException("Product requires valid supplier name");
        }

        //Check the phone number
       // Integer supplierPhone = values.getAsInteger(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_PHONE);

//        if (! ProductsContract.ProductsEntry.isValidSupplierName(supplierPhone)) {
//            throw new IllegalArgumentException("Product requires valid supplier phone");
//        }

        // No need to check the email any value are valid (including null).

        /*-----------ACCESS DATA BASE -------------------*/
        // Gets the database in write mode
        SQLiteDatabase db = productDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        // Insert a new row for Toto in the database, returning the ID of that new row.
        long newRowId = db.insert(ProductsContract.ProductsEntry.TABLE_NAME,
                null,
                values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (newRowId == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, newRowId);
    }
    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ProductsContract.ProductsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update products in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more products).
     * Return the number of rows that were successfully updated.
     */
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //Validation the existing Name field
       if( values.containsKey(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_NAME))
       {
           // Check that the name is not null
           String name = values.getAsString(ProductsContract.ProductsEntry.COLUMN_MOBILE_NAME);
           if (name == null) {
               throw new IllegalArgumentException("Product requires a name");
           }
       }

        // If the {@link ProductEntry #COLUMN_PRODUCT_PRICE} key is present,
        // check that the price value is valid.
        if (values.containsKey(ProductsContract.ProductsEntry.COLUMN_MOBILE_PRICE)) {
            // Check that the price is greater than or equal to 0 $
            Integer price = values.getAsInteger(ProductsContract.ProductsEntry.COLUMN_MOBILE_PRICE);

            if (price == null && price < 0) {
                throw new IllegalArgumentException("Pet requires valid price");
            }
        }
        // If the {@link ProductEntry#COLOMN_SUPPLIER_NAME} key is present,
        // check that the supplierName value is valid.
        if (values.containsKey(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_NAME)) {
            //Check that the supplier_name is valid and not null
            Integer supplierName = values.getAsInteger(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_NAME);

            if (supplierName == null || ! ProductsContract.ProductsEntry.isValidSupplierName(supplierName)) {
                throw new IllegalArgumentException("Product requires valid supplier name");
            }
        }
        // No need to check the email any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = productDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ProductsContract.ProductsEntry.TABLE_NAME, values,selection,selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        //Return the number of rows that were affected
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = productDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted =  database.delete(ProductsContract.ProductsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductsContract.ProductsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted =  database.delete(ProductsContract.ProductsEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}