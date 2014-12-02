package ch.ethz.itet.pps.budgetSplit;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProjectSummary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectSummary extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PROJECT_URI = "projectUri";

    private Uri projectUri;
    private double defaultExchangeRate;
    private String defaultCurrencyCode;

    // Loader ID's
    static final int LOADER_PROJECT_PARTICIPANT_DETAILS_CALCULATE = 0;
    private boolean projectParticipantDetailsCalculateFinished = false;
    static final int LOADER_PARTICIPANT_TAGS_DETAILS = 1;
    private boolean participantTagsDetailsFinished = false;
    static final int LOADER_ITEMS = 2;
    private boolean itemsDetailsFinished = false;

    // memory for all the loaded Data
    Cursor projectCursor;
    Cursor tagCursor;

    // Fills the Listview Adapter
    ParticipantTagsLinker[] data;

    // GUI elements
    View mainView;
    ProgressBar progressBar;
    TextView expences;
    TextView expences1;
    TextView nrOfItems;
    TextView nrOfItems1;
    ListView list;
    Button transactions;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contentUri Parameter 1.
     * @return A new instance of fragment ProjectSummary.
     */
    public static ProjectSummary newInstance(Uri contentUri) {
        ProjectSummary fragment = new ProjectSummary();
        Bundle args = new Bundle();
        args.putParcelable(PROJECT_URI, contentUri);
        fragment.setArguments(args);
        return fragment;
    }

    public ProjectSummary() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectUri = getArguments().getParcelable(PROJECT_URI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_project_summary, container, false);
        progressBar = (ProgressBar) mainView.findViewById(R.id.summaryProgressBar);
        getLoaderManager().initLoader(LOADER_PROJECT_PARTICIPANT_DETAILS_CALCULATE, null, this);
        getLoaderManager().initLoader(LOADER_ITEMS, null, this);
        getLoaderManager().initLoader(LOADER_PARTICIPANT_TAGS_DETAILS, null, this);

        //Initialize Loader who need participant Ids List

        transactions = (Button) mainView.findViewById(R.id.fragment_summary_button);
        transactions.setEnabled(false);
        nrOfItems1 = (TextView) mainView.findViewById(R.id.fragment_summary_textview_nr_items);
        expences1 = (TextView) mainView.findViewById(R.id.summary_listview_tags);
        expences = (TextView) mainView.findViewById(R.id.totalExpenses1);
        nrOfItems = (TextView) mainView.findViewById(R.id.nr_of_Items);


        //Hide Progressbar
