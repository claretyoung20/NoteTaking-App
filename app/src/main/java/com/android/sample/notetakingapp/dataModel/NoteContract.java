package com.android.sample.notetakingapp.dataModel;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Young Claret on 6/3/2017.
 */

public class NoteContract {

    /**
     * The Content Authority is a name for the entire content provider, similar to the relationship
     * between a domain name and its website. A convenient string to use for content authority is
     * the package name for the app, since it is guaranteed to be unique on the device.
     */
    public static final String CONTENT_AUTHORITY = "com.android.sample.notetakingapp";

    /**
     * The content authority is used to create the base of all URIs which apps will use to
     * contact this content provider.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * A list of possible paths that will be appended to the base URI for each of the different
     * tables.
     */

    public static final String PATH_CATEGORY = "category";
    public static final String PATH_NOTE = "notes";

    public NoteContract() {
    }

    public static final class CategoryEntry implements BaseColumns {
        // Content URI represents the base location for the table
        public static final Uri CATEGORY_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CATEGORY);

        // Define the table schema
        public static final String TABLE_NAME = "category";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_CATEGORY_NAME = "cat_name";
        public static final String COLUMN_CATEGORY_DATE = "cat_date";
        public static final String COLUMN_CATEGORY_TIME = "cat_time";

        /**
         * The MIME type of the {@link #CATEGORY_CONTENT_URI} for a list of Notes.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        /**
         * The MIME type of the {@link #CATEGORY_CONTENT_URI} for a single Note.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
    }

    public static final class NoteEntry implements BaseColumns{
        // Content URI represents the base location for the table
        public static final Uri NOTE_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTE);

        // Define the table schema
        public static final String TABLE_NAME = "notes";
        public static final String COLUMN_NOTE_ID = BaseColumns._ID;
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_BODY = "note_body";
        public static final String COLUMN_NOTE_DATE = "note_date";
        public static final String COLUMN_NOTE_TIME = "note_time";
        public static final String COLUMN_CATEGORY_ID = "note_category_id";

        /**
         * The MIME type of the {@link #NOTE_CONTENT_URI} for a list of Notes.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;

        /**
         * The MIME type of the {@link #NOTE_CONTENT_URI} for a single Notes.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;
    }


}
