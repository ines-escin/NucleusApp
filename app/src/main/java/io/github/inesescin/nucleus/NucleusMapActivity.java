package io.github.inesescin.nucleus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdate;
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
    public List<Nucleus> selectedMarkers;
    private Polyline directionPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nucleus_map);
        setActivityEnvironment();
        setUpMapIfNeeded();
        selectedMarkers = new ArrayList<>();
    }

    private void setActivityEnvironment() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Carregando rota", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                requestDirection();
            }
        });
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
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-8.0524376,-34.9511914), 15.2f));
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String id = marker.getTitle();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("entityId", marker.getTitle());
                intent.putExtra("value", ecopoints.get(id).getValue());
                startActivity(intent);

                return true;
            }
        });
    }

    public void requestDirection() {
        LatLng origin = new LatLng(-8.0520081,-34.9477178);
        LatLng destination =  new LatLng(-8.0522683,-34.9480723);
        List<LatLng> waypoints = new ArrayList<>();
        for (Map.Entry<String, Nucleus> entry : ecopoints.entrySet()){
            Nucleus nucleus = entry.getValue();
            if(nucleus.getValue()>=50){
                waypoints.add(new LatLng(nucleus.getLatitude(), nucleus.getLongitude()));
            }
        }

        GoogleDirection.withServerKey("AIzaSyCRGiz73nymFibyZay9tXk0RugdOPj12VY")
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .waypoints(waypoints)
                .optimizeWaypoints(true)
                .execute(this);
    }



    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            List<LatLng> directionPositionList = direction.getRouteList().get(0).getOverviewPolyline().getPointList();
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
