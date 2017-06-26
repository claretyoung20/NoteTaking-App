package com.android.sample.notetakingapp;


import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.sample.notetakingapp.dataModel.NoteContract.NoteEntry;

/**
 * Created by Young Claret on 6/17/2017.
 */

public class NoteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    NoteCursorAdapter noteCursorAdapter;
    private static final int NOTE_LOADER = 1;
    //sharedpreferences key
    public static final String Name = "nameKey";
    int category_ID = 0;

    private ListView listView;

    // Define a new interface OnImageClickListener that triggers a callback in the host activity
    OnItemClickListener mCallback;

    // OnImageClickListener interface, calls a method in the host activity named onImageSelected
    public interface OnItemClickListener {
        void onImageSelected(Uri path);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }

    public NoteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewLayout = inflater.inflate(R.layout.fragment_container,container,false);
         listView = (ListView) viewLayout.findViewById(R.id.list_note_view);

        noteCursorAdapter = new NoteCursorAdapter(getContext(), null);
        listView.setAdapter(noteCursorAdapter);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        category_ID = sharedPreferences.getInt(Name,0) ;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), NoteEditor.class);
                // Form the content URI that represents the specific Note that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link NoteEntry#CONTENT_URI}.
                // For example, the URI would be "com.android.sample.notetakingapp/notes/2"
                // if the note with ID 2 was clicked on.
                Uri currentNoteUri = ContentUris.withAppendedId(NoteEntry.NOTE_CONTENT_URI,id);

              mCallback.onImageSelected(currentNoteUri);
            }
        });

        listView.setEnabled(true);

        getLoaderManager().initLoader(NOTE_LOADER, null,this);

        // Inflate the layout for this fragment
        return viewLayout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
