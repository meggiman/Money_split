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
    private budgetSplitDBSchema() {
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
                + " FOREIGN KEY (" + COLUMN_ADMIN + ") REFERENCES " + participants.TABLE_PARTICIPANTS + "(" + _ID + ") ON DELETE RESTRICT ON UPDATE CASCADE"
                + ");";

        private static final String TRIGGER_DELETE_ALL_ITEMS_ON_DELETE = "triggerDeleteAllItemsOnDelete";
        private static final String TRIGGER_CREATE = "CREATE TRIGGER "
                + TRIGGER_DELETE_ALL_ITEMS_ON_DELETE + " BEFORE DELETE ON " + TABLE_PROJECTS
                + " FOR EACH ROW BEGIN"
                + " DELETE FROM " + items.TABLE_ITEMS + " WHERE " + items.TABLE_ITEMS + "." + items.COLUMN_PROJECT_ID + " = OLD." + _ID + ";"
                + " END;";

        /**
         * Static Method to be called by SQLiteOpenHelper class for better readability.
         *
         * @param database
         */
        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(TABLE_CREATE);
        }

        public static void onCreateTrigger(SQLiteDatabase database) {
            database.execSQL(TRIGGER_CREATE);
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
                + COLUMN_UNIQUEID + " TEXT UNIQUE, "
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
        public static final String COLUMN_PROJECT_ID = "projectId";

        private static final String TABLE_CREATE = "CREATE TABLE "
                + TABLE_ITEMS
                + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_TIMESTAMP + " DATE DEFAULT CURRENT_TIMESTAMP, "
                + COLUMN_CREATOR + " INTEGER NOT NULL, "
                + COLUMN_PROJECT_ID + " INTEGER NOT NULL, "
                + "FOREIGN KEY (" + COLUMN_CREATOR + ") REFERENCES " + participants.TABLE_PARTICIPANTS + "(" + _ID + ") ON UPDATE CASCADE ON DELETE RESTRICT,"
                + "FOREIGN KEY (" + COLUMN_PROJECT_ID + ") REFERENCES " + projects.TABLE_PROJECTS + "(" + _ID + ") ON DELETE CASCADE ON UPDATE CASCADE"
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
                + COLUMN_NAME + " TEXT NOT NULL UNIQUE, "
                + COLUMN_CURRENCY_CODE + " TEXT NOT NULL UNIQUE, "
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

        private static final String TRIGGER_RESTRICT_ITEM_PARTICIPANTS = "restrictItemParticipants";
        private static final String TRIGGER_CREATE = "CREATE TRIGGER "
                + TRIGGER_RESTRICT_ITEM_PARTICIPANTS + " BEFORE DELETE ON " + TABLE_PROJECTS_PARTICIPANTS
                + " FOR EACH ROW BEGIN SELECT CASE WHEN ((SELECT " + itemsParticipants.COLUMN_PARTICIPANTS_ID + " FROM " + itemsParticipants.TABLE_ITEMS_PARTICIPANTS
                + " WHERE " + itemsParticipants.TABLE_ITEMS_PARTICIPANTS + "." + itemsParticipants.COLUMN_PARTICIPANTS_ID + " = OLD." + COLUMN_PARTICIPANTS_ID + ") NOTNULL)"
                + " THEN RAISE(ABORT, 'Participant can not be deleted because he is still payer of some items.')"
                + " WHEN ((SELECT " + items.COLUMN_CREATOR + " FROM " + items.TABLE_ITEMS + " WHERE " + items.TABLE_ITEMS + "." + items.COLUMN_CREATOR + " = OLD." + COLUMN_PARTICIPANTS_ID + ") NOTNULL)"
                + " THEN RAISE(ABORT, 'Participant can not be deleted because he is still creator of some items.')"
                + " END;"
                + " END;";

        /**
         * Static Method to be called by SQLiteOpenHelper class for better readability.
         *
         * @param database
         */
        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(TABLE_CREATE);
        }

        public static void onCreateTrigger(SQLiteDatabase database) {
            database.execSQL(TRIGGER_CREATE);
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

    public static abstract class itemsTags {
        public static final String TABLE_ITEMS_TAGS = "itemsTags";
        public static final String COLUMN_ITEMS_ID = "itemsId";
        public static final String COLUMN_TAGS_ID = "tagsId";

        private static final String TABLE_CREATE = "CREATE TABLE "
                + TABLE_ITEMS_TAGS
                + "("
                + COLUMN_TAGS_ID + " INTEGER NOT NULL, "
                + COLUMN_ITEMS_ID + " INTEGER NOT NULL, "
                + "FOREIGN KEY (" + COLUMN_TAGS_ID + ") REFERENCES " + tags.TABLE_TAGS + "(" + tags._ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
                + "FOREIGN KEY (" + COLUMN_ITEMS_ID + ") REFERENCES " + items.TABLE_ITEMS + "(" + items._ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
                + "PRIMARY KEY (" + COLUMN_ITEMS_ID + ", " + COLUMN_TAGS_ID + ")"
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
                + "FOREIGN KEY (" + COLUMN_CURRENCY_ID + ") REFERENCES " + currencies.TABLE_CURRENCIES + "(" + currencies._ID + ") ON UPDATE CASCADE ON DELETE RESTRICT "
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
                + "FOREIGN KEY (" + COLUMN_PARTICIPANTS_ID + ") REFERENCES " + participants.TABLE_PARTICIPANTS + "(" + participants._ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
                + "FOREIGN KEY (" + COLUMN_ITEM_ID + ") REFERENCES " + items.TABLE_ITEMS + "(" + items._ID + ") ON UPDATE CASCADE ON DELETE CASCADE "
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
                + "FOREIGN KEY (" + COLUMN_PARTICIPANTS_ID + ") REFERENCES " + participants.TABLE_PARTICIPANTS + "(" + participants._ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
                + "FOREIGN KEY (" + COLUMN_TAG_ID + ") REFERENCES " + tags.TABLE_TAGS + "(" + tags._ID + ") ON UPDATE CASCADE ON DELETE RESTRICT "
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
                + items.COLUMN_PROJECT_ID + ", "
                + "COUNT(" + _ID + ") AS " + COLUMN_NR_OF_ITEMS
                + " FROM " + items.TABLE_ITEMS
                + " GROUP BY " + items.COLUMN_PROJECT_ID;

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
                + " LEFT OUTER JOIN (" + VIEW_SUBSELECT_COUNT_ITEMS + ") AS 'sub1' ON " + projects.TABLE_PROJECTS + "." + _ID + " = sub1." + items.COLUMN_PROJECT_ID
                + " LEFT OUTER JOIN (" + VIEW_SUBSELECT_COUNT_PARTICIPANTS + ") AS 'sub2' ON " + projects.TABLE_PROJECTS + "." + _ID + " = sub2." + projectsParticipants.COLUMN_PROJECTS_ID
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
        public static final String COLUMN_PROJECT_ID = "projectId";
        public static final String COLUMN_ITEM_NAME = "itemName";
        public static final String COLUMN_ITEM_TIMESTAMP = "itemTimestamp";
        public static final String COLUMN_ITEM_DATE_ADDED = "itemDateAdded";
        public static final String COLUMN_ITEM_TIME_ADDED = "itemTimeAdded";
        public static final String COLUMN_ITEM_CREATOR_ID = "itemCreatorId";
        public static final String COLUMN_ITEM_CREATOR_NAME = "itemCreatorName";
        public static final String COLUMN_ITEM_CREATOR_IS_VIRTUAL = "itemCreatorIsVirtual";
        public static final String COLUMN_ITEM_CREATOR_UNIQUE_ID = "itemCreatorUniqueId";
        public static final String COLUMN_ITEM_PRICE = "itemPrice";

        private static final String VIEW_SELECT = "SELECT "
                + items.TABLE_ITEMS + "." + _ID + ", "
                + items.TABLE_ITEMS + "." + items.COLUMN_PROJECT_ID + " AS " + COLUMN_PROJECT_ID + ", "
                + items.TABLE_ITEMS + "." + items.COLUMN_NAME + " AS " + COLUMN_ITEM_NAME + ", "
                + items.TABLE_ITEMS + "." + items.COLUMN_TIMESTAMP + " AS " + COLUMN_ITEM_TIMESTAMP + ", "
                + "strftime('%d.%m.%Y', " + items.TABLE_ITEMS + "." + items.COLUMN_TIMESTAMP + ", 'localtime') AS " + COLUMN_ITEM_DATE_ADDED + ", "
                + "strftime('%H:%M', " + items.TABLE_ITEMS + "." + items.COLUMN_TIMESTAMP + ", 'localtime') AS " + COLUMN_ITEM_TIME_ADDED + ", "
                + items.TABLE_ITEMS + "." + items.COLUMN_CREATOR + " AS " + COLUMN_ITEM_CREATOR_ID + ", "
                + participants.TABLE_PARTICIPANTS + "." + participants.COLUMN_NAME + " AS " + COLUMN_ITEM_CREATOR_NAME + ", "
                + participants.TABLE_PARTICIPANTS + "." + participants.COLUMN_ISVIRTUAL + " AS " + COLUMN_ITEM_CREATOR_IS_VIRTUAL + ", "
                + participants.TABLE_PARTICIPANTS + "." + participants.COLUMN_UNIQUEID + " AS " + COLUMN_ITEM_CREATOR_UNIQUE_ID + ", "
                + "total(" + itemsParticipants.TABLE_ITEMS_PARTICIPANTS + "." + itemsParticipants.COLUMN_AMOUNT_PAYED + "*" + currencies.TABLE_CURRENCIES + "." + currencies.COLUMN_EXCHANGE_RATE + ") AS " + COLUMN_ITEM_PRICE
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

    public static abstract class itemsTags_view implements BaseColumns {
        public static final String VIEW_ITEMS_TAGS = "viewItemsTags";
        public static final String COLUMN_ITEM_ID = itemsTags.COLUMN_ITEMS_ID;
        public static final String COLUMN_ITEM_NAME = "itemName";
        public static final String COLUMN_TAG_ID = itemsTags.COLUMN_TAGS_ID;
        public static final String COLUMN_TAG_NAME = "tagName";

        private static final String VIEW_SELECT = "SELECT "
                + itemsTags.TABLE_ITEMS_TAGS + ".rowid AS " + _ID + ", "
                + itemsTags.TABLE_ITEMS_TAGS + "." + itemsTags.COLUMN_ITEMS_ID + " AS " + COLUMN_ITEM_ID + ", "
                + items.TABLE_ITEMS + "." + items.COLUMN_NAME + " AS " + COLUMN_ITEM_NAME + ", "
                + itemsTags.TABLE_ITEMS_TAGS + "." + itemsTags.COLUMN_TAGS_ID + " AS " + COLUMN_TAG_ID + ", "
                + tags.TABLE_TAGS + "." + tags.COLUMN_NAME + " AS " + COLUMN_TAG_NAME
                + " FROM " + itemsTags.TABLE_ITEMS_TAGS
                + " LEFT OUTER JOIN " + items.TABLE_ITEMS + " ON " + itemsTags.TABLE_ITEMS_TAGS + "." + itemsTags.COLUMN_ITEMS_ID + " = " + items.TABLE_ITEMS + "." + items._ID
                + " LEFT OUTER JOIN " + tags.TABLE_TAGS + " ON " + itemsTags.TABLE_ITEMS_TAGS + "." + itemsTags.COLUMN_TAGS_ID + " = " + tags.TABLE_TAGS + "." + tags._ID
                + ";";
        private static final String VIEW_CREATE = "CREATE VIEW "
                + VIEW_ITEMS_TAGS
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

    public static abstract class itemsParticipants_view implements BaseColumns {
        public static final String VIEW_ITEMS_PARTICIPANTS = "viewItemsParticipants";
        public static final String COLUMN_ITEM_ID = "itemId";
        public static final String COLUMN_ITEM_NAME = "itemName";
        public static final String COLUMN_PARTICIPANT_ID = "participantId";
        public static final String COLUMN_PARTICIPANT_NAME = "participantName";
        public static final String COLUMN_PARTICIPANT_UNIQUE_ID = "participantUniqueId";
        public static final String COLUMN_PARTICIPANT_IS_VIRTUAL = "participantIsVirtual";
        public static final String COLUMN_PROJECT_ID = "projectId";
        public static final String COLUMN_CURRENCY_ID = "currencyId";
        public static final String COLUMN_CURRENCY_CODE = "currencyCode";
        public static final String COLUMN_AMOUNT_PAYED = "amountPayed";
        public static final String COLUMN_CURRENCY_EXCHANGE_RATE = "currencyExchangeRate";

        private static final String VIEW_SELECT = "SELECT "
                + itemsParticipants.TABLE_ITEMS_PARTICIPANTS + ".rowid AS " + _ID + ", "
                + itemsParticipants.TABLE_ITEMS_PARTICIPANTS + "." + itemsParticipants.COLUMN_ITEM_ID + " AS " + COLUMN_ITEM_ID + ", "
                + items.TABLE_ITEMS + "." + items.COLUMN_NAME + " AS " + COLUMN_ITEM_NAME + ", "
                + itemsParticipants.TABLE_ITEMS_PARTICIPANTS + "." + itemsParticipants.COLUMN_PARTICIPANTS_ID + " AS " + COLUMN_PARTICIPANT_ID + ", "
                + participants.TABLE_PARTICIPANTS + "." + participants.COLUMN_NAME + " AS " + COLUMN_PARTICIPANT_NAME + ", "
                + participants.TABLE_PARTICIPANTS + "." + participants.COLUMN_UNIQUEID + " AS " + COLUMN_PARTICIPANT_UNIQUE_ID + ", "
                + participants.TABLE_PARTICIPANTS + "." + participants.COLUMN_ISVIRTUAL + " AS " + COLUMN_PARTICIPANT_IS_VIRTUAL + ", "
                + items.TABLE_ITEMS + "." + items.COLUMN_PROJECT_ID + " AS " + COLUMN_PROJECT_ID + ", "
                + itemsParticipants.TABLE_ITEMS_PARTICIPANTS + "." + itemsParticipants.COLUMN_CURRENCY_ID + " AS " + COLUMN_CURRENCY_ID + ", "
                + currencies.TABLE_CURRENCIES + "." + currencies.COLUMN_CURRENCY_CODE + " AS " + COLUMN_CURRENCY_CODE + ", "
                + currencies.TABLE_CURRENCIES + "." + currencies.COLUMN_EXCHANGE_RATE + " AS " + COLUMN_CURRENCY_EXCHANGE_RATE + ", "
                + itemsParticipants.TABLE_ITEMS_PARTICIPANTS + "." + itemsParticipants.COLUMN_AMOUNT_PAYED + " AS " + COLUMN_AMOUNT_PAYED
                + " FROM " + itemsParticipants.TABLE_ITEMS_PARTICIPANTS
                + " LEFT OUTER JOIN " + items.TABLE_ITEMS + " ON " + itemsParticipants.TABLE_ITEMS_PARTICIPANTS + "." + itemsParticipants.COLUMN_ITEM_ID + " = " + items.TABLE_ITEMS + "." + items._ID
                + " LEFT OUTER JOIN " + participants.TABLE_PARTICIPANTS + " ON " + itemsParticipants.TABLE_ITEMS_PARTICIPANTS + "." + itemsParticipants.COLUMN_PARTICIPANTS_ID + " = " + participants.TABLE_PARTICIPANTS + "." + participants._ID
                + " LEFT OUTER JOIN " + currencies.TABLE_CURRENCIES + " ON " + itemsParticipants.TABLE_ITEMS_PARTICIPANTS + "." + itemsParticipants.COLUMN_CURRENCY_ID + " = " + currencies.TABLE_CURRENCIES + "." + currencies._ID
                + ";";
        private static final String VIEW_CREATE = "CREATE VIEW "
                + VIEW_ITEMS_PARTICIPANTS
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

    public static abstract class projectParticipants_view implements BaseColumns {
        public static final String VIEW_PROJECT_PARTICIPANTS = "projectParticipantsView";
        public static final String COLUMN_PROJECT_ID = "projectId";
        public static final String COLUMN_PROJECT_NAME = "projectName";
        public static final String COLUMN_PARTICIPANT_ID = "participantId";
        public static final String COLUMN_PARTICIPANT_NAME = "participantName";
        public static final String COLUMN_PARTICIPANT_UNIQUE_ID = "participantUniqueId";
        public static final String COLUMN_PARTICIPANT_IS_VIRTUAL = "participantIsVirtual";
        public static final String COLUMN_PARTICIPANT_TOTAL_PAYED = "participantsTotalPayed";

        private static final String SUB_SELECT_TOTAL_PAYED = "SELECT "
                + itemsParticipants_view.VIEW_ITEMS_PARTICIPANTS + "." + itemsParticipants_view.COLUMN_PARTICIPANT_ID + ", "
                + "total(" + itemsParticipants_view.VIEW_ITEMS_PARTICIPANTS + "." + itemsParticipants_view.COLUMN_AMOUNT_PAYED + "*" + itemsParticipants_view.VIEW_ITEMS_PARTICIPANTS + "." + itemsParticipants_view.COLUMN_CURRENCY_EXCHANGE_RATE + ") AS totalPayed, "
                + itemsParticipants_view.VIEW_ITEMS_PARTICIPANTS + "." + itemsParticipants_view.COLUMN_PROJECT_ID
                + " FROM " + itemsParticipants_view.VIEW_ITEMS_PARTICIPANTS
                + " GROUP BY " + itemsParticipants_view.VIEW_ITEMS_PARTICIPANTS + "." + itemsParticipants_view.COLUMN_PARTICIPANT_ID
                + ", " + itemsParticipants_view.VIEW_ITEMS_PARTICIPANTS + "." + itemsParticipants_view.COLUMN_PROJECT_ID;

        private static final String VIEW_SELECT = "SELECT "
                + projectsParticipants.TABLE_PROJECTS_PARTICIPANTS + ".rowid AS " + _ID + ", "
                + projectsParticipants.TABLE_PROJECTS_PARTICIPANTS + "." + projectsParticipants.COLUMN_PROJECTS_ID + " AS " + COLUMN_PROJECT_ID + ", "
                + projects.TABLE_PROJECTS + "." + projects.COLUMN_NAME + " AS " + COLUMN_PROJECT_NAME + ", "
                + participants.TABLE_PARTICIPANTS + "." + participants._ID + " AS " + COLUMN_PARTICIPANT_ID + ", "
                + participants.TABLE_PARTICIPANTS + "." + participants.COLUMN_NAME + " AS " + COLUMN_PARTICIPANT_NAME + ", "
                + participants.TABLE_PARTICIPANTS + "." + participants.COLUMN_UNIQUEID + " AS " + COLUMN_PARTICIPANT_UNIQUE_ID + ", "
                + participants.TABLE_PARTICIPANTS + "." + participants.COLUMN_ISVIRTUAL + " AS " + COLUMN_PARTICIPANT_IS_VIRTUAL + ", "
                + "ifnull(sub.totalPayed,0.0) AS " + COLUMN_PARTICIPANT_TOTAL_PAYED
                + " FROM " + projectsParticipants.TABLE_PROJECTS_PARTICIPANTS
                + " LEFT OUTER JOIN " + projects.TABLE_PROJECTS + " ON " + projectsParticipants.TABLE_PROJECTS_PARTICIPANTS + "." + projectsParticipants.COLUMN_PROJECTS_ID + " = " + projects.TABLE_PROJECTS + "." + projects._ID
                + " LEFT OUTER JOIN " + participants.TABLE_PARTICIPANTS + " ON " + projectsParticipants.TABLE_PROJECTS_PARTICIPANTS + "." + projectsParticipants.COLUMN_PARTICIPANTS_ID + " = " + participants.TABLE_PARTICIPANTS + "." + participants._ID
                + " LEFT OUTER JOIN (" + SUB_SELECT_TOTAL_PAYED + ") AS 'sub' ON " + projectsParticipants.TABLE_PROJECTS_PARTICIPANTS + "." + projectsParticipants.COLUMN_PARTICIPANTS_ID + " = sub." + itemsParticipants_view.COLUMN_PARTICIPANT_ID
                + " AND " + projectsParticipants.TABLE_PROJECTS_PARTICIPANTS + "." + projectsParticipants.COLUMN_PROJECTS_ID + " = sub." + COLUMN_PROJECT_ID
                + ";";

        private static final String VIEW_CREATE = "CREATE VIEW "
                + VIEW_PROJECT_PARTICIPANTS
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

    public static abstract class participantTags_view implements BaseColumns {
        public static final String VIEW_PARTICIPANTS_TAGS = "viewParticipantsTags";
        public static final String COLUMN_PARTICIPANT_ID = tagFilter.COLUMN_PARTICIPANTS_ID;
        public static final String COLUMN_PARTICIPANT_NAME = "participantName";
        public static final String COLUMN_TAG_ID = tagFilter.COLUMN_TAG_ID;
        public static final String COLUMN_TAG_NAME = "tagName";

        private static final String VIEW_SELECT = "SELECT "
                + tagFilter.TABLE_TAG_FILTER + ".rowid AS " + _ID + ", "
                + tagFilter.TABLE_TAG_FILTER + "." + tagFilter.COLUMN_PARTICIPANTS_ID + " AS " + COLUMN_PARTICIPANT_ID + ", "
                + participants.TABLE_PARTICIPANTS + "." + participants.COLUMN_NAME + " AS " + COLUMN_PARTICIPANT_NAME + ", "
                + tagFilter.TABLE_TAG_FILTER + "." + tagFilter.COLUMN_TAG_ID + " AS " + COLUMN_TAG_ID + ", "
                + tags.TABLE_TAGS + "." + tags.COLUMN_NAME + " AS " + COLUMN_TAG_NAME
                + " FROM " + tagFilter.TABLE_TAG_FILTER
                + " LEFT OUTER JOIN " + participants.TABLE_PARTICIPANTS + " ON " + tagFilter.TABLE_TAG_FILTER + "." + tagFilter.COLUMN_PARTICIPANTS_ID + " = " + participants.TABLE_PARTICIPANTS + "." + participants._ID
                + " LEFT OUTER JOIN " + tags.TABLE_TAGS + " ON " + tagFilter.TABLE_TAG_FILTER + "." + tagFilter.COLUMN_TAG_ID + " = " + tags.TABLE_TAGS + "." + tags._ID
                + ";";
        private static final String VIEW_CREATE = "CREATE VIEW "
                + VIEW_PARTICIPANTS_TAGS
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
