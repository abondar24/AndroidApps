package org.abondar.experimental.sunshine;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.ArrayAdapter;
import org.abondar.experimental.sunshine.data.WeatherContract.WeatherEntry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * Created by abondar on 1/10/17.
 */
public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    private ArrayAdapter<String> adapter;
    private final Context context;


    public FetchWeatherTask(Context context, ArrayAdapter<String> adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    @Override
    protected String[] doInBackground(String... params) {

        if (params.length == 0) {
                       return null;
                    }
                String locationQuery = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJSON;
        String format = "json";
        String units = "metric";
        int numDays = 7;

        try {
            final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
            final String QUERY_PARAM = "q";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";
            final String APPID_PARAM = "APPID";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNITS_PARAM, units)
                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                    .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream is = urlConnection.getInputStream();
            StringBuffer sb = new StringBuffer();
            if (is == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");

            }

            if (sb.length() == 0) {
                return null;
            }
            forecastJSON = sb.toString();

        } catch (IOException e) {
            Log.e("ForecastFragment", "Error", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("ForecastFragment", "Error closing stream", e);
                }
            }
        }


        try {
            return getWeatherDataFromJson(forecastJSON, locationQuery);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String[] result) {
        if (result != null) {
            adapter.clear();
            for (String dayForecast : result) {

                adapter.add(dayForecast);
            }
        }
    }

    private String getReadableDateString(long time) {
        Date date = new Date(time);
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("E, MMM d");
        return shortenedDateFormat.format(date);
    }

    private String formatHighLows(double high, double low) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String unitType = preferences.getString(
                context.getString(R.string.settings_units_key),
                context.getString(R.string.settings_units_metric));

        if (unitType.equals(context.getString(R.string.settings_units_imperial))) {
            high = (high * 1.8) + 32;
            low = (low * 1.8) + 32;
        } else if (!unitType.equals(context.getString(R.string.settings_units_metric))) {
            Log.d(LOG_TAG, "Unit type not found: " + unitType);
        }
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    public long addLocation(String locationSetting, String cityName, double lat, double lon) {
        return -1;
    }

    private String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv){
        String[] resultStrs = new String[cvv.size()];
        for (int i =0; i<cvv.size();i++){
            ContentValues weatherValues = cvv.elementAt(i);
            String highAndLow = formatHighLows(
                    weatherValues.getAsDouble(WeatherEntry.COLUMN_MAX_TEMP),
                    weatherValues.getAsDouble(WeatherEntry.COLUMN_MIN_TEMP));
            resultStrs[i] = getReadableDateString(weatherValues.getAsLong(WeatherEntry.COLUMN_DATE)) +
                    "-" + weatherValues.getAsString(WeatherEntry.COLUMN_SHORT_DESC) + "-" + highAndLow;
        }

        return resultStrs;
    }

    private String[] getWeatherDataFromJson(String forecast,String locationSetting) throws JSONException {

        final String OWM_CITY = "city";
        final String OWM_CITY_NAME = "name";
        final String OWM_COORD = "coord";
        final String OWM_LATITUDE = "lat";
        final String OWM_LONGITUDE = "lon";
        final String OWM_LIST = "list";
        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WINDSPEED = "speed";
        final String OWM_WIND_DIRECTION = "deg";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";
        final String OWM_WEATHER_ID = "id";


        JSONObject forecastJson = new JSONObject(forecast);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);


        JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
        String cityName = cityJson.getString(OWM_CITY_NAME);

        JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
        double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
        double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);

        long locationId = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);

        Vector<ContentValues> cvv = new Vector<ContentValues>(weatherArray.length());

        Time dayTime = new Time();
        dayTime.setToNow();

        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        //utc
        dayTime = new Time();


        for (int i = 0; i < weatherArray.length(); i++) {
            long dateTime;
            double pressure;
            int humidity;
            double windSpeed;
            double windDirection;

            double high;
            double low;

            String description;
            int weatherId;

            JSONObject dayForecast = weatherArray.getJSONObject(i);

            dateTime = dayTime.setJulianDay(julianStartDay + i);
            pressure = dayForecast.getDouble(OWM_PRESSURE);
            humidity = dayForecast.getInt(OWM_HUMIDITY);
            windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
            windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);
            weatherId = weatherObject.getInt(OWM_WEATHER_ID);

            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            high = temperatureObject.getDouble(OWM_MAX);
            low = temperatureObject.getDouble(OWM_MIN);
            ContentValues weatherValues = new ContentValues();

            weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationId);
            weatherValues.put(WeatherEntry.COLUMN_DATE, dateTime);
            weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, humidity);
            weatherValues.put(WeatherEntry.COLUMN_PRESSURE, pressure);
            weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
            weatherValues.put(WeatherEntry.COLUMN_DEGREES, windDirection);
            weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, high);
            weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, low);
            weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, description);
            weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, weatherId);

            cvv.add(weatherValues);
        }

        if ( cvv.size() > 0 ) {

        }

        String sortOrder = WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

            Cursor cursor = context.getContentResolver().query(weatherForLocationUri,
                    null, null, null, sortOrder);

            cvv = new Vector<>(cursor.getCount());
            if ( cursor.moveToFirst() ) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cursor, cv);
                    cvv.add(cv);
                } while (cursor.moveToNext());
            }

        Log.i(LOG_TAG, "FetchWeatherTask Complete. " + cvv.size() + " Inserted");

        String[] resultStrs = convertContentValuesToUXFormat(cvv);

        return resultStrs;
    }
}