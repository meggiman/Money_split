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


public class FirstScreen extends Activity {

    ContentValues newContactParticipant;
    String hashStringHex = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);


        // Load Google ID
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
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
        // Store Hashed Google Id as Unique_ID
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.pref_user_unique_id), hashStringHex);

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
                    newContactParticipant = new ContentValues();
                    newContactParticipant.put(budgetSplitContract.participants.COLUMN_UNIQUEID, 1234567890987654321L);//hashStringHex
                    newContactParticipant.put(budgetSplitContract.participants.COLUMN_NAME, usernameEditText.getText().toString());
                    newContactParticipant.put(budgetSplitContract.participants.COLUMN_ISVIRTUAL, true);
                    Uri yourUri = getContentResolver().insert(budgetSplitContract.participants.CONTENT_URI, newContactParticipant);
                    long userId = ContentUris.parseId(yourUri);
                    editor.putString(getString(R.string.pref_user_id), Long.toString(userId));
                    editor.putString(getString(R.string.pref_userName), usernameEditText.getText().toString());
                    editor.putBoolean(getString(R.string.pref_not_first_started), true);

                    // Start Main Activity
                    Intent mainActivity = new Intent(FirstScreen.this, Main.class);
                    startActivity(mainActivity);

                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.first_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
