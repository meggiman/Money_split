package ch.ethz.itet.pps.budgetSplit;


import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private OnDefaultCurrencyChangedListener mcallback;

    interface OnDefaultCurrencyChangedListener {
        void onDefaultCurrencyChanged();
    }


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mcallback = (OnDefaultCurrencyChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDefaultCurrencyChangedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //Populate ListPreference defaultCurrency with data from database.
        String[] projection = {budgetSplitContract.currencies._ID, budgetSplitContract.currencies.COLUMN_NAME, budgetSplitContract.currencies.COLUMN_CURRENCY_CODE};
        Cursor cursor = getActivity().getContentResolver().query(budgetSplitContract.currencies.CONTENT_URI, projection, null, null, budgetSplitContract.currencies.COLUMN_NAME);

        String[] currencyIds = new String[cursor.getCount()];
        String[] currencyCodes = new String[cursor.getCount()];
        if (cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                currencyIds[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(budgetSplitContract.currencies._ID));
                currencyCodes[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(budgetSplitContract.currencies.COLUMN_CURRENCY_CODE));
            }
        }
        ListPreference defaultCurrency = (ListPreference) findPreference(getResources().getString(R.string.pref_default_currency));
        defaultCurrency.setEntries(currencyCodes);
        defaultCurrency.setEntryValues(currencyIds);

        Preference tagfilter = findPreference(getString(R.string.pref_tagfilter));
        long id = PreferenceManager.getDefaultSharedPreferences(getActivity()).getLong(getString(R.string.pref_user_id), -1);
        String name = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_userName), "not found");
        Intent intent = new Intent(getActivity(), TagSelection.class);
        intent.putExtra(TagSelection.EXTRA_TAGFILTER_VISIBLE, true);
        intent.putExtra(TagSelection.EXTRA_ID, id);
        intent.putExtra(TagSelection.EXTRA_TITLE, name);
        tagfilter.setIntent(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_userName))) {
            long userId = sharedPreferences.getLong(getString(R.string.pref_user_id), -1);
            Uri contentUri = ContentUris.withAppendedId(budgetSplitContract.participants.CONTENT_URI, userId);
            ContentValues values = new ContentValues();
            values.put(budgetSplitContract.participants.COLUMN_NAME, sharedPreferences.getString(getString(R.string.pref_userName), ""));
            getActivity().getContentResolver().update(contentUri, values, null, null);
        } else if (key.equals(getString(R.string.pref_default_currency))) {
            mcallback.onDefaultCurrencyChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
