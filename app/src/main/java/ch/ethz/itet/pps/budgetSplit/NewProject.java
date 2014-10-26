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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;

public class NewProject extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    //declarations:

    Button createNewProject;
    Button createNewContact;
    Spinner contactsSpinner;
    ListView contactsProjectList;
    Cursor adminCursor;
    SimpleCursorAdapter contactsCursorAdapter;
    EditText projectName;
    EditText projectDescription;
    ArrayList<String> participantNamestoList = new ArrayList<String>();
    ArrayList<Integer> participantIds = new ArrayList<Integer>();
    Uri projectUri;


    AdapterView.OnItemSelectedListener onSpinner =   new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            Cursor participantHelper = (Cursor) parent.getItemAtPosition(position);

            participantNamestoList.add(participantHelper.getString(participantHelper.getColumnIndexOrThrow(budgetSplitContract.participants.COLUMN_NAME)));
            participantIds.add(participantHelper.getInt(participantHelper.getColumnIndexOrThrow(budgetSplitContract.participants._ID)));
            updateContactsList();
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

        contactsProjectList = (ListView) findViewById(R.id.activity_new_project_listView2);

        //participantNamestoList.add("Chrissy");

        setItemsOnSpinner();
        setButtons();


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

    public void setButtons() {

        // initialize actuall button
        createNewProject = (Button) findViewById(R.id.button2);
        // initialize input TextViews
        projectName = (EditText) findViewById(R.id.editText);
        projectDescription = (EditText) findViewById(R.id.editText2);
       

        createNewProject.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                if (projectName.getText() != null) {


                    // load your own Information for the project owner data
                    String[] adminProjection = {budgetSplitContract.participants._ID};
                    adminCursor = getContentResolver().query(GlobalStuffHelper.getUriAtPosition(0), adminProjection, null, null, null);
                    adminCursor.getColumnName(0);

                    // create new Project in Database
                    ContentValues projectValues = new ContentValues();
                    projectValues.put(budgetSplitContract.projects.COLUMN_PROJECT_NAME, projectName.getText().toString());
                    projectValues.put(budgetSplitContract.projects.COLUMN_PROJECT_DESCRIPTION, projectDescription.getText().toString());
                    projectValues.put(budgetSplitContract.projects.COLUMN_PROJECT_OWNER, adminCursor.getColumnName(0));
                    projectUri = getContentResolver().insert(budgetSplitContract.projects.CONTENT_URI, projectValues);

                    //save the uri in a Global Array for the main Listview
                    GlobalStuffHelper.addUri(projectUri);

                    Intent intentNewProject = new Intent(NewProject.this, Main.class);
                    startActivity(intentNewProject);
                }

            }

        });
        createNewContact = (Button) findViewById(R.id.activity_new_project_create_new_contact_button);

        createNewContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent intentNewContact = new Intent(NewProject.this, NewContact.class);
                startActivity(intentNewContact);

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
        contactsCursorAdapter = new SimpleCursorAdapter(this, R.layout.activity_new_poject_contacts_spinner_row, null, participantNames, toSpinner, 0);
        contactsSpinner.setAdapter(contactsCursorAdapter);

        // Link to onItemsSelected Method
        contactsSpinner.setOnItemSelectedListener(onSpinner);


    }

    public void updateContactsList() {


        // fill the list with the chosen names
        if (participantNamestoList.size() > 0) {

            ArrayAdapter<String> contactListAdapter = new ArrayAdapter<String>(this, R.layout.activity_new_project_contacts_list, R.id.projectName, participantNamestoList);
            //contactListAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
            contactsProjectList.setAdapter(contactListAdapter);
        }


    }

    ;

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.new_projects_progressBar);
        progressBar.setVisibility(View.VISIBLE);
        String[] projection = {budgetSplitContract.participants._ID, budgetSplitContract.participants.COLUMN_NAME};
        return new CursorLoader(getApplicationContext(), budgetSplitContract.participants.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        contactsCursorAdapter.swapCursor(cursor);
        // Hide Progress Bar
        ((ProgressBar) findViewById(R.id.new_projects_progressBar)).setVisibility(View.GONE);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        contactsCursorAdapter.changeCursor(null);

    }

    // public void onClick(View context){
    //  ((ListView)context.getParent().
    //  }
}
