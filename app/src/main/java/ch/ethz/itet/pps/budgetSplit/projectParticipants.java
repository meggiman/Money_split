package ch.ethz.itet.pps.budgetSplit;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
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
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final int REQUEST_CHOOSE_CONTACT = 1;
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
    private String myUniqueId;
    private String adminUniqueId;
    private boolean iAmAdmin = false;

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
            projectUri = getArguments().getParcelable(PROJECT_CONTENT_URI);
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        myUniqueId = preferences.getString(getString(R.string.pref_user_unique_id), "noUniqueIdFound");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentLayout = inflater.inflate(R.layout.fragment_project_participants, container, false);
        participantsList = (ListView) fragmentLayout.findViewById(R.id.listView);
        participantsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), TagSelection.class);
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                long id = cursor.getLong(cursor.getColumnIndex(budgetSplitContract.projectsParticipantsDetailsRO.COLUMN_PARTICIPANT_ID));
                String name = cursor.getString(cursor.getColumnIndex(budgetSplitContract.projectsParticipantsDetailsRO.COLUMN_PARTICIPANT_NAME));
                intent.putExtra(TagSelection.EXTRA_ID, id);
                intent.putExtra(TagSelection.EXTRA_TITLE, name);
                intent.putExtra(TagSelection.EXTRA_TAGFILTER_VISIBLE, true);
                startActivityForResult(intent, 0);
            }
        });
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
                if (adminUniqueId.equals(myUniqueId)) {
                    MenuInflater menuInflater = actionMode.getMenuInflater();
                    menuInflater.inflate(R.menu.project_participants_select, menu);
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
                        myDialogBuilder.setTitle(getString(R.string.delete_participants));
                        myDialogBuilder.setMessage(getString(R.string.delete_) + " " + participantsList.getCheckedItemCount() + " " + getString(R.string.participants_questionmark));
                        myDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ArrayList<ContentProviderOperation> operations = new ArrayList<>();
                                ArrayList<String> participantNames = new ArrayList<>();
                                SparseBooleanArray checkedItems = participantsList.getCheckedItemPositions();
                                for (int k = 0; k < participantsList.getAdapter().getCount(); k++) {
                                    if (checkedItems.get(k)) {
                                        Cursor cursorAtDeletePosition = (Cursor) participantsList.getItemAtPosition(k);
                                        String uniqueIdOfParticipant = cursorAtDeletePosition.getString(cursorAtDeletePosition.getColumnIndex(budgetSplitContract.projectsParticipantsDetailsRO.COLUMN_PARTICIPANT_UNIQUE_ID));
                                        if (uniqueIdOfParticipant == null || !uniqueIdOfParticipant.equals(adminUniqueId)) {
                                            long id = cursorAtDeletePosition.getLong(cursorAtDeletePosition.getColumnIndex(budgetSplitContract.projectsParticipantsDetailsRO._ID));
                                            Uri uriToDelete = ContentUris.withAppendedId(budgetSplitContract.projectParticipants.CONTENT_URI, id);
                                            participantNames.add(cursorAtDeletePosition.getString(cursorAtDeletePosition.getColumnIndex(budgetSplitContract.projectsParticipantsDetailsRO.COLUMN_PARTICIPANT_NAME)));
                                            operations.add(ContentProviderOperation.newDelete(uriToDelete).build());
                                        } else {
                                            Toast.makeText(getActivity(), getString(R.string.warning_cant_delete_project_admin), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                                try {
                                    ContentProviderResult[] operationResult = getActivity().getContentResolver().applyBatch(budgetSplitContract.AUTHORITY, operations);
                                    for (int j = 0; j < operationResult.length; j++) {
                                        if (operationResult[j].count == 0) {
                                            Toast.makeText(getActivity(), getString(R.string.coulnt_delete_participant) + " " + participantNames.get(j) + " " + getString(R.string.because_there_were_still_items_linked_to), Toast.LENGTH_LONG).show();
                                        }
                                    }

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (iAmAdmin) {
            inflater.inflate(R.menu.project_participants, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit:
                ArrayList<Participant> selectedContacts = new ArrayList<>();
                for (cursorProjectParticipants.moveToFirst(); !cursorProjectParticipants.isAfterLast(); cursorProjectParticipants.moveToNext()) {
                    Participant newParticipant = new Participant();
                    newParticipant.name = cursorProjectParticipants.getString(cursorProjectParticipants.getColumnIndex(budgetSplitContract.projectsParticipantsDetailsRO.COLUMN_PARTICIPANT_NAME));
                    newParticipant.uniqueId = cursorProjectParticipants.getString(cursorProjectParticipants.getColumnIndex(budgetSplitContract.projectsParticipantsDetailsRO.COLUMN_PARTICIPANT_UNIQUE_ID));
                    newParticipant.id = cursorProjectParticipants.getLong(cursorProjectParticipants.getColumnIndex(budgetSplitContract.projectsParticipantsDetailsRO.COLUMN_PARTICIPANT_ID));
                    selectedContacts.add(newParticipant);
                }
                Intent intent = new Intent(getActivity(), ContactChooser.class);
                intent.putExtra(ContactChooser.EXTRA_SELECTED_PARTICIPANTS, selectedContacts);
                startActivityForResult(intent, REQUEST_CHOOSE_CONTACT);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSE_CONTACT:
                if (resultCode == Activity.RESULT_OK) {
                    Participant tempParticipant = new Participant();
                    ArrayList<Participant> selectedParticipants = data.getParcelableArrayListExtra(ContactChooser.RESULT_EXTRA_SELECTED_PARTICIPANTS);
                    ArrayList<ContentProviderOperation> operations = new ArrayList<>();
                    ArrayList<String> participantsToDelete = new ArrayList<>();
                    for (cursorProjectParticipants.moveToFirst(); !cursorProjectParticipants.isAfterLast(); cursorProjectParticipants.moveToNext()) {
                        tempParticipant.name = cursorProjectParticipants.getString(cursorProjectParticipants.getColumnIndex(budgetSplitContract.projectsParticipantsDetailsRO.COLUMN_PARTICIPANT_NAME));
                        tempParticipant.id = cursorProjectParticipants.getLong(cursorProjectParticipants.getColumnIndex(budgetSplitContract.projectsParticipantsDetailsRO.COLUMN_PARTICIPANT_ID));
                        tempParticipant.uniqueId = cursorProjectParticipants.getString(cursorProjectParticipants.getColumnIndex(budgetSplitContract.projectsParticipantsDetailsRO.COLUMN_PARTICIPANT_UNIQUE_ID));

                        if (!selectedParticipants.remove(tempParticipant)) {
                            if (!adminUniqueId.equals(tempParticipant.uniqueId)) {
                                participantsToDelete.add(tempParticipant.name);
                                String selection = budgetSplitContract.projectParticipants.COLUMN_PARTICIPANTS_ID + " = ? AND "
                                        + budgetSplitContract.projectParticipants.COLUMN_PROJECTS_ID + " = ?";
                                String[] selectionArgs = {Long.toString(tempParticipant.id), Long.toString(projectId)};
                                operations.add(ContentProviderOperation.newDelete(budgetSplitContract.projectParticipants.CONTENT_URI).withSelection(selection, selectionArgs).build());
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.warning_cant_delete_project_admin), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    Uri uri = budgetSplitContract.projectParticipants.CONTENT_URI;
                    for (Participant participant : selectedParticipants) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(budgetSplitContract.projectParticipants.COLUMN_PARTICIPANTS_ID, participant.id);
                        contentValues.put(budgetSplitContract.projectParticipants.COLUMN_PROJECTS_ID, projectId);
                        operations.add(ContentProviderOperation.newInsert(uri).withValues(contentValues).build());
                    }
                    try {
                        ContentProviderResult[] contentProviderResults = getActivity().getContentResolver().applyBatch(budgetSplitContract.AUTHORITY, operations);
                        getActivity().getContentResolver().notifyChange(budgetSplitContract.projectParticipants.CONTENT_URI, null);
                        getActivity().getContentResolver().notifyChange(budgetSplitContract.projectsParticipantsDetailsRO.CONTENT_URI, null);
                        getActivity().getContentResolver().notifyChange(budgetSplitContract.projectParticipantsDetailsCalculateRO.CONTENT_URI, null);
                        getActivity().getContentResolver().notifyChange(budgetSplitContract.projectsDetailsRO.CONTENT_URI, null);

                        for (int i = 0; i < contentProviderResults.length; i++) {
                            if (contentProviderResults[i].count != null) {
                                if (contentProviderResults[i].count == 0) {
                                    Toast.makeText(getActivity(), getString(R.string.coulnt_delete_participant) + " " + participantsToDelete.get(i) + " " + getString(R.string.because_there_were_still_items_linked_to), Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (OperationApplicationException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    class ParticipantsAdapter extends CursorAdapter {

        LayoutInflater inflater;

        public ParticipantsAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
            inflater = getActivity().getLayoutInflater();
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return inflater.inflate(R.layout.fragment_project_participants_row, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ImageView isAdminView = (ImageView) view.findViewById(R.id.imageView);
            TextView participantNameView = (TextView) view.findViewById(R.id.textView_tags_title);
            TextView isVirtualView = (TextView) view.findViewById(R.id.textViewVirtual);

            cursorProject.moveToFirst();
            String uniqueIdOfParticipant = cursor.getString(cursor.getColumnIndex(budgetSplitContract.projectsParticipantsDetailsRO.COLUMN_PARTICIPANT_UNIQUE_ID));
            boolean isProjectAdmin = false;
            if (uniqueIdOfParticipant != null) {
                isProjectAdmin = uniqueIdOfParticipant.equals(cursorProject.getString(cursorProject.getColumnIndex(budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_ADMIN_UNIQUEID)));
            }
            int isVirtual = cursor.getInt(cursor.getColumnIndex(budgetSplitContract.projectsParticipantsDetailsRO.COLUMN_PARTICIPANT_IS_VIRTUAL));
            if (isProjectAdmin) {
                isAdminView.setVisibility(View.VISIBLE);
            } else {
                isAdminView.setVisibility(View.INVISIBLE);
            }
            if (isVirtual > 0) {
                isVirtualView.setVisibility(View.VISIBLE);
            } else {
                isVirtualView.setVisibility(View.INVISIBLE);
            }
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
                cursor.moveToFirst();
                adminUniqueId = cursor.getString(cursor.getColumnIndex(budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_ADMIN_UNIQUEID));
                iAmAdmin = adminUniqueId.equals(myUniqueId);
                getActivity().invalidateOptionsMenu();
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
