package ch.ethz.itet.pps.budgetSplit;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.RemoteException;
import android.preference.PreferenceManager;
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
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProjectItems#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectItems extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PROJECT_CONTENT_URI = "projectContentUri";

    private Uri projectUri;
    private long projectId;
    private String myUniqueId;
    private final int LOADER_ITEMS = 1;
    private ListView itemsList;
    private Cursor cursorProject;
    private Double defaultCurrencyExchange;
    private String defaultCurrencyCode;

    private ItemAdapter itemsSingleAdapter;
    private ProgressDialog progressDialog;
    private static final int REQUEST_EDIT_ITEM = 1;
    private static final int REQUEST_CREATE_ITEM = 2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param projectContentUri Parameter 1.
     * @return A new instance of fragment ProjectItems.
     */
    public static ProjectItems newInstance(Uri projectContentUri) {
        ProjectItems fragment = new ProjectItems();
        Bundle args = new Bundle();
        args.putParcelable(PROJECT_CONTENT_URI, projectContentUri);
        fragment.setArguments(args);
        return fragment;
    }

    public ProjectItems() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectUri = getArguments().getParcelable(PROJECT_CONTENT_URI);
            projectId = ContentUris.parseId(projectUri);
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        myUniqueId = preferences.getString(getString(R.string.pref_user_unique_id), "uniqueIdNotFound");
        cursorProject = getActivity().getContentResolver().query(projectUri, new String[]{budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_ADMIN_UNIQUEID}, null, null, null);
        long defaultCurrencyId = Long.parseLong(preferences.getString(getString(R.string.pref_default_currency), "-1"));
        Cursor defaultCurrencyCursor = getActivity().getContentResolver().query(ContentUris.withAppendedId(budgetSplitContract.currencies.CONTENT_URI, defaultCurrencyId), budgetSplitContract.currencies.PROJECTION_ALL, null, null, null);
        defaultCurrencyCursor.moveToFirst();
        defaultCurrencyExchange = defaultCurrencyCursor.getDouble(defaultCurrencyCursor.getColumnIndex(budgetSplitContract.currencies.COLUMN_EXCHANGE_RATE));
        defaultCurrencyCode = defaultCurrencyCursor.getString(defaultCurrencyCursor.getColumnIndex(budgetSplitContract.currencies.COLUMN_CURRENCY_CODE));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentLayout = inflater.inflate(R.layout.fragment_project_items, container, false);
        //Configure List
        itemsList = (ListView) fragmentLayout.findViewById(R.id.listView);
        String from[] = {budgetSplitContract.itemsDetailsRO.COLUMN_ITEM_DATE_ADDED,
                budgetSplitContract.itemsDetailsRO.COLUMN_ITEM_NAME,
                budgetSplitContract.itemsDetailsRO.COLUMN_CREATOR_NAME,
                budgetSplitContract.itemsDetailsRO.COLUMN_ITEM_PRICE};
        int to[] = {R.id.textViewDateAdded, R.id.textViewItemName, R.id.textViewCreator, R.id.textViewPrice};
        itemsSingleAdapter = new ItemAdapter(getActivity(), null, 0);
        View itemListHeaderView = inflater.inflate(R.layout.fragment_project_items_itemlist_header, null);
        itemsList.addHeaderView(itemListHeaderView, null, false);
        itemsList.setAdapter(itemsSingleAdapter);
        itemsList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        itemsList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                //
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                cursorProject.moveToFirst();
                if (cursorProject.getString(cursorProject.getColumnIndex(budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_ADMIN_UNIQUEID)).equals(myUniqueId)) {
                    MenuInflater menuInflater = actionMode.getMenuInflater();
                    menuInflater.inflate(R.menu.project_items_select, menu);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_delete:
                        AlertDialog.Builder myDialogBuilder = new AlertDialog.Builder(getActivity());
                        myDialogBuilder.setTitle(getString(R.string.delete_items));
                        myDialogBuilder.setMessage(getString(R.string.delete_) + " " + itemsList.getCheckedItemCount() + " " + getString(R.string.items_q));
                        myDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                                SparseBooleanArray checkedItems = itemsList.getCheckedItemPositions();
                                for (int k = 0; k < itemsList.getAdapter().getCount(); k++) {
                                    if (checkedItems.get(k)) {
                                        Cursor cursorAtDeletePosition = (Cursor) itemsList.getItemAtPosition(k);
                                        long id = cursorAtDeletePosition.getLong(cursorAtDeletePosition.getColumnIndex(budgetSplitContract.items._ID));
                                        Uri uriToDelete = ContentUris.withAppendedId(budgetSplitContract.items.CONTENT_URI, id);
                                        operations.add(ContentProviderOperation.newDelete(uriToDelete).build());
                                    }
                                }
                                try {
                                    getActivity().getContentResolver().applyBatch(budgetSplitContract.AUTHORITY, operations);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                } catch (OperationApplicationException e) {
                                    e.printStackTrace();
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
                    default:
                        return false;
                }

            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });
        itemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor itemCursor = ((Cursor) adapterView.getItemAtPosition(i));
                long itemId = itemCursor.getLong(itemCursor.getColumnIndex(budgetSplitContract.itemsDetailsRO._ID));
                Uri itemUri = ContentUris.withAppendedId(budgetSplitContract.itemsDetailsRO.CONTENT_URI, itemId);
                Intent intent = new Intent(getActivity(), add_new_item.class);
                intent.putExtra(add_new_item.EXTRA_PROJECT_DETAILS_URI, projectUri);
                intent.putExtra(add_new_item.EXTRA_ITEM_DETAILS_URI, itemUri);
                startActivityForResult(intent, REQUEST_EDIT_ITEM);
            }
        });

        //Configure Button
        Button addItemButton = (Button) fragmentLayout.findViewById(R.id.btnAddNewItem);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newItemIntent = new Intent(getActivity(), add_new_item.class);
                newItemIntent.putExtra(add_new_item.EXTRA_PROJECT_DETAILS_URI, projectUri);
                startActivityForResult(newItemIntent, REQUEST_CREATE_ITEM);
            }
        });

        return fragmentLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Show ProgressDialog while Loading Cursor.
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        //Start Loader
        getLoaderManager().initLoader(LOADER_ITEMS, null, this);
    }

    @Override
    public void onStop() {
        getLoaderManager().destroyLoader(LOADER_ITEMS);
        progressDialog.dismiss();
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CREATE_ITEM:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getActivity(), getString(R.string.new_item_was_created), Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case REQUEST_EDIT_ITEM:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getActivity(), getString(R.string.item_was_saved), Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case LOADER_ITEMS:
                String[] projection = {budgetSplitContract.itemsDetailsRO._ID, budgetSplitContract.itemsDetailsRO.COLUMN_ITEM_NAME
                        , budgetSplitContract.itemsDetailsRO.COLUMN_ITEM_DATE_ADDED
                        , budgetSplitContract.itemsDetailsRO.COLUMN_ITEM_PRICE
                        , budgetSplitContract.itemsDetailsRO.COLUMN_CREATOR_NAME};
                String selection = budgetSplitContract.itemsDetailsRO.COLUMN_PROJECT_ID + " = ?";
                String[] selectionArgs = {Long.toString(projectId)};
                return new CursorLoader(getActivity(), budgetSplitContract.itemsDetailsRO.CONTENT_URI, projection, selection, selectionArgs, null);
            default:
                throw new IllegalArgumentException("The Loader Id was invalid.");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        itemsSingleAdapter.changeCursor(cursor);
        progressDialog.dismiss();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        itemsSingleAdapter.changeCursor(null);
    }

    private class ItemAdapter extends CursorAdapter {


        public ItemAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return getActivity().getLayoutInflater().inflate(R.layout.fragment_project_items_itemlist_row, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView dateAddedView = (TextView) view.findViewById(R.id.textViewDateAdded);
            TextView itemNameView = (TextView) view.findViewById(R.id.textViewItemName);
            TextView itemCreatorView = (TextView) view.findViewById(R.id.textViewCreator);
            TextView priceView = (TextView) view.findViewById(R.id.textViewPrice);

            dateAddedView.setText(cursor.getString(cursor.getColumnIndex(budgetSplitContract.itemsDetailsRO.COLUMN_ITEM_DATE_ADDED)));
            itemNameView.setText(cursor.getString(cursor.getColumnIndex(budgetSplitContract.itemsDetailsRO.COLUMN_ITEM_NAME)));
            itemCreatorView.setText(cursor.getString(cursor.getColumnIndex(budgetSplitContract.itemsDetailsRO.COLUMN_CREATOR_NAME)));
            priceView.setText(new DecimalFormat(",##0.00").format(cursor.getDouble(cursor.getColumnIndex(budgetSplitContract.itemsDetailsRO.COLUMN_ITEM_PRICE)) / defaultCurrencyExchange) + " " + defaultCurrencyCode);
        }
    }

}
