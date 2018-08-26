package com.example.camilomontoya.piscium;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class SapoaraActivity extends AppCompatActivity {

    public static final String TAG = "SapoaraActivity";

    private Chronometer chrono;

    private SensorManager sensorManager;
    private Sensor acc;

    private MediaPlayer mp;

    private float acelVal, acelLast, shake;
    private int count;
    private boolean active;
    private boolean[] stage;
    private int[] tries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sapoara);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        mp = MediaPlayer.create(this, R.raw.splash01);

        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
        shake = 0.0f;

        tries = new int[3];
        tries[0] = (int) (Math.random() * 8);
        tries[1] = (int) (Math.random() * 8) + 8;
        tries[2] = (int) (Math.random() * 8) + 16;

        stage = new boolean[3];

        new Thread() {
            @Override
            public void run() {
                while (count < 30) {
                    try {
                        Thread.sleep(1000);
                        for (int i = 0; i < 3; i++) {
                            if (count == tries[i]) {
                                active = true;
                                mp.start();
                            }
                        }

                        if (count >= 29) {
                            if(stage[0] && stage[1] && stage[2]){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "U die " + stage[0] + " | " + stage[1] + " | " + stage[2], Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            Thread.interrupted();
                        }
                        count++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
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

            if (shake > 8 && mp.isPlaying() && active) {
                // Accion a hacer al agitar
                Toast.makeText(getApplicationContext(), "Prrro me agitaste", Toast.LENGTH_SHORT).show();
                active = false;
                if(!stage[0]) {
                    stage[0] = true;
                    return;
                } else if(stage[0] && !stage[1]){
                    stage[1] = true;
                } else if(stage[1]){
                    stage[2] = true;
                    return;
                }
                //mp.start();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
