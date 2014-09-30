package ch.ethz.itet.pps.budgetSplit.contentProvider.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by Manuel on 27.09.2014.
 */
public final class budgetSplitDBSchema implements BaseColumns {

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
    public static abstract class projects {
        public static final String TABLE_PROJECTS = "projects";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_OWNER = "owner";

        private static final String TABLE_CREATE = "CREATE TABLE"
                + TABLE_PROJECTS
                + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + "FOREIGN KEY (" + COLUMN_OWNER + ") REFERENCES " + participants.TABLE_PARTICIPANTS + "(" + _ID + ")"
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

    public static abstract class participants {
        public static final String TABLE_PARTICIPANTS = "participants";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_UNIQUEID = "googleAccountId";
        public static final String COLUMN_ISVIRTUAL = "isVirtual";

        private static final String TABLE_CREATE = "CREATE TABLE"
                + TABLE_PARTICIPANTS
                + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL,"
                + COLUMN_UNIQUEID + " TEXT NOT NULL UNIQUE,"
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

    public static abstract class tags {
        public static final String TABLE_TAGS = "tags";
        public static final String COLUMN_NAME = "name";

        private static final String TABLE_CREATE = "CREATE TABLE"
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

    public static abstract class items {
        public static final String TABLE_ITEMS = "items";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_CREATOR = "creator";
        public static final String COLUMN_PROJECT = "project";

        private static final String TABLE_CREATE = "CREATE TABLE"
                + TABLE_ITEMS
                + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL,"
                + COLUMN_TIMESTAMP + " DATE DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY (" + COLUMN_CREATOR + ") REFERENCES " + participants.TABLE_PARTICIPANTS + "(" + _ID + "),"
                + "FOREIGN KEY (" + COLUMN_PROJECT + ") REFERENCES " + projects.TABLE_PROJECTS + "(" + _ID + ")"
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

    public static abstract class currencies {
        public static final String TABLE_CURRENCIES = "currencies";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CURRENCY_CODE = "currencyCode";
        public static final String COLUMN_EXCHANGE_RATE = "exchangeRate";

        private static final String TABLE_CREATE = "CREATE TABLE"
                + TABLE_CURRENCIES
                + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL,"
                + COLUMN_CURRENCY_CODE + " TEXT NOT NULL,"
                + COLUMN_EXCHANGE_RATE + " FLOAT NOT NULL"
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

        private static final String TABLE_CREATE = "CREATE TABLE"
                + TABLE_PROJECTS_PARTICIPANTS
                + "("
                + "FOREIGN KEY (" + COLUMN_PARTICIPANTS_ID + ") REFERENCES " + participants.TABLE_PARTICIPANTS + "(" + _ID + "),"
                + "FOREIGN KEY (" + COLUMN_PROJECTS_ID + ") REFERENCES " + projects.TABLE_PROJECTS + "(" + _ID + "),"
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

        private static final String TABLE_CREATE = "CREATE TABLE"
                + TABLE_PROJECTS_TAGS
                + "("
                + "FOREIGN KEY (" + COLUMN_TAGS_ID + ") REFERENCES " + tags.TABLE_TAGS + "(" + _ID + "),"
                + "FOREIGN KEY (" + COLUMN_PROJECTS_ID + ") REFERENCES " + projects.TABLE_PROJECTS + "(" + _ID + "),"
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
        public static final String COLUMN_PRICE = "price";

        private static final String TABLE_CREATE = "CREATE TABLE"
                + TABLE_ITEMS_PARTICIPANTS
                + "("
                + "FOREIGN KEY (" + COLUMN_PARTICIPANTS_ID + ") REFERENCES " + participants.TABLE_PARTICIPANTS + "(" + _ID + "),"
                + "FOREIGN KEY (" + COLUMN_ITEM_ID + ") REFERENCES " + items.TABLE_ITEMS + "(" + _ID + "),"
                + "FOREIGN KEY (" + COLUMN_CURRENCY_ID + ") REFERENCES " + currencies.TABLE_CURRENCIES + "(" + _ID + "),"
                + COLUMN_PRICE + " FLOAT NOT NULL,"
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

        private static final String TABLE_CREATE = "CREATE TABLE"
                + TABLE_EXCLUDE_ITEMS
                + "("
                + "FOREIGN KEY (" + COLUMN_PARTICIPANTS_ID + ") REFERENCES " + participants.TABLE_PARTICIPANTS + "(" + _ID + "),"
                + "FOREIGN KEY (" + COLUMN_ITEM_ID + ") REFERENCES " + items.TABLE_ITEMS + "(" + _ID + "),"
                + COLUMN_SHARE_RATIO + " FLOAT NOT NULL CHECK (" + COLUMN_SHARE_RATIO + " >= 0 AND " + COLUMN_SHARE_RATIO + " <= 1),"
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

        private static final String TABLE_CREATE = "CREATE TABLE"
                + TABLE_TAG_FILTER
                + "("
                + "FOREIGN KEY (" + COLUMN_PARTICIPANTS_ID + ") REFERENCES " + participants.TABLE_PARTICIPANTS + "(" + _ID + "),"
                + "FOREIGN KEY (" + COLUMN_TAG_ID + ") REFERENCES " + tags.TABLE_TAGS + "(" + _ID + "),"
                + COLUMN_SHARE_RATIO + " FLOAT NOT NULL CHECK (" + COLUMN_SHARE_RATIO + " >= 0 AND " + COLUMN_SHARE_RATIO + " <= 1),"
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
}
