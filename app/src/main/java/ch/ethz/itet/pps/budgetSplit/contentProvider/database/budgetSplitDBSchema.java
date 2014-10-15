package ch.ethz.itet.pps.budgetSplit.contentProvider.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by Manuel on 27.09.2014.
 */
public final class budgetSplitDBSchema {

    static final String DATABASE_NAME = "budgetSplit.db";
    static final int DATABASE_VERSION = 1;

    /**
     * Contract class with all necessary Constants use from within and outside of the Content Provider.
     */
    public budgetSplitDBSchema() {
    }

    /**
     * Constants for the projects tables.
     */
    public static abstract class projects implements BaseColumns {
        public static final String TABLE_PROJECTS = "projects";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_ADMIN = "admin";

        private static final String TABLE_CREATE = "CREATE TABLE "
                + TABLE_PROJECTS
                + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_ADMIN + " INTEGER NOT NULL, "
                + " FOREIGN KEY (" + COLUMN_ADMIN + ") REFERENCES " + participants.TABLE_PARTICIPANTS + "(" + _ID + ") ON DELETE RESTRICTS ON UPDATE CASCADE"
                + ");";

        /**
         * Static Method to be called by SQLiteOpenHelper class for better readability.
         *
         * @param database
         */
        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(TABLE_CREATE);
        }

