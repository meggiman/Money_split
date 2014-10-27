package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


public class FirstScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        //Initialize GUI Elements
        EditText userName = (EditText) findViewById(R.id.first_screen_user_name);

        //Initialize Yourself as Contact (Name = Chrissy --> needs to be in the settings later)
        Uri yourUri;

        ContentValues newContactParticipant = new ContentValues();
        newContactParticipant.put(budgetSplitContract.participants.COLUMN_UNIQUEID, 9876654394386573L);
        newContactParticipant.put(budgetSplitContract.participants.COLUMN_NAME, "Christelle Gloor");
        newContactParticipant.put(budgetSplitContract.participants.COLUMN_ISVIRTUAL, true);
        yourUri = getContentResolver().insert(budgetSplitContract.participants.CONTENT_URI, newContactParticipant);
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
