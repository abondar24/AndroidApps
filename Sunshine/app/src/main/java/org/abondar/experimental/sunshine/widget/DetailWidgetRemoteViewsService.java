package org.abondar.experimental.sunshine.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import org.abondar.experimental.sunshine.R;
import org.abondar.experimental.sunshine.Utility;
import org.abondar.experimental.sunshine.data.WeatherContract;

/**
 * Created by abondar on 1/31/17.
 */
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();
    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
    };

    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_WEATHER_DATE = 1;
    private static final int INDEX_WEATHER_CONDITION_ID = 2;
    private static final int INDEX_WEATHER_DESC = 3;
    private static final int INDEX_WEATHER_MAX_TEMP = 4;
    private static final int INDEX_WEATHER_MIN_TEMP = 5;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                String location = Utility.getPreferredLocation(DetailWidgetRemoteViewsService.this);
                Uri weatherForLocationUri = WeatherContract.WeatherEntry
                        .buildWeatherLocationWithStartDate(location, System.currentTimeMillis());
                data = getContentResolver().query(weatherForLocationUri,
                        FORECAST_COLUMNS,
                        null,
                        null,
                        WeatherContract.WeatherEntry.COLUMN_DATE + " ASC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;

                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);
                int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
                int weatherArtResourceId = Utility.getIconResourceForWeatherCondition(weatherId);
                String description = data.getString(INDEX_WEATHER_DESC);
                long dateInMillis = data.getLong(INDEX_WEATHER_DATE);
                String formattedDate = Utility.getFriendlyDayString(
                        DetailWidgetRemoteViewsService.this, dateInMillis, false);
                double maxTemp = data.getDouble(INDEX_WEATHER_MAX_TEMP);
                double minTemp = data.getDouble(INDEX_WEATHER_MIN_TEMP);
                String formattedMaxTemperature =
                        Utility.formatTemperature(DetailWidgetRemoteViewsService.this, maxTemp);
                String formattedMinTemperature =
                        Utility.formatTemperature(DetailWidgetRemoteViewsService.this, minTemp);

                views.setImageViewResource(R.id.widget_icon, weatherArtResourceId);

                setRemoteContentDescription(views, description);

                views.setTextViewText(R.id.widget_date, formattedDate);
                views.setTextViewText(R.id.widget_description, description);
                views.setTextViewText(R.id.widget_high_temperature, formattedMaxTemperature);
                views.setTextViewText(R.id.widget_low_temperature, formattedMinTemperature);

                final Intent fillInIntent = new Intent();
                String locationSetting = Utility.getPreferredLocation(DetailWidgetRemoteViewsService.this);
                Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                        locationSetting, dateInMillis);
                fillInIntent.setData(weatherUri);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }


            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_icon, description);

            }


            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);

            }

            @Override
            public int getViewTypeCount() {
                return 1;

            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_WEATHER_ID);
                return position;

            }

            @Override
            public boolean hasStableIds() {
                return true;

            }
        };
    }
}
