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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.camilomontoya.hictio.R;

public class PiranhaActivity extends AppCompatActivity {

    private RelativeLayout layout;

    private SensorManager sensorManager;
    private MediaPlayer success;

    private float acelVal, acelLast, shake;
    private int count;
    private boolean start, found, active, capture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piranha);

        layout = (RelativeLayout) findViewById(R.id.piranha_layout);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        success = MediaPlayer.create(getApplicationContext(), R.raw.success);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start =true;
                if (active && !capture) {
                    capture = true;
                    ((Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE)).cancel();
                    success.start();
                }
            }
        });
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            acelLast = acelVal;
            acelVal = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = acelVal - acelLast;
            shake = shake * 0.9f + delta;

            if (start && !capture) {
                if (shake > 2 && !found && !active) {
                    count++;
                    Toast.makeText(getApplicationContext(), "Conteo: " + count, Toast.LENGTH_SHORT).show();
                    ((Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE)).vibrate(150);
                }

                if (count >= 25) {
                    found = true;
                }

                if (found && !active) {
                    ((Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE)).vibrate(20000);
                    active = true;
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
        finish();
    }
}
