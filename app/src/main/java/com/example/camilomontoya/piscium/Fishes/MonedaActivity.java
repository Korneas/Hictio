package com.example.camilomontoya.piscium.Fishes;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camilomontoya.piscium.R;

public class MonedaActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor acc;

    private TextView yAxis;
    private long[] pattern = {0, 150, 1500};

    private boolean pullout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moneda);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        yAxis = (TextView) findViewById(R.id.monedaY);
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {

            float x = event.values[0];
            float y = event.values[1];
            final float z = event.values[2];

            yAxis.setText("Eje Y: " + y + "\n" + "Eje Z: " + z);

            if(z > 7){
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(1000);
                            if(z > 7 && !pullout){
                                ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(pattern, 0);
                                pullout = true;
                            } else {
                                pullout = false;
                                Thread.interrupted();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
            }

            if(pullout){
                if(z < -1){
                    Toast.makeText(getApplicationContext(), "Prrro me atrapaste", Toast.LENGTH_SHORT).show();
                    pullout = false;
                    ((Vibrator)getSystemService(VIBRATOR_SERVICE)).cancel();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
