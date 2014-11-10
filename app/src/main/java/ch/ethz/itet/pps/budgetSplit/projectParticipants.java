package ch.ethz.itet.pps.budgetSplit;


import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link projectParticipants#newInstance} factory method to
 * create an instance of this fragment.
 */
public class projectParticipants extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PROJECT_CONTENT_URI = "projectContentUri";
    private Uri projectUri;
    private ListView participantsList;

    private static final int LOADER_PROJECT = 1;
    private boolean loaderProjectFinished = false;
    private static final int LOADER_PROJECT_PARTICIPANTS = 2;
    private boolean loaderParticipantsFinished = false;

    private Cursor cursorProject;
    private Cursor cursorProjectParticipants;

    private ParticipantsAdapter participantsAdapter;
    private ProgressDialog progressDialog;

    private long projectId;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param projectUri Parameter 1.
     * @return A new instance of fragment projectParticipants.
     */
    public static projectParticipants newInstance(Uri projectUri) {
        projectParticipants fragment = new projectParticipants();
        Bundle args = new Bundle();
        args.putParcelable(PROJECT_CONTENT_URI, projectUri);
        fragment.setArguments(args);
        return fragment;
    }

    public projectParticipants() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectUri = (Uri) getArguments().getParcelable(PROJECT_CONTENT_URI);
        }
        if (projectUri == null) {
            throw new IllegalArgumentException("There was no project Uri.");
        }
        if (!projectUri.getAuthority().equals(budgetSplitContract.AUTHORITY)) {
            throw new IllegalArgumentException("The project Uri's authority did not match budgetSplit.");
        }
        if (!projectUri.getPathSegments().get(0).equals(budgetSplitContract.projectsDetailsRO.CONTENT_URI.getLastPathSegment())) {
            throw new IllegalArgumentException("The wrong table was used in project Uri.");
        }
        try {
            projectId = ContentUris.parseId(projectUri);
        } catch (UnsupportedOperationException e) {
            throw new IllegalArgumentException("The added Uri wasn't a single row uri.");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The added uri wasn't a single row uri.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentLayout = inflater.inflate(R.layout.fragment_project_participants, container, false);
        participantsList = (ListView) fragmentLayout.findViewById(R.id.listView);
        getLoaderManager().initLoader(LOADER_PROJECT_PARTICIPANTS, null, this);
        getLoaderManager().initLoader(LOADER_PROJECT, null, this);

        //Show ProgressDialog while Loading Cursor.
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        participantsAdapter = new ParticipantsAdapter(getActivity(), null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        participantsList.setAdapter(participantsAdapter);
        participantsList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        participantsList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                //
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater menuInflater = actionMode.getMenuInflater();
                menuInflater.inflate(R.menu.project_participants_select, menu);
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
                        myDialogBuilder.setMessage(getString(R.string.delete_) + " " + participantsList.getCheckedItemCount() + " " + getString(R.string.items_q));
                        myDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                                SparseBooleanArray checkedItems = participantsList.getCheckedItemPositions();
                                for (int k = 0; k < participantsList.getAdapter().getCount(); k++) {
                                    if (checkedItems.get(k)) {
                                        Cursor cursorAtDeletePosition = (Cursor) participantsList.getItemAtPosition(k);
                                        long id = cursorAtDeletePosition.getLong(cursorAtDeletePosition.getColumnIndex(budgetSplitContract.projectsParticipantsDetailsRO._ID));
                                        Uri uriToDelete = ContentUris.withAppendedId(budgetSplitContract.projectParticipants.CONTENT_URI, id);
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
        return fragmentLayout;
    }

    class ParticipantsAdapter extends CursorAdapter {

        LayoutInflater inflater;

        public ParticipantsAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
            inflater = getActivity().getLayoutInflater();
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return inflater.inflate(R.layout.fragment_project_participants_row, viewGroup);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ImageView isAdminView = (ImageView) view.findViewById(R.id.imageView);
            TextView participantNameView = (TextView) view.findViewById(R.id.textViewName);
            TextView isVirtualView = (TextView) view.findViewById(R.id.textViewName);

            cursorProject.moveToFirst();
            boolean isProjectAdmin = cursor.getString(cursor.getColumnIndex(budgetSplitContract.projectsParticipantsDetailsRO.COLUMN_PARTICIPANT_UNIQUE_ID)).equals(cursorProject.getString(cursorProject.getColumnIndex(budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_ADMIN_UNIQUEID)));
            boolean isVirtual = cursor.getInt(cursor.getColumnIndex(budgetSplitContract.projectsParticipantsDetailsRO.COLUMN_PARTICIPANT_IS_VIRTUAL)) > 0;
            isAdminView.setVisibility(isProjectAdmin ? View.VISIBLE : View.INVISIBLE);
            isVirtualView.setVisibility(isVirtual ? View.VISIBLE : View.INVISIBLE);
            participantNameView.setText(cursor.getString(cursor.getColumnIndex(budgetSplitContract.projectsParticipantsDetailsRO.COLUMN_PARTICIPANT_NAME)));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case LOADER_PROJECT:
                return new CursorLoader(getActivity(), projectUri, budgetSplitContract.projectsDetailsRO.PROJECTION_ALL, null, null, null);
            case LOADER_PROJECT_PARTICIPANTS:
                Uri uri = ContentUris.withAppendedId(budgetSplitContract.projectsParticipantsDetailsRO.CONTENT_URI, projectId);
                return new CursorLoader(getActivity(), uri, budgetSplitContract.projectsParticipantsDetailsRO.PROJECTION_ALL, null, null, null);
            default:
                throw new IllegalArgumentException("Illegal Loader id " + i);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        switch (cursorLoader.getId()) {
            case LOADER_PROJECT:
                cursorProject = cursor;
                loaderProjectFinished = true;
                if (loaderParticipantsFinished) {
                    participantsAdapter.swapCursor(cursorProjectParticipants);
                    progressDialog.dismiss();
                }
                break;
            case LOADER_PROJECT_PARTICIPANTS:
                cursorProjectParticipants = cursor;
                loaderParticipantsFinished = true;
                if (loaderProjectFinished) {
                    participantsAdapter.swapCursor(cursorProjectParticipants);
                    progressDialog.dismiss();
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
