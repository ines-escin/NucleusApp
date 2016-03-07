package io.github.inesescin.nucleus;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;

import io.github.inesescin.nucleus.asyncTasks.MapMarkingAsyncTask;
import io.github.inesescin.nucleus.connection.FiwareConnection;
import io.github.inesescin.nucleus.models.Nucleus;

public class NucleusMapActivity extends FragmentActivity {

    private GoogleMap map; // Might be null if Google Play services APK is not available.
    private String siteAddress = "130.206.119.206:1026";
    private MapMarkingAsyncTask mapMarkingAsyncTask = new MapMarkingAsyncTask(siteAddress);
    public static ArrayList<Nucleus> ecopoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nucleus_map);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mapMarkingAsyncTask.execute(map);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("entityId", marker.getTitle());
                for (int i = 0; i < ecopoints.size(); i++) {
                    if (ecopoints.get(i).getId().equals(marker.getTitle())) {
                        intent.putExtra("value", ecopoints.get(i).getValue());
                        i = ecopoints.size();
                    }
                }
                startActivity(intent);
                return true;
            }
        });

    }
}
