package org.abondar.experimental.sunshine;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import org.abondar.experimental.sunshine.data.WeatherContract;

/**
 * Created by abondar on 1/8/17.
 */
public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailsFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private ShareActionProvider shareActionProvider;


    private static final int DETAIL_LOADER = 0;
    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;

    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;
    public static final String DETAIL_URI = "URI";

    private String forecast;

    private ImageView iconView;
    private TextView friendlyDateView;
    private TextView dateView;
    private TextView descriptionView;
    private TextView highTempView;
    private TextView lowTempView;
    private TextView humidityView;
    private TextView windView;
    private TextView pressureView;

    private Uri uri;

    public DetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            uri = args.getParcelable(DetailsFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        iconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        dateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        friendlyDateView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        descriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        highTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        lowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        humidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        windView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        pressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);

        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (forecast != null) {
            shareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.e(LOG_TAG, "Share Action Provider is null");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (uri != null) {
            return new CursorLoader(
                    getActivity(),
                    uri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );

        } else {
            return null;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);
            iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

            long date = data.getLong(COL_WEATHER_DATE);
            String friendlyDateText = Utility.getDayName(getActivity(), date);
            String dateText = Utility.getFormattedMonthDay(getActivity(), date);
            friendlyDateView.setText(friendlyDateText);
            dateView.setText(dateText);

            String description = Utility.getStringForWeatherCondition(getActivity(), weatherId);
            descriptionView.setText(description);
            descriptionView.setContentDescription(getString(R.string.a11y_forecast,description));

            iconView.setContentDescription(description);
            iconView.setContentDescription(getString(R.string.a11y_forecast_icon,description));

            boolean isMetric = Utility.isMetric(getActivity());

            double high = data.getDouble(COL_WEATHER_MAX_TEMP);
            String highString = Utility.formatTemperature(getActivity(), high);
            highTempView.setText(highString);
            highTempView.setContentDescription(getString(R.string.a11y_high_temp, highString));

            double low = data.getDouble(COL_WEATHER_MIN_TEMP);
            String lowString = Utility.formatTemperature(getActivity(), low);
            lowTempView.setText(lowString);
            lowTempView.setContentDescription(getString(R.string.a11y_low_temp, lowString));

            float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
            humidityView.setText(getActivity().getString(R.string.format_humidity, humidity));
            humidityView.setContentDescription(humidityView.getText());

            float windSpeedStr = data.getFloat(COL_WEATHER_WIND_SPEED);
            float windDirStr = data.getFloat(COL_WEATHER_DEGREES);
            windView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));
            windView.setContentDescription(windView.getText());

            float pressure = data.getFloat(COL_WEATHER_PRESSURE);
            pressureView.setText(getActivity().getString(R.string.format_pressure, pressure));
            pressureView.setContentDescription(pressureView.getText());

            forecast = String.format("%s - %s : %s/%s", dateText, description, high, low);

            if (shareActionProvider != null) {
                shareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onLocationChanged(String newLocation) {
        Uri uri = this.uri;
        if (uri != null) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updateUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            this.uri = updateUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, forecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }



}
