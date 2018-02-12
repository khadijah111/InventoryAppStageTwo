package com.example.android.inventoryappstagetwo;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryappstagetwo.data.ProductsContract;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

    //private Context mContexts;

    /**
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {

        super(context, c, 0 /* flags */);
       // mContexts = context;
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        // Find fields
        TextView tvProductName = (TextView)view.findViewById(R.id.name);
        TextView tvProductPrice = (TextView)view.findViewById(R.id.price);
        TextView tvProductQuantity = (TextView)view.findViewById(R.id.quantity);
        final Button quantityButton = (Button) view.findViewById(R.id.saleButton);
        final int id = cursor.getInt(cursor.getColumnIndex(ProductsContract.ProductsEntry._ID));

        //current Uri
        final Uri currentProductUri = ContentUris.withAppendedId(ProductsContract.ProductsEntry.CONTENT_URI, id);

        //Find the colomn index for product attribute we are interested in
        int nameColIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_MOBILE_NAME);
        int priceColIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_MOBILE_PRICE);
        int quantityColIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_MOBILE_QUANTITY);

        //Extract properties from Cursor
        String name = cursor.getString(nameColIndex);
        Integer productPrice = cursor.getInt(priceColIndex);
        final Integer productQuantity = cursor.getInt(quantityColIndex);

        //bind fields with extracted properties
        tvProductName.setText(name);
        tvProductPrice.setText(Integer.toString(productPrice));
        tvProductQuantity.setText(Integer.toString(productQuantity));

        //Sale Button
        quantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("INFO", "ID =============" + id);
                Log.i("INFO", "OLD Quantity " + String.valueOf(productQuantity));
                ContentResolver resolver = view.getContext().getContentResolver();
                ContentValues values = new ContentValues();

                if (productQuantity > 0) {
                    //read quantity
                    int quantityValue = productQuantity;

                    Log.i("INFO", "NEW Quantity " + String.valueOf(quantityValue-1));
                    values.put(ProductsContract.ProductsEntry.COLUMN_MOBILE_QUANTITY, quantityValue-1);
                    resolver.update(
                            currentProductUri,
                            values,
                            null,
                            null);

                    context.getContentResolver().notifyChange(currentProductUri, null);
                }
                else {
                    Toast.makeText(context, "Product out of stock", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}