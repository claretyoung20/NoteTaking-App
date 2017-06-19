package com.android.sample.notetakingapp;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.sample.notetakingapp.dataModel.NoteContract.NoteEntry;

/**
 * Created by Young Claret on 6/17/2017.
 */

public class NoteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    NoteCursorAdapter noteCursorAdapter;
    private static final int NOTE_LOADER = 1;

    int category_ID = 0;

    public NoteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewLayout = inflater.inflate(R.layout.fragment_container,container,false);
        ListView listView = (ListView) viewLayout.findViewById(R.id.list_note_view);

        noteCursorAdapter = new NoteCursorAdapter(getContext(), null);
        listView.setAdapter(noteCursorAdapter);

        Bundle bundle = this.getArguments();
        category_ID = bundle.getInt(NoteEntry.COLUMN_CATEGORY_ID);

        getLoaderManager().initLoader(NOTE_LOADER, null,this);
        // Inflate the layout for this fragment
        return viewLayout;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                NoteEntry.COLUMN_NOTE_ID,
                NoteEntry.COLUMN_NOTE_BODY,
                NoteEntry.COLUMN_NOTE_TITLE,
                NoteEntry.COLUMN_NOTE_DATE,
                NoteEntry.COLUMN_NOTE_TIME,
                NoteEntry.COLUMN_CATEGORY_ID
        };

        if(category_ID == 0){
            return new CursorLoader(getContext(),NoteEntry.NOTE_CONTENT_URI,projection,null,null,null);
        }else{
            String selection = NoteEntry.COLUMN_CATEGORY_ID +"=?";
            String[] selectionArg = new String[]{String.valueOf(category_ID)};
            return new CursorLoader(getContext(),NoteEntry.NOTE_CONTENT_URI,projection,selection,selectionArg,null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 0) {
            return;
        }
        noteCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        noteCursorAdapter.swapCursor(null);
    }


}
