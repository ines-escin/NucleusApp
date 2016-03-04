package io.github.inesescin.nucleus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;

import io.github.inesescin.nucleus.asyncTasks.LevelGaugeAsyncTask;
import io.github.inesescin.nucleus.connection.FiwareConnection;

import io.github.inesescin.nucleus.models.Nucleus;
import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity {

    private String nucleusId = "NucleusAlpha";
    private String siteAddress = "130.206.119.206:1026";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                TextView gaugeText = (TextView) findViewById(R.id.levelText);
                CustomGauge levelGauge = (CustomGauge) findViewById (R.id.main_gauge);

                LevelGaugeAsyncTask gaugeAsyncTask = new LevelGaugeAsyncTask(levelGauge, gaugeText);

                gaugeAsyncTask.execute(nucleusId, siteAddress);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
