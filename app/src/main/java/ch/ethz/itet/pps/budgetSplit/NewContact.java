package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;

public class NewContact extends Activity {

    //Inicialization
    Button newContactBluetooth;
    EditText newContactName;
    Uri nameUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        // Adding Objects
        newContactBluetooth = (Button) findViewById(R.id.search_bluetooth);
        newContactName = (EditText) findViewById(R.id.edit_text_create_contact_name);

        newContactBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (newContactName.getText() != null) {
                    ContentValues newContactParticipant = new ContentValues();
                    newContactParticipant.put(budgetSplitContract.participants.COLUMN_NAME, newContactName.getText().toString());
                    newContactParticipant.put(budgetSplitContract.participants.COLUMN_ISVIRTUAL, true);
                    nameUri = getContentResolver().insert(budgetSplitContract.participants.CONTENT_URI, newContactParticipant);
                }
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_contact, menu);
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
