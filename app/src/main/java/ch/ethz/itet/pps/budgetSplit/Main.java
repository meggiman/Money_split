package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


public class Main extends Activity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    //Id to identify different Loaders
    private static final int URL_LOADER_PROJECTS = 0;

    //Adapter to fill the listview with Data
    SimpleCursorAdapter simpleCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add Listeners to GUI-Elements
        Button addProjectButton = (Button) findViewById(R.id.buttonAddProject);
        addProjectButton.setOnClickListener(this);
        ListView listView = (ListView) findViewById(R.id.listViewProjects);

        //Initialize Loader
        getLoaderManager().initLoader(URL_LOADER_PROJECTS, null, this);

        //Create empty Cursor Adapter and attach it to the listview
        String[] fromColumns = {budgetSplitContract.projects.COLUMN_PROJECT_NAME, budgetSplitContract.projects.COLUMN_PROJECT_OWNER};
        int[] toViews = {R.id.projectName, R.id.projectowner};
        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.activity_main_projectlist_row, null, fromColumns, toViews, 0);
        listView.setAdapter(simpleCursorAdapter);

        //Debug
        ContentValues participant = new ContentValues();
        participant.put(budgetSplitContract.participants.COLUMN_NAME, "Manuel Eggimann");
        participant.put(budgetSplitContract.participants.COLUMN_ISVIRTUAL, true);
        participant.put(budgetSplitContract.participants.COLUMN_UNIQUEID, "manuel.eggimann@gmail.com");
        Uri newparticipanturi;
        try {
            newparticipanturi = getContentResolver().insert(budgetSplitContract.participants.CONTENT_URI, participant);
        } catch (SQLiteConstraintException e) {
            //Value already exists.
            return;
        }
        Cursor cursor = getContentResolver().query(newparticipanturi, new String[]{budgetSplitContract.participants._ID}, null, null, null);

        if (cursor.getColumnCount() > 0) {
            cursor.moveToFirst();
            int id = cursor.getInt(0);

            ContentValues project = new ContentValues();
            project.put(budgetSplitContract.projects.COLUMN_PROJECT_NAME, "Testprojekt");
            project.put(budgetSplitContract.projects.COLUMN_PROJECT_OWNER, id);
            getContentResolver().insert(budgetSplitContract.projects.CONTENT_URI, project);
        }

        String[] projection = {budgetSplitContract.participants.COLUMN_NAME, budgetSplitContract.participants._ID};
        Cursor myresult = getContentResolver().query(budgetSplitContract.participants.CONTENT_URI, projection, null, null, null);
        if (myresult.getColumnCount() > 0) {
            myresult.moveToFirst();
            String name = myresult.getString(0);
            myresult.moveToNext();
            String name2 = myresult.getString(0);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAddProject:

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Show progressbar to display while loading the projects
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.projectsLoadProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        switch (i) {
            case URL_LOADER_PROJECTS:
                return new CursorLoader(getApplicationContext(), budgetSplitContract.projects.CONTENT_URI, budgetSplitContract.projects.PROJECTION_ALL, null, null, null);
            default:
                throw new IllegalArgumentException("The Loader id " + i + " is invalid.");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        switch (cursorLoader.getId()) {
            case URL_LOADER_PROJECTS:
                simpleCursorAdapter.swapCursor(cursor);
                break;
            default:
                throw new IllegalArgumentException("Invalid CursorID " + cursorLoader.getId());
        }
        //Hide Progressbar
        ((ProgressBar) findViewById(R.id.projectsLoadProgressBar)).setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        switch (cursorLoader.getId()) {
            case URL_LOADER_PROJECTS:
                simpleCursorAdapter.changeCursor(null);
        }
    }
}
