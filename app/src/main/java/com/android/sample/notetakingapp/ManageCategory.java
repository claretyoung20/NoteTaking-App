package com.android.sample.notetakingapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sample.notetakingapp.dataModel.NoteContract;
import com.android.sample.notetakingapp.dataModel.NoteContract.CategoryEntry;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ManageCategory extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    CateCursoAdapter cateCursoAdapter;
    private static final int CATEGORY_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);

        ListView listView = (ListView) findViewById(R.id.listCar_Views);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
//        View emptyView = findViewById(R.id.empty_view);
//        listView.setEmptyView(emptyView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });

        cateCursoAdapter = new CateCursoAdapter(this, null);
        listView.setAdapter(cateCursoAdapter);

        //listen to click on any listView and show menu
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openOptionsMenu();
            }
        });

        //register for context Menu
        registerForContextMenu(listView);

        getLoaderManager().initLoader(CATEGORY_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                NoteContract.CategoryEntry._ID,
                NoteContract.CategoryEntry.COLUMN_CATEGORY_NAME
        };

        return new CursorLoader(this, NoteContract.CategoryEntry.CATEGORY_CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cateCursoAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cateCursoAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addNote:
                addCategory();
                return true;

            case android.R.id.home:
                // which is the {@link MainActivity}.
                NavUtils.navigateUpFromSameTask(ManageCategory.this);
                return true;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.listCar_Views) {
            getMenuInflater().inflate(R.menu.categorymenu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        long listPosition = info.id;

        int id = item.getItemId();
        switch (id) {
            case R.id.renameCat:
                reName(listPosition);
                return true;
            case R.id.deleteCat:
                deleteAlert(listPosition);
                return true;
        }

        return super.onContextItemSelected(item);
    }

    private void deleteCat(long id) {
        // Form the content URI that represents the specific Category that was clicked on,
        // by appending the "id" (passed as input to this method) onto the
        // {@link CategoryEntry#CONTENT_URI}.
        // For example, the URI would be "content://com.android.sample.notetakingapp/categoryTable/2"
        // if the Category with ID 2 was clicked on.
        Uri currentCategoryUri = ContentUris.withAppendedId(CategoryEntry.CATEGORY_CONTENT_URI, id);

        int rowDelete = getContentResolver().delete(currentCategoryUri, null, null);

        if (rowDelete <= 0) {
            Toast.makeText(this, "Unable to delete Category", Toast.LENGTH_LONG).show();
        }

    }

    private void deleteAlert(final long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you Sure you want to delete this Category");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                    return;
                }
            }
        });
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCat(id);
//                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void reName(final long id) {

        String cateName = "";

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                CategoryEntry.COLUMN_CATEGORY_NAME,
        };

        final Uri currentCategoryUri = ContentUris.withAppendedId(CategoryEntry.CATEGORY_CONTENT_URI, id);

        Cursor cursor = getContentResolver().query(currentCategoryUri, projection, null, null, null);
        if (cursor.moveToFirst()) {
            cateName += cursor.getString(cursor.getColumnIndex(CategoryEntry.COLUMN_CATEGORY_NAME));
        }


        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View viewBuilder = inflater.inflate(R.layout.rename_category, null);
        builder.setView(viewBuilder);

        builder.setTitle("Rename Category");

        final TextView nameCat = (TextView) viewBuilder.findViewById(R.id.reNamecateName);

        nameCat.setText(cateName);

        final String finalCateName = cateName;

        builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = nameCat.getText().toString().trim();

                if (TextUtils.isEmpty(newName)) {
                    newName = finalCateName;
                }
                ContentValues values = new ContentValues();
                values.put(CategoryEntry.COLUMN_CATEGORY_NAME, newName);
                int update = getContentResolver().update(currentCategoryUri, values, null, null);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alert = builder.create();
        alert.show();

    }

    private void addCategory() {
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View viewBuilder = inflater.inflate(R.layout.rename_category, null);
        builder.setView(viewBuilder);

        builder.setTitle("Add new Category");

        final TextView nameCat = (TextView) viewBuilder.findViewById(R.id.reNamecateName);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //category Name
                String categoryName = nameCat.getText().toString().trim();

                if (TextUtils.isEmpty(categoryName)) {
                    dialog.dismiss();
                    return;
                }

                //Date
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy");
                Date currentTime_1 = new Date();
                String dateString = formatter.format(currentTime_1);

                //Time
                SimpleDateFormat formatterTime = new SimpleDateFormat("h:mm a");
                String timeString = formatterTime.format(currentTime_1);

                ContentValues values = new ContentValues();
                values.put(CategoryEntry.COLUMN_CATEGORY_NAME, categoryName);
                values.put(CategoryEntry.COLUMN_CATEGORY_DATE, dateString);
                values.put(CategoryEntry.COLUMN_CATEGORY_TIME, timeString);

                Uri uri = getContentResolver().insert(CategoryEntry.CATEGORY_CONTENT_URI, values);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alert = builder.create();
        alert.show();

    }
}
