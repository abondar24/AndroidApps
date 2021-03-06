package org.abondar.experimental.sunshine.app.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import org.abondar.experimental.sunshine.data.WeatherContract;
import org.abondar.experimental.sunshine.data.WeatherContract.WeatherEntry;
import org.abondar.experimental.sunshine.data.WeatherContract.LocationEntry;
import org.abondar.experimental.sunshine.data.WeatherDbHelper;
import org.abondar.experimental.sunshine.data.WeatherProvider;
import org.junit.*;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by abondar on 1/10/17.
 */
@RunWith(AndroidJUnit4.class)
public class TestProvider {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    private Context appContext = InstrumentationRegistry.getTargetContext();

    private static final int BULK_INSERT_RECORDS_TO_INSERT = 10;

    private static ContentValues[] createBulkInsertWeatherValues(long locationRowId) {
        long currentTestDate = TestUtilities.TEST_DATE;
        long millisecondsInADay = 1000 * 60 * 60 * 24;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, currentTestDate += millisecondsInADay) {
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, currentTestDate);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, 1.1);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 1.2 + 0.01 * (float) i);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, 1.3 - 0.01 * (float) i);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, 75 + i);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, 65 - i);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 5.5 + 0.2 * (float) i);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, 321);
            returnContentValues[i] = weatherValues;
        }
        return returnContentValues;
    }



    @Before
    public  void deleteAllRecordsFromProvider() {
        appContext.getContentResolver().delete(
                WeatherEntry.CONTENT_URI,
                null,
                null
        );
        appContext.getContentResolver().delete(
                LocationEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = appContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = appContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Location table during delete", 0, cursor.getCount());
        cursor.close();
    }

    @Test
    public void testProviderRegistry() {
        PackageManager pm = appContext.getPackageManager();


        ComponentName componentName = new ComponentName(appContext.getPackageName(),
                WeatherProvider.class.getName());
        try {

            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: WeatherProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + WeatherContract.CONTENT_AUTHORITY,
                    providerInfo.authority, WeatherContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: WeatherProvider not registered at " + appContext.getPackageName(),
                    false);
        }
    }

    @Test
    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = appContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the WeatherEntry CONTENT_URI should return WeatherEntry.CONTENT_TYPE",
                WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "94074";
        // content://com.example.android.sunshine.app/weather/94074
        type = appContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocation(testLocation));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the WeatherEntry CONTENT_URI with location should return WeatherEntry.CONTENT_TYPE",
                WeatherEntry.CONTENT_TYPE, type);

        long testDate = 1419120000L; // December 21st, 2014
        // content://com.example.android.sunshine.app/weather/94074/20140612
        type = appContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
        // vnd.android.cursor.item/com.example.android.sunshine.app/weather/1419120000
        assertEquals("Error: the WeatherEntry CONTENT_URI with location and date should return WeatherEntry.CONTENT_ITEM_TYPE",
                WeatherEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = appContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals("Error: the LocationEntry CONTENT_URI should return LocationEntry.CONTENT_TYPE",
                LocationEntry.CONTENT_TYPE, type);
    }

    @Test
    public void testBasicWeatherQuery() {
        WeatherDbHelper dbHelper = new WeatherDbHelper(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        TestUtilities.createNorthPoleLocationValues();

        long locationRowId = TestUtilities.insertNorthPoleLocationValues(appContext);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId);

        long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
        assertTrue("Unable to Insert WeatherEntry into the Database", weatherRowId != -1);

        db.close();

        Cursor weatherCursor = appContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicWeatherQuery", weatherCursor, weatherValues);
    }

    @Test
    public void testBasicLocationQueries() {
        // insert our test records into the database
        WeatherDbHelper dbHelper = new WeatherDbHelper(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
        long locationRowId = TestUtilities.insertNorthPoleLocationValues(appContext);

        // Test the basic content provider query
        Cursor locationCursor = appContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicLocationQueries, location query", locationCursor, testValues);


        if (Build.VERSION.SDK_INT >= 21) {
            assertEquals("Error: Location Query did not properly set NotificationUri",
                    locationCursor.getNotificationUri(), LocationEntry.CONTENT_URI);
        }
    }

    @Test
    public void testUpdateLocation() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createNorthPoleLocationValues();

        Uri locationUri = appContext.getContentResolver().
                insert(LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(LocationEntry._ID, locationRowId);
        updatedValues.put(LocationEntry.COLUMN_CITY_NAME, "Santa's Village");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor locationCursor = appContext.getContentResolver().query(LocationEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = appContext.getContentResolver().update(
                LocationEntry.CONTENT_URI, updatedValues, LocationEntry._ID + "= ?",
                new String[]{Long.toString(locationRowId)});
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();


        Cursor cursor = appContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,   // projection
                LocationEntry._ID + " = " + locationRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateLocation.  Error validating location entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    @Test
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        appContext.getContentResolver().registerContentObserver(LocationEntry.CONTENT_URI, true, tco);
        Uri locationUri = appContext.getContentResolver().insert(LocationEntry.CONTENT_URI, testValues);

        tco.waitForNotificationOrFail();
        appContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(locationUri);

        assertTrue(locationRowId != -1);

        Cursor cursor = appContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating LocationEntry.",
                cursor, testValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        appContext.getContentResolver().registerContentObserver(WeatherEntry.CONTENT_URI, true, tco);

        Uri weatherInsertUri = appContext.getContentResolver()
                .insert(WeatherEntry.CONTENT_URI, weatherValues);
        assertTrue(weatherInsertUri != null);

        tco.waitForNotificationOrFail();
        appContext.getContentResolver().unregisterContentObserver(tco);

        Cursor weatherCursor = appContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating WeatherEntry insert.",
                weatherCursor, weatherValues);

        weatherValues.putAll(testValues);

        weatherCursor = appContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocation(TestUtilities.TEST_LOCATION),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location Data.",
                weatherCursor, weatherValues);

        weatherCursor = appContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithStartDate(
                        TestUtilities.TEST_LOCATION, TestUtilities.TEST_DATE),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location Data with start date.",
                weatherCursor, weatherValues);

        weatherCursor = appContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithDate(TestUtilities.TEST_LOCATION, TestUtilities.TEST_DATE),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location data for a specific date.",
                weatherCursor, weatherValues);
    }

    @Test
    public void testDeleteRecords() {
        testInsertReadProvider();

        TestUtilities.TestContentObserver locationObserver = TestUtilities.getTestContentObserver();
        appContext.getContentResolver().registerContentObserver(LocationEntry.CONTENT_URI, true, locationObserver);

        TestUtilities.TestContentObserver weatherObserver = TestUtilities.getTestContentObserver();
        appContext.getContentResolver().registerContentObserver(WeatherEntry.CONTENT_URI, true, weatherObserver);

        deleteAllRecordsFromProvider();

        locationObserver.waitForNotificationOrFail();
        weatherObserver.waitForNotificationOrFail();

        appContext.getContentResolver().unregisterContentObserver(locationObserver);
        appContext.getContentResolver().unregisterContentObserver(weatherObserver);
    }

    @Test
    public void testBulkInsert() {
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
        Uri locationUri = appContext.getContentResolver().insert(LocationEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        assertTrue(locationRowId != -1);

        Cursor cursor = appContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating LocationEntry.",
                cursor, testValues);

        ContentValues[] bulkInsertContentValues = createBulkInsertWeatherValues(locationRowId);

        TestUtilities.TestContentObserver weatherObserver = TestUtilities.getTestContentObserver();
        appContext.getContentResolver().registerContentObserver(WeatherEntry.CONTENT_URI, true, weatherObserver);

        int insertCount = appContext.getContentResolver().bulkInsert(WeatherEntry.CONTENT_URI, bulkInsertContentValues);

        weatherObserver.waitForNotificationOrFail();
        appContext.getContentResolver().unregisterContentObserver(weatherObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        cursor = appContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                WeatherEntry.COLUMN_DATE + " ASC"  // sort order == by DATE ASCENDING
        );

        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        cursor.moveToFirst();
        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating WeatherEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }


}
