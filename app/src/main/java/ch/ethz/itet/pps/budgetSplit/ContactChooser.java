package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


public class ContactChooser extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    ArrayList<Participant> selectedParticipants;
    static final String EXTRA_SELECTED_PARTICIPANTS = "extraSelectedParticipants";
    static final String RESULT_EXTRA_SELECTED_PARTICIPANTS = "resultExtraParticipants";
    static final int REQUEST_CREATE_CONTACT = 1;

    private static final int LOADER_PARTICIPANTS = 1;


    ListView contactsList;
    CursorAdapter contactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_contact);

        selectedParticipants = getIntent().getParcelableArrayListExtra(EXTRA_SELECTED_PARTICIPANTS);
        if (selectedParticipants == null) {
            throw new IllegalArgumentException("The ArrayList of already selected Participants was not passed as Extra via intent.");
        }

        contactsList = (ListView) findViewById(R.id.listViewContacts);
        String[] from = {budgetSplitContract.participants.COLUMN_NAME};
        int[] to = {android.R.id.text1};
        contactsAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_multiple_choice, null, from, to, 0);
        contactsList.setAdapter(contactsAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLoaderManager().initLoader(LOADER_PARTICIPANTS, null, this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CREATE_CONTACT:
                Toast.makeText(this, getString(R.string.contact_successfully_created), Toast.LENGTH_SHORT).show();
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_save:
                SparseBooleanArray checked = contactsList.getCheckedItemPositions();
                Cursor cursor = contactsAdapter.getCursor();
                cursor.moveToFirst();
                Participant participant = new Participant();
                for (int i = 0; i < cursor.getCount(); i++) {
                    participant.id = cursor.getLong(cursor.getColumnIndex(budgetSplitContract.participants._ID));
                    participant.name = cursor.getString(cursor.getColumnIndex(budgetSplitContract.participants.COLUMN_NAME));
                    participant.uniqueId = cursor.getString(cursor.getColumnIndex(budgetSplitContract.participants.COLUMN_UNIQUEID));
                    if (checked.get(i)) {
                        if (!selectedParticipants.contains(participant)) {
                            selectedParticipants.add(participant);
                            participant = new Participant();
                        }
                    } else {
                        selectedParticipants.remove(participant);
                    }
                    cursor.moveToNext();
                }
                Intent result = new Intent();
                result.putParcelableArrayListExtra(RESULT_EXTRA_SELECTED_PARTICIPANTS, selectedParticipants);
                setResult(RESULT_OK, result);
                finish();
                return true;
            case R.id.action_add_contact:
                checked = contactsList.getCheckedItemPositions();
                cursor = contactsAdapter.getCursor();
                cursor.moveToFirst();
                participant = new Participant();
                for (int i = 0; i < checked.size(); i++) {
                    participant.id = cursor.getLong(cursor.getColumnIndex(budgetSplitContract.participants._ID));
                    participant.name = cursor.getString(cursor.getColumnIndex(budgetSplitContract.participants.COLUMN_NAME));
                    participant.uniqueId = cursor.getString(cursor.getColumnIndex(budgetSplitContract.participants.COLUMN_UNIQUEID));
                    if (checked.valueAt(i)) {
                        if (!selectedParticipants.contains(participant)) {
                            selectedParticipants.add(participant);
                            participant = new Participant();
                        }
                    } else {
                        selectedParticipants.remove(participant);
                    }
                }
                Intent intent = new Intent(this, NewContact.class);
                startActivityForResult(intent, REQUEST_CREATE_CONTACT);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case LOADER_PARTICIPANTS:
                String[] projection = {budgetSplitContract.participants._ID, budgetSplitContract.participants.COLUMN_NAME, budgetSplitContract.participants.COLUMN_UNIQUEID};
                return new CursorLoader(this, budgetSplitContract.participants.CONTENT_URI, projection, null, null, null);
            default:
                throw new IllegalArgumentException("Illegal Loader number: " + i);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        contactsAdapter.swapCursor(cursor);
        Participant participant = new Participant();
        boolean[] checkedList = new boolean[cursor.getCount()];
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            participant.name = cursor.getString(cursor.getColumnIndex(budgetSplitContract.participants.COLUMN_NAME));
            participant.uniqueId = cursor.getString(cursor.getColumnIndex(budgetSplitContract.participants.COLUMN_UNIQUEID));
            participant.id = cursor.getLong(cursor.getColumnIndex(budgetSplitContract.participants._ID));
            if (selectedParticipants.contains(participant)) {
                checkedList[cursor.getPosition()] = true;
            }
        }
        for (int i = 0; i < checkedList.length; i++) {
            contactsList.setItemChecked(i, checkedList[i]);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        contactsAdapter.swapCursor(null);
    }

    @Override
    protected void onStop() {
        getLoaderManager().destroyLoader(LOADER_PARTICIPANTS);
        super.onStop();
    }
}

class Participant implements Parcelable {
    String name;
    String uniqueId;
    long id;

    Participant() {
    }

    ;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Participant that = (Participant) o;

        if (id != that.id) return false;
        if (!name.equals(that.name)) return false;
        if (uniqueId != null ? !uniqueId.equals(that.uniqueId) : that.uniqueId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (uniqueId != null ? uniqueId.hashCode() : 0);
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }

    Participant(String name, String uniqueId, long id) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.id = id;
    }

    Participant(Parcel in) {
        name = in.readString();
        uniqueId = in.readString();
        id = in.readLong();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(uniqueId);
        parcel.writeLong(id);
    }

    public static Creator<Participant> CREATOR = new Creator<Participant>() {
        @Override
        public Participant createFromParcel(Parcel parcel) {
            return new Participant(parcel);
        }

        @Override
        public Participant[] newArray(int i) {
            return new Participant[0];
        }
    };
}
