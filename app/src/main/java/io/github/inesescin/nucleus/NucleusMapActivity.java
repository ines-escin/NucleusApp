package io.github.inesescin.nucleus;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.github.inesescin.nucleus.asyncTasks.MapMarkingAsyncTask;
import io.github.inesescin.nucleus.callback.EcopointsCallback;
import io.github.inesescin.nucleus.models.Nucleus;
import io.github.inesescin.nucleus.util.Constants;
import io.github.inesescin.nucleus.util.MapUtil;
import io.github.inesescin.nucleus.util.PermissionRequest;

public class NucleusMapActivity extends FragmentActivity implements DirectionCallback, OnMapReadyCallback, EcopointsCallback {

    private GoogleMap googleMap; // Might be null if Google Play services APK is not available.
    private Map<String, Nucleus> ecopoints;
    private List<Nucleus> selectedMarkers;
    private Polyline directionPolyline;
    private boolean isRequestingRoute;
    private boolean isSelectingEntry;
    private boolean isSelectingExit;
    private List<LatLng> selectedEcopointsLatLng = new ArrayList<>();
    private LatLng startPoint;
    private LatLng stopPoint;
    private Marker startMarker;
    private Marker stopMarker;
    private List<Marker> ecopointMarkers;
    private List<Marker> entryMarkers;
    private List<Marker> exitMarkers;
    private Snackbar snackBar;

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
                if (googleMap == null || ecopoints == null) return;

                if (isRequestingRoute) {
                    //Here we cancel and let it all default
                    fab.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_local_shipping_white_48dp));
                    MapUtil.removeMapMarkers(entryMarkers);
                    MapUtil.removeMapMarkers(exitMarkers);
                    MapUtil.removePolyline(directionPolyline);
                    MapUtil.removeMarker(startMarker);
                    MapUtil.removeMarker(stopMarker);
                    MapUtil.setMapMarkersVisible(ecopointMarkers, true);
                    isRequestingRoute = false;
                    isSelectingEntry = false;
                    isSelectingExit = false;
                    snackBar.dismiss();
                } else {
                    isRequestingRoute = true;
                    //Let the user choose the entry point
                    fab.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_close_white_48dp));
                    MapUtil.setMapMarkersVisible(ecopointMarkers, false);
                    entryMarkers = MapUtil.drawEntryMarkers(googleMap);
                    isSelectingEntry = true;
                    snackBar = Snackbar.make(view, "Escolha por onde entrar na UFPE", Snackbar.LENGTH_INDEFINITE);
                    snackBar.show();
                }

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
        this.googleMap = googleMap;
        setUpMap();
    }

    private void setUpMap() {

        if(PermissionRequest.checkLocationPermission(this)){
            googleMap.setMyLocationEnabled(true);
        }else{
            PermissionRequest.requestLocationPermission(this);
        }

        markMap();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MapUtil.CENTER_POINT, MapUtil.ZOOM));
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (isSelectingEntry) {
                    int id = Integer.parseInt(marker.getTitle());
                    startPoint = MapUtil.ENTRY_POINTS[id];
                    MapUtil.removeMapMarkers(entryMarkers);
                    isSelectingEntry = false;

                    exitMarkers = MapUtil.drawExitMarkers(googleMap);
                    isSelectingExit = true;
                    snackBar = Snackbar.make(findViewById(R.id.coordinator_layout), "Escolha por onde sair na UFPE", Snackbar.LENGTH_INDEFINITE);
                    snackBar.show();
                }else if(isSelectingExit){
                    int id = Integer.parseInt(marker.getTitle());
                    stopPoint = MapUtil.EXIT_POINTS[id];
                    MapUtil.removeMapMarkers(exitMarkers);
                    isSelectingExit = false;
                    requestDirection();
                    MapUtil.setMapMarkersVisible(ecopointMarkers, true);
                    snackBar.dismiss();
                }else {
                    String id = marker.getTitle();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("entityId", marker.getTitle());
                    intent.putExtra("value", ecopoints.get(id).getValue());
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    public void requestDirection() {
        if(ecopoints!=null && !ecopoints.isEmpty()){
            selectedEcopointsLatLng = new ArrayList<>();
            for (Map.Entry<String, Nucleus> entry : ecopoints.entrySet()){
                Nucleus nucleus = entry.getValue();
                if(nucleus.getValue()>=50){
                    selectedEcopointsLatLng.add(new LatLng(nucleus.getLatitude(), nucleus.getLongitude()));
                }
            }
            GoogleDirection.withServerKey("AIzaSyD9Vc8q8uM30dSAH4-ifLoIFwVqM9ouVBk")
                    .from(startPoint)
                    .to(stopPoint)
                    .transportMode(TransportMode.DRIVING)
                    .waypoints(selectedEcopointsLatLng)
                    .optimizeWaypoints(true)
                    .execute(this);
        }
    }



    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {

            List<LatLng> directionPositionList = direction.getRouteList().get(0).getOverviewPolyline().getPointList();
            List<Integer> directionPositionOrder = direction.getRouteList().get(0).getWaypointOrder();

            List<LatLng> orderedWaypoints = MapUtil.getOrderedPoints(selectedEcopointsLatLng, directionPositionOrder);

            startMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_start)).position(startPoint));
            stopMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_stop)).position(stopPoint));
            directionPolyline = googleMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 4, Color.BLACK));


            redirectToNativeGoogleMaps(orderedWaypoints);
        }
    }
    @Override
    public void onDirectionFailure(Throwable t) {

    }

    public void redirectToNativeGoogleMaps( List<LatLng> orderedWaypoints){
        Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse(MapUtil.getNativeGoogleMapsURL(startPoint, stopPoint, orderedWaypoints)));
        startActivity(navigation);
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
                        if(!isSelectingEntry && !isSelectingExit) {
                            MapMarkingAsyncTask mapMarkingAsyncTask = new MapMarkingAsyncTask(NucleusMapActivity.this);
                            mapMarkingAsyncTask.execute();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsyncMapMarkingTask, 0, Constants.ECOPOINTS_REQUEST_SCHEDULE_TIME);
    }

    @Override
    public void onEcopointsReceived(Map<String, Nucleus> ecopoints) {
        if(ecopoints!=null && !ecopoints.isEmpty()){
            MapUtil.removeMapMarkers(this.ecopointMarkers);
            this.ecopointMarkers = MapUtil.drawEcopointMarkers(this, ecopoints, googleMap);
            if(isSelectingEntry || isSelectingExit){
                MapUtil.setMapMarkersVisible(ecopointMarkers, false);
            }
            this.ecopoints = ecopoints;

        }
    }

}
