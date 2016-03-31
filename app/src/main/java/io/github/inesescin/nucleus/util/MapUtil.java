package io.github.inesescin.nucleus.util;

import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.inesescin.nucleus.R;
import io.github.inesescin.nucleus.models.Nucleus;

/**
 * Created by jal3 on 28/03/2016.
 */
public class MapUtil {
    
    public static final LatLng CENTER_POINT = new LatLng(-8.0524376, -34.9511914);
    public static final LatLng DESTINATION_POINT = new LatLng(-8.052276, -34.946933);

    public static final LatLng[] ENTRY_POINTS = { new LatLng(-8.051995, -34.946845),
                                                new LatLng(-8.055428, -34.956152),
                                                new LatLng(-8.046545, -34.950556)};

    public static final float ZOOM = 15.2f;

    public static List<Marker> drawEcopointMarkers(Map<String, Nucleus> ecopoints, GoogleMap googleMap) {
        List<Marker> markers = new ArrayList<>();
        for (Map.Entry<String, Nucleus> entry : ecopoints.entrySet()){
            Nucleus nucleus = entry.getValue();
            double level = nucleus.getValue();
            MarkerOptions markerOptions = new MarkerOptions();
            if (level > 70) {
                markerOptions.title(nucleus.getId()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_place_red_48dp)).position(new LatLng(nucleus.getLatitude(), nucleus.getLongitude()));
            } else if (level >= 50) {
                markerOptions.title(nucleus.getId()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_place_yellow_48dp)).position(new LatLng(nucleus.getLatitude(), nucleus.getLongitude()));
            } else {
                markerOptions.title(nucleus.getId()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_place_green_48dp)).position(new LatLng(nucleus.getLatitude(), nucleus.getLongitude()));
            }
            Marker marker = googleMap.addMarker(markerOptions);
            markers.add(marker);
        }
        return markers;
    }

    public static void removeMapMarkers(List<Marker> markers) {
        if(markers==null) return;
        for (Marker marker: markers) {
            marker.remove();
        }
    }

    public static void setMapMarkersVisible(List<Marker> markers, boolean visible) {
        if(markers==null) return;
        for (Marker marker: markers) {
            marker.setVisible(visible);
        }
    }

    public static List<Marker> drawEntryMarkers(GoogleMap googleMap){
        List<Marker> markers = new ArrayList<>();
        for (int i = 0; i < ENTRY_POINTS.length; i++) {
            LatLng latLng = ENTRY_POINTS[i];
            Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(i+""));
            markers.add(marker);
        }
        return markers;
    }

    public static String getNativeGoogleMapsURL(LatLng origin, LatLng destination, List<LatLng> waypoints, List<Integer> waypointsOrder){
        String URL = "http://maps.google.com/maps?" +
                    "saddr=" + origin.latitude + "," + origin.longitude +
                    "&daddr=" + waypointsToString(getOrderedPoints(waypoints, waypointsOrder)) +
                    "+to:" + destination.latitude + "," + destination.longitude;
        return URL;
    }

    private static String waypointsToString(List<LatLng> waypoints){
        if (waypoints != null && !waypoints.isEmpty()) {
            String string = waypoints.get(0).latitude + "," + waypoints.get(0).longitude;
            for (int i = 1; i < waypoints.size(); i++) {
                string += "+to:" + waypoints.get(i).latitude + "," + waypoints.get(i).longitude;
            }
            return string;
        }
        return null;
    }

    private static List<LatLng> getOrderedPoints(List<LatLng> points, List<Integer> pointOrder){
        List<LatLng> orderedWaypoints = new ArrayList<>();
        for (int i = 0; i < pointOrder.size(); i++) {
            int order = pointOrder.get(i);
            orderedWaypoints.add(points.get(order));
        }
        return orderedWaypoints;
    }

    public static void removePolyline(Polyline directionPolyline) {
        if (directionPolyline != null) directionPolyline.remove();
    }
}