//        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.summaryProgressBar);
        //   progressBar.setVisibility(View.GONE);


        return mainView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //Show progressbar while Loading
        progressBar.setVisibility(View.VISIBLE);
        String[] projection;
        String[] selectionArgs;
        String selection;
        String column;

        Long projectId = (ContentUris.parseId(projectUri));

        switch (i) {

            case LOADER_PROJECT_PARTICIPANT_DETAILS_CALCULATE:
                projection = new String[]{
                        budgetSplitContract.projectParticipantsDetailsCalculateRO.COLUMN_PARTICIPANT_NAME,
                        budgetSplitContract.projectParticipantsDetailsCalculateRO.COLUMN_PARTICIPANT_ID,
                        budgetSplitContract.projectParticipantsDetailsCalculateRO.COLUMN_PARTICIPANT_TOTAL_SHARE,
                        budgetSplitContract.projectParticipantsDetailsCalculateRO.COLUMN_PARTICIPANT_TOTAL_DEPTHS
                };
                String sortOrder = new String(budgetSplitContract.projectParticipantsDetailsCalculateRO.COLUMN_PARTICIPANT_ID + " ASC");
                return new CursorLoader(getActivity(), ContentUris.withAppendedId(budgetSplitContract.projectParticipantsDetailsCalculateRO.CONTENT_URI, projectId), projection, null, null, sortOrder);

            case LOADER_PARTICIPANT_TAGS_DETAILS:
                projection = new String[]{
                        budgetSplitContract.participantsTagsDetails.COLUMN_PARTICIPANT_ID,
                        budgetSplitContract.participantsTagsDetails.COLUMN_TAG_NAME,
                };
                String sortOrder1 = new String(budgetSplitContract.participantsTagsDetails.COLUMN_PARTICIPANT_ID + " ASC");
                return new CursorLoader(getActivity(), budgetSplitContract.participantsTagsDetails.CONTENT_URI_ALL, projection, null, null, sortOrder1);

            case LOADER_ITEMS:
                projection = new String[]{
                        budgetSplitContract.itemsDetailsRO.COLUMN_ITEM_PRICE};
                selection = budgetSplitContract.itemsDetailsRO.COLUMN_PROJECT_ID + " = ?";
                selectionArgs = new String[]{projectUri.getLastPathSegment()};
                return new CursorLoader(getActivity(), budgetSplitContract.itemsDetailsRO.CONTENT_URI, projection, selection, selectionArgs, null);
            default:
                throw new IllegalArgumentException("Unknown Loader");

        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        switch (cursorLoader.getId()) {

            case LOADER_PROJECT_PARTICIPANT_DETAILS_CALCULATE:
                projectCursor = cursor;
                projectParticipantDetailsCalculateFinished = true;
                break;

            case LOADER_PARTICIPANT_TAGS_DETAILS:
                tagCursor = cursor;
                participantTagsDetailsFinished = true;
                break;

            case LOADER_ITEMS:
                // Sum up all the item Costs for the Expences Display
                double finalExpenses = 0;
                if (cursor.getCount() > 0) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        finalExpenses += cursor.getDouble(cursor.getColumnIndex(budgetSplitContract.itemsDetailsRO.COLUMN_ITEM_PRICE));
                    }
                }
                SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getActivity());
                long currencyId = Long.parseLong(preference.getString(getString(R.string.pref_default_currency), "-1"));
                Uri currencyUri = ContentUris.withAppendedId(budgetSplitContract.currencies.CONTENT_URI, currencyId);
                Cursor cursor1 = getActivity().getContentResolver().query(currencyUri, budgetSplitContract.currencies.PROJECTION_ALL, null, null, null);
                cursor1.moveToFirst();
                defaultExchangeRate = cursor1.getFloat(cursor1.getColumnIndex(budgetSplitContract.currencies.COLUMN_EXCHANGE_RATE));
                defaultCurrencyCode = cursor1.getString(cursor1.getColumnIndex(budgetSplitContract.currencies.COLUMN_CURRENCY_CODE));
                finalExpenses = finalExpenses / defaultExchangeRate;
                expences = (TextView) getView().findViewById(R.id.totalExpenses1);
                expences.setText(new DecimalFormat(",##0.00").format(finalExpenses) + " " + defaultCurrencyCode);
                nrOfItems = (TextView) getView().findViewById(R.id.nr_of_Items);
                nrOfItems.setText(Integer.toString(cursor.getCount()));
                itemsDetailsFinished = true;
                break;

            default:
                throw new IllegalArgumentException("Unknown Loader");

        }

        if (projectParticipantDetailsCalculateFinished && participantTagsDetailsFinished && itemsDetailsFinished) {
            int capacity = projectCursor.getCount();
            data = new ParticipantTagsLinker[capacity];
            int i = 0;
            for (projectCursor.moveToFirst(); !projectCursor.isAfterLast(); projectCursor.moveToNext()) {
                data[i] = new ParticipantTagsLinker();
                data[i].name = projectCursor.getString(projectCursor.getColumnIndex(budgetSplitContract.projectParticipantsDetailsCalculateRO.COLUMN_PARTICIPANT_NAME));
                data[i].expenses = projectCursor.getDouble(projectCursor.getColumnIndex(budgetSplitContract.projectParticipantsDetailsCalculateRO.COLUMN_PARTICIPANT_TOTAL_SHARE));
                data[i].depths = projectCursor.getDouble(projectCursor.getColumnIndex(budgetSplitContract.projectParticipantsDetailsCalculateRO.COLUMN_PARTICIPANT_TOTAL_DEPTHS));
                StringBuffer tags = new StringBuffer();
                tagCursor.moveToFirst();
                long participantId = projectCursor.getLong(projectCursor.getColumnIndex(budgetSplitContract.projectParticipantsDetailsCalculateRO.COLUMN_PARTICIPANT_ID));
                for (tagCursor.moveToFirst(); !tagCursor.isAfterLast(); tagCursor.moveToNext()) {
                    long tagFilterParticipantId = tagCursor.getLong(tagCursor.getColumnIndex(budgetSplitContract.participantsTagsDetails.COLUMN_PARTICIPANT_ID));
                    if (participantId == tagFilterParticipantId) {
                        tags.append(tagCursor.getString(tagCursor.getColumnIndex(budgetSplitContract.participantsTagsDetails.COLUMN_TAG_NAME)));
                        tags.append(" ");
                    } else if (participantId < tagFilterParticipantId) {
                        break;
                    }
                }
                data[i].tags = tags.toString().trim();
                i++;
            }
            // Set GUI elements
            ParticipantTagsLinkerAdapter adapter = new ParticipantTagsLinkerAdapter(getActivity(), R.layout.fragment_project_summary_listview_row, data);
            list = (ListView) mainView.findViewById(R.id.fragment_summary_listview);
            list.setAdapter(adapter);

            //Hide Progressbar
            ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.summaryProgressBar);
            progressBar.setVisibility(View.GONE);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    /**
     * Created by Chrissy on 06.11.2014.
     */
    public class ParticipantTagsLinker {
        public String name;
        public String tags;
        public Double expenses;
        public Double depths;

        public ParticipantTagsLinker() {
            super();
        }

        public ParticipantTagsLinker(String name, String tags, Double expenses, Double depths) {
            super();
            this.name = name;
            this.tags = tags;
            this.expenses = expenses;
            this.depths = depths;
        }
    }


    /**
     * Created by Chrissy on 06.11.2014.
     */
    public class ParticipantTagsLinkerAdapter extends ArrayAdapter<ParticipantTagsLinker> {

        Context context;
        int layoutResourceId;
        ParticipantTagsLinker[] data = null;

        public ParticipantTagsLinkerAdapter(Context c, int l, ParticipantTagsLinker[] d) {
            super(c, l, d);
            this.context = c;
            this.layoutResourceId = l;
            this.data = d;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ParticipantTagsLinkerHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new ParticipantTagsLinkerHolder();
                holder.name = (TextView) row.findViewById(R.id.summary_listview_participant_name);
                holder.expences = (TextView) row.findViewById(R.id.summary_listview_expences);
                holder.tags = (TextView) row.findViewById(R.id.summary_listview_tags);
                holder.depths = (TextView) row.findViewById(R.id.textViewDepths);
                holder.variable = (TextView) row.findViewById(R.id.summary_variable);

                row.setTag(holder);
            } else {
                holder = (ParticipantTagsLinkerHolder) row.getTag();
            }

            ParticipantTagsLinker ptl = data[position];
            holder.name.setText(ptl.name);
            holder.expences.setText(new DecimalFormat(",##0.00").format(ptl.expenses / defaultExchangeRate) + " " + defaultCurrencyCode);
            holder.depths.setText(new DecimalFormat(",##0.00").format(ptl.depths / defaultExchangeRate) + " " + defaultCurrencyCode);
            if (ptl.depths > 0) {
                holder.depths.setTextColor(Color.RED);
                holder.variable.setText("Debths:");
            } else {
                holder.depths.setTextColor(getResources().getColor(R.color.primary_dark_green));
                holder.variable.setText("Refund:");

            }
            holder.tags.setText(ptl.tags);

            return row;
        }

        class ParticipantTagsLinkerHolder {
            TextView name;
            TextView tags;
            TextView expences;
            TextView depths;
            TextView variable;
        }
    }

}
