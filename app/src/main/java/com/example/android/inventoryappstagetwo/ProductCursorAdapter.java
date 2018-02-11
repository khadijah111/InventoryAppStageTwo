package com.example.android.inventoryappstagetwo;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryappstagetwo.data.ProductsContract;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

    /**
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
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
        // TODO: Fill out this method and return the list item view (instead of null)
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
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields
        TextView tvProductName = (TextView)view.findViewById(R.id.name);
        TextView tvProductPrice = (TextView)view.findViewById(R.id.price);
        TextView tvProductQuantity = (TextView)view.findViewById(R.id.quantity);


        //Find the colomn index for product attribute we are interested in
        int nameColIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_MOBILE_NAME);
        int priceColIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_MOBILE_PRICE);
        int quantityColIndex = cursor.getColumnIndex(ProductsContract.ProductsEntry.COLUMN_MOBILE_QUANTITY);

        //Extract properties from Cursor
        String name = cursor.getString(nameColIndex);
        Integer productPrice = cursor.getInt(priceColIndex);
        Integer productQuantity = cursor.getInt(quantityColIndex);

        //bind fields with extracted properties
        tvProductName.setText(name);
        tvProductPrice.setText(Integer.toString(productPrice));
        tvProductQuantity.setText(Integer.toString(productQuantity));
    }
}