
package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


public class TagSelection extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    // For Item or Participant
    static final String EXTRA_ID = "Id";
    static final String EXTRA_TAGFILTER_VISIBLE = "tagfilterVisible"; // tells you if you're coming from a contacts activity or from a items activity
    static final String EXTRA_TITLE = "title";
    static final String EXTRA_ITEM_TAGS_ALREADY_ADDED = "itemTagsAlreadyAdded";
    static final String EXTRA_ITEM_TAGS_TO_ADD = "itemTagsToAdd";
    static final String EXTRA_ITEM_TAGS_TO_DELETE = "itemTagsToDelete";
    static final String RESULT_EXTRA_ITEM_TAGS_TO_ADD = "resultItemTagsToAdd";
    static final String RESULT_EXTRA_ITEM_TAGS_TO_DELETE = "resultItemTagsToDelete";
    static final String RESULT_EXTRA_ITEM_TAGS_STRING = "resultItemTagsString";
    static final int LOADER_TAGS_ALL = 0;
    static final int LOADER_TAGS_PARTICIPANTS = 1;
    static final int LOADER_TAGS_ITEM = 2;
    boolean tagsAllFinished = false;
    boolean tagsFinished = false;
    boolean tagsItemFinished = false;
    Intent intent = new Intent();
    private List<Long> toDeleteList = new ArrayList<Long>();
    private List<Long> toInsertList = new ArrayList<Long>();

    private ArrayList<ch.ethz.itet.pps.budgetSplit.Tag> itemTagsAlreadyAdded;
    private ArrayList<ch.ethz.itet.pps.budgetSplit.Tag> itemTagsToAdd;
    private ArrayList<ch.ethz.itet.pps.budgetSplit.Tag> itemTagsToDelete;
    private StringBuffer itemTagsString;


    ProgressBar progressBar;
    ArrayList<IdHolder> tagIds;
    List<Tag> data = new ArrayList<Tag>();
    TagAdapter tagsGridAdapter;


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

        if (intent.getBooleanExtra(EXTRA_TAGFILTER_VISIBLE, true)) {
            getLoaderManager().initLoader(LOADER_TAGS_PARTICIPANTS, null, this);
        } else {
            itemTagsAlreadyAdded = intent.getParcelableArrayListExtra(EXTRA_ITEM_TAGS_ALREADY_ADDED);
            itemTagsToAdd = intent.getParcelableArrayListExtra(EXTRA_ITEM_TAGS_TO_ADD);
            itemTagsToDelete = intent.getParcelableArrayListExtra(EXTRA_ITEM_TAGS_TO_DELETE);
            itemTagsString = new StringBuffer();
            getLoaderManager().initLoader(LOADER_TAGS_ITEM, null, this);
        }
        getLoaderManager().initLoader(LOADER_TAGS_ALL, null, this);

        ok = (Button) findViewById(R.id.tag_selection_ok);
        ok.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View view) {
                                      // coming from a Contacts Activity
                                      if (intent.getBooleanExtra(EXTRA_TAGFILTER_VISIBLE, true)) {
                                          for (int i = 0; i < data.size(); i++) {
                                              if (data.get(i).checked) {
                                                  boolean insert = true;
                                                  for (int j = 0; j < tagIds.size(); j++) {
                                                      if (data.get(i).id == tagIds.get(j).tagId) {
                                                          // Item was already Checked and thus stays in the Tagfilter
                                                          // do nothing especially no insert
                                                          insert = false;
                                                      }
                                                  }
                                                  if (insert == true) {
                                                      // The right junktion does not yet exist -> insert
                                                      ContentValues cv = new ContentValues();
                                                      cv.put(budgetSplitContract.tagFilter.COLUMN_PARTICIPANTS_ID, intent.getLongExtra(EXTRA_ID, -1));
                                                      cv.put(budgetSplitContract.tagFilter.COLUMN_TAG_ID, data.get(i).id);
                                                      cv.put(budgetSplitContract.tagFilter.COLUMN_SHARE_RATIO, 0);
                                                      getContentResolver().insert(budgetSplitContract.tagFilter.CONTENT_URI, cv);
                                                  }
                                              } else { // data.checked == false
                                                  for (int j = 0; j < tagIds.size(); j++) {
                                                      if (data.get(i).id == tagIds.get(j).tagId) {
                                                          // The Tag was checked before but isn't anmore -> delete
                                                          toDeleteList.add(tagIds.get(j).tableId);
                                                      }
                                                  }
                                              }
                                          }
                                          Uri toDelete;
                                          ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                                          for (int k = 0; k < toDeleteList.size(); k++) {
                                              toDelete = ContentUris.withAppendedId(budgetSplitContract.tagFilter.CONTENT_URI, toDeleteList.get(k));
                                              operations.add(ContentProviderOperation.newDelete(toDelete).build());
                                          }
                                          try {
                                              getContentResolver().applyBatch(budgetSplitContract.AUTHORITY, operations);
                                          } catch (RemoteException e) {
                                              e.printStackTrace();
                                          } catch (OperationApplicationException e) {
                                              e.printStackTrace();
                                          }
                                      }

                                      // coming from an Items Activity (tagfilterVisible==false)
                                      else {
                                          for (ch.ethz.itet.pps.budgetSplit.Tag tag : itemTagsAlreadyAdded) {
                                              if (!itemTagsToDelete.contains(tag)) {
                                                  itemTagsString.append(tag.name).append(", ");
                                              }
                                          }
                                          for (ch.ethz.itet.pps.budgetSplit.Tag tag : itemTagsToAdd) {
                                              itemTagsString.append(tag.name).append(", ");
                                          }
                                          itemTagsString.delete(itemTagsString.length() - 2, itemTagsString.length());
                                          Intent result = new Intent();
                                          result.putParcelableArrayListExtra(RESULT_EXTRA_ITEM_TAGS_TO_DELETE, itemTagsToDelete);
                                          result.putParcelableArrayListExtra(RESULT_EXTRA_ITEM_TAGS_TO_ADD, itemTagsToAdd);
                                          result.putExtra(RESULT_EXTRA_ITEM_TAGS_STRING, itemTagsString.toString());
                                          setResult(RESULT_OK, result);
                                      }
                                      finish();
                                  }
                              }

        );
        newTag = (Button)

                findViewById(R.id.tag_selection_button_add_tag1);

        newTag.setOnClickListener(new View.OnClickListener()

                                  {
                                      @Override
                                      public void onClick(View view) {
                                          showCreateTagPopup(view);
                                      }
                                  }

        );
        tagGrid = (GridView)

                findViewById(R.id.tag_selection_gridView_tags);
        // Sets the selection colours
        //tagGrid.setSelector(R.drawable.gridview_item_background);
        tagGrid.setAdapter(new

                        TagAdapter(this, R.layout.activity_tag_selection_checkable_row, data)

        );

        tagfilter = (TextView)

                findViewById(R.id.tag_selection_tagfilter);

        tagfilter.setVisibility(View.VISIBLE);
        if (!intent.getBooleanExtra(EXTRA_TAGFILTER_VISIBLE, false))

        {
            tagfilter.setVisibility(View.GONE);
        }

        title = (TextView)

                findViewById(R.id.tag_selection_variable);

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

            case LOADER_TAGS_PARTICIPANTS:
                String[] projection1 = new String[]{
                        budgetSplitContract.participantsTagsDetails.COLUMN_TAG_ID,
                        budgetSplitContract.participantsTagsDetails._ID
                };
                Long contactId = getIntent().getLongExtra(EXTRA_ID, -1);
                String selection = new String(budgetSplitContract.participantsTagsDetails.COLUMN_PARTICIPANT_ID + " = " + contactId.toString());
                return new CursorLoader(this, budgetSplitContract.participantsTagsDetails.CONTENT_URI_ALL, projection1, selection, null, null);

            case LOADER_TAGS_ITEM:
                String[] projection2 = new String[]{
                        budgetSplitContract.itemsTagsDetailsRO.COLUMN_TAG_ID,
                        budgetSplitContract.itemsTagsDetailsRO._ID
                };
                Long itemId = getIntent().getLongExtra(EXTRA_ID, -1);
                String selection1 = new String(budgetSplitContract.itemsTagsDetailsRO.COLUMN_ITEM_ID + " = " + itemId.toString());
                return new CursorLoader(this, budgetSplitContract.itemsTagsDetailsRO.CONTENT_URI_ALL, projection2, selection1, null, null);

            default:
                throw new IllegalArgumentException("Unknown Loader");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        switch (cursorLoader.getId()) {

            case LOADER_TAGS_ALL:
                data.clear();
                if (cursor.getCount() > 0) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        data.add(new Tag(cursor.getString(cursor.getColumnIndex(budgetSplitContract.tags.COLUMN_NAME)), cursor.getLong(cursor.getColumnIndex(budgetSplitContract.tags._ID)), false));
                    }
                    tagsGridAdapter = new TagAdapter(this, R.layout.activity_tag_selection_checkable_row, data);
                }
                tagGrid.setAdapter(tagsGridAdapter);
                tagsAllFinished = true;
                break;

            case LOADER_TAGS_PARTICIPANTS:
                tagIds = new ArrayList<IdHolder>();
                if (cursor.getCount() > 0) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        tagIds.add(new IdHolder(cursor.getLong(cursor.getColumnIndex(budgetSplitContract.participantsTagsDetails.COLUMN_TAG_ID)), cursor.getLong(cursor.getColumnIndex(budgetSplitContract.participantsTagsDetails._ID))));
                    }
                    tagsFinished = true;
                }
                break;
        }


        if (tagsFinished && tagsAllFinished) {
            for (int i = 0; i < data.size(); i++) {
                for (int j = 0; j < tagIds.size(); j++) {
                    if (data.get(i).id == (Long) tagIds.get(j).tagId) {
                        data.get(i).checked = true;
                    }
                }
            }
        }
        if (!intent.getBooleanExtra(EXTRA_TAGFILTER_VISIBLE, true) && tagsAllFinished) {
            for (Tag tagToCheck : data) {
                for (ch.ethz.itet.pps.budgetSplit.Tag tag : itemTagsAlreadyAdded) {
                    if (tagToCheck.id == tag.id && !itemTagsToDelete.contains(tag)) {
                        tagToCheck.checked = true;
                    }
                }
                for (ch.ethz.itet.pps.budgetSplit.Tag tag : itemTagsToAdd) {
                    if (tagToCheck.id == tag.id) {
                        tagToCheck.checked = true;
                    }
                }
            }
        }
        tagGrid.setAdapter(new TagAdapter(this, R.layout.activity_tag_selection_checkable_row, data));
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
        Long tableId;
        boolean checked;


        public Tag() {
            super();
        }

        public Tag(String n, Long i, boolean c) {

            this.name = n;
            this.id = i;
            this.checked = c;
        }
    }

    class TagHolder extends ClickableListAdapter.ViewHolder {
        TextView name;

        public TagHolder(TextView t) {
            name = t;
        }
    }

    public class TagAdapter extends ClickableListAdapter {
        Context context;
        int layoutResourceId;
        List<Tag> data = null;


        public TagAdapter(Context c, int l, List<Tag> d) {
            super(c, l, d);
            this.context = c;
            this.layoutResourceId = l;
            this.data = d;
        }

        protected void bindHolder(ViewHolder h) {
            // Binding the holder keeps our data up to date.
            // In contrast to createHolder this method is called for all items
            // So, be aware when doing a lot of heavy stuff here.
            // we simply transfer our object's data to the list item representative

            //cast the TagHolder
            TagHolder th = (TagHolder) h;
            Tag item = (Tag) th.data;
            // transfer the name
            th.name.setText(item.name);
            if (item.checked) th.name.setBackgroundColor(Color.CYAN);
        }

        @Override
        protected ViewHolder createHolder(View v) {
            // createHolder will be called only as long, as the ListView is not filled
            // entirely. That is, where we gain our performance:
            // We use the relatively costly findViewById() methods and
            // bind the view's reference to the holder objects.

            TextView textV = (TextView) v.findViewById(R.id.activity_tag_selection_checkable_row_textview);
            ViewHolder holder = new TagHolder(textV);

            textV.setOnClickListener(new ClickableListAdapter.OnClickListener(holder) {
                public void onClick(View v, ViewHolder viewHolder) {
                    TagHolder clickHolder = (TagHolder) viewHolder;
                    Tag tag = (Tag) clickHolder.data;
                    tag.checked = !tag.checked; // toggle
                    if (tag.checked) {
                        clickHolder.name.setBackgroundColor(Color.CYAN);
                    } else {
                        clickHolder.name.setBackgroundColor(0);
                    }

                    if (!intent.getBooleanExtra(EXTRA_TAGFILTER_VISIBLE, true)) {
                        ch.ethz.itet.pps.budgetSplit.Tag tagForResult = new ch.ethz.itet.pps.budgetSplit.Tag(tag.id, tag.name);
                        if (tag.checked) {
                            itemTagsToDelete.remove(tagForResult);
                            if (!itemTagsAlreadyAdded.contains(tagForResult)) {
                                itemTagsToAdd.add(tagForResult);
                            }
                        } else {
                            itemTagsToAdd.remove(tagForResult);
                            if (itemTagsAlreadyAdded.contains(tagForResult)) {
                                itemTagsToDelete.add(tagForResult);
                            }
                        }
                    }
                }

            });

            // Still need to implement long clicklistener --> Delete!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            return holder;
        }
    }

    class IdHolder {
        Long tagId;
        Long tableId;

        public IdHolder() {
            super();
        }

        public IdHolder(Long tagId, Long tableId) {
            this.tagId = tagId;
            this.tableId = tableId;
        }
    }
}










