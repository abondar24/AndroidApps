package org.abondar.experimental.mapone;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;

public class LocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<Status> {

    private final String TAG = "Location";
    private TextView lastLatTextView;
    private TextView lastLonTextView;
    private TextView currentLatTextView;
    private TextView currentLonTextView;
    private TextView statusTextView;
    private Button requestUpdatesButton;
    private Button removeUpdatesButton;
    private Button geofenceButton;

    private ArrayList<Geofence> geofenceList;
    private ActivityDetectionBroadcastReceiver broadcastReceiver;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        broadcastReceiver = new ActivityDetectionBroadcastReceiver();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();

        geofenceList = new ArrayList<>();
        populateGeofenceList();

        lastLatTextView = (TextView) this.findViewById(R.id.last_latitude_text);
        lastLonTextView = (TextView) this.findViewById(R.id.last_longitude_text);
        currentLatTextView = (TextView) this.findViewById(R.id.current_latitude_text);
        currentLonTextView = (TextView) this.findViewById(R.id.current_longitude_text);

        statusTextView = (TextView) this.findViewById(R.id.detectedActivities);
        requestUpdatesButton = (Button) findViewById(R.id.request_activity_updates_button);

        requestUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!googleApiClient.isConnected()) {
                    Toast.makeText(LocationActivity.this, getString(R.string.not_connected),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                        googleApiClient,
                        Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                        getActivityDetectionPendingIntent()).setResultCallback(LocationActivity.this);
                requestUpdatesButton.setEnabled(false);
                removeUpdatesButton.setEnabled(true);
            }
        });


        removeUpdatesButton = (Button) this.findViewById(R.id.remove_activity_updates_button);
        removeUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!googleApiClient.isConnected()) {
                    Toast.makeText(LocationActivity.this, getString(R.string.not_connected),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                        googleApiClient, getActivityDetectionPendingIntent()).setResultCallback(LocationActivity.this);
                requestUpdatesButton.setEnabled(true);
                removeUpdatesButton.setEnabled(false);
            }
        });

        geofenceButton = (Button) this.findViewById(R.id.geofence_button);
        geofenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!googleApiClient.isConnected()) {
                    Toast.makeText(LocationActivity.this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ActivityCompat.checkSelfPermission(LocationActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                LocationActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 108);

                }
                LocationServices.GeofencingApi.addGeofences(
                        googleApiClient,
                        getGeofencingRequest(),
                        getGeofencePendingIntent()
                ).setResultCallback(LocationActivity.this);



            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_ACTION));
        super.onResume();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 108);

        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        lastLatTextView.setText(String.valueOf(lastLocation.getLatitude()));
        lastLonTextView.setText(String.valueOf(lastLocation.getLongitude()));
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleAPIConnection has been suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleAPIConnection has failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, location.toString());
        currentLatTextView.setText(String.valueOf(location.getLatitude()));
        currentLonTextView.setText(String.valueOf(location.getLongitude()));
    }


    public String getActivityString(int detectedType) {
        Resources resources = this.getResources();

        switch (detectedType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity);


        }
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess() ) {
            Log.e(TAG, "Successfully added activity detection.");
            Toast.makeText(this,"Geofences Added",Toast.LENGTH_SHORT).show();

        } else {
            Log.e(TAG, "Error adding or removing activity detection: " + status.getStatusMessage());

            String errorMsg = GeofenceErrorMessages.getErrorString(this,status.getStatusCode());
            Log.e(TAG,errorMsg);
        }
    }


    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivites =
                    intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);

            String strStatus = "";
            for (DetectedActivity thisActivity : updatedActivites) {
                strStatus += getActivityString(thisActivity.getType()) + thisActivity.getConfidence() + "%\n";
            }
            statusTextView.setText(strStatus);
        }
    }

    public PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivityIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.LANDMARKS.entrySet()) {
            geofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS)
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());

        }
    }

    public PendingIntent getGeofencePendingIntent(){
        Intent intent = new Intent(this,GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);

        return builder.build();
    }


}
