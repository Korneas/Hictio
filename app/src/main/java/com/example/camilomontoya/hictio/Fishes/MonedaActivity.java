package com.example.camilomontoya.hictio.Fishes;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camilomontoya.hictio.R;

public class MonedaActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor acc;
    private MediaPlayer success;

    private TextView yAxis;
    private long[] pattern = {0, 150, 1500};

    private boolean pullout, found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moneda);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        success = MediaPlayer.create(getApplicationContext(), R.raw.success);
        yAxis = (TextView) findViewById(R.id.monedaY);
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {

            float x = event.values[0];
            float y = event.values[1];
            final float z = event.values[2];

            yAxis.setText("Eje Y: " + y + "\n" + "Eje Z: " + z);

            if (z > 7) {
                new Thread() {
                    @Override
                    public void run() {
                        while (!pullout) {
                            try {
                                Thread.sleep(1000);
                                if (z > 7) {
                                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(pattern, 0);
                                    pullout = true;
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            }

            if (pullout && !found) {
                if (z < -1) {
                    Toast.makeText(getApplicationContext(), "Prrro me atrapaste", Toast.LENGTH_SHORT).show();
                    found = true;
                    success.start();
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).cancel();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     * Metodo para salir de la app si se esta en la actividad y devolverlo a la lista
     */
    @Override
    public void onBackPressed() {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).cancel();
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).cancel();
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).cancel();
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).cancel();
        finish();
    }
}
