package org.abondar.experimental.mapone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    boolean mapReady = false;

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

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        map = googleMap;
        LatLng latLng = new LatLng(37.40716303, -121.9265720);
        CameraPosition position = CameraPosition.builder().target(latLng).zoom(27).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(position));
    }
}
