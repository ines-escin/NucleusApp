package io.github.inesescin.nucleus.connection;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by danielmaida on 24/02/16.
 */
public class FiwareConnection {

    private OkHttpClient client;

    public FiwareConnection()
    {
        client = new OkHttpClient();
    }

    public String getRequest(String url) throws IOException
    {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
