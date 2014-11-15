package ch.ethz.itet.pps.budgetSplit;

import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;

public class ProjectNavigation extends Activity implements ActionBar.TabListener {

    /**
     * Extra messages to use for Intent to start this Activity.
     */
    public final static String EXTRA_CONTENT_URI = "projectContentUri"; // Information mitgegeben mit Intent
    public final static String EXTRA_PROJECT_TITLE = "projectTitle";
    /**
     * Integer constants to identify the sections of our SectionAdapter
     */
    private final static int SECTION_OVERVIEW = 0;
    private final static int SECTION_ITEM_LIST = 1;
    private final static int SECTION_PARTICIPANTS = 2;
    private final static int SECTION_SUMMARY = 3;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    /**
     * Uri of the Project that is loaded at the moment.
     */
    Uri projectContentUri;

    AlertDialog deleteProjectDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_navigation);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        //Set Activity title that appear in the upper left corner of the screen to match the project name.
        setTitle(getIntent().getStringExtra(EXTRA_PROJECT_TITLE));
        //Get the Uri of the project to be load out of the Intent bundle.
        projectContentUri = getIntent().getParcelableExtra(EXTRA_CONTENT_URI);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String myuniqueId = preferences.getString(getString(R.string.pref_user_unique_id), "uniqueIdNotFound");
        Cursor project = getContentResolver().query(projectContentUri, new String[]{budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_ADMIN_UNIQUEID}, null, null, null);
        project.moveToFirst();
        if (project.getString(project.getColumnIndex(budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_ADMIN_UNIQUEID)).equals(myuniqueId)) {
            getMenuInflater().inflate(R.menu.project_navigation_admin, menu);
        } else {
            getMenuInflater().inflate(R.menu.project_navigation, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete_project:
                if (deleteProjectDialog == null) {
                    AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(this);
                    myAlertBuilder.setTitle(getString(R.string.action_delete_project));
                    myAlertBuilder.setMessage(getString(R.string.warning_message_project_delete));
                    myAlertBuilder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getContentResolver().delete(ContentUris.withAppendedId(budgetSplitContract.projects.CONTENT_URI, ContentUris.parseId(projectContentUri)), null, null);
                            setResult(Main.RESULT_PROJECT_DELETED);
                            finish();
                        }
                    });
                    myAlertBuilder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    deleteProjectDialog = myAlertBuilder.create();
                }
                deleteProjectDialog.show();
                return true;
            case R.id.action_currencies:
                Intent intent = new Intent(this, Currencies.class);
                startActivity(intent);
            default:
                return false;
        }

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return the correct Fragment for each tab.
            switch (position) {
                case SECTION_OVERVIEW:
                    return ProjectOverview.newInstance(projectContentUri);
                case SECTION_ITEM_LIST:
                    return ProjectItems.newInstance(projectContentUri);
                case SECTION_PARTICIPANTS:
                    return projectParticipants.newInstance(projectContentUri);
                case SECTION_SUMMARY:
                    return ProjectSummary.newInstance(projectContentUri);
                default:
                    throw new IllegalArgumentException("Illegal Section Number: " + position);
            }
        }

        @Override
        public int getCount() {
            // Show 4 pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case SECTION_OVERVIEW:
                    return getString(R.string.title_section_overview).toUpperCase(l);
                case SECTION_ITEM_LIST:
                    return getString(R.string.items).toUpperCase(l);
                case SECTION_PARTICIPANTS:
                    return getString(R.string.member).toUpperCase(l);
                case SECTION_SUMMARY:
                    return getString(R.string.project_summary_title).toUpperCase(l);
            }
            return null;
        }
    }


}
