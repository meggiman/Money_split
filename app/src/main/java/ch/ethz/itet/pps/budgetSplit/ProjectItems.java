package ch.ethz.itet.pps.budgetSplit;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;

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
    private final int LOADER_ITEMS = 1;
    private ListView itemsList;
    private SimpleCursorAdapter itemsSingleAdapter;
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
        itemsSingleAdapter = new SimpleCursorAdapter(getActivity(), R.layout.fragment_project_items_itemlist_row, null, from, to, 0);
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
                MenuInflater menuInflater = actionMode.getMenuInflater();
                menuInflater.inflate(R.menu.project_items_select, menu);
                return true;
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

        //Show ProgressDialog while Loading Cursor.
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        //Start Loader
        getLoaderManager().initLoader(LOADER_ITEMS, null, this);
        return fragmentLayout;
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
                switch (requestCode) {
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
        itemsSingleAdapter.swapCursor(cursor);
        progressDialog.dismiss();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        itemsSingleAdapter.changeCursor(null);
        progressDialog.show();
    }
}
