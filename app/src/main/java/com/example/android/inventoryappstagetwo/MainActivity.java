package com.example.android.inventoryappstagetwo;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryappstagetwo.data.ProductsContract;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 0;
    // This is the Adapter being used to display the list's data.
    ProductCursorAdapter mProductCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

         // Display the number of rows in the Cursor (which reflects the number of rows in the
        // pets table in the database).
        // Find ListView to populate
        ListView productsListView = (ListView) findViewById(R.id.listView);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        productsListView.setEmptyView(emptyView);

        //set the adapter
        mProductCursorAdapter = new ProductCursorAdapter(this, null);
        productsListView.setAdapter(mProductCursorAdapter);

        // Setup the item click listener to go to the editor activity EDIT MODE
        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);

                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link PetEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.pets/pets/2"
                // if the pet with ID 2 was clicked on.
                Uri currentProductUri = ContentUris.withAppendedId(ProductsContract.ProductsEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentProductUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                //Insert dummy data
                insertProduct();
                //then display the added data to the textView
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                //delete all pets from DB
                int rowsDeleted = getContentResolver().delete(ProductsContract.ProductsEntry.CONTENT_URI, null, null);
                Toast.makeText(this, getString(R.string.editor_delete_all_products_finished), Toast.LENGTH_SHORT).show();
                Log.v("MainActivity", rowsDeleted + " rows deleted from products database");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertProduct() {

        ContentValues petValues = new ContentValues();

        //Create a ContentValues object where column names are the keys
        //ADD DUMMY PET (INSERT)
        petValues.put(ProductsContract.ProductsEntry.COLUMN_MOBILE_NAME, "Galaxy Note 8");
        petValues.put(ProductsContract.ProductsEntry.COLUMN_MOBILE_PRICE, 800);
        petValues.put(ProductsContract.ProductsEntry.COLUMN_MOBILE_QUANTITY, 10);
        petValues.put(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_NAME, ProductsContract.ProductsEntry.SUPPLIER_NAME_TECH_COMPANY);
        petValues.put(ProductsContract.ProductsEntry.COLUMN_SUPPLIER_EMAIL, "om.albaraa33@gmail.com");

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri uri = getContentResolver().insert(ProductsContract.ProductsEntry.CONTENT_URI,  // the user dictionary content URI
                petValues); // the values to insert

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
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.

        // These are the Pets rows that we will retrieve.
        //Set the projection ..SELECT name, breed, weight, gender
        String[] projection = {
                ProductsContract.ProductsEntry._ID,
                ProductsContract.ProductsEntry.COLUMN_MOBILE_NAME,
                ProductsContract.ProductsEntry.COLUMN_MOBILE_PRICE,
                ProductsContract.ProductsEntry.COLUMN_MOBILE_QUANTITY,
                ProductsContract.ProductsEntry.COLUMN_SUPPLIER_NAME,
                ProductsContract.ProductsEntry.COLUMN_SUPPLIER_EMAIL,
        };

        return new CursorLoader(
                this,
                ProductsContract.ProductsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mProductCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mProductCursorAdapter.swapCursor(null);
    }
}