package ch.ethz.itet.pps.budgetSplit;


import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {


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
}
