package com.android.sample.notetakingapp.dataModel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.sample.notetakingapp.dataModel.NoteContract.CategoryEntry;
import com.android.sample.notetakingapp.dataModel.NoteContract.NoteEntry;

/**
 * Created by Young Claret on 6/14/2017.
 */

public class NoteDBHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "noteTakingApplication.db";
    public static int DATABASE_VERSION = 1;

    public NoteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        categoryTable(db);
        noteTable(db);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



    public void categoryTable(SQLiteDatabase db) {
        //create category table
        String categoryTable = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" + CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                CategoryEntry.COLUMN_CATEGORY_NAME + " TEXT NOT NULL," +
                CategoryEntry.COLUMN_CATEGORY_DATE + " TEXT NOT NULL," +
                CategoryEntry.COLUMN_CATEGORY_TIME + " TEXT NOT NULL);";

        db.execSQL(categoryTable);
    }
    public void noteTable(SQLiteDatabase db) {


        String note_Table = "CREATE TABLE " + NoteEntry.TABLE_NAME +" (" + NoteEntry.COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+
                NoteEntry.COLUMN_NOTE_TITLE+" TEXT NOT NULL, "+
                NoteEntry.COLUMN_NOTE_BODY+ " TEXT NOT NULL, "+
                NoteEntry.COLUMN_NOTE_DATE+ " TEXT NOT NULL, "+
                NoteEntry.COLUMN_NOTE_TIME+" TEXT NOT NULL, "+
                NoteEntry.COLUMN_CATEGORY_ID+" INTEGER NOT NULL, " +
                "FOREIGN KEY ("+NoteEntry.COLUMN_CATEGORY_ID+") REFERENCES "+CategoryEntry.TABLE_NAME +"("+CategoryEntry._ID+") );";

        db.execSQL(note_Table);
    }
}
