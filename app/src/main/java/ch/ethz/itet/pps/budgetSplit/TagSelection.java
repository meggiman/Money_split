
package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


public class TagSelection extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {


    static final int LOADER_TAGS = 0;
    ProgressBar progressBar;
    ArrayList<String> tags;
    ArrayAdapter<String> tagsGridAdapter;


    Button newTag;
    Button ok;
    AlertDialog tagCreatePopup;
    GridView tagGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_selection);

        getLoaderManager().initLoader(LOADER_TAGS, null, this);

        ok = (Button) findViewById(R.id.tag_selection_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // update Tagfilter!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                finish();
            }
        });
        newTag = (Button) findViewById(R.id.tag_selection_button_add_tag);
        newTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateTagPopup(view);
            }
        });
        tagGrid = (GridView) findViewById(R.id.tag_selection_gridView_tags);
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

        String[] projection = {budgetSplitContract.tags.COLUMN_NAME};
        String sortOrder = new String(budgetSplitContract.tags.COLUMN_NAME + " ASC");

        return new CursorLoader(this, budgetSplitContract.tags.CONTENT_URI, projection, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        progressBar.setVisibility(View.GONE);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        //contactsCursorAdapter.changeCursor(null);

    }
}

