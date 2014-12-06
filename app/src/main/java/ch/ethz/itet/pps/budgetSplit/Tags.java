package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


public class Tags extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    static final int LOADER_TAGS = 5;
    ProgressBar progressBar;
    SimpleCursorAdapter tagCursorAdapter;
    ArrayList<String> tags;
    ArrayAdapter<String> tagsGridAdapter;


    Button newTag;
    AlertDialog tagCreatePopup;
    GridView tagGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        // --> Add Button to Actionbar
        /*newTag = (Button) findViewById(R.id.tag_selection_button_add_tag1);
        newTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateTagPopup(view);
            }
        });*/
        tagGrid = (GridView) findViewById(R.id.tag_selection_gridView_tags);
        // Create Simple Cursor Adapter
        String[] fromColumns = {budgetSplitContract.tags.COLUMN_NAME};
        int[] toViews = {R.id.activity_new_tag_gridview_textview};
        tagCursorAdapter = new SimpleCursorAdapter(this, R.layout.activity_tags_gridview_layout, null, fromColumns, toViews, 0);
        tagGrid.setAdapter(tagCursorAdapter);
        tagGrid.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        tagGrid.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

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
                AlertDialog.Builder myDialogBuilder = new AlertDialog.Builder(Tags.this);
                myDialogBuilder.setTitle(getString(R.string.delete_tags));
                if (tagGrid.getCheckedItemCount() > 1) {
                    myDialogBuilder.setMessage(getString(R.string.delete_) + " " + tagGrid.getCheckedItemCount() + " " + getString(R.string.tags_questionmark));
                } else {
                    myDialogBuilder.setMessage(getString(R.string.delete_) + " " + tagGrid.getCheckedItemCount() + " " + getString(R.string.tag_questionmark));
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
                        ArrayList<ContentProviderOperation> deleteOperations = new ArrayList<ContentProviderOperation>();
                        ArrayList<String> tagsToDelete = new ArrayList<String>();
                        SparseBooleanArray checkedItems = tagGrid.getCheckedItemPositions();
                        for (int k = 0; k < tagGrid.getAdapter().getCount(); k++) {
                            if (checkedItems.get(k)) {
                                Cursor cursorAtDeletePosition = (Cursor) tagGrid.getItemAtPosition(k);
                                long id = cursorAtDeletePosition.getLong(cursorAtDeletePosition.getColumnIndex(budgetSplitContract.tags._ID));
                                Uri uriToDelete = ContentUris.withAppendedId(budgetSplitContract.tags.CONTENT_URI, id);
                                tagsToDelete.add(cursorAtDeletePosition.getString(cursorAtDeletePosition.getColumnIndex(budgetSplitContract.tags.COLUMN_NAME)));
                                deleteOperations.add(ContentProviderOperation.newDelete(uriToDelete).build());
                            }
                        }
                        try {
                            ContentProviderResult[] operationResult = getContentResolver().applyBatch(budgetSplitContract.AUTHORITY, deleteOperations);

                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (OperationApplicationException e) {
                            e.printStackTrace();
                        } catch (SQLiteConstraintException e) {
                            Toast.makeText(getApplicationContext(), getString(R.string.tags_delete_error), Toast.LENGTH_LONG).show();
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

        getLoaderManager().initLoader(LOADER_TAGS, null, this);
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
        switch (id) {
            case R.id.action_add_tag:
                showCreateTagPopup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void showCreateTagPopup() {
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
                    try {
                        getContentResolver().insert(budgetSplitContract.tags.CONTENT_URI, newTag);
                    } catch (SQLiteException e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.warning_tag_exists), Toast.LENGTH_SHORT).show();
                    }
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

        String[] projection = {budgetSplitContract.tags.COLUMN_NAME, budgetSplitContract.tags._ID};
        String sortOrder = new String(budgetSplitContract.tags.COLUMN_NAME + " ASC");

        return new CursorLoader(this, budgetSplitContract.tags.CONTENT_URI, projection, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        tagCursorAdapter.swapCursor(cursor);

        // Hide Progress Bar
        progressBar.setVisibility(View.GONE);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        //contactsCursorAdapter.changeCursor(null);

    }
}
