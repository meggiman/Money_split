package ch.ethz.itet.pps.budgetSplit;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.itet.pps.budgetSplit.contentProvider.budgetSplitContract;


public class add_new_item extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    static final String LOG_TAG = "addNewItemActivity";

    public static final String EXTRA_ITEM_URI = "itemUri";
    public static final String EXTRA_PROJECT_URI = "projectUri";

    //Define integer constants to identify the individual loaders.
    private static final int LOADER_ITEM = 0;
    private boolean loaderItemFinishedInitialLoading = false;
    private static final int LOADER_PARTICIPANTS = 1;
    private boolean loaderParticipantsFinishedInitialLoading = false;
    private static final int LOADER_ITEM_PARTICIPANTS = 2;
    private boolean loaderItemParticipantsFinishedInitialLoading = false;
    private static final int LOADER_ITEM_TAGS = 3;
    private boolean loaderItemTagsFinishedInitialLoading = false;
    private static final int LOADER_TAGS = 4;
    private boolean loaderTagsFinishedInitialLoading = false;
    private static final int LOADER_CURRENCIES = 5;
    private boolean loaderCurrenciesFinishedInitialLoading = false;
    private static final int LOADER_PROJECT = 6;
    private boolean loaderProjectFinishedInitialLoading = false;

    boolean isNewItem = true;
    Uri itemUri;
    Uri projectUri;
    long itemId;
    long projectId;
    Cursor cursorItem = null;
    Cursor cursorItemParticipants = null;
    Cursor cursorItemTags = null;
    Cursor cursorTags = null;
    Cursor cursorProject = null;
    Cursor cursorCurrencies = null;

    //View variables
    LinearLayout tagsLayout;
    ListView participantsList;
    List<TextView> tagViews;
    List<Long> tempTags; //Temporary List of tag IDs which will eventually be written to DB.
    EditText itemName;
    Spinner CreatorSpinner;

    //Variables used to restrict full privileges for non-Creating users.
    int localUserId = 0;
    boolean fullAccess = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);
        //Try to get ItemUri.
        itemUri = getIntent().getParcelableExtra(EXTRA_ITEM_URI);
        projectUri = getIntent().getParcelableExtra(EXTRA_PROJECT_URI);

        //Check if a valid Project Uri was added to intent.
        if (projectUri != null) {
            if (!projectUri.getAuthority().equals(budgetSplitContract.AUTHORITY)) {
                throw new IllegalArgumentException("The Project uri added to intent didn't match the budgetSplit Authority.");
            }
            if (!itemUri.getPathSegments().get(0).equals(budgetSplitContract.items.CONTENT_URI.getLastPathSegment())) {
                throw new IllegalArgumentException("The Project Uri added to intent didn't match the project table.");
            }
            try {
                projectId = ContentUris.parseId(projectUri);
            } catch (UnsupportedOperationException e) {
                throw new IllegalArgumentException("The project Uri added to intent wasn't a single row URI.", e);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("The project Uri added to intent wasn't a single row URI.", e);
            }
        } else {
            throw new IllegalArgumentException("There was no current Project Uri added to intent.");
        }

        //Check if a valid Item-Content-Uri was added to intent. If a valid item-URI is present, the activity will load the item into edit-mode.
        if (itemUri != null) {
            if (!itemUri.getAuthority().equals(budgetSplitContract.AUTHORITY)) {
                throw new IllegalArgumentException("The Item Uri added to intent didn't match the budgetSplit Authority.");
            }
            if (!itemUri.getPathSegments().get(0).equals(budgetSplitContract.items.CONTENT_URI.getLastPathSegment())) {
                throw new IllegalArgumentException("The Uri added to intent didn't match the item table.");
            }
            try {
                itemId = ContentUris.parseId(itemUri);
            } catch (UnsupportedOperationException e) {
                throw new IllegalArgumentException("The item Uri added to intent wasn't a single row URI.", e);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("The item Uri added to intent wasn't a single row URI.", e);
            }
            isNewItem = false;
            getLoaderManager().initLoader(LOADER_ITEM, null, this);
            getLoaderManager().initLoader(LOADER_ITEM_PARTICIPANTS, null, this);
            getLoaderManager().initLoader(LOADER_ITEM_TAGS, null, this);
        } else {
            isNewItem = true;
        }
        //Start Loader for all the necessary Data for the GUI.
        getLoaderManager().initLoader(LOADER_PROJECT, null, this);
        getLoaderManager().initLoader(LOADER_PARTICIPANTS, null, this);
        getLoaderManager().initLoader(LOADER_TAGS, null, this);
        getLoaderManager().initLoader(LOADER_CURRENCIES, null, this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_new_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case LOADER_ITEM:
                return new CursorLoader(getApplicationContext(), itemUri, budgetSplitContract.items.PROJECTION_ALL, null, null, null);
            case LOADER_ITEM_TAGS:
                return new CursorLoader(getApplicationContext(), ContentUris.withAppendedId(budgetSplitContract.itemsTagsDetailsRO.CONTENT_URI_SINGLE_ITEM, itemId), budgetSplitContract.itemsTagsDetailsRO.PROJECTION_ALL, null, null, null);
            case LOADER_ITEM_PARTICIPANTS:
                return new CursorLoader(getApplicationContext(), ContentUris.withAppendedId(budgetSplitContract.itemsParticipantsDetailsRO.CONTENT_URI_SINGLE_ITEM, itemId), budgetSplitContract.itemsParticipantsDetailsRO.PROJECTION_ALL, null, null, null);
            case LOADER_TAGS:
                return new CursorLoader(getApplicationContext(), budgetSplitContract.tags.CONTENT_URI, budgetSplitContract.tags.PROJECTION_ALL, null, null, null);
            case LOADER_CURRENCIES:
                return new CursorLoader(getApplicationContext(), budgetSplitContract.currencies.CONTENT_URI, budgetSplitContract.currencies.PROJECTION_ALL, null, null, null);
            case LOADER_PROJECT:
                String[] projection = {budgetSplitContract.projectsDetailsRO._ID,
                        budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_NAME,
                        budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_ADMIN_ID,
                        budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_ADMIN_NAME,
                        budgetSplitContract.projectsDetailsRO.COLUMN_PROJECT_ADMIN_UNIQUEID};
                return new CursorLoader(getApplicationContext(), budgetSplitContract.projectsDetailsRO.CONTENT_URI, projection, null, null, null);
            default:
                throw new IllegalArgumentException("The LoaderId i didn't match with any of the defined Loaders.");
        }
    }

    @Override
    synchronized public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        switch (cursorLoader.getId()) {
            case LOADER_ITEM:
                //call drawGUI only if Load finished for the first time. For future calls, use redrawGUI.
                if (loaderCurrenciesFinishedInitialLoading && loaderItemParticipantsFinishedInitialLoading
                        && loaderItemTagsFinishedInitialLoading && loaderProjectFinishedInitialLoading && loaderTagsFinishedInitialLoading) {
                    if (loaderItemFinishedInitialLoading) {
                        refreshItem(cursor);
                        cursorItem = cursor;
                    } else {
                        cursorItem = cursor;
                        drawGUIForExistingItem();
                    }
                }
                loaderItemFinishedInitialLoading = true;
                break;


            //If activity was started with a new item, drawGUIForNewItem() is called, if all necessary loaders finished their work. If activity was started with an existing
            //item, drawGUIForExistingItem() is called if all all necessary loaders finished their work. This Loader is destroyed after initial Load. Future changes in the Content Providers
            // project Table don't take effect.
            case LOADER_PROJECT:
                cursorProject = cursor;
                if (isNewItem) {
                    if (loaderCurrenciesFinishedInitialLoading && loaderTagsFinishedInitialLoading) {
                        drawGUIForNewItem();
                    }
                }
                if (loaderItemFinishedInitialLoading && loaderCurrenciesFinishedInitialLoading && loaderItemParticipantsFinishedInitialLoading
                        && loaderItemTagsFinishedInitialLoading && loaderTagsFinishedInitialLoading) {
                    drawGUIForExistingItem();
                }
                loaderProjectFinishedInitialLoading = true;
                getLoaderManager().destroyLoader(LOADER_PROJECT);
                break;


            //If activity was started with a new item, drawGUIForNewItem() is called, if all necessary loaders finished their work. If activity was started with an existing
            //item, drawGUIForExistingItem() of redrawGUIForExistingItem() is called if all all necessary loaders finished their work.
            case LOADER_TAGS:
                //New Item
                if (isNewItem) {
                    if (loaderCurrenciesFinishedInitialLoading && loaderProjectFinishedInitialLoading) {
                        if (loaderTagsFinishedInitialLoading) {
                            refreshTags(cursor);
                            cursorTags = cursor;
                        } else {
                            cursorTags = cursor;
                            drawGUIForNewItem();
                        }
                    }
                }
                //Existing Item
                if (loaderItemFinishedInitialLoading && loaderCurrenciesFinishedInitialLoading && loaderItemParticipantsFinishedInitialLoading
                        && loaderItemTagsFinishedInitialLoading && loaderProjectFinishedInitialLoading) {
                    if (loaderTagsFinishedInitialLoading) {
                        refreshTags(cursor);
                        cursorTags = cursor;
                    } else {
                        cursorTags = cursor;
                        drawGUIForExistingItem();
                    }
                }
                loaderTagsFinishedInitialLoading = true;
                break;


            //If activity was started with a new item, drawGUIForNewItem() is called, if all necessary loaders finished their work. If activity was started with an existing
            //item, drawGUIForExistingItem() of redrawGUIForExistingItem() is called if all all necessary loaders finished their work.
            case LOADER_CURRENCIES:
                //New Item
                if (isNewItem) {
                    if (loaderTagsFinishedInitialLoading && loaderProjectFinishedInitialLoading) {
                        if (loaderCurrenciesFinishedInitialLoading) {
                            refreshCurrencies(cursor);
                            cursorCurrencies = cursor;
                        } else {
                            cursorCurrencies = cursor;
                            drawGUIForNewItem();
                        }
                    }
                }
                //Existing Item
                if (loaderItemFinishedInitialLoading && loaderTagsFinishedInitialLoading && loaderItemParticipantsFinishedInitialLoading
                        && loaderItemTagsFinishedInitialLoading && loaderProjectFinishedInitialLoading) {
                    if (loaderCurrenciesFinishedInitialLoading) {
                        refreshCurrencies(cursor);
                        cursorTags = cursor;
                    } else {
                        cursorCurrencies = cursor;
                        drawGUIForExistingItem();
                    }
                }
                loaderCurrenciesFinishedInitialLoading = true;
                break;


            //This case is only reached, if activity was started for an existing item. Determines, whether to wait for other Loaders or to call the drawGUIForExistingItem() or refresh-Method.
            case LOADER_ITEM_PARTICIPANTS:
                if (loaderItemFinishedInitialLoading && loaderCurrenciesFinishedInitialLoading && loaderTagsFinishedInitialLoading
                        && loaderItemTagsFinishedInitialLoading && loaderProjectFinishedInitialLoading) {
                    if (loaderItemParticipantsFinishedInitialLoading) {
                        refreshItemsParticipants(cursor);
                        cursorItemParticipants = cursor;
                    } else {
                        cursorItemParticipants = cursor;
                        drawGUIForExistingItem();
                    }
                }
                loaderItemParticipantsFinishedInitialLoading = true;
                break;


            //This case is only reached, if activity was started for an existing item. Determines, whether to wait for other Loaders or to call the drawGUIForExistingItem() or refresh-Method.
            case LOADER_ITEM_TAGS:
                if (loaderItemFinishedInitialLoading && loaderCurrenciesFinishedInitialLoading && loaderItemParticipantsFinishedInitialLoading
                        && loaderTagsFinishedInitialLoading && loaderProjectFinishedInitialLoading) {
                    if (loaderItemTagsFinishedInitialLoading) {
                        refreshItemsTags(cursor);
                        cursorItemTags = cursor;
                    } else {
                        cursorItemTags = cursor;
                        drawGUIForExistingItem();
                    }
                }
                loaderItemTagsFinishedInitialLoading = true;
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        switch (cursorLoader.getId()) {
            case LOADER_ITEM:
        }
    }

    void redrawGUIForExistingItem(int loaderID, Cursor cursor) {

    }

    void refreshItem(Cursor newItemCursor) {

    }

    void refreshTags(Cursor newTagsCursor) {

    }

    void refreshCurrencies(Cursor newCurrenciesCursor) {

    }

    void refreshItemsParticipants(Cursor newItemsParticipantsCursor) {

    }

    void refreshItemsTags(Cursor newItemsTagsCursor) {

    }

    void drawGUIForExistingItem() {

    }

    void drawGUIForNewItem() {

    }

    /**
     * Class Data Type for use in ArrayAdapters.
     */
    static class Tag {
        long id;
        String name;

        Tag(long id, String name) {
            this.id = id;
            this.name = name;
        }

        /**
         * Converts a Cursor of tags to a {@link java.util.List} of Tags.
         * The Cursor must at least Contain the Columns {@value budgetSplitContract.tags#_ID}  and {@value budgetSplitContract.tags#COLUMN_NAME}. If the more specific Column identifiers
         * {@value budgetSplitContract.itemsTagsDetailsRO#COLUMN_TAG_ID} and {@value budgetSplitContract.itemsTagsDetailsRO#COLUMN_TAG_NAME} they are interpreted as the Values for the Objects.
         *
         * @param cursor A Cursor containing tags to convert into List of Tags.
         * @return A {@link java.util.List} of Tags containing all Tags in cursor. If the cursors row count was zero, an empty List is returned.
         * @throws java.lang.IllegalArgumentException if the Cursor does not contain any rows with the Column names mentioned above.
         */
        static List<Tag> cursorToList(Cursor cursor) {
            if (cursor == null) {
                throw new NullPointerException("The given Cursor cursor was null.");
            }
            int idIndex = cursor.getColumnIndex(budgetSplitContract.itemsTagsDetailsRO.COLUMN_TAG_ID);
            if (idIndex == -1) {
                idIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
            }
            if (cursor.getType(idIndex) != Cursor.FIELD_TYPE_INTEGER) {
                throw new IllegalArgumentException("The Cursors Tag-Id Column Data-Type didn't contain numbers.");
            }
            int nameIndex = cursor.getColumnIndex(budgetSplitContract.itemsTagsDetailsRO.COLUMN_TAG_NAME);
            if (nameIndex == -1) {
                nameIndex = cursor.getColumnIndexOrThrow(budgetSplitContract.tags.COLUMN_NAME);
            }
            if (cursor.getType(nameIndex) != Cursor.FIELD_TYPE_STRING) {
                throw new IllegalArgumentException("The Cursors Tag-Name Column Data-Type didn't contain strings.");
            }
            ArrayList<Tag> list = new ArrayList<Tag>();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    list.add(new Tag(cursor.getLong(idIndex), cursor.getString(nameIndex)));
                    cursor.moveToNext();
                }
            }
            return list;
        }
    }

    static class Payer {
        long itemId;
        long payerId;
        String payerName;
        boolean isVirtual;
        int uniqueId;
        long currencyId;
        String currencyCode;
        float exchangeRate;
        double amountPayed;

        public Payer(long payerId, String payerName, boolean isVirtual, int uniqueId, long currencyId, String currencyCode, float exchangeRate, double amountPayed) {
            this.payerId = payerId;
            this.payerName = payerName;
            this.isVirtual = isVirtual;
            this.uniqueId = uniqueId;
            this.currencyId = currencyId;
            this.currencyCode = currencyCode;
            this.exchangeRate = exchangeRate;
            this.amountPayed = amountPayed;
        }

        /**
         * Converts a Cursor of tags to a {@link java.util.List} of Tags.
         * The Cursor must at least contain the Columns {@value budgetSplitContract.itemsParticipants#COLUMN_PARTICIPANTS_ID}, {@value budgetSplitContract.itemsParticipantsDetailsRO#COLUMN_PARTICIPANT_NAME},
         * {@value budgetSplitContract.itemsParticipantsDetailsRO#COLUMN_PARTICIPANT_IS_VIRTUAL} and {@value budgetSplitContract.itemsParticipantsDetailsRO#COLUMN_PARTICIPANT_UNIQUE_ID}.
         *
         * @param cursor A Cursor containing tags to convert into List of Tags.
         * @return A {@link java.util.List} of Tags containing all Tags in cursor. If the cursors row count was zero, an empty List is returned.
         * @throws java.lang.IllegalArgumentException if the Cursor does not contain any rows with the Column names mentioned above.
         */
        static List<Payer> cursorToList(Cursor cursor) {
            if (cursor == null) {
                throw new NullPointerException("The given Cursor cursor was null.");
            }
            int payerIdIndex = cursor.getColumnIndexOrThrow(budgetSplitContract.itemsParticipantsDetailsRO.COLUMN_PARTICIPANT_ID);
            int payerNameIndex = cursor.getColumnIndexOrThrow(budgetSplitContract.itemsParticipantsDetailsRO.COLUMN_PARTICIPANT_NAME);
            int isVirtualIndex = cursor.getColumnIndexOrThrow(budgetSplitContract.itemsParticipantsDetailsRO.COLUMN_PARTICIPANT_IS_VIRTUAL);
            int uniqueIDIndex = cursor.getColumnIndexOrThrow(budgetSplitContract.itemsParticipantsDetailsRO.COLUMN_PARTICIPANT_UNIQUE_ID);
            int currencyIdIndex = cursor.getColumnIndexOrThrow(budgetSplitContract.itemsParticipantsDetailsRO.COLUMN_CURRENCY_ID);
            int currencyCodeIndex = cursor.getColumnIndexOrThrow(budgetSplitContract.itemsParticipantsDetailsRO.COLUMN_CURRENCY_CODE);
            int exchangeRateIndex = cursor.getColumnIndexOrThrow(budgetSplitContract.itemsParticipantsDetailsRO.COLUMN_AMOUNT_PAYED);
            ArrayList<Payer> list = new ArrayList<Payer>();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    list.add(new Payer(cursor.getLong(payerIdIndex), cursor.getString(payerNameIndex), cursor.getInt(isVirtualIndex) == 1, cursor.getInt(uniqueIDIndex), cursor.getLong(currencyIdIndex), cursor.getString(currencyCodeIndex), cursor.getFloat(exchangeRateIndex), cursor.getDouble(payerNameIndex)));
                    cursor.moveToNext();
                }
            }
            return list;
        }
    }
}
