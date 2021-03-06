package org.abondar.experimental.earthquakereporter;

import android.content.AsyncTaskLoader;
import android.content.Context;


import java.util.List;

/**
 * Created by abondar on 12/20/16.
 */
public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    private String url;


    public EarthquakeLoader(Context context, String url){
        super(context);
        this.url=url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Earthquake> loadInBackground() {
        if (url==null){
        return null;
        }

        List<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(url);
        return earthquakes;
    }


}
