package io.github.inesescin.nucleus.connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by danielmaida on 24/02/16.
 */
public class FiwareConnection {

    private OkHttpClient client;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public FiwareConnection()
    {
        client = new OkHttpClient();
    }


    public String getEntityById(String siteAddress, String entityId) throws IOException
    {
        String url = "http://" + siteAddress + "/v1/contextEntites/" + entityId;
        return doGetRequest(url);
    }

    public String getEntityByType(String siteAddress, String type) throws IOException
    {
        String url = "http://" + siteAddress + "/v1/queryContext/";
        String json = "{" + "\"entities\": [" + "{" + "\"type\": \"" + type + "\"," + "\"isPattern\": \"true\"," +  "\"id\": \".*\"" + "}]}";

        return doPostRequest(url,json);
    }


    public String getAttributePropertyValue(String attributeName, String entityId, String  siteAddress, String property) throws IOException
    {
        String url = "http://" + siteAddress + "/v1/contextEntities/" + entityId + "/attributes/" + attributeName;
        String response = doGetRequest(url);
        String value = "";
        try
        {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray attributesArray = jsonResponse.getJSONArray("attributes");
            JSONObject attribute = attributesArray.getJSONObject(0);
            value = attribute.getString(property);
        }
        catch (JSONException jsonException)
        {
            jsonException.printStackTrace();
        }
        return value;
    }

    private String doGetRequest(String url) throws IOException
    {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .build();

        Response response;
        int requestAttempts = 0;

        do
        {
            response = client.newCall(request).execute();
            requestAttempts++;
        }
        while(response.code() != 200 || requestAttempts < 5);

        return response.body().string();
    }

    private String doPostRequest(String url, String json) throws IOException
    {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response;
        int requestAttempts = 0;

        do
        {
            response = client.newCall(request).execute();
            requestAttempts++;
        }
        while(response.code() != 200 || requestAttempts < 5);

        return response.body().string();
    }


}
