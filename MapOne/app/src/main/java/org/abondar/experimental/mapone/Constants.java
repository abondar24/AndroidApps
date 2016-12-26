package org.abondar.experimental.mapone;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by abondar on 12/24/16.
 */
public final class Constants {
    private Constants(){}

    public static final String PACKAGE_NAME = "org.abondar.experimental.mapone";
    public static final String BROADCAST_ACTION = PACKAGE_NAME + ".BROADCAST_ACTION";
    public static final String ACTIVITY_EXTRA = PACKAGE_NAME + ".ACTIVITY_EXTRA";
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 0;
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 1; // 1 mile, 1.6 km
    public static final HashMap<String,LatLng> LANDMARKS = new HashMap<>();
    static {
        LANDMARKS.put("sjc18",new LatLng(37.40716303, -121.9265720));
        LANDMARKS.put("sjc19", new LatLng(37.4059630, -121.9273580121));
        LANDMARKS.put("sjc18ss", new LatLng(37.4072372, -121.9269113));

    }
}
