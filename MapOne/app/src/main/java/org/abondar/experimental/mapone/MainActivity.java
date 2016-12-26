package org.abondar.experimental.mapone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.*;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private boolean mapReady = false;
    private boolean newPos = false;
    private static final int MOVE_MODE = 0;
    private static final int ANIM_MODE = 1;

    private LatLng moscow = new LatLng(55.7706594, 37.42389249);
    private LatLng sjc18 = new LatLng(37.40716303, -121.9265720);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mapButton = (Button) this.findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapReady) {
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });

        Button satelliteButton = (Button) this.findViewById(R.id.satellite_button);
        satelliteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapReady) {
                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
            }
        });

        Button hybridButton = (Button) this.findViewById(R.id.hybrid_button);
        hybridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapReady) {
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            }
        });

        final Button moveButton = (Button) this.findViewById(R.id.move_button);
        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng target;
                if (!newPos) {
                    moveCam(moscow, ANIM_MODE, "Cisco Krilatskyi Hills 4");
                    moveButton.setText(R.string.back_move);
                    newPos = true;
                } else {
                    target = new LatLng(37.40716303, -121.9265720);
                    moveCam(target, MOVE_MODE, "Cisco Building 18");
                    moveButton.setText(R.string.default_move);
                    newPos = false;

                }

            }
        });


        Button triangleButton = (Button) this.findViewById(R.id.triangle_button);
        triangleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!newPos) {

                    LatLng sjc19 = new LatLng(37.4059630, -121.9273580121);
                    LatLng sjc17 = new LatLng(37.4083880, -121.9273600);

                    map.addPolyline(new PolylineOptions().geodesic(true)
                            .add(sjc18)
                            .add(sjc17)
                            .add(sjc19)
                            .add(sjc18));

                } else {
                    map.addCircle(new CircleOptions()
                            .center(moscow)
                            .radius(150)
                            .strokeColor(Color.BLACK)
                            .fillColor(Color.argb(64, 0, 255, 0)));
                }
            }
        });


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.street_view:
                Intent intent = new Intent(MainActivity.this, StreetViewActivity.class);
                startActivity(intent);
                return true;
            case R.id.location:
                Intent intent1 = new Intent(MainActivity.this, LocationActivity.class);
                startActivity(intent1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        map = googleMap;
        moveCam(sjc18, 0, "Cisco Building 18");
    }

    private void moveCam(LatLng target, int mode, String markerName) {
        CameraPosition position = CameraPosition.builder().target(target)
                .zoom(17)
                .tilt(75)
                .build();

        MarkerOptions marker = new MarkerOptions().position(target).title(markerName);
        map.addMarker(marker);

        switch (mode) {
            case MOVE_MODE:
                map.moveCamera(CameraUpdateFactory.newCameraPosition(position));
                break;
            case ANIM_MODE:
                map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000, null);
                break;
        }

    }

}
