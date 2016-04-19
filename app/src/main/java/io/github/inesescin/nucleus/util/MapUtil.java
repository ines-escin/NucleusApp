package io.github.inesescin.nucleus.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

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

    public static final LatLng[] EXIT_POINTS = { new LatLng(-8.052300, -34.946882),
                                                new LatLng(-8.055428, -34.956152),
                                                new LatLng(-8.046545, -34.950556)};

    public static final float ZOOM = 15.2f;

    public static List<Marker> drawEcopointMarkers(Activity activity, Map<String, Nucleus> ecopoints, GoogleMap googleMap) {
        List<Marker> markers = new ArrayList<>();
        for (Map.Entry<String, Nucleus> entry : ecopoints.entrySet()){
            Nucleus nucleus = entry.getValue();
            int level = (int)nucleus.getValue();
            int drawableId;
            if (level > 70) {
                drawableId = R.drawable.marker_red_ecopoint;
            } else if (level >= 50) {
                drawableId = R.drawable.marker_orange_ecopoint;
            } else {
                drawableId = R.drawable.marker_green_ecopoint;
            }
            Bitmap markerBmp = DrawableUtil.getMarkerView(activity, level + "", drawableId);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(nucleus.getId()).icon(BitmapDescriptorFactory.fromBitmap(markerBmp)).position(new LatLng(nucleus.getLatitude(), nucleus.getLongitude()));
            Marker marker = googleMap.addMarker(markerOptions);
            markers.add(marker);
        }
        return markers;
    }


    public static void removeMapMarkers(List<Marker> markers) {
        if(markers==null) return;
        for (Marker marker: markers) {
            removeMarker(marker);
        }
    }

    public static void removeMarker(Marker marker){
        if(marker!=null) marker.remove();
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
            Marker marker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_start)).position(latLng).title(i + ""));
            markers.add(marker);
        }
        return markers;
    }

    public static List<Marker> drawExitMarkers(GoogleMap googleMap){
        List<Marker> markers = new ArrayList<>();
        for (int i = 0; i < EXIT_POINTS.length; i++) {
            LatLng latLng = EXIT_POINTS[i];
            Marker marker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_stop)).position(latLng).title(i + ""));
            markers.add(marker);
        }
        return markers;
    }

    public static String getNativeGoogleMapsURL(LatLng origin, LatLng destination, List<LatLng> orderedWaypoints){
        String URL = "http://maps.google.com/maps?" +
                    "saddr=" + origin.latitude + "," + origin.longitude +
                    "&daddr=" + waypointsToString(orderedWaypoints) +
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

    public static List<LatLng> getOrderedPoints(List<LatLng> points, List<Integer> pointOrder){
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
    public static void showMarkerInfoWindow(Marker marker){
        if(marker!=null) marker.showInfoWindow();
    }
    public static void setMakerVisible(Marker marker, boolean visible){
        if(marker!=null) marker.setVisible(visible);
    }

}
