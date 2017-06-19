package com.android.sample.notetakingapp.dataModel;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.android.sample.notetakingapp.dataModel.NoteContract.CategoryEntry;
import com.android.sample.notetakingapp.dataModel.NoteContract.NoteEntry;

/**
 * Created by Young Claret on 6/14/2017.
 */

public class NoteProvider extends ContentProvider {

    // Use an int for each URI we will run, this represents the different queries
    private static final int CATEGORY = 100;
    private static final int CATEGORY_ID = 101;
    private static final int NOTE = 200;
    private static final int NOTE_ID = 201;

    private NoteDBHelper noteDBHelper;

    private static final UriMatcher sUriMatcher = nUriMatcher();


//    public NoteProvider() {
//    }

    @Override
    public boolean onCreate() {
        noteDBHelper = new NoteDBHelper(getContext());
        return true;
    }

    //Uri matcher to determine the which database to use4
    public static UriMatcher nUriMatcher() {

        String content = NoteContract.CONTENT_AUTHORITY;

        // All paths to the UriMatcher have a corresponding code to return
        // when a match is found (the ints above).

        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, NoteContract.PATH_CATEGORY, CATEGORY);
        matcher.addURI(content, NoteContract.PATH_CATEGORY + "/#", CATEGORY_ID);
        matcher.addURI(content, NoteContract.PATH_NOTE, NOTE);
        matcher.addURI(content, NoteContract.PATH_NOTE + "/#", NOTE_ID);

        return matcher;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        //open database for writing data
        SQLiteDatabase db = noteDBHelper.getReadableDatabase();

