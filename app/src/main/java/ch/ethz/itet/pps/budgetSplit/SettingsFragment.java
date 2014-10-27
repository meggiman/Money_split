package ch.ethz.itet.pps.budgetSplit;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.ListPreference;
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


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //Populate ListPreference defaultCurrency with data from database.
        String[] projection = {budgetSplitContract.currencies.COLUMN_NAME, budgetSplitContract.currencies.COLUMN_CURRENCY_CODE};
        Cursor cursor = getActivity().getContentResolver().query(budgetSplitContract.currencies.CONTENT_URI, projection, null, null, budgetSplitContract.currencies.COLUMN_NAME);

        String[] currencies = new String[cursor.getCount()];
        String[] currencyCodes = new String[cursor.getCount()];
        if (cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                currencies[cursor.getPosition()] = cursor.getString(0);
                currencyCodes[cursor.getPosition()] = cursor.getString(1);
            }
        }
        ListPreference defaultCurrency = (ListPreference) findPreference(getResources().getString(R.string.pref_default_currency));
        defaultCurrency.setEntries(currencies);
        defaultCurrency.setEntryValues(currencyCodes);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_userName))) {
            long userId = sharedPreferences.getLong(getString(R.string.pref_user_id), -1);
            Uri contentUri = ContentUris.withAppendedId(budgetSplitContract.participants.CONTENT_URI, userId);
            ContentValues values = new ContentValues();
            values.put(budgetSplitContract.participants.COLUMN_NAME, sharedPreferences.getString(getString(R.string.pref_userName), ""));
            getActivity().getContentResolver().update(contentUri, values, null, null);
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
