package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.List;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


public class currencies extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_CURRENCIES = 1;

    private SimpleCursorAdapter currencyAdapter;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currencies);
        ListView currencyList = (ListView) findViewById(R.id.listViewCurrencies);
        String[] from = {budgetSplitContract.currencies.COLUMN_CURRENCY_CODE, budgetSplitContract.currencies.COLUMN_NAME, budgetSplitContract.currencies.COLUMN_EXCHANGE_RATE};
        int[] to = {R.id.textViewCurrencyCode, R.id.textViewCurrencyName, R.id.textViewCurrencyExchangeRate};
        currencyAdapter = new SimpleCursorAdapter(this, R.layout.activity_currencies_row, null, from, to, 0);
        currencyList.setAdapter(currencyAdapter);
        Button addCurrency = (Button) findViewById(R.id.buttonAddCurrency);
        addCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onResume() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setIndeterminate(true);
        }
        getLoaderManager().initLoader(LOADER_CURRENCIES, null, this);
        super.onResume();
    }

    @Override
    protected void onStop() {
        getLoaderManager().destroyLoader(LOADER_CURRENCIES);
        super.onStop();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case LOADER_CURRENCIES:
                progressDialog.show();
                return new CursorLoader(this, budgetSplitContract.currencies.CONTENT_URI, budgetSplitContract.currencies.PROJECTION_ALL, null, null, null);
            default:
                throw new IllegalArgumentException("Invalid LoaderCode " + i);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        switch (cursorLoader.getId()) {
            case LOADER_CURRENCIES:
                currencyAdapter.swapCursor(cursor);
                progressDialog.dismiss();
                break;
            default:
                throw new IllegalArgumentException("Illegal LoaderCode nr " + cursorLoader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        currencyAdapter.changeCursor(null);
    }
}
