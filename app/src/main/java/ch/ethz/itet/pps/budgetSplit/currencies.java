package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


public class currencies extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_CURRENCIES = 1;

    private SimpleCursorAdapter currencyAdapter;

    private ProgressDialog progressDialog;

    private CreateCurrency createCurrency;

    private ActionMode actionMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currencies);
        final ListView currencyList = (ListView) findViewById(R.id.listViewCurrencies);
        String[] from = {budgetSplitContract.currencies.COLUMN_CURRENCY_CODE, budgetSplitContract.currencies.COLUMN_NAME, budgetSplitContract.currencies.COLUMN_EXCHANGE_RATE};
        int[] to = {R.id.textViewCurrencyCode, R.id.textViewExcludeItems, R.id.textViewCurrencyExchangeRate};
        currencyAdapter = new SimpleCursorAdapter(this, R.layout.activity_currencies_row, null, from, to, 0);
        currencyList.setAdapter(currencyAdapter);
        currencyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currencyCursor = (Cursor) adapterView.getItemAtPosition(i);
                String currencyCode = currencyCursor.getString(currencyCursor.getColumnIndex(budgetSplitContract.currencies.COLUMN_CURRENCY_CODE));
                String currencyName = currencyCursor.getString(currencyCursor.getColumnIndex(budgetSplitContract.currencies.COLUMN_NAME));
                Double currencyExchangeRate = currencyCursor.getDouble(currencyCursor.getColumnIndex(budgetSplitContract.currencies.COLUMN_EXCHANGE_RATE));
                Uri currencyUri = ContentUris.withAppendedId(budgetSplitContract.currencies.CONTENT_URI, l);

                showCreateCurrencyDialog();
                createCurrency.isNewCurrency = false;
                createCurrency.currencyCodeEditText.setText(currencyCode);
                createCurrency.currencyNameEditText.setText(currencyName);
                createCurrency.currencyRateEditText.setText(currencyExchangeRate.toString());
                createCurrency.currencyUri = currencyUri;
                createCurrency.dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(getString(R.string.update));
            }
        });
        currencyList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (actionMode != null) {
                    return false;
                }
                actionMode = currencies.this.startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        actionMode.getMenuInflater().inflate(R.menu.currencies_select, menu);
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
                                AlertDialog.Builder myBuilder = new AlertDialog.Builder(currencies.this);
                                myBuilder.setTitle(getString(R.string.delete_currency));
                                myBuilder.setMessage(getString(R.string.warning_delete_currency));
                                myBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        long currencyId = currencyList.getItemIdAtPosition(currencyList.getCheckedItemPosition());
                                        Uri currencyToDeleteUri = ContentUris.withAppendedId(budgetSplitContract.currencies.CONTENT_URI, currencyId);
                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                        if (Long.parseLong(preferences.getString(getString(R.string.pref_default_currency), "-1")) != currencyId) {
                                            try {
                                                getContentResolver().delete(currencyToDeleteUri, null, null);
                                            } catch (SQLiteConstraintException e) {
                                                Toast.makeText(getBaseContext(), getString(R.string.toast_currency_was_not_deleted), Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(getBaseContext(), getString(R.string.warning_can_not_delete_default_currency), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                myBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                                myBuilder.create().show();
                                actionMode.finish();
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode actionMode) {
                        currencies.this.actionMode = null;
                    }
                });
                Cursor checkedCurrency = (Cursor) adapterView.getItemAtPosition(i);
                actionMode.setTitle(checkedCurrency.getString(checkedCurrency.getColumnIndex(budgetSplitContract.currencies.COLUMN_NAME)));
                currencyList.setItemChecked(i, true);
                return true;
            }
        });
        Button addCurrency = (Button) findViewById(R.id.buttonAddCurrency);
        addCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCurrency = null;
                showCreateCurrencyDialog();
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

    class CreateCurrency {
        AlertDialog dialog;
        EditText currencyNameEditText;
        EditText currencyCodeEditText;
        EditText currencyRateEditText;
        boolean isNewCurrency = true;
        Uri currencyUri;
    }

    private void showNewCurrencyToast() {
        Toast.makeText(this, getString(R.string.currency_saved), Toast.LENGTH_SHORT).show();
    }

    private void showCreateCurrencyDialog() {
        if (createCurrency == null) {
            createCurrency = new CreateCurrency();
            AlertDialog.Builder myDialogBuilder = new AlertDialog.Builder(this);
            myDialogBuilder.setTitle(getString(R.string.create_new_currency));
            View newCurrencyView = getLayoutInflater().inflate(R.layout.activity_currencies_add_new_currency, null);
            createCurrency.currencyCodeEditText = (EditText) newCurrencyView.findViewById(R.id.editTextCode);
            createCurrency.currencyCodeEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().length() > 3) {
                        editable.delete(editable.length() - 1, editable.length());
                    }
                    if (editable.toString().matches(".*[a-z].*")) {
                        editable.replace(0, editable.length(), editable.toString().toUpperCase());
                    }
                }
            });
            createCurrency.currencyNameEditText = (EditText) newCurrencyView.findViewById(R.id.editTextCurrencyName);
            createCurrency.currencyRateEditText = (EditText) newCurrencyView.findViewById(R.id.editTextCurrencyRate);
            myDialogBuilder.setView(newCurrencyView);
            myDialogBuilder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            myDialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            createCurrency.dialog = myDialogBuilder.create();
        }
        createCurrency.dialog.show();
        createCurrency.dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (createCurrency.currencyCodeEditText.getText().toString().length() != 3) {
                    Animation shake = AnimationUtils.loadAnimation(currencies.this, R.anim.shake);
                    createCurrency.currencyCodeEditText.startAnimation(shake);
                    Toast.makeText(getBaseContext(), getString(R.string.please_enter_valid_currency_code), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (createCurrency.currencyNameEditText.getText().toString().isEmpty()) {
                    Animation shake = AnimationUtils.loadAnimation(currencies.this, R.anim.shake);
                    createCurrency.currencyNameEditText.startAnimation(shake);
                    Toast.makeText(getBaseContext(), getString(R.string.please_enter_valid_currencyname), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (createCurrency.currencyRateEditText.getText().toString().isEmpty() || Double.parseDouble(createCurrency.currencyRateEditText.getText().toString()) == 0) {
                    Animation shake = AnimationUtils.loadAnimation(currencies.this, R.anim.shake);
                    createCurrency.currencyRateEditText.startAnimation(shake);
                    Toast.makeText(getBaseContext(), getString(R.string.please_enter_valid_exchange_rate), Toast.LENGTH_SHORT).show();
                    return;
                }
                String currencyCode = createCurrency.currencyCodeEditText.getText().toString();
                String currencyName = createCurrency.currencyNameEditText.getText().toString();
                Double exchangeRate = Double.parseDouble(createCurrency.currencyRateEditText.getText().toString());
                ContentValues contentValues = new ContentValues();
                contentValues.put(budgetSplitContract.currencies.COLUMN_CURRENCY_CODE, currencyCode);
                contentValues.put(budgetSplitContract.currencies.COLUMN_NAME, currencyName);
                contentValues.put(budgetSplitContract.currencies.COLUMN_EXCHANGE_RATE, exchangeRate);
                try {
                    if (createCurrency.isNewCurrency) {
                        getContentResolver().insert(budgetSplitContract.currencies.CONTENT_URI, contentValues);
                    } else {
                        int i = getContentResolver().update(createCurrency.currencyUri, contentValues, null, null);
                        if (i < 1) {
                            throw new SQLiteException("Currency wasn't updated");
                        }
                    }
                } catch (SQLiteException e) {
                    Animation shake = AnimationUtils.loadAnimation(currencies.this, R.anim.shake);
                    createCurrency.dialog.getButton(DialogInterface.BUTTON_POSITIVE).startAnimation(shake);
                    Toast.makeText(currencies.this, getString(R.string.warning_currency_already_exists), Toast.LENGTH_SHORT).show();
                    return;
                }
                createCurrency.dialog.dismiss();
                createCurrency = null;
                showNewCurrencyToast();
            }
        });
    }
}



