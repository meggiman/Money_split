
package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


public class Tags extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    static final int LOADER_TAGS = 0;
    ProgressBar progressBar;
    Cursor tagCursor;
    ArrayList<String> tags;
    ArrayAdapter<String> tagsGridAdapter;



    Button newTag;
    AlertDialog tagCreatePopup;
    GridView tagGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        getLoaderManager().initLoader(LOADER_TAGS, null, this);

        newTag = (Button) findViewById(R.id.button_add_tag);
        newTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateTagPopup(view);
            }
        });
        tagGrid = (GridView) findViewById(R.id.gridView_tags);
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

        return new CursorLoader(this, budgetSplitContract.tags.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        tags = new ArrayList<String>();
        tagsGridAdapter = new ArrayAdapter<String>(this, R.layout.activity_tags_gridview_layout, R.id.activity_new_tag_gridview_textview, tags);
        if (cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                tags.add(cursor.getString(cursor.getColumnIndex(budgetSplitContract.tags.COLUMN_NAME)));
            }
            tagsGridAdapter = new ArrayAdapter<String>(this, R.layout.activity_tags_gridview_layout, R.id.activity_new_tag_gridview_textview, tags);
        }
        tagGrid.setAdapter(tagsGridAdapter);


        // Hide Progress Bar
        progressBar.setVisibility(View.GONE);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        //contactsCursorAdapter.changeCursor(null);

    }
}

