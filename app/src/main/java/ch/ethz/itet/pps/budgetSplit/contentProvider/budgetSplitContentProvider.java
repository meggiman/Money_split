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

import ch.ethz.itet.pps.budgetSplit.contentProvider.database.budgetSplitDBHelper;
import ch.ethz.itet.pps.budgetSplit.contentProvider.database.budgetSplitDBSchema;


public class budgetSplitContentProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private budgetSplitDBHelper dbHelper;

    static {
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.projects.TABLE_PROJECTS, budgetSplitContract.projects.PROJECTS);
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.projects.TABLE_PROJECTS + "/#", budgetSplitContract.projects.PROJECT);

        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.projectParticipants.TABLE_PROJECT_PARTICIPANTS, budgetSplitContract.projectParticipants.PROJECT_PARTICIPANTS);
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.projectParticipants.TABLE_PROJECT_PARTICIPANTS + "/#", budgetSplitContract.projectParticipants.PROJECT_PARTICIPANT);

        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.participants.TABLE_PARTICIPANTS, budgetSplitContract.participants.PARTICIPANTS);
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.participants.TABLE_PARTICIPANTS + "/#", budgetSplitContract.participants.PARTICIPANT);

        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.tags.TABLE_TAGS, budgetSplitContract.tags.TAGS);
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.tags.TABLE_TAGS + "/#", budgetSplitContract.tags.TAG);

        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.items.TABLE_ITEMS, budgetSplitContract.items.ITEMS);
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.items.TABLE_ITEMS + "/#", budgetSplitContract.items.ITEM);

        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.itemsParticipants.TABLE_ITEMS_PARTICIPANTS, budgetSplitContract.itemsParticipants.ITEMS_PARTICIPANTS);
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.itemsParticipants.TABLE_ITEMS_PARTICIPANTS + "/#", budgetSplitContract.itemsParticipants.ITEM_PARTICIPANTS);

        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.excludeItems.TABLE_EXCLUDE_ITEMS, budgetSplitContract.excludeItems.EXCLUDE_ITEMS);
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.excludeItems.TABLE_EXCLUDE_ITEMS + "/#", budgetSplitContract.excludeItems.EXCLUDE_ITEM);

        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.tagFilter.TABLE_TAG_FILTER, budgetSplitContract.tagFilter.TAGS_FILTER);
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.tagFilter.TABLE_TAG_FILTER + "/#", budgetSplitContract.tagFilter.TAG_FILTER);

        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.projectsDetailsRO.TABLE_PROJECTS_DETAILS_RO, budgetSplitContract.projectsDetailsRO.PROJECTS_DETAILS);
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.projectsDetailsRO.TABLE_PROJECTS_DETAILS_RO + "/#", budgetSplitContract.projectsDetailsRO.PROJECT_DETAILS);

        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.itemsDetailsRO.TABLE_ITEMS_DETAILS_RO, budgetSplitContract.itemsDetailsRO.ITEMS_DETAILS_RO);
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.itemsDetailsRO.TABLE_ITEMS_DETAILS_RO + "/#", budgetSplitContract.itemsDetailsRO.ITEM_DETAILS_RO);

        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.itemsTagsDetailsRO.TABLE_ITEMS_TAGS_DETAILS_RO, budgetSplitContract.itemsTagsDetailsRO.ITEMS_TAGS_DETAILS);
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.itemsTagsDetailsRO.TABLE_ITEM_TAGS_DETAILS_RO + "/#", budgetSplitContract.itemsTagsDetailsRO.ITEM_TAGS_DETAILS);
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.itemsTagsDetailsRO.TABLE_ITEMS_TAG_DETAILS_RO + "/#", budgetSplitContract.itemsTagsDetailsRO.ITEMS_TAG_DETAILS);

        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.itemsParticipantsDetailsRO.TABLE_ITEMS_PARTICIPANTS_DETAILS_RO, budgetSplitContract.itemsParticipantsDetailsRO.ITEMS_PARTICIPANTS_DETAILS);
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.itemsParticipantsDetailsRO.TABLE_ITEM_PARTICIPANTS_DETAILS_RO + "/#", budgetSplitContract.itemsParticipantsDetailsRO.ITEM_PARTICIPANTS_DETAILS);
        sUriMatcher.addURI(budgetSplitContract.AUTHORITY, budgetSplitContract.itemsParticipantsDetailsRO.TABLE_ITEMS_PARTICIPANT_DETAILS_RO + "/#", budgetSplitContract.itemsParticipantsDetailsRO.ITEMS_PARTICIPANT_DETAILS);
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

            //Get Type of Table projects
            case budgetSplitContract.projects.PROJECTS:
                return budgetSplitContract.projects.CONTENT_TYPE;
            case budgetSplitContract.projects.PROJECT:
                return budgetSplitContract.projects.CONTENT_ITEM_TYPE;

            //Get Type of Table projectParticipants
            case budgetSplitContract.projectParticipants.PROJECT_PARTICIPANTS:
                return budgetSplitContract.projectParticipants.CONTENT_TYPE;
            case budgetSplitContract.projectParticipants.PROJECT_PARTICIPANT:
                return budgetSplitContract.projectParticipants.CONTENT_ITEM_TYPE;

            //Get Type of Table participants
            case budgetSplitContract.participants.PARTICIPANTS:
                return budgetSplitContract.participants.CONTENT_TYPE;
            case budgetSplitContract.participants.PARTICIPANT:
                return budgetSplitContract.participants.CONTENT_ITEM_TYPE;

            //Get Type of Table tags
            case budgetSplitContract.tags.TAGS:
                return budgetSplitContract.tags.CONTENT_TYPE;
            case budgetSplitContract.tags.TAG:
                return budgetSplitContract.tags.CONTENT_ITEM_TYPE;

            //Get Type of Table itemsTags
            case budgetSplitContract.itemsTags.ITEMSTAGS:
                return budgetSplitContract.itemsTags.CONTENT_TYPE;
            case budgetSplitContract.itemsTags.ITEMTAGS:
                return budgetSplitContract.itemsTags.CONTENT_ITEM_TYPE;

            //Get Type of Table currencies
            case budgetSplitContract.currencies.CURRENCIES:
                return budgetSplitContract.currencies.CONTENT_TYPE;
            case budgetSplitContract.currencies.CURRENCY:
                return budgetSplitContract.currencies.CONTENT_ITEM_TYPE;

            //Get Type of Table items
            case budgetSplitContract.items.ITEMS:
                return budgetSplitContract.items.CONTENT_TYPE;
            case budgetSplitContract.items.ITEM:
                return budgetSplitContract.items.CONTENT_ITEM_TYPE;

            //Get Type of Table itemsParticipants
            case budgetSplitContract.itemsParticipants.ITEMS_PARTICIPANTS:
                return budgetSplitContract.itemsParticipants.CONTENT_TYPE;
            case budgetSplitContract.itemsParticipants.ITEM_PARTICIPANTS:
                return budgetSplitContract.itemsParticipants.CONTENT_ITEM_TYPE;

            //Get Type of Table excludeItems
            case budgetSplitContract.excludeItems.EXCLUDE_ITEMS:
                return budgetSplitContract.excludeItems.CONTENT_TYPE;
            case budgetSplitContract.excludeItems.EXCLUDE_ITEM:
                return budgetSplitContract.excludeItems.CONTENT_ITEM_TYPE;

            //Get Type of Table tagFilter
            case budgetSplitContract.tagFilter.TAGS_FILTER:
                return budgetSplitContract.tagFilter.CONTENT_TYPE;
            case budgetSplitContract.tagFilter.TAG_FILTER:
                return budgetSplitContract.tagFilter.CONTENT_ITEM_TYPE;

            //Get Type of Table projectDetailsRO
            case budgetSplitContract.projectsDetailsRO.PROJECTS_DETAILS:
                return budgetSplitContract.projectsDetailsRO.CONTENT_TYPE;
            case budgetSplitContract.projectsDetailsRO.PROJECT_DETAILS:
                return budgetSplitContract.projectsDetailsRO.CONTENT_ITEM_TYPE;

            //Get Type of Table itemsDetailsRO
            case budgetSplitContract.itemsDetailsRO.ITEMS_DETAILS_RO:
                return budgetSplitContract.itemsDetailsRO.CONTENT_TYPE;
            case budgetSplitContract.itemsDetailsRO.ITEM_DETAILS_RO:
                return budgetSplitContract.itemsDetailsRO.CONTENT_ITEM_TYPE;

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

            //Delete Rows in Table project
            case budgetSplitContract.projects.PROJECTS:
                deletedCount = db.delete(budgetSplitDBSchema.projects.TABLE_PROJECTS, selection, selectionArgs);
                break;
            case budgetSplitContract.projects.PROJECT:
                idString = uri.getLastPathSegment();
                whereString = budgetSplitDBSchema.projects._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                deletedCount = db.delete(budgetSplitDBSchema.projects.TABLE_PROJECTS, whereString, selectionArgs);
                break;

            //Delete Rows in Table projectParticipants
            case budgetSplitContract.projectParticipants.PROJECT_PARTICIPANTS:
                deletedCount = db.delete(budgetSplitDBSchema.projectsParticipants.TABLE_PROJECTS_PARTICIPANTS, selection, selectionArgs);
                break;
            case budgetSplitContract.projectParticipants.PROJECT_PARTICIPANT:
                idString = uri.getLastPathSegment();
                whereString = "rowid" + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                deletedCount = db.delete(budgetSplitDBSchema.projectsParticipants.TABLE_PROJECTS_PARTICIPANTS, whereString, selectionArgs);
                break;

            //Delete Rows in Table participants
            case budgetSplitContract.participants.PARTICIPANTS:
                deletedCount = db.delete(budgetSplitDBSchema.participants.TABLE_PARTICIPANTS, selection, selectionArgs);
                break;
            case budgetSplitContract.participants.PARTICIPANT:
                idString = uri.getLastPathSegment();
                whereString = budgetSplitDBSchema.participants._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                deletedCount = db.delete(budgetSplitDBSchema.participants.TABLE_PARTICIPANTS, whereString, selectionArgs);
                break;

            //Delete Rows in Table tags
            case budgetSplitContract.tags.TAGS:
                deletedCount = db.delete(budgetSplitDBSchema.tags.TABLE_TAGS, selection, selectionArgs);
                break;
            case budgetSplitContract.tags.TAG:
                idString = uri.getLastPathSegment();
                whereString = budgetSplitDBSchema.tags._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                deletedCount = db.delete(budgetSplitDBSchema.tags.TABLE_TAGS, whereString, selectionArgs);
                break;

            //Delete Rows in Table itemsTags
            case budgetSplitContract.itemsTags.ITEMSTAGS:
                deletedCount = db.delete(budgetSplitDBSchema.itemsTags.TABLE_ITEMS_TAGS, selection, selectionArgs);
                break;
            case budgetSplitContract.itemsTags.ITEMTAGS:
                idString = uri.getLastPathSegment();
                whereString = "rowid = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                deletedCount = db.delete(budgetSplitDBSchema.itemsTags.TABLE_ITEMS_TAGS, whereString, selectionArgs);
                break;

            //Delete Rows in Table currencies
            case budgetSplitContract.currencies.CURRENCIES:
                deletedCount = db.delete(budgetSplitDBSchema.currencies.TABLE_CURRENCIES, selection, selectionArgs);
                break;
            case budgetSplitContract.currencies.CURRENCY:
                idString = uri.getLastPathSegment();
                whereString = budgetSplitDBSchema.currencies._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                deletedCount = db.delete(budgetSplitDBSchema.currencies.TABLE_CURRENCIES, whereString, selectionArgs);
                break;

            //Delete Rows in Table items
            case budgetSplitContract.items.ITEMS:
                deletedCount = db.delete(budgetSplitDBSchema.tags.TABLE_TAGS, selection, selectionArgs);
                break;
            case budgetSplitContract.items.ITEM:
                idString = uri.getLastPathSegment();
                whereString = budgetSplitDBSchema.items._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                deletedCount = db.delete(budgetSplitDBSchema.items.TABLE_ITEMS, whereString, selectionArgs);
                break;

            //Delete Rows in Table itemsParticipants
            case budgetSplitContract.itemsParticipants.ITEMS_PARTICIPANTS:
                deletedCount = db.delete(budgetSplitDBSchema.itemsParticipants.TABLE_ITEMS_PARTICIPANTS, selection, selectionArgs);
                break;
            case budgetSplitContract.itemsParticipants.ITEM_PARTICIPANTS:
                idString = uri.getLastPathSegment();
                whereString = "rowid = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                deletedCount = db.delete(budgetSplitDBSchema.itemsParticipants.TABLE_ITEMS_PARTICIPANTS, whereString, selectionArgs);
                break;

            //Delete Rows in Table excludeItems
            case budgetSplitContract.excludeItems.EXCLUDE_ITEMS:
                deletedCount = db.delete(budgetSplitDBSchema.excludeItems.TABLE_EXCLUDE_ITEMS, selection, selectionArgs);
                break;
            case budgetSplitContract.excludeItems.EXCLUDE_ITEM:
                idString = uri.getLastPathSegment();
                whereString = "rowid = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                deletedCount = db.delete(budgetSplitDBSchema.excludeItems.TABLE_EXCLUDE_ITEMS, whereString, selectionArgs);
                break;

            //Delete Rows in Table tagFilter
            case budgetSplitContract.tagFilter.TAGS_FILTER:
                deletedCount = db.delete(budgetSplitDBSchema.tagFilter.TABLE_TAG_FILTER, selection, selectionArgs);
                break;
            case budgetSplitContract.tagFilter.TAG_FILTER:
                idString = uri.getLastPathSegment();
                whereString = "rowid = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                deletedCount = db.delete(budgetSplitDBSchema.tagFilter.TABLE_TAG_FILTER, whereString, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Invalid Uri for delete: " + uri);
        }
        if (deletedCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            notifyViews(uri);
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
                notifyViews(uri);
                return getUriForId(uri, id);

            //Insert into ProjectParticipants-Table
            case budgetSplitContract.projectParticipants.PROJECT_PARTICIPANTS:
                db = dbHelper.getWritableDatabase();
                id = db.insert(budgetSplitContract.projectParticipants.TABLE_PROJECT_PARTICIPANTS, null, values);
                notifyViews(uri);
                return getUriForId(uri, id);

            //Insert into Participants-Table
            case budgetSplitContract.participants.PARTICIPANTS:
                db = dbHelper.getWritableDatabase();
                id = db.insert(budgetSplitContract.participants.TABLE_PARTICIPANTS, null, values);
                notifyViews(uri);
                return getUriForId(uri, id);

            //Insert into Tags-Table
            case budgetSplitContract.tags.TAGS:
                db = dbHelper.getWritableDatabase();
                id = db.insert(budgetSplitContract.tags.TABLE_TAGS, null, values);
                notifyViews(uri);
                return getUriForId(uri, id);

            //Insert into itemsTags-Table
            case budgetSplitContract.itemsTags.ITEMSTAGS:
                db = dbHelper.getWritableDatabase();
                id = db.insert(budgetSplitContract.itemsTags.TABLE_ITEMS_TAGS, null, values);
                notifyViews(uri);
                return getUriForId(uri, id);

            //Insert into currencies-Table
            case budgetSplitContract.currencies.CURRENCIES:
                db = dbHelper.getWritableDatabase();
                id = db.insert(budgetSplitContract.currencies.TABLE_CURRENCIES, null, values);
                notifyViews(uri);
                return getUriForId(uri, id);

            //Insert into items-Table
            case budgetSplitContract.items.ITEMS:
                db = dbHelper.getWritableDatabase();
                id = db.insert(budgetSplitContract.items.TABLE_ITEMS, null, values);
                notifyViews(uri);
                return getUriForId(uri, id);

            //Insert into Tags-Table
            case budgetSplitContract.itemsParticipants.ITEMS_PARTICIPANTS:
                db = dbHelper.getWritableDatabase();
                id = db.insert(budgetSplitContract.itemsParticipants.TABLE_ITEMS_PARTICIPANTS, null, values);
                notifyViews(uri);
                return getUriForId(uri, id);

            //Insert into Tags-Table
            case budgetSplitContract.excludeItems.EXCLUDE_ITEMS:
                db = dbHelper.getWritableDatabase();
                id = db.insert(budgetSplitContract.excludeItems.TABLE_EXCLUDE_ITEMS, null, values);
                notifyViews(uri);
                return getUriForId(uri, id);

            //Insert into tagFilter-Table
            case budgetSplitContract.tagFilter.TAGS_FILTER:
                db = dbHelper.getWritableDatabase();
                id = db.insert(budgetSplitContract.tagFilter.TABLE_TAG_FILTER, null, values);
                notifyViews(uri);
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
            //Query Table project
            case budgetSplitContract.projects.PROJECTS:
                builder.setTables(budgetSplitDBSchema.projects.TABLE_PROJECTS);
                break;
            case budgetSplitContract.projects.PROJECT:
                builder.setTables(budgetSplitDBSchema.projects.TABLE_PROJECTS);
                builder.appendWhere(budgetSplitDBSchema.projects.TABLE_PROJECTS + "." + budgetSplitDBSchema.projects._ID + " = " + uri.getLastPathSegment());
                break;

            //Query Table projectParticipants
            case budgetSplitContract.projectParticipants.PROJECT_PARTICIPANTS:
                builder.setTables(budgetSplitDBSchema.projectsParticipants.TABLE_PROJECTS_PARTICIPANTS);
                break;
            case budgetSplitContract.projectParticipants.PROJECT_PARTICIPANT:
                builder.setTables(budgetSplitDBSchema.projectsParticipants.TABLE_PROJECTS_PARTICIPANTS);
                builder.appendWhere("rowid = " + uri.getLastPathSegment());
                break;

            //Query Table participants
            case budgetSplitContract.participants.PARTICIPANTS:
                builder.setTables(budgetSplitDBSchema.participants.TABLE_PARTICIPANTS);
                break;
            case budgetSplitContract.participants.PARTICIPANT:
                builder.setTables(budgetSplitDBSchema.participants.TABLE_PARTICIPANTS);
                builder.appendWhere(budgetSplitDBSchema.participants.TABLE_PARTICIPANTS + "." + budgetSplitDBSchema.participants._ID + " = " + uri.getLastPathSegment());
                break;

            //Query Table tags
            case budgetSplitContract.tags.TAGS:
                builder.setTables(budgetSplitDBSchema.tags.TABLE_TAGS);
                break;
            case budgetSplitContract.tags.TAG:
                builder.setTables(budgetSplitDBSchema.tags.TABLE_TAGS);
                builder.appendWhere(budgetSplitDBSchema.tags.TABLE_TAGS + "." + budgetSplitDBSchema.tags._ID + " = " + uri.getLastPathSegment());
                break;

            //Query Table tags
            case budgetSplitContract.itemsTags.ITEMSTAGS:
                builder.setTables(budgetSplitDBSchema.itemsTags.TABLE_ITEMS_TAGS);
                break;
            case budgetSplitContract.itemsTags.ITEMTAGS:
                builder.setTables(budgetSplitDBSchema.itemsTags.TABLE_ITEMS_TAGS);
                builder.appendWhere("rowid = " + uri.getLastPathSegment());
                break;

            //Query Table currencies
            case budgetSplitContract.currencies.CURRENCIES:
                builder.setTables(budgetSplitDBSchema.currencies.TABLE_CURRENCIES);
                break;
            case budgetSplitContract.currencies.CURRENCY:
                builder.setTables(budgetSplitDBSchema.currencies.TABLE_CURRENCIES);
                builder.appendWhere(budgetSplitDBSchema.currencies.TABLE_CURRENCIES + "." + budgetSplitDBSchema.currencies._ID + " = " + uri.getLastPathSegment());
                break;

            //Query Table items
            case budgetSplitContract.items.ITEMS:
                builder.setTables(budgetSplitDBSchema.items.TABLE_ITEMS);
                break;
            case budgetSplitContract.items.ITEM:
                builder.setTables(budgetSplitDBSchema.items.TABLE_ITEMS);
                builder.appendWhere(budgetSplitDBSchema.items.TABLE_ITEMS + "." + budgetSplitDBSchema.items._ID + " = " + uri.getLastPathSegment());
                break;

            //Query Table itemsParticipants
            case budgetSplitContract.itemsParticipants.ITEMS_PARTICIPANTS:
                builder.setTables(budgetSplitDBSchema.itemsParticipants.TABLE_ITEMS_PARTICIPANTS);
                break;
            case budgetSplitContract.itemsParticipants.ITEM_PARTICIPANTS:
                builder.setTables(budgetSplitDBSchema.itemsParticipants.TABLE_ITEMS_PARTICIPANTS);
                builder.appendWhere("rowid = " + uri.getLastPathSegment());
                break;

            //Query Table exclude_items
            case budgetSplitContract.excludeItems.EXCLUDE_ITEMS:
                builder.setTables(budgetSplitDBSchema.excludeItems.TABLE_EXCLUDE_ITEMS);
                break;
            case budgetSplitContract.excludeItems.EXCLUDE_ITEM:
                builder.setTables(budgetSplitDBSchema.excludeItems.TABLE_EXCLUDE_ITEMS);
                builder.appendWhere("rowid = " + uri.getLastPathSegment());
                break;

            //Query Table tagsFilter
            case budgetSplitContract.tagFilter.TAGS_FILTER:
                builder.setTables(budgetSplitDBSchema.tagFilter.TABLE_TAG_FILTER);
                break;
            case budgetSplitContract.tagFilter.TAG_FILTER:
                builder.setTables(budgetSplitDBSchema.tagFilter.TABLE_TAG_FILTER);
                builder.appendWhere("rowid = " + uri.getLastPathSegment());
                break;

            //Query Table projectDetailsRO
            case budgetSplitContract.projectsDetailsRO.PROJECTS_DETAILS:
                builder.setTables(budgetSplitContract.projectsDetailsRO.TABLE_PROJECTS_DETAILS_RO);
                break;
            case budgetSplitContract.projectsDetailsRO.PROJECT_DETAILS:
                builder.setTables(budgetSplitDBSchema.projects_view.VIEW_PROJECTS);
                builder.appendWhere(budgetSplitDBSchema.projects_view.VIEW_PROJECTS + "." + budgetSplitDBSchema.projects_view._ID + " = " + uri.getLastPathSegment());
                break;

            //Query Table itemsDetailsRO
            case budgetSplitContract.itemsDetailsRO.ITEMS_DETAILS_RO:
                builder.setTables(budgetSplitDBSchema.items_view.VIEW_ITEMS);
                break;
            case budgetSplitContract.itemsDetailsRO.ITEM_DETAILS_RO:
                builder.setTables(budgetSplitDBSchema.items_view.VIEW_ITEMS);
                builder.appendWhere(budgetSplitDBSchema.items_view.VIEW_ITEMS + "." + budgetSplitDBSchema.items_view._ID + " = " + uri.getLastPathSegment());
                break;

            //Query Table itemsTagsDetailsRO
            case budgetSplitContract.itemsTagsDetailsRO.ITEMS_TAGS_DETAILS:
                builder.setTables(budgetSplitDBSchema.itemsTags_view.VIEW_ITEMS_TAGS);
                break;
            case budgetSplitContract.itemsTagsDetailsRO.ITEM_TAGS_DETAILS:
                builder.setTables(budgetSplitDBSchema.itemsTags_view.VIEW_ITEMS_TAGS);
                builder.appendWhere(budgetSplitDBSchema.itemsTags_view.VIEW_ITEMS_TAGS + "." + budgetSplitDBSchema.itemsTags_view.COLUMN_ITEM_ID + " = " + uri.getLastPathSegment());
                break;
            case budgetSplitContract.itemsTagsDetailsRO.ITEMS_TAG_DETAILS:
                builder.setTables(budgetSplitDBSchema.itemsTags_view.VIEW_ITEMS_TAGS);
                builder.appendWhere(budgetSplitDBSchema.itemsTags_view.VIEW_ITEMS_TAGS + "." + budgetSplitDBSchema.itemsTags_view.COLUMN_TAG_ID + " = " + uri.getLastPathSegment());
                break;

            //Query Table itemsParticipantDetailsRO
            case budgetSplitContract.itemsParticipantsDetailsRO.ITEMS_PARTICIPANTS_DETAILS:
                builder.setTables(budgetSplitDBSchema.itemsParticipants_view.VIEW_ITEMS_PARTICIPANTS);
                break;
            case budgetSplitContract.itemsParticipantsDetailsRO.ITEM_PARTICIPANTS_DETAILS:
                builder.setTables(budgetSplitDBSchema.itemsParticipants_view.VIEW_ITEMS_PARTICIPANTS);
                builder.appendWhere(budgetSplitDBSchema.itemsParticipants_view.VIEW_ITEMS_PARTICIPANTS + "." + budgetSplitDBSchema.itemsParticipants_view.COLUMN_ITEM_ID + " = " + uri.getLastPathSegment());
                break;
            case budgetSplitContract.itemsParticipantsDetailsRO.ITEMS_PARTICIPANT_DETAILS:
                builder.setTables(budgetSplitDBSchema.itemsParticipants_view.VIEW_ITEMS_PARTICIPANTS);
                builder.appendWhere(budgetSplitDBSchema.itemsParticipants_view.VIEW_ITEMS_PARTICIPANTS + "." + budgetSplitDBSchema.itemsParticipants_view.COLUMN_PARTICIPANT_ID + " = " + uri.getLastPathSegment());
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
            //update rows in Table project
            case budgetSplitContract.projects.PROJECTS:
                updateCount = db.update(budgetSplitDBSchema.projects.TABLE_PROJECTS, values, selection, selectionArgs);
                break;
            case budgetSplitContract.projects.PROJECT:
                idString = uri.getLastPathSegment();
                whereString = budgetSplitDBSchema.projects._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                updateCount = db.update(budgetSplitDBSchema.projects.TABLE_PROJECTS, values, whereString, selectionArgs);
                break;

            //update rows in Table projectParticipants
            case budgetSplitContract.projectParticipants.PROJECT_PARTICIPANTS:
                updateCount = db.update(budgetSplitDBSchema.projectsParticipants.TABLE_PROJECTS_PARTICIPANTS, values, selection, selectionArgs);
                break;
            case budgetSplitContract.projectParticipants.PROJECT_PARTICIPANT:
                idString = uri.getLastPathSegment();
                whereString = "rowid = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                updateCount = db.update(budgetSplitDBSchema.projects.TABLE_PROJECTS, values, whereString, selectionArgs);
                break;

            //update rows in Table participants
            case budgetSplitContract.participants.PARTICIPANTS:
                updateCount = db.update(budgetSplitDBSchema.participants.TABLE_PARTICIPANTS, values, selection, selectionArgs);
                break;
            case budgetSplitContract.participants.PARTICIPANT:
                idString = uri.getLastPathSegment();
                whereString = budgetSplitDBSchema.participants._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                updateCount = db.update(budgetSplitDBSchema.participants.TABLE_PARTICIPANTS, values, whereString, selectionArgs);
                break;

            //update rows in Table tags
            case budgetSplitContract.tags.TAGS:
                updateCount = db.update(budgetSplitDBSchema.tags.TABLE_TAGS, values, selection, selectionArgs);
                break;
            case budgetSplitContract.tags.TAG:
                idString = uri.getLastPathSegment();
                whereString = budgetSplitDBSchema.tags._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                updateCount = db.update(budgetSplitDBSchema.tags.TABLE_TAGS, values, whereString, selectionArgs);
                break;

            //update rows in Table itemsTags
            case budgetSplitContract.itemsTags.ITEMSTAGS:
                updateCount = db.update(budgetSplitDBSchema.itemsTags.TABLE_ITEMS_TAGS, values, selection, selectionArgs);
                break;
            case budgetSplitContract.itemsTags.ITEMTAGS:
                idString = uri.getLastPathSegment();
                whereString = "rowid = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                updateCount = db.update(budgetSplitDBSchema.tags.TABLE_TAGS, values, whereString, selectionArgs);
                break;

            //update rows in Table currencies
            case budgetSplitContract.currencies.CURRENCIES:
                updateCount = db.update(budgetSplitDBSchema.currencies.TABLE_CURRENCIES, values, selection, selectionArgs);
                break;
            case budgetSplitContract.currencies.CURRENCY:
                idString = uri.getLastPathSegment();
                whereString = budgetSplitDBSchema.currencies._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                updateCount = db.update(budgetSplitDBSchema.currencies.TABLE_CURRENCIES, values, whereString, selectionArgs);
                break;

            //update rows in Table items
            case budgetSplitContract.items.ITEMS:
                updateCount = db.update(budgetSplitDBSchema.items.TABLE_ITEMS, values, selection, selectionArgs);
                break;
            case budgetSplitContract.items.ITEM:
                idString = uri.getLastPathSegment();
                whereString = budgetSplitDBSchema.items._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                updateCount = db.update(budgetSplitDBSchema.items.TABLE_ITEMS, values, whereString, selectionArgs);
                break;

            //update rows in Table itemsParticipants
            case budgetSplitContract.itemsParticipants.ITEMS_PARTICIPANTS:
                updateCount = db.update(budgetSplitDBSchema.itemsParticipants.TABLE_ITEMS_PARTICIPANTS, values, selection, selectionArgs);
                break;
            case budgetSplitContract.itemsParticipants.ITEM_PARTICIPANTS:
                idString = uri.getLastPathSegment();
                whereString = "rowid = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                updateCount = db.update(budgetSplitDBSchema.itemsParticipants.TABLE_ITEMS_PARTICIPANTS, values, whereString, selectionArgs);
                break;

            //update rows in Table excludeItems
            case budgetSplitContract.excludeItems.EXCLUDE_ITEMS:
                updateCount = db.update(budgetSplitDBSchema.excludeItems.TABLE_EXCLUDE_ITEMS, values, selection, selectionArgs);
                break;
            case budgetSplitContract.excludeItems.EXCLUDE_ITEM:
                idString = uri.getLastPathSegment();
                whereString = "rowid = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                updateCount = db.update(budgetSplitDBSchema.excludeItems.TABLE_EXCLUDE_ITEMS, values, whereString, selectionArgs);
                break;

            //update rows in Table tagFilter
            case budgetSplitContract.tagFilter.TAGS_FILTER:
                updateCount = db.update(budgetSplitDBSchema.projectsParticipants.TABLE_PROJECTS_PARTICIPANTS, values, selection, selectionArgs);
                break;
            case budgetSplitContract.tagFilter.TAG_FILTER:
                idString = uri.getLastPathSegment();
                whereString = "rowid = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereString += " AND " + selection;
                }
                updateCount = db.update(budgetSplitDBSchema.tagFilter.TABLE_TAG_FILTER, values, whereString, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Invalid Uri for upadate: " + uri);
        }
        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            notifyViews(uri);
        }
        return updateCount;
    }

    /**
     * Determines which SQLite Views will change depending on the changed Table behind the Uri and notifies them.
     *
     * @param uri the Uri of the Table which was changed.
     */
    void notifyViews(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case budgetSplitContract.projects.PROJECTS:
                getContext().getContentResolver().notifyChange(budgetSplitContract.projectsDetailsRO.CONTENT_URI, null);
                break;
            case budgetSplitContract.projects.PROJECT:
                getContext().getContentResolver().notifyChange(budgetSplitContract.projectsDetailsRO.CONTENT_URI, null);
                break;

            case budgetSplitContract.participants.PARTICIPANTS:
                getContext().getContentResolver().notifyChange(budgetSplitContract.projectsDetailsRO.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsDetailsRO.CONTENT_URI, null);
                break;
            case budgetSplitContract.participants.PARTICIPANT:
                getContext().getContentResolver().notifyChange(budgetSplitContract.projectsDetailsRO.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsDetailsRO.CONTENT_URI, null);
                break;

            case budgetSplitContract.projectParticipants.PROJECT_PARTICIPANTS:
                getContext().getContentResolver().notifyChange(budgetSplitContract.projectsDetailsRO.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsDetailsRO.CONTENT_URI, null);
                break;
            case budgetSplitContract.projectParticipants.PROJECT_PARTICIPANT:
                getContext().getContentResolver().notifyChange(budgetSplitContract.projectsDetailsRO.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsDetailsRO.CONTENT_URI, null);
                break;

            case budgetSplitContract.items.ITEMS:
                getContext().getContentResolver().notifyChange(budgetSplitContract.projectsDetailsRO.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsDetailsRO.CONTENT_URI, null);
                break;
            case budgetSplitContract.items.ITEM:
                getContext().getContentResolver().notifyChange(budgetSplitContract.projectsDetailsRO.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsDetailsRO.CONTENT_URI, null);
                break;

            case budgetSplitContract.itemsParticipants.ITEMS_PARTICIPANTS:
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsDetailsRO.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsParticipantsDetailsRO.CONTENT_URI_ALL, null);
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsParticipantsDetailsRO.CONTENT_URI_SINGLE_ITEM, null);
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsParticipantsDetailsRO.CONTENT_URI_SINGLE_PARTICIPANT, null);
                break;
            case budgetSplitContract.itemsParticipants.ITEM_PARTICIPANTS:
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsDetailsRO.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsParticipantsDetailsRO.CONTENT_URI_ALL, null);
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsParticipantsDetailsRO.CONTENT_URI_SINGLE_ITEM, null);
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsParticipantsDetailsRO.CONTENT_URI_SINGLE_PARTICIPANT, null);
                break;

            case budgetSplitContract.itemsTags.ITEMSTAGS:
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsTagsDetailsRO.CONTENT_URI_ALL, null);
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsTagsDetailsRO.CONTENT_URI_SINGLE_ITEM, null);
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsTagsDetailsRO.CONTENT_URI_SINGLE_TAG, null);
                break;
            case budgetSplitContract.itemsTags.ITEMTAGS:
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsParticipantsDetailsRO.CONTENT_URI_ALL, null);
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsTagsDetailsRO.CONTENT_URI_ALL, null);
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsTagsDetailsRO.CONTENT_URI_SINGLE_ITEM, null);
                getContext().getContentResolver().notifyChange(budgetSplitContract.itemsTagsDetailsRO.CONTENT_URI_SINGLE_TAG, null);
                break;
        }
    }
}
