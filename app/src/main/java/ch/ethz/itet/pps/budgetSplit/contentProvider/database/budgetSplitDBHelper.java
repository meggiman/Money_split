package ch.ethz.itet.pps.budgetSplit.contentProvider.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Manuel on 29.09.2014.
 */
public class budgetSplitDBHelper extends SQLiteOpenHelper {

    public budgetSplitDBHelper(Context context) {
        super(context, budgetSplitDBSchema.DATABASE_NAME, null, budgetSplitDBSchema.DATABASE_VERSION);
    }

    /**
     * Uses the static Methods out of the nested classes in @see budgetSplitDBSchema to create the Database.
     *
     * @param database The database.
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        budgetSplitDBSchema.participants.onCreate(database);
        budgetSplitDBSchema.projects.onCreate(database);
        budgetSplitDBSchema.items.onCreate(database);
        budgetSplitDBSchema.tags.onCreate(database);
        budgetSplitDBSchema.currencies.onCreate(database);

        budgetSplitDBSchema.itemsParticipants.onCreate(database);
        budgetSplitDBSchema.excludeItems.onCreate(database);
        budgetSplitDBSchema.projectsParticipants.onCreate(database);
        budgetSplitDBSchema.itemsTags.onCreate(database);
        budgetSplitDBSchema.tagFilter.onCreate(database);

        budgetSplitDBSchema.projects_view.onCreate(database);
        budgetSplitDBSchema.items_view.onCreate(database);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            //Enables Foreign Key support.
            db.execSQL("PRAGMA foreign_keys=ON");
        }
    }

    /**
     * Uses the static Methods out of the nested classes in @see budgetSplitDBSchema to upgrade the Database.
     * Don't forget to implement those Methods if you're upgrading the database structure.
     *
     * @param database The database.
     * @param i        The old database version.
     * @param i2       The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2) {
        budgetSplitDBSchema.projects.onUpgrade(database, i, i2);
        budgetSplitDBSchema.participants.onUpgrade(database, i, i2);
        budgetSplitDBSchema.items.onUpgrade(database, i, i2);
        budgetSplitDBSchema.tags.onUpgrade(database, i, i2);
        budgetSplitDBSchema.currencies.onUpgrade(database, i, i2);

        budgetSplitDBSchema.itemsParticipants.onUpgrade(database, i, i2);
        budgetSplitDBSchema.excludeItems.onUpgrade(database, i, i2);
        budgetSplitDBSchema.projectsParticipants.onUpgrade(database, i, i2);
        budgetSplitDBSchema.itemsTags.onUpgrade(database, i, i2);
        budgetSplitDBSchema.tagFilter.onUpgrade(database, i, i2);

        budgetSplitDBSchema.projects_view.onUpgrade(database, i, i2);
        budgetSplitDBSchema.items_view.onUpgrade(database, i, i2);
    }
}
