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
 * Created by Young Claret on 6/16/2017.
 */

public class CateCursoAdapter extends CursorAdapter {

    public CateCursoAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.category_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView cateName = (TextView) view.findViewById(R.id.cateName);

        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndex(NoteContract.CategoryEntry.COLUMN_CATEGORY_NAME));

        //set the values to Text View
        //value for title
        cateName.setText(name);
    }
}
