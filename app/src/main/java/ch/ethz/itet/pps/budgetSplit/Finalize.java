package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class Finalize extends FragmentActivity {

    public static final String EXTRA_FINALIZE_PROJECT_URI = "projectContentUri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalize);


        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                // If activity has already been initialized it should not do it again.
                return;
            }
            ProjectSummary summaryFragment = ProjectSummary.newInstance((Uri) getIntent().getParcelableExtra(EXTRA_FINALIZE_PROJECT_URI));
            getFragmentManager().beginTransaction().add(R.id.fragment_container, summaryFragment).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.finalize, menu);
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
