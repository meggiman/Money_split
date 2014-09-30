package ch.ethz.itet.pps.budgetSplit.contentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.sql.SQLException;

import ch.ethz.itet.pps.budgetSplit.contentProvider.database.budgetSplitDBHelper;
import ch.ethz.itet.pps.budgetSplit.contentProvider.database.budgetSplitDBSchema;


public class budgetSplitContentProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private budgetSplitDBHelper dbHelper;

    static {
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.projects.TABLE_PROJECTS, budgetSplitContract.projects.PROJECTS);
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.projects.TABLE_PROJECTS + "/#", budgetSplitContract.projects.PROJECT);

        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.participants.TABLE_PARTICIPANTS, budgetSplitContract.participants.PARTICIPANTS);
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.participants.TABLE_PARTICIPANTS + "/#", budgetSplitContract.participants.PARTICIPANT);

        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.tags.TABLE_TAGS, budgetSplitContract.tags.TAGS);
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.tags.TABLE_TAGS, budgetSplitContract.tags.TAG);
    }

    public budgetSplitContentProvider() {
    }

    /**
     * Used to generate the Uri for a newly inserted record.
     *
     * @param uri The Content Uri of the Table to which the record was added.
     * @param id  The id of the new record.
     * @return The Content Uri of the inserted record.
     * @throws java.lang.IllegalArgumentException If the ID is below zero.
     */
    private Uri getUriForId(Uri uri, long id) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        } else {
            throw new SQLiteException("Problem while inserting into uri: " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case budgetSplitContract.projects.PROJECTS:
                return budgetSplitContract.projects.CONTENT_TYPE;
            case budgetSplitContract.projects.PROJECT:
                return budgetSplitContract.projects.CONTENT_ITEM_TYPE;

            case budgetSplitContract.participants.PARTICIPANTS:
                return budgetSplitContract.participants.CONTENT_TYPE;
            case budgetSplitContract.participants.PARTICIPANT:
                return budgetSplitContract.participants.CONTENT_ITEM_TYPE;

            case budgetSplitContract.tags.TAGS:
                return budgetSplitContract.tags.CONTENT_TYPE;
            case budgetSplitContract.tags.TAG:
                return budgetSplitContract.tags.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unsupported Uri: " + uri);
        }
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedCount = 0;
        String idString;
        String whereString;
        switch (sUriMatcher.match(uri)) {
            case budgetSplitContract.projects.PROJECTS:
                deletedCount = db.delete(budgetSplitDBSchema.projects.TABLE_PROJECTS, selection, selectionArgs);
                break;
            case budgetSplitContract.projects.PROJECT:
                idString = uri.getLastPathSegment();
                whereString = budgetSplitDBSchema._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                deletedCount = db.delete(budgetSplitDBSchema.projects.TABLE_PROJECTS, whereString, selectionArgs);
                break;

            case budgetSplitContract.participants.PARTICIPANTS:
                deletedCount = db.delete(budgetSplitDBSchema.participants.TABLE_PARTICIPANTS, selection, selectionArgs);
                break;
            case budgetSplitContract.participants.PARTICIPANT:
                idString = uri.getLastPathSegment();
                whereString = budgetSplitDBSchema._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                deletedCount = db.delete(budgetSplitDBSchema.participants.TABLE_PARTICIPANTS, whereString, selectionArgs);
                break;

            case budgetSplitContract.tags.TAGS:
                deletedCount = db.delete(budgetSplitDBSchema.tags.TABLE_TAGS, selection, selectionArgs);
                break;
            case budgetSplitContract.tags.TAG:
                idString = uri.getLastPathSegment();
                whereString = budgetSplitDBSchema._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                deletedCount = db.delete(budgetSplitDBSchema.tags.TABLE_TAGS, whereString, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Invalid Uri for delete: " + uri);
        }
        if (deletedCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedCount;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db;
        long id;
        switch (sUriMatcher.match(uri)) {
            //Insert into Projects-Table
            case budgetSplitContract.projects.PROJECTS:
                db = dbHelper.getWritableDatabase();
                id = db.insert(budgetSplitContract.projects.TABLE_PROJECTS, null, values);
                return getUriForId(uri, id);

            //Insert into Participants-Table
            case budgetSplitContract.participants.PARTICIPANTS:
                db = dbHelper.getWritableDatabase();
                id = db.insert(budgetSplitContract.participants.TABLE_PARTICIPANTS, null, values);
                return getUriForId(uri, id);

            //Insert into Tags-Table
            case budgetSplitContract.tags.TAGS:
                db = dbHelper.getWritableDatabase();
                id = db.insert(budgetSplitContract.tags.TABLE_TAGS, null, values);
                return getUriForId(uri, id);
            default:
                throw new IllegalArgumentException("Unsupported Uri for insertion: " + uri);

        }
    }

    @Override
    public boolean onCreate() {
        dbHelper = new budgetSplitDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {

            case budgetSplitContract.projects.PROJECTS:
                builder.setTables(budgetSplitDBSchema.projects.TABLE_PROJECTS);
                break;
            case budgetSplitContract.projects.PROJECT:
                builder.setTables(budgetSplitDBSchema.projects.TABLE_PROJECTS);
                builder.appendWhere(budgetSplitDBSchema.projects.TABLE_PROJECTS + "." + budgetSplitDBSchema._ID + " = " + uri.getLastPathSegment());
                break;

            case budgetSplitContract.participants.PARTICIPANTS:
                builder.setTables(budgetSplitDBSchema.participants.TABLE_PARTICIPANTS);
                break;
            case budgetSplitContract.participants.PARTICIPANT:
                builder.setTables(budgetSplitDBSchema.participants.TABLE_PARTICIPANTS);
                builder.appendWhere(budgetSplitDBSchema.participants.TABLE_PARTICIPANTS + "." + budgetSplitDBSchema._ID + " = " + uri.getLastPathSegment());
                break;

            case budgetSplitContract.tags.TAGS:
                builder.setTables(budgetSplitDBSchema.tags.TABLE_TAGS);
                break;
            case budgetSplitContract.tags.TAG:
                builder.setTables(budgetSplitDBSchema.tags.TABLE_TAGS);
                builder.appendWhere(budgetSplitDBSchema.tags.TABLE_TAGS + "." + budgetSplitDBSchema._ID + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Invalid Uri: " + uri);
        }
        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updateCount = 0;
        String idString;
        String whereString;
        switch (sUriMatcher.match(uri)) {
            case budgetSplitContract.projects.PROJECTS:
                updateCount = db.update(budgetSplitDBSchema.projects.TABLE_PROJECTS, values, selection, selectionArgs);
                break;
            case budgetSplitContract.projects.PROJECT:
                idString = uri.getLastPathSegment();
                whereString = budgetSplitDBSchema._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                updateCount = db.update(budgetSplitDBSchema.projects.TABLE_PROJECTS, values, whereString, selectionArgs);
                break;

            case budgetSplitContract.participants.PARTICIPANTS:
                updateCount = db.update(budgetSplitDBSchema.participants.TABLE_PARTICIPANTS, values, selection, selectionArgs);
                break;
            case budgetSplitContract.participants.PARTICIPANT:
                idString = uri.getLastPathSegment();
                whereString = budgetSplitDBSchema._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                updateCount = db.update(budgetSplitDBSchema.participants.TABLE_PARTICIPANTS, values, whereString, selectionArgs);
                break;

            case budgetSplitContract.tags.TAGS:
                updateCount = db.update(budgetSplitDBSchema.tags.TABLE_TAGS, values, selection, selectionArgs);
                break;
            case budgetSplitContract.tags.TAG:
                idString = uri.getLastPathSegment();
                whereString = budgetSplitDBSchema._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                updateCount = db.update(budgetSplitDBSchema.tags.TABLE_TAGS, values, whereString, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Invalid Uri for upadate: " + uri);
        }
        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }
}
