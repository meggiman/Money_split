
package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


public class TagSelection extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    // For Item or Participant
    static final String EXTRA_ID = "Id";
    static final String EXTRA_TAGFILTER_VISIBLE = "tagfilterVisible";
    static final String EXTRA_TITLE = "title";
    static final int LOADER_TAGS_ALL = 0;
    static final int LOADER_TAGS = 1;
    boolean tagsAllFinished = false;
    boolean tagsFinished = false;
    Intent intent = new Intent();


    ProgressBar progressBar;
    ArrayList<Long> tagIds;
    TagAdapter tagsGridAdapter;
    Cursor tagCursor;


    Button newTag;
    Button ok;
    AlertDialog tagCreatePopup;
    GridView tagGrid;
    TextView tagfilter;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_selection);


        intent = getIntent();


        getLoaderManager().initLoader(LOADER_TAGS, null, this);
        getLoaderManager().initLoader(LOADER_TAGS_ALL, null, this);

        ok = (Button) findViewById(R.id.tag_selection_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // update Tagfilter!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                int tagCount = tagGrid.getCount();
                SparseBooleanArray checked = tagGrid.getCheckedItemPositions();
                ContentValues tagIds = new ContentValues();
                for (int i = 0; i < tagCount; i++) {
                    if (checked.get(i)) {
                        Tag item = (Tag) tagGrid.getItemAtPosition(i);
                        tagIds.put(budgetSplitContract.tagFilter.COLUMN_TAG_ID, item.id);
                        tagIds.put(budgetSplitContract.tagFilter.COLUMN_PARTICIPANTS_ID, intent.getStringExtra(EXTRA_ID));
                        getContentResolver().insert(budgetSplitContract.tagFilter.CONTENT_URI, tagIds);
                        tagIds.clear();
                    }
                }
                finish();
            }
        });
        newTag = (Button) findViewById(R.id.tag_selection_button_add_tag1);
        newTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateTagPopup(view);
            }
        });
        tagGrid = (GridView) findViewById(R.id.tag_selection_gridView_tags);
        // Sets the selection colours
        tagGrid.setSelector(R.drawable.gridview_item_background);

        tagfilter = (TextView) findViewById(R.id.tag_selection_tagfilter);
        tagfilter.setVisibility(View.VISIBLE);
        if (!intent.getBooleanExtra(EXTRA_TAGFILTER_VISIBLE, false)) {
            tagfilter.setVisibility(View.GONE);
        }

        title = (TextView) findViewById(R.id.tag_selection_variable);

        char[] titleName = (intent.getStringExtra(EXTRA_TITLE)).toCharArray();

        title.setText(titleName, 0, titleName.length);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tags, menu);
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

    void showCreateTagPopup(final View view) {
        if (tagCreatePopup == null) {
            AlertDialog.Builder myDialogBuilder = new AlertDialog.Builder(this);
            myDialogBuilder.setTitle(getString(R.string.create_a_new_tag));
            final EditText editText = new EditText(getBaseContext());
            editText.setHint(getString(R.string.tag_name));
            myDialogBuilder.setView(editText);
            myDialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            myDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ContentValues newTag = new ContentValues();
                    newTag.put(budgetSplitContract.tags.COLUMN_NAME, editText.getText().toString().trim());
                    getContentResolver().insert(budgetSplitContract.tags.CONTENT_URI, newTag);
                    dialogInterface.dismiss();
                }
            });
            tagCreatePopup = myDialogBuilder.create();
        }
        tagCreatePopup.show();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        progressBar = (ProgressBar) findViewById(R.id.progressBar_tags);
        progressBar.setVisibility(View.VISIBLE);

        switch (i) {
            case LOADER_TAGS_ALL:
                String[] projection = {budgetSplitContract.tags.COLUMN_NAME,
                        budgetSplitContract.tags._ID};
                String sortOrder = new String(budgetSplitContract.tags.COLUMN_NAME + " ASC");

                return new CursorLoader(this, budgetSplitContract.tags.CONTENT_URI, projection, null, null, sortOrder);

            case LOADER_TAGS:
                String[] projection1 = new String[]{
                        budgetSplitContract.tagFilter.COLUMN_TAG_ID
                };
                Long contactId = getIntent().getLongExtra(EXTRA_ID, -1);
                String selection = new String(budgetSplitContract.tagFilter.COLUMN_PARTICIPANTS_ID + " = " + contactId.toString());
                return new CursorLoader(this, budgetSplitContract.tagFilter.CONTENT_URI, projection1, selection, null, null);

            default:
                throw new IllegalArgumentException("Unknown Loader");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        switch (cursorLoader.getId()) {

            case LOADER_TAGS_ALL:
                if (cursor.getCount() > 0) {
                    int capacity = cursor.getCount();
                    Tag[] data = new Tag[capacity];
                    int i = 0;
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        data[i] = new Tag();
                        data[i].name = cursor.getString(cursor.getColumnIndex(budgetSplitContract.tags.COLUMN_NAME));
                        data[i].id = cursor.getLong(cursor.getColumnIndex(budgetSplitContract.tags._ID));
                        i++;
                    }
                    tagsGridAdapter = new TagAdapter(this, R.layout.activity_tags_gridview_layout, data);
                }
                tagGrid.setAdapter(tagsGridAdapter);
                tagsAllFinished = true;
                break;

            case LOADER_TAGS:
                tagCursor = cursor;
                tagsFinished = true;
                break;
        }

        if (tagsFinished && tagsAllFinished) {
            tagIds = new ArrayList<Long>();
            if (tagCursor.getCount() > 0) {
                for (tagCursor.moveToFirst(); !tagCursor.isAfterLast(); tagCursor.moveToNext()) {
                    tagIds.add(tagCursor.getLong(tagCursor.getColumnIndex(budgetSplitContract.participantsTagsDetails.COLUMN_TAG_ID)));
                }
                Tag item;
                for (int i = 0; i < tagGrid.getCount(); i++) {
                    item = (Tag) tagGrid.getItemAtPosition(i);
                    for (int j = 0; j < tagIds.size(); j++) {
                        if (item.id == tagIds.get(j)) {
                            tagGrid.setSelection(i);
                        }
                    }
                }
            }

            tagGrid.setMultiChoiceModeListener(new MultiChoiceModeListener());

        }

        // Hide Progress Bar

        progressBar.setVisibility(View.GONE);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        //contactsCursorAdapter.changeCursor(null);

    }

    class Tag {
        String name;
        Long id;

        public Tag() {
            super();
        }

        public Tag(String n) {
            this.name = n;
        }
    }

    class TagHolder {
        TextView name;
    }

    public class TagAdapter extends ArrayAdapter<Tag> {
        Context context;
        int layoutResourceId;
        Tag[] data = null;


        public TagAdapter(Context c, int l, Tag[] d) {
            super(c, l, d);
            this.context = c;
            this.layoutResourceId = l;
            this.data = d;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            TagHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new TagHolder();
                holder.name = (TextView) row.findViewById(R.id.activity_new_tag_gridview_textview);

                row.setTag(holder);
            } else {
                holder = (TagHolder) row.getTag();
            }

            Tag t = data[position];
            holder.name.setText(t.name);

            return row;
        }
    }

    public class MultiChoiceModeListener implements GridView.MultiChoiceModeListener {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Select Items");
            mode.setSubtitle("No item selected");
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            int selectCount = tagGrid.getCheckedItemCount();
            mode.setTitle("Selected Tags");
            mode.setSubtitle(selectCount + "Tags selected");
        }

    }
}



