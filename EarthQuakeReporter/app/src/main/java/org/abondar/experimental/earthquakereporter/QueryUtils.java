package org.abondar.experimental.earthquakereporter;


import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by abondar on 12/18/16.
 */
public final class QueryUtils {

    private static String LOG_TAG = "QueryUtils";

    private QueryUtils() {
    }


    public static List<Earthquake> fetchEarthquakeData(String requestUrl) {

        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Earthquake> earthquakes = extractEarthquakesFromJSON(jsonResponse);

        return earthquakes;
    }

    private static List<Earthquake> extractEarthquakesFromJSON(String earthquakeJSON) {

        List<Earthquake> earthquakes = new ArrayList<>();

        try {

            JSONObject rootObj = new JSONObject(earthquakeJSON);
            JSONArray earthQuakesJSON = rootObj.getJSONArray("features");

            for (int i = 0; i < earthQuakesJSON.length(); i++) {
                JSONObject curEarthquakeObj = earthQuakesJSON.getJSONObject(i);
                JSONObject curProps = curEarthquakeObj.getJSONObject("properties");

                Double magnitute = curProps.getDouble("mag");
                String location = curProps.getString("place");
                String timeInMilliseconds = curProps.getString("time");

                Date date = new Date(Long.valueOf(timeInMilliseconds));
                String url = curProps.getString("url");

                Earthquake curEarthquake = new Earthquake(magnitute, location, date, url);
                earthquakes.add(curEarthquake);

            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }

        return earthquakes;
    }


    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }


    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

}
