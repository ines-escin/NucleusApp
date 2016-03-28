package io.github.inesescin.nucleus;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.github.inesescin.nucleus.asyncTasks.MapMarkingAsyncTask;
import io.github.inesescin.nucleus.models.Nucleus;

public class NucleusMapActivity extends FragmentActivity implements DirectionCallback, OnMapReadyCallback {

    private GoogleMap map; // Might be null if Google Play services APK is not available.
    private String siteAddress = "130.206.119.206:1026";
    public static Map<String, Nucleus> ecopoints;
    public List<Nucleus> selectedMarkers;
    private Polyline directionPolyline;
    private boolean requestedRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nucleus_map);
        setActivityEnvironment();
        selectedMarkers = new ArrayList<>();
    }

    private void setActivityEnvironment() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(map==null || ecopoints==null) return;
                if(requestedRoute){
                    fab.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_local_shipping_white_48dp));
                    if(directionPolyline!=null) directionPolyline.remove();
                }else{
                    Snackbar.make(view, "Carregando rota...", Snackbar.LENGTH_LONG).show();
                    requestDirection();
                    fab.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_close_white_48dp));
                }
                requestedRoute = !requestedRoute;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGoogleMapAsync();
    }


    private void getGoogleMapAsync() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setUpMap();
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
        LatLng origin = new LatLng(-8.052005, -34.946925);
        LatLng destination =  new LatLng(-8.052276, -34.946933);
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
