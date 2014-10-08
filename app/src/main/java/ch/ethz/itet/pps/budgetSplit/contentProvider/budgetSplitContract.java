package ch.ethz.itet.pps.budgetSplit.contentProvider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import ch.ethz.itet.pps.budgetSplit.contentProvider.database.budgetSplitDBSchema;

/**
 * Created by Manuel on 29.09.2014.
 */
public class budgetSplitContract {
    public static final String AUTHORITY = "ch.ethz.itet.pps_2014.budgetSplit";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class projects implements BaseColumns {
        static final String TABLE_PROJECTS = "projects";
        static final int PROJECTS = 10;
        static final int PROJECT = 11;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(budgetSplitContract.CONTENT_URI, TABLE_PROJECTS);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_PROJECTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_PROJECTS;
        public static final String COLUMN_PROJECT_NAME = budgetSplitDBSchema.projects.COLUMN_NAME;
        public static final String COLUMN_PROJECT_DESCRIPTION = budgetSplitDBSchema.projects.COLUMN_DESCRIPTION;
        public static final String COLUMN_PROJECT_OWNER = budgetSplitDBSchema.projects.COLUMN_ADMIN;
        public static final String[] PROJECTION_ALL = {_ID, COLUMN_PROJECT_NAME, COLUMN_PROJECT_DESCRIPTION, COLUMN_PROJECT_OWNER};
    }

    public static final class participants implements BaseColumns {
        static final String TABLE_PARTICIPANTS = "participants";
        static final int PARTICIPANTS = 20;
        static final int PARTICIPANT = 21;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(budgetSplitContract.CONTENT_URI, TABLE_PARTICIPANTS);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_PARTICIPANTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_PARTICIPANTS;
        public static final String COLUMN_NAME = budgetSplitDBSchema.participants.COLUMN_NAME;
        public static final String COLUMN_UNIQUEID = budgetSplitDBSchema.participants.COLUMN_UNIQUEID;
        public static final String COLUMN_ISVIRTUAL = budgetSplitDBSchema.participants.COLUMN_ISVIRTUAL;
        public static final String[] PROJECTION_ALL = {_ID, COLUMN_NAME, COLUMN_UNIQUEID, COLUMN_ISVIRTUAL};
    }

    public static final class tags implements BaseColumns {
        static final String TABLE_TAGS = "tags";
        static final int TAGS = 30;
        static final int TAG = 31;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(budgetSplitContract.CONTENT_URI, TABLE_TAGS);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_TAGS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_TAGS;
        public static final String COLUMN_NAME = "name";
        public static final String[] PROJECTION_ALL = {_ID, COLUMN_NAME};
    }

    public static final class projectsDetails implements BaseColumns {
        static final String TABLE_PROJECTS_DETAILS = "projectsDetails";
        static final int PROJECTS_DETAILS = 40;
        static final int PROJECT_DETAILS = 41;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(budgetSplitContract.CONTENT_URI, TABLE_PROJECTS_DETAILS);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_PROJECTS_DETAILS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_PROJECTS_DETAILS;
        public static final String COLUMN_PROJECT_NAME = budgetSplitDBSchema.projects_view.COLUMN_PROJECT_NAME;
        public static final String COLUMN_PROJECT_DESCRIPTION = budgetSplitDBSchema.projects_view.COLUMN_PROJECT_DESCRIPTION;
        public static final String COLUMN_PROJECT_ADMIN_ID = budgetSplitDBSchema.projects_view.COLUMN_PROJECT_ADMIN_ID;
        public static final String COLUMN_PROJECT_ADMIN_NAME = budgetSplitDBSchema.projects_view.COLUMN_PROJECT_ADMIN_NAME;
        public static final String COLUMN_PROJECT_ADMIN_UNIQUEID = budgetSplitDBSchema.projects_view.COLUMN_PROJECT_ADMIN_UNIQUEID;
        public static final String COLUMN_NR_OF_PARTICIPANTS = budgetSplitDBSchema.projects_view.COLUMN_NR_OF_PARTICIPANTS;
        public static final String COLUMN_NR_OF_ITEMS = budgetSplitDBSchema.projects_view.COLUMN_NR_OF_ITEMS;
        public static final String[] PROJECTION_ALL = {_ID, COLUMN_PROJECT_NAME, COLUMN_PROJECT_DESCRIPTION, COLUMN_PROJECT_ADMIN_ID, COLUMN_PROJECT_ADMIN_NAME, COLUMN_PROJECT_ADMIN_UNIQUEID, COLUMN_NR_OF_PARTICIPANTS, COLUMN_NR_OF_ITEMS};
    }
}
