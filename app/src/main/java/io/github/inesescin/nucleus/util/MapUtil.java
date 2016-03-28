package io.github.inesescin.nucleus.util;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

import io.github.inesescin.nucleus.R;
import io.github.inesescin.nucleus.models.Nucleus;

/**
 * Created by jal3 on 28/03/2016.
 */
public class MapUtil {
    
    public static final LatLng CENTER_POINT = new LatLng(-8.0524376, -34.9511914);
    public static final LatLng ORIGIN_POINT = new LatLng(-8.052005, -34.946925);
    public static final LatLng DESTINATION_POINT = new LatLng(-8.052276, -34.946933);
    public static final float ZOOM = 15.2f;

    public static void drawEcopointMarkers(Map<String, Nucleus> ecopoints, GoogleMap googleMap) {
        for (Map.Entry<String, Nucleus> entry : ecopoints.entrySet()){
            Nucleus nucleus = entry.getValue();
            double level = nucleus.getValue();
            if (level > 70) {
                googleMap.addMarker(new MarkerOptions().title(nucleus.getId()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_place_red_48dp)).position(new LatLng(nucleus.getLatitude(), nucleus.getLongitude())));
            } else if (level >= 50) {
                googleMap.addMarker(new MarkerOptions().title(nucleus.getId()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_place_yellow_48dp)).position(new LatLng(nucleus.getLatitude(), nucleus.getLongitude())));
            } else {
                googleMap.addMarker(new MarkerOptions().title(nucleus.getId()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_place_green_48dp)).position(new LatLng(nucleus.getLatitude(), nucleus.getLongitude())));
            }
        }
    }

}
