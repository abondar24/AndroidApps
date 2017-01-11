package org.abondar.experimental.sunshine.app.data;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.abondar.experimental.sunshine.FetchWeatherTask;
import org.abondar.experimental.sunshine.data.WeatherContract;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.fail;

/**
 * Created by abondar on 1/10/17.
 */
@RunWith(AndroidJUnit4.class)
public class TestFetchWeatherTask {
    static final String ADD_LOCATION_SETTING = "Sunnydale, CA";
    static final String ADD_LOCATION_CITY = "Sunnydale";
    static final double ADD_LOCATION_LAT = 34.425833;
    static final double ADD_LOCATION_LON = -119.714167;

    private Context appContext = InstrumentationRegistry.getTargetContext();

    @Test
    @TargetApi(21)
    public void testAddLocation() {
        appContext.getContentResolver().delete(WeatherContract.LocationEntry.CONTENT_URI,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{ADD_LOCATION_SETTING});

        FetchWeatherTask fwt = new FetchWeatherTask(appContext, null);
        long locationId = fwt.addLocation(ADD_LOCATION_SETTING, ADD_LOCATION_CITY,
                ADD_LOCATION_LAT, ADD_LOCATION_LON);

        assertFalse("Error: addLocation returned an invalid ID on insert",
                locationId == -1);

        for (int i = 0; i < 2; i++) {

            Cursor locationCursor = appContext.getContentResolver().query(
                    WeatherContract.LocationEntry.CONTENT_URI,
                    new String[]{
                            WeatherContract.LocationEntry._ID,
                            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
                            WeatherContract.LocationEntry.COLUMN_CITY_NAME,
                            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
                            WeatherContract.LocationEntry.COLUMN_COORD_LONG
                    },
                    WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                    new String[]{ADD_LOCATION_SETTING},
                    null);

            if (locationCursor.moveToFirst()) {
                assertEquals("Error: the queried value of locationId does not match the returned value" +
                        "from addLocation", locationCursor.getLong(0), locationId);
                assertEquals("Error: the queried value of location setting is incorrect",
                        locationCursor.getString(1), ADD_LOCATION_SETTING);
                assertEquals("Error: the queried value of location city is incorrect",
                        locationCursor.getString(2), ADD_LOCATION_CITY);
                assertEquals("Error: the queried value of latitude is incorrect",
                        locationCursor.getDouble(3), ADD_LOCATION_LAT);
                assertEquals("Error: the queried value of longitude is incorrect",
                        locationCursor.getDouble(4), ADD_LOCATION_LON);
            } else {
                fail("Error: the id you used to query returned an empty cursor");
            }

            assertFalse("Error: there should be only one record returned from a location query",
                    locationCursor.moveToNext());

            long newLocationId = fwt.addLocation(ADD_LOCATION_SETTING, ADD_LOCATION_CITY,
                    ADD_LOCATION_LAT, ADD_LOCATION_LON);

            assertEquals("Error: inserting a location again should return the same ID",
                    locationId, newLocationId);
        }

        appContext.getContentResolver().delete(WeatherContract.LocationEntry.CONTENT_URI,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{ADD_LOCATION_SETTING});

        appContext.getContentResolver().
                acquireContentProviderClient(WeatherContract.LocationEntry.CONTENT_URI).
                getLocalContentProvider().shutdown();
    }



}
