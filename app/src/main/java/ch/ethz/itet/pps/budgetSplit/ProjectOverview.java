package ch.ethz.itet.pps.budgetSplit;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProjectOverview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectOverview extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PROJECT_CONTENT_URI = "projectContentUri";

    private Uri projectUri;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contentUri Parameter 1.
     * @return A new instance of fragment ProjectOverview.
     */
    public static ProjectOverview newInstance(Uri contentUri) {
        ProjectOverview fragment = new ProjectOverview();
        Bundle args = new Bundle();
        args.putParcelable(PROJECT_CONTENT_URI, contentUri);
        fragment.setArguments(args);
        return fragment;
    }

    public ProjectOverview() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectUri = getArguments().getParcelable(PROJECT_CONTENT_URI);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_overview, container, false);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Show progressbar while Loading
        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.projectOverviewProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        final String[] PROJECTION = {
                budgetSplitContract.projectsDetailsRO._ID,
                budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_NAME,
                budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_DESCRIPTION,
                budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_ADMIN_NAME,
                budgetSplitContract.projectsDetailsRO.COLUMN_NR_OF_PARTICIPANTS,
                budgetSplitContract.projectsDetailsRO.COLUMN_NR_OF_ITEMS};

        return new CursorLoader(getActivity(), projectUri, PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        //Hide Progressbar
        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.projectOverviewProgressBar);
        progressBar.setVisibility(View.GONE);

        //Refresh Data of GUI Elements
        if (cursor.getColumnCount() == 1) {
            cursor.moveToFirst();
            String projectName = cursor.getString(cursor.getColumnIndexOrThrow(budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_NAME));
            String projectDescription = cursor.getString(cursor.getColumnIndexOrThrow(budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_DESCRIPTION));
            String projectAdminName = cursor.getString(cursor.getColumnIndexOrThrow(budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_ADMIN_NAME));
            int nrOfParticipants = cursor.getInt(cursor.getColumnIndexOrThrow(budgetSplitContract.projectsDetailsRO.COLUMN_NR_OF_PARTICIPANTS));
            int nrOfItems = cursor.getInt(cursor.getColumnIndexOrThrow(budgetSplitContract.projectsDetailsRO.COLUMN_NR_OF_ITEMS));

            ((TextView) getView().findViewById(R.id.projectName)).setText(projectName);
            ((TextView) getView().findViewById(R.id.projectDescription)).setText(projectDescription);
            ((TextView) getView().findViewById(R.id.administrator)).setText(projectAdminName);
            ((TextView) getView().findViewById(R.id.CountOfParticipants)).setText(nrOfParticipants);
        } else {
            throw new IllegalArgumentException("Illegal Content-Uri. The returned Cursor for Project-Overview was either empty or contained more than one row.");
        }
        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
