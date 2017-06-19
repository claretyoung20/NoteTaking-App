package com.android.sample.notetakingapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.android.sample.notetakingapp.dataModel.NoteContract;

/**
 * Created by Young Claret on 6/14/2017.
 */

public class NoteCursorAdapter extends CursorAdapter {

    public NoteCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_note_view, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView title = (TextView) view.findViewById(R.id.textvi);
        TextView body = (TextView) view.findViewById(R.id.txtContent);
        TextView date = (TextView) view.findViewById(R.id.txtDate);

        // Extract properties from cursor
        String nTitle = cursor.getString(cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TITLE));
        String nBody = cursor.getString(cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_BODY));
        String nDate = cursor.getString(cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_DATE));

        //set the values to Text View
        //value for title
        title.setText(nTitle);

        //if the length of the body content is less than 30
        String content = "";
        if (nBody.length() < 30) {
            //show all the contect
            body.setText(nBody);
        }else{
            content = nBody.substring(0, 26) + "...";
            body.setText(content);
        }

        //if the length of the body content is greater than 30
        //show only from some words

        //set date
        date.setText(nDate);
    }

}
