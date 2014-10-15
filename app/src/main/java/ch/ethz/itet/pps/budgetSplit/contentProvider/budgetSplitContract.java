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
        public static final String COLUMN_NAME = budgetSplitDBSchema.tags.COLUMN_NAME;
        public static final String[] PROJECTION_ALL = {_ID, COLUMN_NAME};
    }

    public static final class currencies implements BaseColumns {
        static final String TABLE_CURRENCIES = "currencies";
        static final int CURRENCIES = 110;
        static final int CURRENCY = 111;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(budgetSplitContract.CONTENT_URI, TABLE_CURRENCIES);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_CURRENCIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_CURRENCIES;
        public static final String COLUMN_NAME = budgetSplitDBSchema.tags.COLUMN_NAME;
        public static final String COLUMN_CURRENCY_CODE = budgetSplitDBSchema.currencies.COLUMN_CURRENCY_CODE;
        public static final String COLUMN_EXCHANGE_RATE = budgetSplitDBSchema.currencies.COLUMN_EXCHANGE_RATE;
        public static final String[] PROJECTION_ALL = {_ID, COLUMN_NAME, COLUMN_CURRENCY_CODE, COLUMN_EXCHANGE_RATE};
    }

    public static final class items implements BaseColumns {
        static final String TABLE_ITEMS = "items";
        static final int ITEMS = 60;
        static final int ITEM = 61;

        public static Uri CONTENT_URI = Uri.withAppendedPath(budgetSplitContract.CONTENT_URI, TABLE_ITEMS);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_ITEMS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_ITEMS;
        public static final String COLUMN_NAME = budgetSplitDBSchema.items.COLUMN_NAME;
        public static final String COLUMN_TIMESTAMP = budgetSplitDBSchema.items.COLUMN_TIMESTAMP;
        public static final String COLUMN_CREATOR = budgetSplitDBSchema.items.COLUMN_CREATOR;
        public static final String COLUMN_PROJECT = budgetSplitDBSchema.items.COLUMN_PROJECT;
        public static final String[] PROJECTION_ALL = {COLUMN_NAME, COLUMN_TIMESTAMP, COLUMN_CREATOR, COLUMN_PROJECT};
    }

    public static final class projectParticipants {
        static final String TABLE_PROJECT_PARTICIPANTS = "projectParticipants";
        static final int PROJECT_PARTICIPANTS = 70;
        static final int PROJECT_PARTICIPANT = 71;

        public static Uri CONTENT_URI = Uri.withAppendedPath(budgetSplitContract.CONTENT_URI, TABLE_PROJECT_PARTICIPANTS);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_PROJECT_PARTICIPANTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_PROJECT_PARTICIPANTS;
        public static final String COLUMN_PROJECTS_ID = budgetSplitDBSchema.projectsParticipants.COLUMN_PROJECTS_ID;
        public static final String COLUMN_PARTICIPANTS_ID = budgetSplitDBSchema.projectsParticipants.COLUMN_PARTICIPANTS_ID;
        public static final String[] PROJECTION_ALL = {COLUMN_PROJECTS_ID, COLUMN_PARTICIPANTS_ID};
    }

    public static final class itemsParticipants {
        static final String TABLE_ITEMS_PARTICIPANTS = "itemsParticipants";
        static final int ITEMS_PARTICIPANTS = 80;
        static final int ITEM_PARTICIPANTS = 81;

        public static Uri CONTENT_URI = Uri.withAppendedPath(budgetSplitContract.CONTENT_URI, TABLE_ITEMS_PARTICIPANTS);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_ITEMS_PARTICIPANTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_ITEMS_PARTICIPANTS;
        public static final String COLUMN_PARTICIPANTS_ID = budgetSplitDBSchema.itemsParticipants.COLUMN_PARTICIPANTS_ID;
        public static final String COLUMN_ITEM_ID = budgetSplitDBSchema.itemsParticipants.COLUMN_ITEM_ID;
        public static final String COLUMN_CURRENCY_ID = budgetSplitDBSchema.itemsParticipants.COLUMN_CURRENCY_ID;
        public static final String COLUMN_AMOUNT_PAYED = budgetSplitDBSchema.itemsParticipants.COLUMN_AMOUNT_PAYED;
        public static final String[] PROJECTION_ALL = {COLUMN_PARTICIPANTS_ID, COLUMN_ITEM_ID, COLUMN_CURRENCY_ID, COLUMN_AMOUNT_PAYED};
    }

    public static final class excludeItems {
        static final String TABLE_EXCLUDE_ITEMS = "excludeItems";
        static final int EXCLUDE_ITEMS = 90;
        static final int EXCLUDE_ITEM = 91;

        public static Uri CONTENT_URI = Uri.withAppendedPath(budgetSplitContract.CONTENT_URI, TABLE_EXCLUDE_ITEMS);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_EXCLUDE_ITEMS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_EXCLUDE_ITEMS;
        public static final String COLUMN_PARTICIPANTS_ID = budgetSplitDBSchema.excludeItems.COLUMN_PARTICIPANTS_ID;
        public static final String COLUMN_ITEM_ID = budgetSplitDBSchema.excludeItems.COLUMN_ITEM_ID;
        public static final String COLUMN_SHARE_RATIO = budgetSplitDBSchema.excludeItems.COLUMN_SHARE_RATIO;
        public static final String[] PROJECTION_ALL = {COLUMN_PARTICIPANTS_ID, COLUMN_ITEM_ID, COLUMN_SHARE_RATIO};
    }

    public static final class tagFilter {
        static final String TABLE_TAG_FILTER = "tagFilter";
        static final int TAGS_FILTER = 100;
        static final int TAG_FILTER = 101;

        public static Uri CONTENT_URI = Uri.withAppendedPath(budgetSplitContract.CONTENT_URI, TABLE_TAG_FILTER);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_TAG_FILTER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_TAG_FILTER;
        public static final String COLUMN_PARTICIPANTS_ID = budgetSplitDBSchema.tagFilter.COLUMN_PARTICIPANTS_ID;
        public static final String COLUMN_TAG_ID = budgetSplitDBSchema.tagFilter.COLUMN_TAG_ID;
        public static final String COLUMN_SHARE_RATIO = budgetSplitDBSchema.tagFilter.COLUMN_SHARE_RATIO;
        public static final String[] PROJECTION_ALL = {COLUMN_PARTICIPANTS_ID, COLUMN_TAG_ID, COLUMN_SHARE_RATIO};
    }

    /**
     * This Table is Read-Only. If you want to change Data, use the other tables.
     */
    public static final class projectsDetailsRO implements BaseColumns {
        static final String TABLE_PROJECTS_DETAILS_RO = "projectsDetailsRO";
        static final int PROJECTS_DETAILS = 40;
        static final int PROJECT_DETAILS = 41;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(budgetSplitContract.CONTENT_URI, TABLE_PROJECTS_DETAILS_RO);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_PROJECTS_DETAILS_RO;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_PROJECTS_DETAILS_RO;
        public static final String COLUMN_PROJECT_NAME = budgetSplitDBSchema.projects_view.COLUMN_PROJECT_NAME;
        public static final String COLUMN_PROJECT_DESCRIPTION = budgetSplitDBSchema.projects_view.COLUMN_PROJECT_DESCRIPTION;
        public static final String COLUMN_PROJECT_ADMIN_ID = budgetSplitDBSchema.projects_view.COLUMN_PROJECT_ADMIN_ID;
        public static final String COLUMN_PROJECT_ADMIN_NAME = budgetSplitDBSchema.projects_view.COLUMN_PROJECT_ADMIN_NAME;
        public static final String COLUMN_PROJECT_ADMIN_UNIQUEID = budgetSplitDBSchema.projects_view.COLUMN_PROJECT_ADMIN_UNIQUEID;
        public static final String COLUMN_NR_OF_PARTICIPANTS = budgetSplitDBSchema.projects_view.COLUMN_NR_OF_PARTICIPANTS;
        public static final String COLUMN_NR_OF_ITEMS = budgetSplitDBSchema.projects_view.COLUMN_NR_OF_ITEMS;
        public static final String[] PROJECTION_ALL = {_ID, COLUMN_PROJECT_NAME, COLUMN_PROJECT_DESCRIPTION, COLUMN_PROJECT_ADMIN_ID, COLUMN_PROJECT_ADMIN_NAME, COLUMN_PROJECT_ADMIN_UNIQUEID, COLUMN_NR_OF_PARTICIPANTS, COLUMN_NR_OF_ITEMS};
    }

    /**
     * This Table is Read-Only. If you want to change Data, use the other tables.
     */
    public static final class itemsDetailsRO implements BaseColumns {
        static final String TABLE_ITEMS_DETAILS_RO = "itemsDetailsRO";
        static final int ITEMS_DETAILS_RO = 50;
        static final int ITEM_DETAILS_RO = 51;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(budgetSplitContract.CONTENT_URI, TABLE_ITEMS_DETAILS_RO);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_ITEMS_DETAILS_RO;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + AUTHORITY + "." + TABLE_ITEMS_DETAILS_RO;
        public static final String COLUMN_ITEM_NAME = budgetSplitDBSchema.items_view.COLUMN_ITEM_NAME;
        public static final String COLUMN_ITEM_TIMESTAMP = budgetSplitDBSchema.items_view.COLUMN_ITEM_TIMESTAMP;
        public static final String COLUMN_ITEM_DATE_ADDED = budgetSplitDBSchema.items_view.COLUMN_ITEM_DATE_ADDED;
        public static final String COLUMN_TIME_ADDED = budgetSplitDBSchema.items_view.COLUMN_ITEM_TIME_ADDED;
        public static final String COLUMN_CREATOR_ID = budgetSplitDBSchema.items_view.COLUMN_ITEM_CREATOR_ID;
        public static final String COLUMN_CREATOR_NAME = budgetSplitDBSchema.items_view.COLUMN_ITEM_CREATOR_NAME;
        public static final String COLUMN_ITEM_PRICE = budgetSplitDBSchema.items_view.COLUMN_ITEM_PRICE;
        public static final String[] PROJECTION_ALL = {_ID, COLUMN_ITEM_NAME, COLUMN_ITEM_TIMESTAMP, COLUMN_ITEM_DATE_ADDED, COLUMN_TIME_ADDED, COLUMN_CREATOR_ID, COLUMN_CREATOR_NAME, COLUMN_ITEM_PRICE};
    }
}
