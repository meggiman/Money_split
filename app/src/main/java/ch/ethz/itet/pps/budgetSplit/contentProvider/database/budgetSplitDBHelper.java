package ch.ethz.itet.pps.budgetSplit.contentProvider.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Manuel on 29.09.2014.
 */
public class budgetSplitDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "budgetSplit.db";
    private static final int DATABASE_VERSION = 1;

    public budgetSplitDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Uses the static Methods out of the nested classes in @see budgetSplitDBContract to create the Database.
     *
     * @param database The database.
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        budgetSplitDBContract.projects.onCreate(database);
        budgetSplitDBContract.participant.onCreate(database);
        budgetSplitDBContract.items.onCreate(database);
        budgetSplitDBContract.tags.onCreate(database);
        budgetSplitDBContract.currencies.onCreate(database);

        budgetSplitDBContract.itemsParticipants.onCreate(database);
        budgetSplitDBContract.excludeItems.onCreate(database);
        budgetSplitDBContract.projectsParticipants.onCreate(database);
        budgetSplitDBContract.projectsTags.onCreate(database);
        budgetSplitDBContract.tagFilter.onCreate(database);
    }

    /**
     * Uses the static Methods out of the nested classes in @see budgetSplitDBContract to upgrade the Database.
     * Don't forget to implement those Methods if you're upgrading the database structure.
     *
     * @param database The database.
     * @param i        The old database version.
     * @param i2       The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2) {
        budgetSplitDBContract.projects.onUpgrade(database, i, i2);
        budgetSplitDBContract.participant.onUpgrade(database, i, i2);
        budgetSplitDBContract.items.onUpgrade(database, i, i2);
        budgetSplitDBContract.tags.onUpgrade(database, i, i2);
        budgetSplitDBContract.currencies.onUpgrade(database, i, i2);

        budgetSplitDBContract.itemsParticipants.onUpgrade(database, i, i2);
        budgetSplitDBContract.excludeItems.onUpgrade(database, i, i2);
        budgetSplitDBContract.projectsParticipants.onUpgrade(database, i, i2);
        budgetSplitDBContract.projectsTags.onUpgrade(database, i, i2);
        budgetSplitDBContract.tagFilter.onUpgrade(database, i, i2);
    }
}
