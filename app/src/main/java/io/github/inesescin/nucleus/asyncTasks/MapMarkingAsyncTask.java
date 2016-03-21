package io.github.inesescin.nucleus.asyncTasks;

import android.content.Intent;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static java.util.Map.Entry;

import io.github.inesescin.nucleus.MainActivity;
import io.github.inesescin.nucleus.NucleusMapActivity;
import io.github.inesescin.nucleus.connection.FiwareConnection;
import io.github.inesescin.nucleus.models.Nucleus;

/**
 * Created by danielmaida on 03/03/16.
 */
public class MapMarkingAsyncTask extends AsyncTask<GoogleMap, Void,  Map<String, Nucleus>> {

    private String siteAddress;
    private GoogleMap map;

    public MapMarkingAsyncTask(String siteAddress)
    {
        this.siteAddress = siteAddress;
    }

    @Override
    protected Map<String, Nucleus>  doInBackground(GoogleMap... params) {

        map = params[0];
        FiwareConnection fiwareConnection = new FiwareConnection();
        Map<String, Nucleus> ecopoints = new HashMap<>();
        try
        {
            String stringResponse = fiwareConnection.getEntityByType(siteAddress, "Nucleus");
            ecopoints = parseJsonToNucleusArray(ecopoints, stringResponse);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        NucleusMapActivity.ecopoints = ecopoints;
        return ecopoints;
    }

    private  Map<String, Nucleus>  parseJsonToNucleusArray( Map<String, Nucleus> ecopoints, String stringResponse) throws JSONException
    {
        JSONObject response = new JSONObject(stringResponse);
        JSONArray contextResponse = response.getJSONArray("contextResponses");
        for(int i = 0; i < contextResponse.length(); i++)
        {
            Nucleus nucleus = new Nucleus();
            JSONObject currentEntityResponse = contextResponse.getJSONObject(i);
            JSONObject contextElement = currentEntityResponse.getJSONObject("contextElement");
            nucleus.setId(contextElement.getString("id"));
            JSONArray attributes = contextElement.getJSONArray("attributes");
            nucleus.setCoordinates(attributes.getJSONObject(0).getString("value"));
            nucleus.setValue(Double.parseDouble(attributes.getJSONObject(1).getString("value")));
            ecopoints.put(nucleus.getId(), nucleus);
        }
        return ecopoints;
    }

    @Override
    protected void onPostExecute(Map<String, Nucleus>  ecopoints) {
        super.onPostExecute(ecopoints);
        for (Entry<String, Nucleus> entry : ecopoints.entrySet()){
            Nucleus nucleus = entry.getValue();
            map.addMarker(new MarkerOptions().title(nucleus.getId()).position(new LatLng(nucleus.getLatitude(), nucleus.getLongitude())));
        }
    }
}
