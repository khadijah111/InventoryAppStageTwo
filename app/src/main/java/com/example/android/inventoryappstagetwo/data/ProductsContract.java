package com.example.android.inventoryappstagetwo.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by khadijah on 1/26/2018.
 */
public class ProductsContract {

    //CONTENT_AUTHORITY
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryappstagetwo";

    //BASE_CONTENT_URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //PATH_TableName
    //This constants stores the path for each of the tables which will be appended to the base content URI.
    public static final String PATH_PETS = "products";

    public static final class ProductsEntry implements BaseColumns {

        public final static String TABLE_NAME = "products";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_MOBILE_NAME = "name";//STRING
        public final static String COLUMN_MOBILE_PRICE = "price";//INTEGER
        public final static String COLUMN_MOBILE_QUANTITY = "quantity";//INTEGER
        public final static String COLUMN_SUPPLIER_NAME = "supplierName";//INTEGER "UI spinner"
        public final static String COLUMN_SUPPLIER_EMAIL = "supplierEmail";//STRING
        public final static String COLUMN_SUPPLIER_PHONE = "phone";//INTEGER

        public final static int SUPPLIER_NAME_UNKNOWN = 0;
        public final static int SUPPLIER_NAME_TECH_COMPANY = 1;
        public final static int SUPPLIER_NAME_PHONE_COMPANY = 2;

        //Complete CONTENT_URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         * CURSOR_DIR_BASE_TYPE (which maps to the constant "vnd.android.cursor.dir"
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         * CURSOR_ITEM_BASE_TYPE (which maps to the constant “vnd.android.cursor.item”)
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        /**
         * Returns whether or not the given supplier name is {@link #SUPPLIER_NAME_UNKNOWN}, {@link #SUPPLIER_NAME_TECH_COMPANY},
         * or {@link #SUPPLIER_NAME_PHONE_COMPANY}.
         */
        public static boolean isValidSupplierName(int supplierName) {
            if (supplierName == SUPPLIER_NAME_UNKNOWN || supplierName == SUPPLIER_NAME_TECH_COMPANY || supplierName == SUPPLIER_NAME_PHONE_COMPANY) {
                return true;
            }
            return false;
        }
    }
}
