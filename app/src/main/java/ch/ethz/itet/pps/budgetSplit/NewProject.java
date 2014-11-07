package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.util.Log;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;

public class NewProject extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    //declarations:

    Button createNewProject;
    Button createNewContact;
    Spinner contactsSpinner;
    ListView contactsProjectList;
    Cursor contactsCursor;
    ArrayAdapter<String> contactsSpinnerAdapter;
    ArrayAdapter<String> contactsListAdapter;
    EditText projectName;
    EditText projectDescription;
    ArrayList<String> participantNamestoList = new ArrayList<String>();
    ArrayList<String> participantNamestoSpinner = new ArrayList<String>();
    Uri projectUri;

    static final int REQUEST_CREATE_CONTACT = 1;


    AdapterView.OnItemSelectedListener onSpinner = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            //Ignore first Selection
            if (position == 0) {
            } else {
                String participantHelper = (String) parent.getItemAtPosition(position);
                // Remove Name from SpinnerList and add it to ListViewList
                participantNamestoList.add(participantHelper);
                participantNamestoSpinner.remove(participantHelper);
                // Ajust ID Memory
                long selectedId = GlobalStuffHelper.popParticipantIds(position - 1);
                GlobalStuffHelper.addParticipantIdsToProject(selectedId);

                //
                updateContactsList();
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

        //Initialize Loader and set items on Spinner
        getLoaderManager().initLoader(0, null, this);

        contactsProjectList = (ListView) findViewById(R.id.activity_new_project_listView2);

        participantNamestoSpinner.add("+ Contacts");

        // Initialization
        setButtons();

        // Initialize Clickable Listview
        contactsProjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // get project Uri
                String transferName = (String) adapterView.getItemAtPosition(position);
                // Ajust ID Memory
                Long transferId = GlobalStuffHelper.popParticipantIdsToProject(position);
                GlobalStuffHelper.addParticipantsIds(transferId);

                // Remove Name from ListViewList and add it to SpinnerList
                contactsListAdapter.remove(transferName);
                contactsSpinnerAdapter.add(transferName);

                // Update
                updateContactsList();
                updateContactsSpinner();

            }
        });


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CREATE_CONTACT:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, getString(R.string.contact_successfully_created), Toast.LENGTH_SHORT).show();
                }
                break;
        }
        // was passiert wenn contact richtig erstellt wurde
    }

    public void setButtons() {

        // initialize actuall button
        createNewProject = (Button) findViewById(R.id.button2);
        // initialize input TextViews
        projectName = (EditText) findViewById(R.id.editText);
        projectDescription = (EditText) findViewById(R.id.editText2);


        createNewProject.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                // Do not create a Nameless Project
                if (projectName.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.name_your_project), Toast.LENGTH_SHORT).show();
                } else {


                    // load your own Information for the project owner data

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                    long adminId = preferences.getLong(getString(R.string.pref_user_id), -1);


                    // create new Project in Database
                    ContentValues projectValues = new ContentValues();
                    projectValues.put(budgetSplitContract.projects.COLUMN_PROJECT_NAME, projectName.getText().toString());
                    projectValues.put(budgetSplitContract.projects.COLUMN_PROJECT_DESCRIPTION, projectDescription.getText().toString());
                    projectValues.put(budgetSplitContract.projects.COLUMN_PROJECT_OWNER, adminId);
                    projectUri = getContentResolver().insert(budgetSplitContract.projects.CONTENT_URI, projectValues);


                    // create Junktions in Database

                    long projectId = ContentUris.parseId(projectUri);
                    ContentValues junktionValues = new ContentValues();

                    while (GlobalStuffHelper.sizeParticipantIdsToProject() > 0) {
                        junktionValues.put(budgetSplitContract.projectParticipants.COLUMN_PROJECTS_ID, projectId);
                        junktionValues.put(budgetSplitContract.projectParticipants.COLUMN_PARTICIPANTS_ID, GlobalStuffHelper.popParticipantIdsToProject(0));
                        getContentResolver().insert(budgetSplitContract.projectParticipants.CONTENT_URI, junktionValues);
                        junktionValues.clear();
                    }

                    SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    long id = preferences1.getLong(getString(R.string.pref_user_id), -1);

                    junktionValues.put(budgetSplitContract.projectParticipants.COLUMN_PROJECTS_ID, projectId);
                    junktionValues.put(budgetSplitContract.projectParticipants.COLUMN_PARTICIPANTS_ID, id);
                    getContentResolver().insert(budgetSplitContract.projectParticipants.CONTENT_URI, junktionValues);


                    finish();
                }


            }

        });


        // Button for Virtual Contacts
        createNewContact = (Button) findViewById(R.id.activity_new_project_create_new_contact_button);

        createNewContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent intentNewContact = new Intent(NewProject.this, NewContact.class);
                startActivityForResult(intentNewContact, REQUEST_CREATE_CONTACT);


            }
        });
    }

    /**
     * Fills the Spinner with the Contacts saved in the database
     */
    public void setItemsOnSpinner() {
        // Link Spinner
        contactsSpinner = (Spinner) findViewById(R.id.contacts_spinner);


        // Load all the Contact Names from Database creating cursorAdapter
        String[] participantNames = {budgetSplitContract.participants.COLUMN_NAME};
        contactsCursor = getContentResolver().query(budgetSplitContract.participants.CONTENT_URI, participantNames, null, null, null);


        if (contactsCursor.getCount() > 0) {


            for (contactsCursor.moveToFirst(); !contactsCursor.isAfterLast(); contactsCursor.moveToNext()) {
                if (contactsCursor != null) {
                    participantNamestoSpinner.add(contactsCursor.getString(contactsCursor.getColumnIndex(budgetSplitContract.participants.COLUMN_NAME)));
                }
            }

            // Remove yourself from Spinner
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String adminName = preferences.getString(getString(R.string.pref_userName), "Error");
            participantNamestoSpinner.remove(adminName);

        }


        contactsSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.activity_new_poject_contacts_spinner_row, R.id.contacts_spinner_row_textview, participantNamestoSpinner);
        contactsSpinnerAdapter.setDropDownViewResource(R.layout.activity_new_poject_contacts_spinner_row);
        contactsSpinner.setAdapter(contactsSpinnerAdapter);

        // Link to onItemsSelected Method
        contactsSpinner.setOnItemSelectedListener(onSpinner);


    }

    public void updateContactsList() {


        // fill the list with the chosen names
        if (participantNamestoList.size() > 0) {

            contactsListAdapter = new ArrayAdapter<String>(this, R.layout.activity_new_project_contacts_list, R.id.projectName, participantNamestoList);
            contactsProjectList.setAdapter(contactsListAdapter);
        }


    }

    // Call this method from Listview
    public void updateContactsSpinner() {


        if (participantNamestoSpinner.size() > 0) {
            contactsSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.activity_new_poject_contacts_spinner_row, R.id.contacts_spinner_row_textview, participantNamestoSpinner);

            contactsSpinner.setAdapter(contactsSpinnerAdapter);
        }


    }

    // Call this method after new Contact has been inserted into database
    public void updateContactsSpinner2() {

        //Initialize Loader
        getLoaderManager().initLoader(0, null, this);

        // Load all the Contact Names from Database creating cursorAdapter
        String[] participantNames = {budgetSplitContract.participants.COLUMN_NAME};
        contactsCursor = getContentResolver().query(budgetSplitContract.participants.CONTENT_URI, participantNames, null, null, null);

        if (contactsCursor.getColumnCount() > 0) {
            participantNamestoSpinner.clear();
            contactsCursor.moveToFirst();

            for (contactsCursor.moveToFirst(); contactsCursor.isAfterLast(); contactsCursor.moveToNext()) {
                if (contactsCursor != null) {
                    participantNamestoSpinner.add(contactsCursor.getString(contactsCursor.getColumnIndex(budgetSplitContract.participants.COLUMN_NAME)));
                }
            }

            if (participantNamestoList.size() > 0) {
                for (int i = 0; i < participantNamestoList.size(); i++) {
                    participantNamestoSpinner.remove(participantNamestoList.get(i));
                }
            }
        }


        contactsSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.activity_new_poject_contacts_spinner_row, R.id.contacts_spinner_row_textview, participantNamestoSpinner);
        if (contactsSpinnerAdapter != null)
            contactsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contactsSpinner.setAdapter(contactsSpinnerAdapter);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.new_projects_progressBar);
        progressBar.setVisibility(View.VISIBLE);
        // Clear Old Arrays
        GlobalStuffHelper.clearListArray();
        GlobalStuffHelper.clearSpinnerArray();
        // Load Data
        String[] projection = {budgetSplitContract.participants._ID, budgetSplitContract.participants.COLUMN_NAME};
        return new CursorLoader(getApplicationContext(), budgetSplitContract.participants.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        //contactsSpinnerAdapter.swapCursor(cursor);
        // Hide Progress Bar
        ((ProgressBar) findViewById(R.id.new_projects_progressBar)).setVisibility(View.GONE);

        // Link Spinner
        contactsSpinner = (Spinner) findViewById(R.id.contacts_spinner);

        // Clear Old Arrays
        GlobalStuffHelper.clearSpinnerArray();
        participantNamestoSpinner.clear();
        participantNamestoSpinner.add("+ Contact");

        if (cursor.getCount() > 0) {

            // Refill spinner and Id List
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                if (cursor != null) {
                    participantNamestoSpinner.add(cursor.getString(cursor.getColumnIndex(budgetSplitContract.participants.COLUMN_NAME)));
                    GlobalStuffHelper.addParticipantsIds(cursor.getLong(cursor.getColumnIndex(budgetSplitContract.participants._ID)));
                }
            }

            // Remove yourself from Spinner
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String adminName = preferences.getString(getString(R.string.pref_userName), "Error");
            participantNamestoSpinner.remove(adminName);
            // and from Id Memory
            long id = preferences.getLong(getString(R.string.pref_user_id), -1);
            GlobalStuffHelper.popParticipantIds(id);


        }

        // Remove List Items from Spinner and ID Memory


        if (participantNamestoList.size() > 0 && participantNamestoSpinner.size() > 0) {
            for (int i = 0; i < participantNamestoList.size(); i++) {
                participantNamestoSpinner.remove(participantNamestoList.get(i));
                GlobalStuffHelper.popParticipantIds(GlobalStuffHelper.getParticipantIdsToProject(i));
            }
        }

        contactsSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.activity_new_poject_contacts_spinner_row, R.id.contacts_spinner_row_textview, participantNamestoSpinner);
        contactsSpinnerAdapter.setDropDownViewResource(R.layout.activity_new_poject_contacts_spinner_row);
        contactsSpinner.setAdapter(contactsSpinnerAdapter);

        // Link to onItemsSelected Method
        contactsSpinner.setOnItemSelectedListener(onSpinner);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        //contactsCursorAdapter.changeCursor(null);

    }

    public void onRestart() {
        super.onRestart();
        //updateContactsSpinner2();
    }

    public void onResume() {
        super.onResume();
        //updateContactsSpinner2();
    }

    // public void onClick(View context){
    //  ((ListView)context.getParent().
    //  }
}
