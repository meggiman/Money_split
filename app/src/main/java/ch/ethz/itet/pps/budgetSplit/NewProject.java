package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;

public class NewProject extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    //declarations:

    Button createNewProject;
    Spinner contactsSpinner;
    SimpleCursorAdapter contactsCursor;
    Uri fixSpinnerItemsUri;
    Uri fixSpinnerItemsUri2;
    AdapterView.OnItemSelectedListener onSpinner =   new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) ;
            else if (position == 1) {
                Intent intentAddProject = new Intent(NewProject.this, NewContact.class);
                startActivity(intentAddProject);
            } else {
                // Create propper junctions
                // and list chosen participants in Listview
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        // define the first two virtual Contacts as " " and "+ Contacts" --> facilitates Spinner fill
        ContentValues fixSpinnerItems = new ContentValues();
        fixSpinnerItems.put(budgetSplitContract.participants.COLUMN_NAME, " ");
        fixSpinnerItems.put(budgetSplitContract.participants.COLUMN_ISVIRTUAL, true);
        fixSpinnerItemsUri = getContentResolver().insert(budgetSplitContract.participants.CONTENT_URI, fixSpinnerItems);

        ContentValues fixSpinnerItems2 = new ContentValues();
        fixSpinnerItems2.put(budgetSplitContract.participants.COLUMN_NAME, "+ Contacts");
        fixSpinnerItems2.put(budgetSplitContract.participants.COLUMN_ISVIRTUAL, true);
        fixSpinnerItemsUri2 = getContentResolver().insert(budgetSplitContract.participants.CONTENT_URI, fixSpinnerItems2);

        setItemsOnSpinner();
        setButton();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_project, menu);
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

    public void setButton() {
        createNewProject = (Button) findViewById(R.id.button2);
        createNewProject.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                //save new project in database
            }

        });
    }

    /**
     * Fills the Spinner with the Contacts saved in the database and the "+ contact" field
     */
    public void setItemsOnSpinner() {
        // Link Spinner
        contactsSpinner = (Spinner) findViewById(R.id.contacts_spinner);

        //Initialize Loader
        getLoaderManager().initLoader(0, null, this);

        // Load all the Contact Names from Database creating cursorAdapter
        String[] participantNames = {budgetSplitContract.participants.COLUMN_NAME};
        int[] toSpinner = {R.id.contacts_spinner_row_textview};
        contactsCursor = new SimpleCursorAdapter(this, R.layout.activity_new_poject_contacts_spinner_row, null, participantNames, toSpinner, 0);
        contactsSpinner.setAdapter(contactsCursor);

        // Link to onItemsSelected Method
        contactsSpinner.setOnItemSelectedListener(onSpinner);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.new_projects_progressBar);
        progressBar.setVisibility(View.VISIBLE);
        String[] projection = {budgetSplitContract.participants._ID, budgetSplitContract.participants.COLUMN_NAME};
        return new CursorLoader(getApplicationContext(), budgetSplitContract.participants.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        contactsCursor.swapCursor(cursor);
        // Hide Progress Bar
        ((ProgressBar) findViewById(R.id.new_projects_progressBar)).setVisibility(View.GONE);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        contactsCursor.changeCursor(null);

    }
}
