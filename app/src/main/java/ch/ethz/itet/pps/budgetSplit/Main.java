package ch.ethz.itet.pps.budgetSplit;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


public class Main extends ActionBarActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REQUEST_LOAD_PROJECT = 1;
    public static final int RESULT_PROJECT_DELETED = 1;
    private static final int REQUEST_CREATE_PROJECT = 2;
    //Id to identify different Loaders
    private static final int URL_LOADER_PROJECTS = 0;

    //Adapter to fill the listview with Data
    private SimpleCursorAdapter simpleCursorAdapter;


    //Inicializing the button and listview (local inicialization in onCreate produced a crash)
    private Button addProjectButton;
    private ListView listView;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        {
            switch (requestCode) {
                case REQUEST_LOAD_PROJECT:
                    switch (resultCode) {
                        case RESULT_PROJECT_DELETED:
                            Toast.makeText(this, getString(R.string.project_was_deleted_message), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case REQUEST_CREATE_PROJECT:
                    Toast.makeText(this, getString(R.string.new_project_created), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Add Listeners to GUI-Elements
        addProjectButton = (Button) findViewById(R.id.buttonAddProject);

        listView = (ListView) findViewById(R.id.listViewProjects);

        // Implement Clickable Listview Items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent overwiewIntent = new Intent(Main.this, ProjectNavigation.class);

                // get project Uri
                Cursor projectCursor = (Cursor) adapterView.getItemAtPosition(position);
                if (projectCursor.getCount() < 1) {
                    throw new IllegalArgumentException("No project does exist.");
                }
                long projectId = projectCursor.getLong(projectCursor.getColumnIndex(budgetSplitContract.projectsDetailsRO._ID));
                String projectTitle = projectCursor.getString(projectCursor.getColumnIndex(budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_NAME));
                Uri projectUri = ContentUris.withAppendedId(budgetSplitContract.projectsDetailsRO.CONTENT_URI, projectId);
                // query for project name

                overwiewIntent.putExtra(ProjectNavigation.EXTRA_CONTENT_URI, projectUri);
                overwiewIntent.putExtra(ProjectNavigation.EXTRA_PROJECT_TITLE, projectTitle);
                startActivityForResult(overwiewIntent, REQUEST_LOAD_PROJECT);
            }
        });


        //Implementing on click function of "addProjectButton"
        addProjectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intentAddProject = new Intent(Main.this, NewProject.class);
                startActivityForResult(intentAddProject, REQUEST_LOAD_PROJECT);
            }
        });


        //Initialize Loader
        getLoaderManager().initLoader(URL_LOADER_PROJECTS, null, this);

        //Create empty Cursor Adapter and attach it to the listview
        String[] fromColumns = {budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_NAME, budgetSplitContract.projectsDetailsRO.COLUMN_NR_OF_PARTICIPANTS};
        int[] toViews = {R.id.projectName, R.id.nrOfParticipants};
        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.activity_main_projectlist_row, null, fromColumns, toViews, 0);
        listView.setAdapter(simpleCursorAdapter);


        // Starting First Screen

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.contains(getString(R.string.pref_not_first_started))) {
            Intent firstScreenIntent = new Intent(this, FirstScreen.class);
            startActivityForResult(firstScreenIntent, 0);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.mainScreenContacts:
                Intent contactIntent = new Intent(Main.this, ContactsList.class);
                startActivity(contactIntent);
                break;

            case R.id.Tags:
                Intent tagIntent = new Intent(Main.this, Tags.class);
                startActivity(tagIntent);
                break;

            case R.id.MainScreenSettings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAddProject:
                Intent intentAddProject = new Intent("android.intent.action.NEWPROJECT");
                startActivity(intentAddProject);
                break;


        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Show progressbar to display while loading the projects
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.projectsLoadProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        switch (i) {
            case URL_LOADER_PROJECTS:
                return new CursorLoader(getApplicationContext(), budgetSplitContract.projectsDetailsRO.CONTENT_URI, budgetSplitContract.projectsDetailsRO.PROJECTION_ALL, null, null, null);
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
        findViewById(R.id.projectsLoadProgressBar).setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        switch (cursorLoader.getId()) {
            case URL_LOADER_PROJECTS:
                simpleCursorAdapter.changeCursor(null);
        }
    }
}
