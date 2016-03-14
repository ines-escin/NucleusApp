package io.github.inesescin.nucleus;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import io.github.inesescin.nucleus.asyncTasks.LevelGaugeAsyncTask;
import io.github.inesescin.nucleus.connection.FiwareConnection;

import io.github.inesescin.nucleus.models.Nucleus;
import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity {

    private String nucleusId;
    private String siteAddress = "130.206.119.206:1026";
    private double value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();

        nucleusId = extras.getString("entityId");
        value = extras.getDouble("value");

        TextView nucleusIdText = (TextView) findViewById(R.id.nucleusId);
        nucleusIdText.setText(nucleusId);

        TextView gaugeText = (TextView) findViewById(R.id.levelText);
        CustomGauge levelGauge = (CustomGauge) findViewById (R.id.main_gauge);

        gaugeText.setText(String.valueOf((int) value));
        levelGauge.setValue((int) value);

        updateGaugeValue(levelGauge, gaugeText);

    }

    private void updateGaugeValue(final CustomGauge gauge, final TextView gaugeText)
    {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask updateGaugeValue = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        LevelGaugeAsyncTask gaugeAsyncTask = new LevelGaugeAsyncTask(gauge, gaugeText);
                        gaugeAsyncTask.execute(nucleusId,siteAddress);
                    }
                });
            }
        };
        timer.schedule(updateGaugeValue, 0, 30000);
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
