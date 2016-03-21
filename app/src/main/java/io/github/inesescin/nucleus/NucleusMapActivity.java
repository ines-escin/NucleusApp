package io.github.inesescin.nucleus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.github.inesescin.nucleus.asyncTasks.MapMarkingAsyncTask;
import io.github.inesescin.nucleus.connection.FiwareConnection;
import io.github.inesescin.nucleus.models.Nucleus;

public class NucleusMapActivity extends FragmentActivity implements DirectionCallback {

    private GoogleMap map; // Might be null if Google Play services APK is not available.
    private String siteAddress = "130.206.119.206:1026";
    public static Map<String, Nucleus> ecopoints;
    private boolean isSelecting;
    public int counter;
    public List<Nucleus> selectedMarkers;
    private Polyline directionPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nucleus_map);
        setUpMapIfNeeded();
        isSelecting = true;
        selectedMarkers = new ArrayList<>();
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

        map.setMyLocationEnabled(true);
        markMap();
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String id = marker.getTitle();
                if (isSelecting) {
                    if(directionPolyline!=null) directionPolyline.remove();
                    counter++;
                    selectedMarkers.add(ecopoints.get(id));
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("entityId", marker.getTitle());
                    intent.putExtra("value", ecopoints.get(id).getValue());
                    startActivity(intent);
                }
                if (counter==2){
                    requestDirection();
                    counter=0;
                    selectedMarkers.clear();
                }
                return true;
            }
        });
    }

    public void requestDirection() {
        List<Nucleus> list = selectedMarkers;

        LatLng origin = new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude());
        LatLng destination =  new LatLng(list.get(1).getLatitude(), list.get(1).getLongitude());

        GoogleDirection.withServerKey("AIzaSyCRGiz73nymFibyZay9tXk0RugdOPj12VY")
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            directionPolyline = map.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED));
        }
    }
    @Override
    public void onDirectionFailure(Throwable t) {

    }


    private void markMap()
    {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsyncMapMarkingTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MapMarkingAsyncTask mapMarkingAsyncTask = new MapMarkingAsyncTask(siteAddress);
                        mapMarkingAsyncTask.execute(map);
                    }
                });
            }
        };
        timer.schedule(doAsyncMapMarkingTask, 0, 50000);
    }
}