        //pass the uri to determine which query get executed\
        switch (sUriMatcher.match(uri)) {
            case CATEGORY:
                cursor = db.query(
                        CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case CATEGORY_ID:
                cursor = db.query(
                        CategoryEntry.TABLE_NAME,
                        projection,
                        CategoryEntry._ID + "=?",
                        //ContentUris.parseId(uri) extract the id at the end of the Uri
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;

            case NOTE:
                cursor = db.query(
                        NoteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case NOTE_ID:
                cursor = db.query(
                        NoteEntry.TABLE_NAME,
                        projection,
                        NoteEntry.COLUMN_NOTE_ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        // Set the notification URI for the cursor to the one passed into the function. This
        // causes the cursor to register a content observer to watch for changes that happen to
        // this URI and any of it's descendants. By descendants, we mean any URI that begins
        // with this path.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CATEGORY:
                return CategoryEntry.CONTENT_LIST_TYPE;
            case CATEGORY_ID:
                return CategoryEntry.CONTENT_ITEM_TYPE;
            case NOTE:
                return NoteEntry.CONTENT_LIST_TYPE;
            case NOTE_ID:
                return NoteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case CATEGORY:
                return insertCategory(uri, values);
            case NOTE:
                return insertNote(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int idDelete;

        SQLiteDatabase db = noteDBHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CATEGORY:
                // Delete all rows that match the selection and selection args
                idDelete = db.delete(CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CATEGORY_ID:
                // Delete a single row given by the ID in the URI
                selection = CategoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                idDelete = db.delete(CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;


            case NOTE:
                idDelete = db.delete(NoteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case NOTE_ID:
                selection = NoteEntry.COLUMN_NOTE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                idDelete = db.delete(NoteEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Cannot Delete rows for that Uri " + uri);
        }
        if (idDelete != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return idDelete;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        switch (sUriMatcher.match(uri)) {
            case CATEGORY:
                return updateCategory(uri, values, selection, selectionArgs);
            case CATEGORY_ID:
                // For the CATEGORY code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = CategoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateCategory(uri, values, selection, selectionArgs);

            case NOTE:
                return updateNote(uri, values, selection, selectionArgs);
            case NOTE_ID:
                // For the NOTE code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = NoteEntry.COLUMN_NOTE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateNote(uri, values, selection, selectionArgs);
        }
        return 0;
    }

    /**
     * Insert  Note in the database with the given content values.
     */
    public Uri insertNote(Uri uri, ContentValues values) {
        //get the values
        String noteTitle = values.getAsString(NoteEntry.COLUMN_NOTE_TITLE);
        String noteBody = values.getAsString(NoteEntry.COLUMN_NOTE_BODY);
        String noteDate = values.getAsString(NoteEntry.COLUMN_NOTE_DATE);
        String noteTime = values.getAsString(NoteEntry.COLUMN_NOTE_TIME);
        int noteCatId = values.getAsInteger(NoteEntry.COLUMN_CATEGORY_ID);


        //validate the values before inserting to database
        if (noteBody == null) {
            throw new IllegalArgumentException("Body is need");
        } else if (noteTitle == null) {
            throw new IllegalArgumentException("Need note Title");
        } else if (noteDate == null) {
            throw new IllegalArgumentException("Need note Note date");
        } else if (noteTime == null) {
            throw new IllegalArgumentException("Time needed");
        } else if (noteCatId < 1) {
            throw new IllegalArgumentException("Need to have the Category or put it in your default category");
        }

        SQLiteDatabase db = noteDBHelper.getWritableDatabase();

        long id = db.insert(NoteEntry.TABLE_NAME, null, values);

        // Use this on the URI passed into the function to notify any observers that the uri has
        // changed.
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    public Uri insertCategory(Uri uri, ContentValues values) {
        //get the values
        String cateName = values.getAsString(CategoryEntry.COLUMN_CATEGORY_NAME);
        String catTime = values.getAsString(CategoryEntry.COLUMN_CATEGORY_TIME);
        String catDate = values.getAsString(CategoryEntry.COLUMN_CATEGORY_DATE);

        //validate the values before inserting to database
        if (cateName == null) {
            throw new IllegalArgumentException("Name is need");
        } else if (catTime == null) {
            throw new IllegalArgumentException("Date is need");
        } else if (catDate == null) {
            throw new IllegalArgumentException("Category need date");
        }

        SQLiteDatabase db = noteDBHelper.getWritableDatabase();

        long id = db.insert(CategoryEntry.TABLE_NAME, null, values);

        // Use this on the URI passed into the function to notify any observers that the uri has
        // changed.
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Update note in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    public int updateNote(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If the {@link NoteEntry#COLUMN_CATEGORY_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(NoteEntry.COLUMN_NOTE_TITLE)) {
            if (values.getAsString(NoteEntry.COLUMN_NOTE_TITLE) == null) {
                throw new IllegalArgumentException("Name is need");
            }
        }

        if (values.containsKey(NoteEntry.COLUMN_NOTE_BODY)) {
            if (values.getAsString(NoteEntry.COLUMN_NOTE_BODY) == null) {
                throw new IllegalArgumentException("Body is need");
            }
        }

        //check if the user did give any value
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase db = noteDBHelper.getWritableDatabase();

        //update the data in the database
        int idUpdate = db.update(NoteEntry.TABLE_NAME, values, selection, selectionArgs);

        //if the update is successful the notify the cursor that the uri has changed
        if (idUpdate != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return idUpdate;
    }

    public int updateCategory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If the {@link CategoryEntry#COLUMN_CATEGORY_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(CategoryEntry.COLUMN_CATEGORY_NAME)) {
            if (values.getAsString(CategoryEntry.COLUMN_CATEGORY_NAME) == null) {
                throw new IllegalArgumentException("Name is need");
            }
        }

        //check if the user did give any value
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase db = noteDBHelper.getWritableDatabase();

        //update the data in the database
        int idUpdate = db.update(CategoryEntry.TABLE_NAME, values, selection, selectionArgs);

        //if the update is successful the notify the cursor that the uri has changed
        if (idUpdate != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return idUpdate;
    }
}
