package ch.ethz.itet.pps.budgetSplit;


import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.net.URI;
import java.util.List;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link projectParticipants#newInstance} factory method to
 * create an instance of this fragment.
 */
public class projectParticipants extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PROJECT_CONTENT_URI = "projectContentUri";
    // TODO: Rename and change types of parameters
    private Uri projectUri;
    private ListView participantsList;

    private int LOADER_PROJECT = 1;
    private boolean loaderProjectFinished = false;
    private int LOADER_PARTICIPANTS = 2;
    private boolean loaderParticipantsFinished = false;

    private long projectId;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param projectUri Parameter 1.
     * @return A new instance of fragment projectParticipants.
     */
    // TODO: Rename and change types and number of parameters
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
            throw new IllegalArgumentException("The adde uri wasn't a single row uri.");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentLayout = inflater.inflate(R.layout.fragment_project_participants, container, false);
        participantsList = (ListView) fragmentLayout.findViewById(R.id.listView);
        getLoaderManager().initLoader(LOADER_PARTICIPANTS, null, this);
        getLoaderManager().initLoader(LOADER_PROJECT, null, this);
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
            ImageView isAdmin = (ImageView) view.findViewById(R.id.imageView);
            TextView participantName = (TextView) view.findViewById(R.id.textViewName);
            TextView isVirtual = (TextView) view.findViewById(R.id.textViewName);
            //TODO Add projectParticipantsDetails Table to ContentProvider.

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
