package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


public class ContactsList extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView list;
    ProgressBar progressBar;
    Button addContact;


    Contact[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts__list);

        progressBar = (ProgressBar) findViewById(R.id.contacts_list_progressbar);

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
        String sortOrder = new String(budgetSplitContract.participants.COLUMN_NAME + " ASC");
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
                startActivity(tagSelectionIntent);
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
            ContactHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new ContactHolder();
                holder.name = (TextView) row.findViewById(R.id.textView_tags_title);
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
