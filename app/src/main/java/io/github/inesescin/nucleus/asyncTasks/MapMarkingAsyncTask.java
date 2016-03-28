package io.github.inesescin.nucleus.asyncTasks;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.github.inesescin.nucleus.callback.EcopointsCallback;
import io.github.inesescin.nucleus.connection.FiwareConnection;
import io.github.inesescin.nucleus.models.Nucleus;
import io.github.inesescin.nucleus.util.Constants;

/**
 * Created by danielmaida on 03/03/16.
 */
public class MapMarkingAsyncTask extends AsyncTask<Void, Void,  Map<String, Nucleus>> {

    private EcopointsCallback callback;

    public MapMarkingAsyncTask(EcopointsCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Map<String, Nucleus>  doInBackground(Void... params) {

        FiwareConnection fiwareConnection = new FiwareConnection();
        Map<String, Nucleus> ecopoints = new HashMap<>();
        try{
            String stringResponse = fiwareConnection.getEntityByType(Constants.FIWARE_ADDRESS, "Nucleus");
            ecopoints = parseJsonToNucleusArray(ecopoints, stringResponse);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ecopoints;
    }

    private  Map<String, Nucleus>  parseJsonToNucleusArray( Map<String, Nucleus> ecopoints, String stringResponse) throws JSONException
    {
        JSONObject response = new JSONObject(stringResponse);
        JSONArray contextResponse = response.getJSONArray("contextResponses");
        for (int i = 0; i < contextResponse.length(); i++) {
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
        if(callback!=null) {
            callback.onEcopointsReceived(ecopoints);
        }
    }
}