        /**
         * Method to be implemented for future Changes in Database structure.
         *
         * @param database
         * @param oldVersion
         * @param newVersion
         */
        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        }
    }

    public static abstract class participants implements BaseColumns {
        public static final String TABLE_PARTICIPANTS = "participants";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_UNIQUEID = "uniqueId";
        public static final String COLUMN_ISVIRTUAL = "isVirtual";

        private static final String TABLE_CREATE = "CREATE TABLE "
                + TABLE_PARTICIPANTS
                + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_UNIQUEID + " TEXT NOT NULL UNIQUE, "
                + COLUMN_ISVIRTUAL + " INTEGER DEFAULT 0"
                + ");";

        /**
         * Static Method to be called by SQLiteOpenHelper class for better readability.
         *
         * @param database
         */
        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(TABLE_CREATE);
        }

        /**
         * Method to be implemented for future Changes in Database structure.
         *
         * @param database
         * @param oldVersion
         * @param newVersion
         */
        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        }
    }

    public static abstract class tags implements BaseColumns {
        public static final String TABLE_TAGS = "tags";
        public static final String COLUMN_NAME = "name";

        private static final String TABLE_CREATE = "CREATE TABLE "
                + TABLE_TAGS
                + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL UNIQUE"
                + ");";

        /**
         * Static Method to be called by SQLiteOpenHelper class for better readability.
         *
         * @param database
         */
        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(TABLE_CREATE);
        }

        /**
         * Method to be implemented for future Changes in Database structure.
         *
         * @param database
         * @param oldVersion
         * @param newVersion
         */
        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        }
    }

    public static abstract class items implements BaseColumns {
        public static final String TABLE_ITEMS = "items";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_CREATOR = "creator";
        public static final String COLUMN_PROJECT = "project";

        private static final String TABLE_CREATE = "CREATE TABLE "
                + TABLE_ITEMS
                + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_TIMESTAMP + " DATE DEFAULT CURRENT_TIMESTAMP, "
                + COLUMN_CREATOR + " INTEGER NOT NULL, "
                + COLUMN_PROJECT + " INTEGER NOT NULL, "
                + "FOREIGN KEY (" + COLUMN_CREATOR + ") REFERENCES " + participants.TABLE_PARTICIPANTS + "(" + _ID + ") ON UPDATE CASCADE ON DELETE RESTRICT,"
                + "FOREIGN KEY (" + COLUMN_PROJECT + ") REFERENCES " + projects.TABLE_PROJECTS + "(" + _ID + ") ON DELETE CASCADE ON UPDATE CASCADE"
                + ");";

        /**
         * Static Method to be called by SQLiteOpenHelper class for better readability.
         *
         * @param database
         */
        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(TABLE_CREATE);
        }

        /**
         * Method to be implemented for future Changes in Database structure.
         *
         * @param database
         * @param oldVersion
         * @param newVersion
         */
        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        }
    }

    public static abstract class currencies implements BaseColumns {
        public static final String TABLE_CURRENCIES = "currencies";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CURRENCY_CODE = "currencyCode";
        public static final String COLUMN_EXCHANGE_RATE = "exchangeRate";

        private static final String TABLE_CREATE = "CREATE TABLE "
                + TABLE_CURRENCIES
                + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_CURRENCY_CODE + " TEXT NOT NULL, "
                + COLUMN_EXCHANGE_RATE + " FLOAT NOT NULL "
                + ");";


        /**
         * Static Method to be called by SQLiteOpenHelper class for better readability.
         *
         * @param database
         */
        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(TABLE_CREATE);
        }

        /**
         * Method to be implemented for future Changes in Database structure.
         *
         * @param database
         * @param oldVersion
         * @param newVersion
         */
        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        }
    }

    // <editor-fold desc="Junction Tables">

    public static abstract class projectsParticipants {
        public static final String TABLE_PROJECTS_PARTICIPANTS = "projectsParticipants";
        public static final String COLUMN_PROJECTS_ID = "projectsId";
        public static final String COLUMN_PARTICIPANTS_ID = "participantsId";

        private static final String TABLE_CREATE = "CREATE TABLE "
                + TABLE_PROJECTS_PARTICIPANTS
                + "("
                + COLUMN_PARTICIPANTS_ID + " INTEGER NOT NULL, "
                + COLUMN_PROJECTS_ID + " INTEGER NOT NULL, "
                + "FOREIGN KEY (" + COLUMN_PARTICIPANTS_ID + ") REFERENCES " + participants.TABLE_PARTICIPANTS + "(" + participants._ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
                + "FOREIGN KEY (" + COLUMN_PROJECTS_ID + ") REFERENCES " + projects.TABLE_PROJECTS + "(" + projects._ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
                + "PRIMARY KEY (" + COLUMN_PROJECTS_ID + ", " + COLUMN_PARTICIPANTS_ID + ")"
                + ");";

        /**
         * Static Method to be called by SQLiteOpenHelper class for better readability.
         *
         * @param database
         */
        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(TABLE_CREATE);
        }

        /**
         * Method to be implemented for future Changes in Database structure.
         *
         * @param database
         * @param oldVersion
         * @param newVersion
         */
        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        }
    }

    public static abstract class projectsTags {
        public static final String TABLE_PROJECTS_TAGS = "projectsTags";
        public static final String COLUMN_PROJECTS_ID = "projectsId";
        public static final String COLUMN_TAGS_ID = "tagsId";

        private static final String TABLE_CREATE = "CREATE TABLE "
                + TABLE_PROJECTS_TAGS
                + "("
                + COLUMN_TAGS_ID + " INTEGER NOT NULL, "
                + COLUMN_PROJECTS_ID + " INTEGER NOT NULL, "
                + "FOREIGN KEY (" + COLUMN_TAGS_ID + ") REFERENCES " + tags.TABLE_TAGS + "(" + tags._ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
                + "FOREIGN KEY (" + COLUMN_PROJECTS_ID + ") REFERENCES " + projects.TABLE_PROJECTS + "(" + tags._ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
                + "PRIMARY KEY (" + COLUMN_PROJECTS_ID + ", " + COLUMN_TAGS_ID + ")"
                + ");";

        /**
         * Static Method to be called by SQLiteOpenHelper class for better readability.
         *
         * @param database
         */
        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(TABLE_CREATE);
        }

        /**
         * Method to be implemented for future Changes in Database structure.
         *
         * @param database
         * @param oldVersion
         * @param newVersion
         */
        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        }
    }

    public static abstract class itemsParticipants {
        public static final String TABLE_ITEMS_PARTICIPANTS = "itemsParticipants";
        public static final String COLUMN_ITEM_ID = "itemId";
        public static final String COLUMN_PARTICIPANTS_ID = "participantsId";
        public static final String COLUMN_CURRENCY_ID = "currencyId";
        public static final String COLUMN_AMOUNT_PAYED = "amountPayed";

        private static final String TABLE_CREATE = "CREATE TABLE "
                + TABLE_ITEMS_PARTICIPANTS
                + "("
                + COLUMN_PARTICIPANTS_ID + " INTEGER NOT NULL, "
                + COLUMN_ITEM_ID + " INTEGER NOT NULL, "
                + COLUMN_CURRENCY_ID + " INTEGER NOT NULL, "
                + COLUMN_AMOUNT_PAYED + " FLOAT NOT NULL, "
                + "FOREIGN KEY (" + COLUMN_PARTICIPANTS_ID + ") REFERENCES " + participants.TABLE_PARTICIPANTS + "(" + participants._ID + ") ON UPDATE CASCADE ON DELETE RESTRICT, "
                + "FOREIGN KEY (" + COLUMN_ITEM_ID + ") REFERENCES " + items.TABLE_ITEMS + "(" + items._ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
                + "FOREIGN KEY (" + COLUMN_CURRENCY_ID + ") REFERENCES " + currencies.TABLE_CURRENCIES + "(" + currencies._ID + "), ON UPDATE CASCADE ON DELETE RESTRICT"
                + "PRIMARY KEY (" + COLUMN_ITEM_ID + ", " + COLUMN_PARTICIPANTS_ID + ")"
                + ");";

        /**
         * Static Method to be called by SQLiteOpenHelper class for better readability.
         *
         * @param database
         */
        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(TABLE_CREATE);
        }

        /**
         * Method to be implemented for future Changes in Database structure.
         *
         * @param database
         * @param oldVersion
         * @param newVersion
         */
        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        }
    }

    public static abstract class excludeItems {
        public final static String TABLE_EXCLUDE_ITEMS = "excludeItems";
        public final static String COLUMN_ITEM_ID = "itemId";
        public final static String COLUMN_PARTICIPANTS_ID = "participantsId";
        public final static String COLUMN_SHARE_RATIO = "shareRatio";

        private static final String TABLE_CREATE = "CREATE TABLE "
                + TABLE_EXCLUDE_ITEMS
                + "("
                + COLUMN_PARTICIPANTS_ID + " INTEGER NOT NULL, "
                + COLUMN_ITEM_ID + " INTEGER NOT NULL, "
                + COLUMN_SHARE_RATIO + " FLOAT NOT NULL CHECK (" + COLUMN_SHARE_RATIO + " >= 0 AND " + COLUMN_SHARE_RATIO + " <= 1), "
                + "FOREIGN KEY (" + COLUMN_PARTICIPANTS_ID + ") REFERENCES " + participants.TABLE_PARTICIPANTS + "(" + participants._ID + "), ON UPDATE CASCADE ON DELETE CASCADE"
                + "FOREIGN KEY (" + COLUMN_ITEM_ID + ") REFERENCES " + items.TABLE_ITEMS + "(" + items._ID + "), ON UPDATE CASCADE ON DELETE CASCADE"
                + "PRIMARY KEY (" + COLUMN_ITEM_ID + ", " + COLUMN_PARTICIPANTS_ID + ")"
                + ");";

        /**
         * Static Method to be called by SQLiteOpenHelper class for better readability.
         *
         * @param database
         */
        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(TABLE_CREATE);
        }

        /**
         * Method to be implemented for future Changes in Database structure.
         *
         * @param database
         * @param oldVersion
         * @param newVersion
         */
        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        }
    }

    public static abstract class tagFilter {
        public final static String TABLE_TAG_FILTER = "tagFilter";
        public final static String COLUMN_PARTICIPANTS_ID = "participantsId";
        public final static String COLUMN_TAG_ID = "tagId";
        public final static String COLUMN_SHARE_RATIO = "shareRatio";

        private static final String TABLE_CREATE = "CREATE TABLE "
                + TABLE_TAG_FILTER
                + "("
                + COLUMN_PARTICIPANTS_ID + " INTEGER NOT NULL, "
                + COLUMN_TAG_ID + " INTEGER NOT NULL, "
                + COLUMN_SHARE_RATIO + " FLOAT NOT NULL CHECK (" + COLUMN_SHARE_RATIO + " >= 0 AND " + COLUMN_SHARE_RATIO + " <= 1), "
                + "FOREIGN KEY (" + COLUMN_PARTICIPANTS_ID + ") REFERENCES " + participants.TABLE_PARTICIPANTS + "(" + participants._ID + "), ON UPDATE CASCADE ON DELETE CASCADE"
                + "FOREIGN KEY (" + COLUMN_TAG_ID + ") REFERENCES " + tags.TABLE_TAGS + "(" + tags._ID + "), ON UPDATE CASCADE ON DELETE RESTRICT"
                + "PRIMARY KEY (" + COLUMN_TAG_ID + ", " + COLUMN_PARTICIPANTS_ID + ")"
                + ");";

        /**
         * Static Method to be called by SQLiteOpenHelper class for better readability.
         *
         * @param database
         */
        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(TABLE_CREATE);
        }

        /**
         * Method to be implemented for future Changes in Database structure.
         *
         * @param database
         * @param oldVersion
         * @param newVersion
         */
        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        }
    }

    // </editor-fold>


    public static abstract class projects_view implements BaseColumns {
        public static final String VIEW_PROJECTS = "viewProjects";
        public static final String COLUMN_PROJECT_NAME = "projectName";
        public static final String COLUMN_PROJECT_DESCRIPTION = "projectDescription";
        public static final String COLUMN_PROJECT_ADMIN_ID = "projectAdminId";
        public static final String COLUMN_PROJECT_ADMIN_NAME = "projectAdminName";
        public static final String COLUMN_PROJECT_ADMIN_UNIQUEID = "projectAdminUniqueId";
        public static final String COLUMN_NR_OF_PARTICIPANTS = "projectCountParticipants";
        public static final String COLUMN_NR_OF_ITEMS = "projectCountItems";

        private static final String VIEW_SUBSELECT_COUNT_PARTICIPANTS = "SELECT "
                + projectsParticipants.COLUMN_PROJECTS_ID + ", "
                + "COUNT(" + projectsParticipants.COLUMN_PARTICIPANTS_ID + ") AS " + COLUMN_NR_OF_PARTICIPANTS
                + " FROM " + projectsParticipants.TABLE_PROJECTS_PARTICIPANTS
                + " GROUP BY " + projectsParticipants.COLUMN_PROJECTS_ID;
        private static final String VIEW_SUBSELECT_COUNT_ITEMS = "SELECT "
                + items.COLUMN_PROJECT + ", "
                + "COUNT(" + _ID + ") AS " + COLUMN_NR_OF_ITEMS
                + " FROM " + items.TABLE_ITEMS
                + " GROUP BY " + items.COLUMN_PROJECT;

        private static final String VIEW_SELECT = "SELECT "
                + projects.TABLE_PROJECTS + "." + _ID + ", "
                + projects.TABLE_PROJECTS + "." + projects.COLUMN_NAME + " AS " + COLUMN_PROJECT_NAME + ", "
                + projects.COLUMN_DESCRIPTION + " AS " + COLUMN_PROJECT_DESCRIPTION + ", "
                + projects.COLUMN_ADMIN + " AS " + COLUMN_PROJECT_ADMIN_ID + ", "
                + participants.TABLE_PARTICIPANTS + "." + participants.COLUMN_NAME + " AS " + COLUMN_PROJECT_ADMIN_NAME + ", "
                + participants.TABLE_PARTICIPANTS + "." + participants.COLUMN_UNIQUEID + " AS " + COLUMN_PROJECT_ADMIN_UNIQUEID + ", "
                + "sub1." + COLUMN_NR_OF_ITEMS + ", "
                + "sub2." + COLUMN_NR_OF_PARTICIPANTS
                + " FROM " + projects.TABLE_PROJECTS
                + " LEFT OUTER JOIN " + participants.TABLE_PARTICIPANTS + " ON " + projects.TABLE_PROJECTS + "." + projects.COLUMN_ADMIN + " = " + participants.TABLE_PARTICIPANTS + "." + _ID
                + " LEFT OUTER JOIN (" + VIEW_SUBSELECT_COUNT_ITEMS + ") AS 'sub1' ON " + projects.TABLE_PROJECTS + "." + _ID + " = sub1." + _ID
                + " LEFT OUTER JOIN (" + VIEW_SUBSELECT_COUNT_PARTICIPANTS + ") AS 'sub2' ON " + projects.TABLE_PROJECTS + "." + _ID + " = sub2." + _ID
                + ";";
        private static final String VIEW_CREATE = "CREATE VIEW "
                + VIEW_PROJECTS
                + " AS "
                + VIEW_SELECT;

        /**
         * Static Method to be called by SQLiteOpenHelper class for better readability.
         *
         * @param database
         */
        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(VIEW_CREATE);
        }

        /**
         * Method to be implemented for future Changes in Database structure.
         *
         * @param database
         * @param oldVersion
         * @param newVersion
         */
        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        }
    }

    public static abstract class items_view implements BaseColumns {
        public static final String VIEW_ITEMS = "viewItems";
        public static final String COLUMN_ITEM_NAME = "itemName";
        public static final String COLUMN_ITEM_TIMESTAMP = "itemTimestamp";
        public static final String COLUMN_ITEM_DATE_ADDED = "itemDateAdded";
        public static final String COLUMN_ITEM_TIME_ADDED = "itemTimeAdded";
        public static final String COLUMN_ITEM_CREATOR_ID = "itemCreatorId";
        public static final String COLUMN_ITEM_CREATOR_NAME = "itemCreatorName";
        public static final String COLUMN_ITEM_PRICE = "itemPrice";

        private static final String VIEW_SELECT = "SELECT "
                + items.TABLE_ITEMS + "." + _ID + ", "
                + items.TABLE_ITEMS + "." + items.COLUMN_NAME + " AS " + COLUMN_ITEM_NAME + ", "
                + items.TABLE_ITEMS + "." + items.COLUMN_TIMESTAMP + " AS " + COLUMN_ITEM_TIMESTAMP + ", "
                + "strftime('%d.%m.%Y', " + items.TABLE_ITEMS + "." + items.COLUMN_TIMESTAMP + ", localtime) AS " + COLUMN_ITEM_DATE_ADDED + ", "
                + "strftime('%H:%M', " + items.TABLE_ITEMS + "." + items.COLUMN_TIMESTAMP + ", localtime) AS " + COLUMN_ITEM_TIME_ADDED + ", "
                + items.TABLE_ITEMS + "." + items.COLUMN_CREATOR + " AS " + COLUMN_ITEM_CREATOR_ID + ", "
                + participants.TABLE_PARTICIPANTS + "." + participants.COLUMN_NAME + " AS " + COLUMN_ITEM_CREATOR_NAME + ", "
                + "sum(" + itemsParticipants.TABLE_ITEMS_PARTICIPANTS + "." + itemsParticipants.COLUMN_AMOUNT_PAYED + "*" + currencies.TABLE_CURRENCIES + "." + currencies.COLUMN_EXCHANGE_RATE + ") AS " + COLUMN_ITEM_PRICE
                + " FROM " + items.TABLE_ITEMS
                + " LEFT OUTER JOIN " + itemsParticipants.TABLE_ITEMS_PARTICIPANTS + " ON " + items.TABLE_ITEMS + "." + _ID + " = " + itemsParticipants.TABLE_ITEMS_PARTICIPANTS + "." + itemsParticipants.COLUMN_ITEM_ID
                + " LEFT OUTER JOIN " + currencies.TABLE_CURRENCIES + " ON " + itemsParticipants.TABLE_ITEMS_PARTICIPANTS + "." + itemsParticipants.COLUMN_CURRENCY_ID + " = " + currencies.TABLE_CURRENCIES + "." + _ID
                + " LEFT OUTER JOIN " + participants.TABLE_PARTICIPANTS + " ON " + items.TABLE_ITEMS + "." + items.COLUMN_CREATOR + " = " + participants.TABLE_PARTICIPANTS + "." + _ID
                + " GROUP BY " + items.TABLE_ITEMS + "." + _ID
                + ";";


        private static final String VIEW_CREATE = "CREATE VIEW "
                + VIEW_ITEMS
                + " AS "
                + VIEW_SELECT;

        /**
         * Static Method to be called by SQLiteOpenHelper class for better readability.
         *
         * @param database
         */
        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(VIEW_CREATE);
        }

        /**
         * Method to be implemented for future Changes in Database structure.
         *
         * @param database
         * @param oldVersion
         * @param newVersion
         */
        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        }
    }


}
