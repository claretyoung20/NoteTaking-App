package com.android.sample.notetakingapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sample.notetakingapp.dataModel.NoteContract.NoteEntry;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteEditor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText title, body;

    public String shareTitle = null;
    public String shareText = "";
    /**
     * Content URI for the existing Note (null if it's a new Note)
     */
    private Uri mCurrentNoteUri;

    int categoryID = 1;
    /**
     * Boolean flag that keeps track of whether the Note has been edited (true) or not (false)
     */
    private boolean mNoteHasChanged = false;

    private final int EXISTING_NOTE_LOADER = 0;

    /** sharedpreferences key */
    public static final String Name = "nameKey";

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mCurrentNoteUri boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mNoteHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        //get haredpreferences key
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        categoryID = sharedPreferences.getInt(Name,1) ;

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new Note or editing an existing one.
        Intent intent = getIntent();
        mCurrentNoteUri = intent.getData();
        // If the intent DOES NOT contain a Note content URI, then we know that we are
        // creating a new Note.
        if (mCurrentNoteUri == null) {

            // This is a new Note, so change the app bar to say "Add a Note"
            setTitle("Add Note");

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a Note that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing Note, so change app bar to say "Edit Note"
            setTitle("Edit Note");

            // Initialize a loader to read the Note data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_NOTE_LOADER, null, this);
        }

        title = (EditText) findViewById(R.id.noteTitle);
        body = (EditText) findViewById(R.id.noteContent);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        title.setOnTouchListener(mTouchListener);
        body.setOnTouchListener(mTouchListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                NoteEntry.COLUMN_NOTE_TITLE,
                NoteEntry.COLUMN_NOTE_BODY,
                NoteEntry.COLUMN_NOTE_DATE,
                NoteEntry.COLUMN_NOTE_TIME
        };

        //this loader will execute the ContentProvider's query on a background thread
        return new CursorLoader(this, mCurrentNoteUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //if there is no data
        if (data == null || data.getCount() < 0) {
            return;
        }

        if (data.moveToFirst()) {
            String nTitle = data.getString(data.getColumnIndex(NoteEntry.COLUMN_NOTE_TITLE));
            String nBody = data.getString(data.getColumnIndex(NoteEntry.COLUMN_NOTE_BODY));

            body.setText(nBody);
            title.setText(nTitle);
            shareTitle = nTitle;
            shareText = nBody;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        body.setText("");
        title.setText("");
    }

    private void showUnSaveDialog(DialogInterface.OnClickListener discardChanges) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discardChages);
        builder.setNegativeButton("Discard", discardChanges);
        builder.setPositiveButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the Notes.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the Note hasn't changed, continue with handling back button press
        if (!mNoteHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.

        DialogInterface.OnClickListener discardChages = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };

        showUnSaveDialog(discardChages);
    }

    /**
     * Check if the user want to enter new note and disable the share detail and delete menu
     **/
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentNoteUri == null) {
            MenuItem menuItem = menu.findItem(R.id.deleteNote);
            menuItem.setVisible(false);
            MenuItem menuItem2 = menu.findItem(R.id.shareNote);
            menuItem2.setVisible(false);
            MenuItem menuItem3 = menu.findItem(R.id.noteDetail);
            menuItem3.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addNote) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.saveNote:
                //do something
                insertNote();
                finish();
                return true;

            case R.id.shareNote:
                shareQuote();
                return true;

            case R.id.deleteNote:
                deleteDialog();
                return true;

            case R.id.noteDetail:
                noteDetail();
                return true;
            case android.R.id.home:
                // If the Note hasn't changed, continue with navigating up to parent activity
                // which is the {@link MainActivity}.
                if (!mNoteHasChanged) {
                    NavUtils.navigateUpFromSameTask(NoteEditor.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded
                DialogInterface.OnClickListener discardChanges = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(NoteEditor.this);
                    }
                };

                showUnSaveDialog(discardChanges);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertNote() {
        //get the user input
        String noteTitle = title.getText().toString().trim();
        String noteContent = body.getText().toString().trim();


        //validate user input
        if (TextUtils.isEmpty(noteContent) || TextUtils.isEmpty(noteTitle)) {
            return;
        }

        //get current Date
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy");
        Date currentTime_1 = new Date();
        String dateString = formatter.format(currentTime_1);

        //get current Time
        SimpleDateFormat formatterTime = new SimpleDateFormat("h:mm a");
        String timeString = formatterTime.format(currentTime_1);

        ContentValues values = new ContentValues();
        values.put(NoteEntry.COLUMN_NOTE_TITLE, noteTitle);
        values.put(NoteEntry.COLUMN_NOTE_BODY, noteContent);
        values.put(NoteEntry.COLUMN_NOTE_DATE, dateString);
        values.put(NoteEntry.COLUMN_NOTE_TIME, timeString);
        values.put(NoteEntry.COLUMN_CATEGORY_ID, categoryID);

        //check if it is new note or existing note
        if (mCurrentNoteUri == null) {
            // Insert the new row, returning the primary key value of the new row
            Uri uri = getContentResolver().insert(NoteEntry.NOTE_CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (uri == null) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, "Error with saving note", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast with the row ID.
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
            }

        } else {
            //update the Note data
            int affectedRows = getContentResolver().update(mCurrentNoteUri, values, null, null);

            if (affectedRows == 0) {
                // If the affected rows is, then there was an error with updating the Note table.
                Toast.makeText(this, "Error with Updating Note", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast message.
                Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this Note");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the Note
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNote();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteNote() {

        //if the it an existing note
        if (mCurrentNoteUri != null) {
            int rowAffected = getContentResolver().delete(mCurrentNoteUri, null, null);

            //check if the deletion was deleteDialog
            if (rowAffected == 0) {
                Toast.makeText(this, "Delete Unsuccessful", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Delete deleteDialog", Toast.LENGTH_LONG).show();
            }
        }

        finish();
    }

    private void noteDetail() {
        String nTitle = "", nBody = "", nDate = "", nTime = "";
        if (mCurrentNoteUri != null) {
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    NoteEntry.COLUMN_NOTE_TITLE,
                    NoteEntry.COLUMN_NOTE_BODY,
                    NoteEntry.COLUMN_NOTE_DATE,
                    NoteEntry.COLUMN_NOTE_TIME
            };
            Cursor cursor = getContentResolver().query(mCurrentNoteUri, projection, null, null, null);
            if (cursor.moveToFirst()) {
                nTitle += cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_NOTE_TITLE));
                nBody += cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_NOTE_BODY));
                nDate += cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_NOTE_DATE));
                nTime += cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_NOTE_TIME));
            }

            // Get the layout inflater
            LayoutInflater inflater = this.getLayoutInflater();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View viewBuilder = inflater.inflate(R.layout.note_detail, null);
            builder.setView(viewBuilder);

            TextView header = (TextView) viewBuilder.findViewById(R.id.nTitle);
            TextView noteTitle = (TextView) viewBuilder.findViewById(R.id.txtTiles);
            TextView noteDate = (TextView) viewBuilder.findViewById(R.id.txtDate);
            TextView noteTime = (TextView) viewBuilder.findViewById(R.id.txtTime);
            TextView noteLength = (TextView) viewBuilder.findViewById(R.id.txtNum);

            header.setText(nTitle + " Details");
            noteTitle.setText(nTitle);
            noteDate.setText(nDate);
            noteTime.setText(nTime);
            int bLen = nBody.length();
            noteLength.setText(Integer.toString(bLen));


            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void shareQuote(){
        if(shareText != null) {
            String M_TYPE = "text/plain";
            String tTile = "Share this note via..";
            ShareCompat.IntentBuilder.from(this).setChooserTitle(tTile).setType(M_TYPE).setText(shareTitle +"\n"+ shareText).startChooser();
        }
    }

}
