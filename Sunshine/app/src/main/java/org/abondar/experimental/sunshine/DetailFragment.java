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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import org.abondar.experimental.sunshine.data.WeatherContract;

/**
 * Created by abondar on 1/8/17.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    public static final String DETAIL_TRANSITION_ANIMATION = "DTA";


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
    private TextView dateView;
    private TextView descriptionView;
    private TextView highTempView;
    private TextView lowTempView;
    private TextView humidityView;
    private TextView humidityLabelView;
    private TextView windView;
    private TextView windLabelView;
    private TextView pressureView;
    private TextView pressureLabelView;

    private Uri uri;

    private boolean transtiotionAnimation;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            uri = args.getParcelable(DetailFragment.DETAIL_URI);
            transtiotionAnimation = args.getBoolean(DetailFragment.DETAIL_TRANSITION_ANIMATION,false);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail_start, container, false);
        iconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        dateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        descriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        highTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        lowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        humidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        humidityLabelView = (TextView) rootView.findViewById(R.id.detail_humidity_label_textview);
        windView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        windLabelView = (TextView) rootView.findViewById(R.id.detail_wind_label_textview);
        pressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        pressureLabelView = (TextView) rootView.findViewById(R.id.detail_pressure_label_textview);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getActivity() instanceof DetailActivity) {
            inflater.inflate(R.menu.detailfragment, menu);
            finishCreatingMenu(menu);
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

        }

        ViewParent vp = getView().getParent();
        if (vp instanceof CardView) {
            ((View) vp).setVisibility(View.INVISIBLE);
        }
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            ViewParent vp = getView().getParent();
            if (vp instanceof CardView) {
                ((View) vp).setVisibility(View.VISIBLE);
            }
            int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);
            iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

            long date = data.getLong(COL_WEATHER_DATE);
            String dateText = Utility.getFullFriendlyDayString(getActivity(), date);

            dateView.setText(dateText);

            String description = data.getString(COL_WEATHER_DESC);
            descriptionView.setText(description);
            descriptionView.setContentDescription(getString(R.string.a11y_forecast, description));

            iconView.setContentDescription(description);
            iconView.setContentDescription(getString(R.string.a11y_forecast_icon, description));

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
            humidityView.setContentDescription(getString(R.string.a11y_humidity, humidityView.getText()));
            humidityLabelView.setContentDescription(humidityView.getContentDescription());

            float windSpeedStr = data.getFloat(COL_WEATHER_WIND_SPEED);
            float windDirStr = data.getFloat(COL_WEATHER_DEGREES);
            windView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));
            windView.setContentDescription(getString(R.string.a11y_wind, windView.getText()));
            windLabelView.setContentDescription(windView.getContentDescription());

            float pressure = data.getFloat(COL_WEATHER_PRESSURE);
            pressureView.setText(getActivity().getString(R.string.format_pressure, pressure));
            pressureView.setText(getString(R.string.format_pressure, pressure));
            pressureView.setContentDescription(getString(R.string.a11y_pressure, pressureView.getText()));
            pressureLabelView.setContentDescription(pressureView.getContentDescription());

            forecast = String.format("%s - %s : %s/%s", dateText, description, high, low);

        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbarView = (Toolbar) getView().findViewById(R.id.toolbar);
        if (transtiotionAnimation) {
            activity.supportStartPostponedEnterTransition();

            if (null != toolbarView) {
                activity.setSupportActionBar(toolbarView);
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

        } else {

        }
        if (null != toolbarView) {
            Menu menu = toolbarView.getMenu();
            if (null != menu) menu.clear();
            toolbarView.inflateMenu(R.menu.detailfragment);
            finishCreatingMenu(toolbarView.getMenu());
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

    private void finishCreatingMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
    }


}
