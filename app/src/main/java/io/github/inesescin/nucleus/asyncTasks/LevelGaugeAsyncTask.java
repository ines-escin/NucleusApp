package io.github.inesescin.nucleus.asyncTasks;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.IOException;

import io.github.inesescin.nucleus.MainActivity;
import io.github.inesescin.nucleus.connection.FiwareConnection;
import pl.pawelkleczkowski.customgauge.CustomGauge;

/**
 * Created by danielmaida on 01/03/16.
 */
public class LevelGaugeAsyncTask extends AsyncTask<String, Void, String> {

    private String oilLevel;
    private CustomGauge oilLevelGauge;
    private TextView oilLevelText;

    public LevelGaugeAsyncTask(CustomGauge customGauge, TextView textView)
    {
        this.oilLevelGauge = customGauge;
        this.oilLevelText = textView;
    }

    @Override
    protected String doInBackground(String... params) {

        FiwareConnection fiwareConnection = new FiwareConnection();

        oilLevel = "";

        try {
            oilLevel = fiwareConnection.getAttributePropertyValue("level", params[0], params[1], "value");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return oilLevel;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        oilLevelText.setText(result);
        oilLevelGauge.setValue(Integer.parseInt(result));
    }
}
