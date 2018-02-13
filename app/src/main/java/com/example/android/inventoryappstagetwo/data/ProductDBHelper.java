package com.example.android.inventoryappstagetwo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by khadijah on 1/27/2018.
 * Database helper for Products app. Manages database creation and version management.
 */
public class ProductDBHelper extends SQLiteOpenHelper {

    //IF you change the DB scheme you SHOULD increment the DB version
    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    public static final int DATABASE_VERSION = 1;
    /**
     * Name of the database file
     */
    public static final String DATABASE_NAME = "Products1.db";

    //CONSTRUCTOR
    public ProductDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //This is called when the database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the products table
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductsContract.ProductsEntry.TABLE_NAME + " ("
                + ProductsContract.ProductsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductsContract.ProductsEntry.COLUMN_MOBILE_NAME + " TEXT NOT NULL, "
                + ProductsContract.ProductsEntry.COLUMN_MOBILE_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + ProductsContract.ProductsEntry.COLUMN_MOBILE_QUANTITY + " INTEGER, "
                + ProductsContract.ProductsEntry.COLUMN_SUPPLIER_NAME + " INTEGER , "
                + ProductsContract.ProductsEntry.COLUMN_SUPPLIER_PHONE + " INTEGER , "
                + ProductsContract.ProductsEntry.COLUMN_SUPPLIER_EMAIL + " TEXT );";
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
        //db.execSQL(SQL_DELETE_ENTRIES);
    }
}