package ch.ethz.itet.pps.budgetSplit;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProjectOverview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectOverview extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PROJECT_CONTENT_URI = "projectContentUri";
    static final int REQUEST_CREATE_ITEM = 1;
    static final int RESULT_CODE_ERROR = 3;
    static final String RESULT_EXTRA_ERROR_MESSAGE = "errorMessage";

    static final int LOADER_PROJECT = 1;
    private boolean loaderProjectFinished = false;
    static final int LOADER_EXPENSES = 2;
    private boolean loaderExpensesFinished = false;

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

    ProgressBar progressBar;

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
        View myView = inflater.inflate(R.layout.fragment_project_overview, container, false);
        progressBar = (ProgressBar) myView.findViewById(R.id.projectOverviewProgressBar);
        Button addItemButton = (Button) myView.findViewById(R.id.buttonAddItem);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newItemIntent = new Intent(getActivity().getBaseContext(), add_new_item.class);
                newItemIntent.putExtra(add_new_item.EXTRA_PROJECT_DETAILS_URI, projectUri);
                startActivityForResult(newItemIntent, REQUEST_CREATE_ITEM);
            }
        });

        return myView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CREATE_ITEM:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getActivity(), getString(R.string.new_item_was_created), Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        //do nothing
                        break;
                    case RESULT_CODE_ERROR:
                        Toast.makeText(getActivity(), data.getStringExtra(RESULT_EXTRA_ERROR_MESSAGE), Toast.LENGTH_LONG).show();
                        break;
                }
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Show progressbar while Loading
        progressBar.setVisibility(View.VISIBLE);
        String[] projection;
        switch (i) {
            case LOADER_PROJECT:
                projection = new String[]{
                        budgetSplitContract.projectsDetailsRO._ID,
                        budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_NAME,
                        budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_DESCRIPTION,
                        budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_ADMIN_NAME,
                        budgetSplitContract.projectsDetailsRO.COLUMN_NR_OF_PARTICIPANTS,
                        budgetSplitContract.projectsDetailsRO.COLUMN_NR_OF_ITEMS};

                return new CursorLoader(getActivity(), projectUri, projection, null, null, null);
            case LOADER_EXPENSES:
                projection = new String[]{
                        budgetSplitContract.itemsDetailsRO.COLUMN_ITEM_PRICE};
                String selection = budgetSplitContract.itemsDetailsRO.COLUMN_PROJECT_ID + " = ?";
                String[] selectionArgs = {projectUri.getLastPathSegment()};
                return new CursorLoader(getActivity(), budgetSplitContract.itemsDetailsRO.CONTENT_URI, projection, selection, selectionArgs, null);
            default:
                throw new IllegalArgumentException("Unknown Loader Id.");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {


        //Refresh Data of GUI Elements
        switch (cursorLoader.getId()) {
            case LOADER_PROJECT:
                if (cursor.getCount() == 1) {
                    cursor.moveToFirst();
                    String projectName = cursor.getString(cursor.getColumnIndexOrThrow(budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_NAME));
                    String projectDescription = cursor.getString(cursor.getColumnIndexOrThrow(budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_DESCRIPTION));
                    String projectAdminName = cursor.getString(cursor.getColumnIndexOrThrow(budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_ADMIN_NAME));
                    int nrOfParticipants = cursor.getInt(cursor.getColumnIndexOrThrow(budgetSplitContract.projectsDetailsRO.COLUMN_NR_OF_PARTICIPANTS));
                    int nrOfItems = cursor.getInt(cursor.getColumnIndexOrThrow(budgetSplitContract.projectsDetailsRO.COLUMN_NR_OF_ITEMS));

                    ((TextView) getView().findViewById(R.id.projectName)).setText(projectName);
                    ((TextView) getView().findViewById(R.id.projectDescription)).setText(projectDescription);
                    ((TextView) getView().findViewById(R.id.administrator)).setText(projectAdminName);
                    ((TextView) getView().findViewById(R.id.CountOfParticipants)).setText(Integer.toString(nrOfParticipants));
                } else {
                    throw new IllegalArgumentException("Illegal Content-Uri. The returned Cursor for Project-Overview was either empty or contained more than one row.");
                }
                loaderProjectFinished = true;
                break;
            case LOADER_EXPENSES:
                double totalExpenses = 0;
                if (cursor.getCount() > 0) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        totalExpenses += cursor.getDouble(cursor.getColumnIndex(budgetSplitContract.itemsDetailsRO.COLUMN_ITEM_PRICE));
                    }
                }
                SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getActivity());
                long currencyId = Long.parseLong(preference.getString(getString(R.string.pref_default_currency), "-1"));
                Uri currencyUri = ContentUris.withAppendedId(budgetSplitContract.currencies.CONTENT_URI, currencyId);
                Cursor cursor1 = getActivity().getContentResolver().query(currencyUri, budgetSplitContract.currencies.PROJECTION_ALL, null, null, null);
                cursor1.moveToFirst();
                float exchangeRate = cursor1.getFloat(cursor1.getColumnIndex(budgetSplitContract.currencies.COLUMN_EXCHANGE_RATE));
                String currencyCode = cursor1.getString(cursor1.getColumnIndex(budgetSplitContract.currencies.COLUMN_CURRENCY_CODE));
                totalExpenses = totalExpenses * exchangeRate;
                ((TextView) getView().findViewById(R.id.expenses)).setText(new DecimalFormat(",##0.00").format(totalExpenses) + " " + currencyCode);
                loaderExpensesFinished = true;
                break;
        }
        //Hide Progressbar
        if (loaderProjectFinished && loaderExpensesFinished) {
            ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.projectOverviewProgressBar);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(LOADER_EXPENSES, null, this);
        getLoaderManager().initLoader(LOADER_PROJECT, null, this);
    }

    @Override
    public void onStop() {
        getLoaderManager().destroyLoader(LOADER_PROJECT);
        getLoaderManager().destroyLoader(LOADER_EXPENSES);
        super.onStop();
    }
}
