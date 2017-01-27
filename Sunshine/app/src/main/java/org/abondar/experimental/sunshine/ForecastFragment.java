package org.abondar.experimental.sunshine;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import org.abondar.experimental.sunshine.data.WeatherContract;
import org.abondar.experimental.sunshine.sync.SunshineSyncAdapter;


/**
 * Created by abondar on 1/7/17.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String LOG_TAG = ForecastFragment.class.getSimpleName();


    public interface Callback {
        void onItemSelected(Uri dataUri, ForecastAdapter.ForecastAdapterViewHolder viewHolder);
    }

    private static final int FORECAST_LOADER = 0;
    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG};

    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    private ForecastAdapter adapter;
    private static final String SELECTED_KEY = "selected_position";
    private boolean useTodayLayout;
    private RecyclerView recyclerView;
    private int position = RecyclerView.NO_POSITION;
    private boolean autoSelectView;
    private int choiceMode;
    private boolean holdForTransition;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        View emptyView = rootView.findViewById(R.id.recyclerview_forecast_empty);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_forecast);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        adapter = new ForecastAdapter(getActivity(), new ForecastAdapter.ForecastAdapterOnClickHandler() {

            @Override
            public void onClick(Long date, ForecastAdapter.ForecastAdapterViewHolder viewHolder) {
                String locationSetting = Utility.getPreferredLocation(getActivity());
                ((Callback) getActivity()).onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                        locationSetting, date), viewHolder);
                position = viewHolder.getAdapterPosition();
            }
        }, emptyView, choiceMode);
        recyclerView.setAdapter(adapter);


        final View parallaxView = rootView.findViewById(R.id.parallax_bar);
        if (parallaxView != null) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int max = parallaxView.getHeight();
                    if (dy > 0) {
                        parallaxView.setTranslationY(Math.max(-max, parallaxView.getTranslationY() - dy / 2));
                    } else {
                        parallaxView.setTranslationY(Math.min(0, parallaxView.getTranslationY() - dy / 2));
                    }
                }
            });
        }
        final AppBarLayout appbarView = (AppBarLayout) rootView.findViewById(R.id.appbar);
        if (appbarView != null) {
            ViewCompat.setElevation(appbarView, 0);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                     if (recyclerView.computeVerticalScrollOffset() == 0) {
                        appbarView.setElevation(0);
                    } else {
                        appbarView.setElevation(appbarView.getTargetElevation());

                    }
                }
            });
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_KEY)) {
                position = savedInstanceState.getInt(SELECTED_KEY);
            }
            adapter.onRestoreInstanceState(savedInstanceState);
        }

        adapter.setUseTodayLayout(useTodayLayout);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (holdForTransition) {
            getActivity().supportPostponeEnterTransition();
        }
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (position != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY, position);
        }
        adapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String locationSetting = Utility.getPreferredLocation(getActivity());

        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
        if (position != RecyclerView.NO_POSITION) {
            recyclerView.smoothScrollToPosition(position);
        }
        updateEmptyView();

        if (cursor.getCount() == 0) {
            getActivity().supportPostponeEnterTransition();
        } else {
            recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (recyclerView.getChildCount() > 0) {
                        recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        int itemPos = adapter.getSelectedItemPosition();
                        if (RecyclerView.NO_POSITION == itemPos) {
                            itemPos = 0;
                        }
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(itemPos);
                        if (viewHolder != null && autoSelectView) {
                            adapter.selectView(viewHolder);
                        }
                        if (holdForTransition) {
                            getActivity().supportPostponeEnterTransition();
                        }
                        return true;
                    }

                    return false;
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_location_status_key))) {
            updateEmptyView();
        }
    }

    @Override
    public void onResume() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }


    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        TypedArray typedArray = getActivity().obtainStyledAttributes(attrs, R.styleable.ForecastFragment,
                0, 0);
        choiceMode = typedArray.getInt(R.styleable.ForecastFragment_android_choiceMode, AbsListView.CHOICE_MODE_NONE);
        autoSelectView = typedArray.getBoolean(R.styleable.ForecastFragment_autoSelectView, false);
        holdForTransition = typedArray.getBoolean(R.styleable.ForecastFragment_sharedElementTransitions,
                false);
        typedArray.recycle();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recyclerView != null) {
            recyclerView.clearOnScrollListeners();
        }
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        this.useTodayLayout = useTodayLayout;
        if (adapter != null) {
            adapter.setUseTodayLayout(this.useTodayLayout);

        }

    }


    public void onLocationChanged() {
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    private void updateWeather() {
        SunshineSyncAdapter.syncImmediately(getActivity());
    }


    private void openPreferredLocationInMap() {
        if (adapter != null) {
            Cursor c = adapter.getCursor();
            if (c != null) {
                c.moveToPosition(0);
                String posLat = c.getString(COL_COORD_LAT);
                String posLong = c.getString(COL_COORD_LONG);
                Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(geoLocation);

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);

                } else {
                    Log.d(LOG_TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");

                }

            }

        }

    }

    private void updateEmptyView() {
        if (adapter.getItemCount() == 0) {
            TextView textView = (TextView) getView().findViewById(R.id.recyclerview_forecast_empty);
            if (textView != null) {
                int msg = R.string.empty_forecast_list;

                @SunshineSyncAdapter.LocationStatus int location = Utility.getLocationStatus(getActivity());
                switch (location) {
                    case SunshineSyncAdapter.LOCATION_STATUS_SERVER_DOWN:
                        msg = R.string.empty_forecast_list_server_down;
                        break;
                    case SunshineSyncAdapter.LOCATION_STATUS_SERVER_INVALID:
                        msg = R.string.empty_forecast_list_server_error;
                        break;
                    case SunshineSyncAdapter.LOCATION_STATUS_INVALID:
                        msg = R.string.empty_forecast_list_invalid_location;
                        break;
                    default:
                        if (!Utility.isNetworkAvailable(getActivity())) {
                            msg = R.string.empty_forecast_list_no_network;
                        }
                }
                textView.setText(msg);
            }
        }
    }

}
