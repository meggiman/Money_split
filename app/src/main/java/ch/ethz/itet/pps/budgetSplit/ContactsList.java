package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.os.RemoteException;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


public class ContactsList extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView list;
    private ProgressBar progressBar;
    Button addContact;


    private Contact[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts__list);

        progressBar = (ProgressBar) findViewById(R.id.contacts_list_progressbar);

        list = (ListView) findViewById(R.id.contact_list_listwiev);
        list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            /**
             * Called when an item is checked or unchecked during selection mode.
             *
             * @param mode     The {@link android.view.ActionMode} providing the selection mode
             * @param position Adapter position of the item that was checked or unchecked
             * @param id       Adapter ID of the item that was checked or unchecked
             * @param checked  <code>true</code> if the item is now checked, <code>false</code>
             */
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                //
            }

            /**
             * Called when action mode is first created. The menu supplied will be used to
             * generate action buttons for the action mode.
             *
             * @param mode ActionMode being created
             * @param menu Menu used to populate action buttons
             * @return true if the action mode should be created, false if entering this
             * mode should be aborted.
             */
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.project_participants_select, menu);
                return true;
            }

            /**
             * Called to refresh an action mode's action menu whenever it is invalidated.
             *
             * @param mode ActionMode being prepared
             * @param menu Menu used to populate action buttons
             * @return true if the menu or action mode was updated, false otherwise.
             */
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            /**
             * Called to report a user click on an action button.
             *
             * @param mode The current ActionMode
             * @param item The item that was clicked
             * @return true if this callback handled the event, false if the standard MenuItem
             * invocation should continue.
             */
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                AlertDialog.Builder myDialogBuilder = new AlertDialog.Builder(ContactsList.this);
                myDialogBuilder.setTitle(getString(R.string.delete_contacts));
                if (list.getCheckedItemCount() > 1) {
                    myDialogBuilder.setMessage(getString(R.string.delete_) + " " + list.getCheckedItemCount() + " " + getString(R.string.contacts_questionmark));
                } else {
                    myDialogBuilder.setMessage(getString(R.string.delete_) + " " + list.getCheckedItemCount() + " " + getString(R.string.contact_questionmark));
                }
                myDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    /**
                     * This method will be invoked when a button in the dialog is clicked.
                     *
                     * @param dialog The dialog that received the click.
                     * @param which  The button that was clicked (e.g.
                     *               {@link android.content.DialogInterface#BUTTON1}) or the position
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<ContentProviderOperation> deleteOperations = new ArrayList<>();
                        ArrayList<String> contactsToDelete = new ArrayList<>();
                        SparseBooleanArray checkedItems = list.getCheckedItemPositions();
                        for (int k = 0; k < list.getAdapter().getCount(); k++) {
                            if (checkedItems.get(k)) {
                                Contact toDelete = (Contact) list.getItemAtPosition(k);
                                long id = toDelete.id;
                                Uri uriToDelete = ContentUris.withAppendedId(budgetSplitContract.participants.CONTENT_URI, id);
                                contactsToDelete.add(toDelete.name);
                                deleteOperations.add(ContentProviderOperation.newDelete(uriToDelete).build());
                            }
                        }
                        try {
                            getContentResolver().applyBatch(budgetSplitContract.AUTHORITY, deleteOperations);

                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (OperationApplicationException e) {
                            e.printStackTrace();
                        } catch (SQLiteConstraintException e) {
                            Toast.makeText(ContactsList.this, getString(R.string.contacts_delete_error), Toast.LENGTH_LONG).show();
                        }

                    }
                });

                myDialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Do nothing
                    }
                });
                myDialogBuilder.create().show();
                return true;
            }

            /**
             * Called when an action mode is about to be exited and destroyed.
             *
             * @param mode The current ActionMode being destroyed
             */
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                //
            }
        });
        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contacts__list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_contact:
                Intent createContactIntent = new Intent(ContactsList.this, NewContact.class);
                startActivityForResult(createContactIntent, 0);
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        progressBar.setVisibility(View.VISIBLE);

        String[] projection = new String[]{
                budgetSplitContract.participants.COLUMN_NAME,
                budgetSplitContract.participants._ID};
        String sortOrder = budgetSplitContract.participants.COLUMN_NAME + " ASC";
        return new CursorLoader(this, budgetSplitContract.participants.CONTENT_URI, projection, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        if (cursor.getCount() > 0) {
            int capacity = cursor.getCount();
            data = new Contact[capacity];
            int i = 0;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                data[i] = new Contact();
                data[i].name = cursor.getString(cursor.getColumnIndex(budgetSplitContract.participants.COLUMN_NAME));
                data[i].id = cursor.getLong(cursor.getColumnIndex(budgetSplitContract.participants._ID));
                i++;
            }
        }

        //Set GUI elements
        ContactAdapter adapter = new ContactAdapter(this, R.layout.activity_contacts_list_row, data);
        list = (ListView) findViewById(R.id.contact_list_listwiev);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                    long id) {

                Intent tagSelectionIntent = new Intent(ContactsList.this, TagSelection.class);

                // get contact ID
                Contact c = (Contact) adapterView.getItemAtPosition(position);

                long contactId = c.getContactId();
                String name = c.name;

                tagSelectionIntent.putExtra(TagSelection.EXTRA_ID, contactId);
                tagSelectionIntent.putExtra(TagSelection.EXTRA_TITLE, name);
                tagSelectionIntent.putExtra(TagSelection.EXTRA_TAGFILTER_VISIBLE, true);
                startActivityForResult(tagSelectionIntent, 0);
            }
        });

        //Hide Progressbar
        progressBar.setVisibility(View.GONE);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    class Contact {
        String name;
        Long id;


        public Contact() {
            super();
        }

        public Contact(String n, long i, ArrayList<Long> t) {
            this.name = n;
            this.id = i;
        }

        public long getContactId() {
            return this.id;
        }
    }

    class ContactHolder {
        TextView name;
        ImageView edit;
    }

    public class ContactAdapter extends ArrayAdapter<Contact> {

        Context context;
        int layoutResourceId;
        Contact[] data = null;

        public ContactAdapter(Context c, int l, Contact[] d) {
            super(c, l, d);
            this.context = c;
            this.layoutResourceId = l;
            this.data = d;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ContactHolder holder;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new ContactHolder();
                holder.name = (TextView) row.findViewById(R.id.contact_list_row_name);
                holder.edit = (ImageView) row.findViewById(R.id.contact_list_row_edit);

                row.setTag(holder);
            } else {
                holder = (ContactHolder) row.getTag();
            }

            Contact cnt = data[position];
            holder.name.setText(cnt.name);

            return row;
        }
    }
}
