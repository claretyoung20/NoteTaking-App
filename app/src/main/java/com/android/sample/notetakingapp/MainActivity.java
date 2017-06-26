package com.android.sample.notetakingapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sample.notetakingapp.dataModel.NoteContract.CategoryEntry;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>,NoteFragment.OnItemClickListener {


    NavigationView navigationView;
    TextView manageCat;
    Menu m;

    //declare SharedPreferences
    SharedPreferences sharedpreferences;
    //constant values for sharedpreferences name and key
    public static final String Name = "nameKey";

    private static final int NOTE_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        manageCat = (TextView) findViewById(R.id.magageCate);
        manageCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ManageCategory.class);
                startActivity(intent);
            }
        });
        /*---------------*/

        m = navigationView.getMenu();
        m.clear();

        //sharedpreferences
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // Begin the transaction
        FragmentTransaction fTraction = getSupportFragmentManager().beginTransaction();
        //Declare the Fragment class
        NoteFragment noteFragments =  new NoteFragment();

        // Replace the contents of the container with the new fragment
        fTraction.replace(R.id.notesFragment, noteFragments);
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        fTraction.commit();

        //getContentResolver().delete(NoteEntry.NOTE_CONTENT_URI,null,null);
        getLoaderManager().initLoader(NOTE_LOADER, null, this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            Intent intent = new Intent(this, NoteEditor.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int catId = 1;
        String name = "";

        // Handle navigation view item clicks here.
        int id = item.getItemId();

//         Define a projection that specifies which columns from the database
//         you will actually use after this query.
        String[] projection = {
                CategoryEntry._ID,
                CategoryEntry.COLUMN_CATEGORY_NAME
        };
        Cursor cursors = getContentResolver().query(CategoryEntry.CATEGORY_CONTENT_URI, projection, null, null, null);
        while (cursors.moveToNext()) {
            name = cursors.getString(cursors.getColumnIndex(CategoryEntry.COLUMN_CATEGORY_NAME));
            catId = cursors.getInt(cursors.getColumnIndex(CategoryEntry._ID));

            // Begin the transaction
            FragmentTransaction ft =    getSupportFragmentManager().beginTransaction();
            //Declare the Fragment class
            NoteFragment noteFragment =  new NoteFragment();

            if (id == catId) {
                Toast.makeText(this, name + ": " + catId, Toast.LENGTH_LONG).show();

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(Name,catId);
                editor.commit();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.notesFragment, noteFragment);
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void DummyText() {
        //category Name
        String categoryName = "ToDo";

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


        String title = "Day 1";
        String body = "";

        Log.v("MainActivity", "DATE AND TIME FOR TODAY:  " + categoryName + " " + dateString + " " + timeString);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                CategoryEntry._ID,
                CategoryEntry.COLUMN_CATEGORY_NAME
        };

        Log.v("EditorActivity", "onCreateLoader has been called");

        return new CursorLoader(this, CategoryEntry.CATEGORY_CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        /*---------Manually adding menu---------*/
        while (data.moveToNext()) {
            String cateName = data.getString(data.getColumnIndex(CategoryEntry.COLUMN_CATEGORY_NAME));
            int cateID = data.getInt(data.getColumnIndex(CategoryEntry._ID));

            Log.v("MainActivity", "CATEGORY NAME AND ID :  " + cateName + " " + cateID);

            MenuItem menuItem = m.add(R.id.groupID, cateID, cateID, cateName);
            menuItem.setIcon(R.drawable.ic_menu_folder);
        }
        Log.v("EditorActivity", "onLoadFinished has been called");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getLoaderManager().restartLoader(0, null, this);
        m.clear();
        Log.v("EditorActivity", "onLoaderReset has been called");
    }

    @Override
    public void onImageSelected(Uri path) {
        Intent intent = new Intent(this,NoteEditor.class);
        intent.setData(path);
        startActivity(intent);
    }
}
