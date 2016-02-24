package io.github.inesescin.nucleus.connection;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.github.inesescin.nucleus.models.Nucleus;
import pl.pawelkleczkowski.customgauge.CustomGauge;

/**
 * Created by danielmaida on 24/02/16.
 */
public class GaugeUpdateAsyncTask extends AsyncTask <String, Void, Nucleus> {

    private FiwareConnection fiwareConnection = new FiwareConnection();
    private Context context;

    public GaugeUpdateAsyncTask(Context context)
    {
        this.context = context;
    }

    @Override
    protected  void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected Nucleus doInBackground(String... params)
    {
        Nucleus nucleus = null;
        String id = String.valueOf(params[0]);
        int value = 0;

        String url = "http://130.206.119.2016:1026/v1/contextEntities" + id;
        String response = "";


        try
        {
            response = fiwareConnection.getRequest(url);

            try
            {
                JSONObject nucleusJson = new JSONObject(response);
                JSONArray attributes = nucleusJson.getJSONArray("attributes");
                JSONObject level = attributes.getJSONObject(0);
                value = level.getInt("value");

            }
            catch (JSONException je)
            {
                je.printStackTrace();
            }

            nucleus = new Nucleus(id, value);


        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return nucleus;
    }

}
