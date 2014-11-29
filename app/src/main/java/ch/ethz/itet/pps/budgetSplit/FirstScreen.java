package ch.ethz.itet.pps.budgetSplit;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Pattern;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


public class FirstScreen extends ActionBarActivity {

    ContentValues newContactParticipant;
    String hashStringHex = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);


        //Initialize Views
        Button btnOK = (Button) findViewById(R.id.buttonOk);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load Data into shared Preferences
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                EditText usernameEditText = (EditText) findViewById(R.id.first_screen_user_name);

                // Load inputet User name and UniqueID into Database
                if (usernameEditText.getText().length() > 0) {


                    // Load Google ID
                    Pattern emailPattern = Patterns.EMAIL_ADDRESS;
                    Account[] accounts = AccountManager.get(getApplicationContext()).getAccountsByType("com.google");
                    String googleAccountName = "";
                    MessageDigest md;
                    byte[] hashedAccountNameBytes;
                    for (int i = 0; !emailPattern.matcher(accounts[i].name).matches() || i >= accounts.length; i++) {
                        googleAccountName = accounts[i].name;
                    }
                    try {
                        // Hash Google id
                        md = MessageDigest.getInstance("SHA-256");
                        md.update(googleAccountName.getBytes("UTF-8"));
                        hashedAccountNameBytes = md.digest();
                        StringBuffer buffer = new StringBuffer();
                        for (int i = 0; i < hashedAccountNameBytes.length; i++) {
                            buffer.append(Integer.toString((hashedAccountNameBytes[i] & 0xff) + 0x100, 16).substring(1));
                        }
                        hashStringHex = buffer.toString();

                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    //Save Data to database
                    newContactParticipant = new ContentValues();
                    newContactParticipant.put(budgetSplitContract.participants.COLUMN_UNIQUEID, hashStringHex);//hashStringHex
                    newContactParticipant.put(budgetSplitContract.participants.COLUMN_NAME, usernameEditText.getText().toString());
                    newContactParticipant.put(budgetSplitContract.participants.COLUMN_ISVIRTUAL, 0);
                    Uri yourUri = getContentResolver().insert(budgetSplitContract.participants.CONTENT_URI, newContactParticipant);
                    long userId = ContentUris.parseId(yourUri);


                    //Add CHF as first currency
                    ContentValues newCurrency = new ContentValues();
                    newCurrency.put(budgetSplitContract.currencies.COLUMN_NAME, getString(R.string.swiss_franc));
                    newCurrency.put(budgetSplitContract.currencies.COLUMN_CURRENCY_CODE, "CHF");
                    newCurrency.put(budgetSplitContract.currencies.COLUMN_EXCHANGE_RATE, 1);
                    Uri newCurrencyUri = getContentResolver().insert(budgetSplitContract.currencies.CONTENT_URI, newCurrency);
                    editor.putString(getString(R.string.pref_default_currency), newCurrencyUri.getLastPathSegment());

                    // Store all values to respective sharedPreferences-Keys.
                    editor.putString(getString(R.string.pref_user_unique_id), hashStringHex);
                    editor.putLong(getString(R.string.pref_user_id), userId);
                    editor.putString(getString(R.string.pref_userName), usernameEditText.getText().toString());
                    editor.putBoolean(getString(R.string.pref_not_first_started), true);
                    editor.commit();

                    // Go back to main activity
                    finish();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }
}
