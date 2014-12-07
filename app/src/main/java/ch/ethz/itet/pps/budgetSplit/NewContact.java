package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;

public class NewContact extends ActionBarActivity {

    //Inicialization
    Button newContactVirtual;
    private EditText newContactName;
    private CheckBox isVirtual;
    private Uri nameUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        // Adding Objects
        // Add to Button in Action Bar!!
        /*newContactVirtual = (Button) findViewById(R.id.search_bluetooth);
        newContactVirtual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = newContactName.getText().toString();

                if (name.trim().length() > 0) {
                    ContentValues newContactParticipant = new ContentValues();
                    newContactParticipant.put(budgetSplitContract.participants.COLUMN_NAME, newContactName.getText().toString().trim());
                    newContactParticipant.put(budgetSplitContract.participants.COLUMN_ISVIRTUAL, 1);
                    nameUri = getContentResolver().insert(budgetSplitContract.participants.CONTENT_URI, newContactParticipant);

                    // Add Id to Global Array
                    long contactId = ContentUris.parseId(nameUri);
                    GlobalStuffHelper.addParticipantsIds(contactId);
                    setResult(RESULT_OK);// overgives that the contact has succesfully been created to the activity new project
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.enter_a_name), Toast.LENGTH_SHORT).show();
                }

            }
        });*/
        newContactName = (EditText) findViewById(R.id.edit_text_create_contact_name);
        isVirtual = (CheckBox) findViewById(R.id.checkBox_virtual);
        // We can only add Virtual Contacts at the moment.
        isVirtual.setEnabled(false);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                String name = newContactName.getText().toString();

                if (name.trim().length() > 0) {
                    ContentValues newContactParticipant = new ContentValues();
                    newContactParticipant.put(budgetSplitContract.participants.COLUMN_NAME, newContactName.getText().toString().trim());
                    newContactParticipant.put(budgetSplitContract.participants.COLUMN_ISVIRTUAL, 1);
                    nameUri = getContentResolver().insert(budgetSplitContract.participants.CONTENT_URI, newContactParticipant);

                    // Add Id to Global Array
                    long contactId = ContentUris.parseId(nameUri);
                    GlobalStuffHelper.addParticipantsIds(contactId);
                    setResult(RESULT_OK);// overgives that the contact has succesfully been created to the activity new project
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.enter_a_name), Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
