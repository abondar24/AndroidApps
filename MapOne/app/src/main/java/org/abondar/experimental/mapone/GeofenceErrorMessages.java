package org.abondar.experimental.mapone;

import android.content.Context;
import android.content.res.Resources;
import com.google.android.gms.location.GeofenceStatusCodes;

/**
 * Created by abondar on 12/25/16.
 */
public class GeofenceErrorMessages {

    private GeofenceErrorMessages(){}

    public static String getErrorString(Context context, int errorCode) {
        Resources resources = context.getResources();
        switch (errorCode){
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence service is not available now";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Your app hsd registered too many geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "You have provided too many PendingRequests to the addGeo...";
            default:
                return "Unknown error: the Geofence service is not available now";
        }
    }
}
