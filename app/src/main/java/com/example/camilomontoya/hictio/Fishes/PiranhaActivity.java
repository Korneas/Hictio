package com.example.camilomontoya.hictio.Fishes;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camilomontoya.hictio.Misc.CloseGesture;
import com.example.camilomontoya.hictio.Misc.Typo;
import com.example.camilomontoya.hictio.Network.Client;
import com.example.camilomontoya.hictio.R;

import java.util.Observable;
import java.util.Observer;

public class PiranhaActivity extends AppCompatActivity implements Observer {

    private ConstraintLayout layout;
    private TextView title;

    private MediaPlayer success, head, tail, middle;

    private int globalCurrentX1, globalCurrentX2;
    private int count;
    private boolean ready, active, found, touchHead, touchMiddle, touchTail;

    private ScaleGestureDetector gestureDetector;
    private CloseGesture closeGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piranha);

        Client.getInstance().setObserver(this);

        closeGesture = new CloseGesture(this);
        gestureDetector = new ScaleGestureDetector(this, closeGesture);
        closeGesture.setGestureDetector(gestureDetector);

        layout = (ConstraintLayout) findViewById(R.id.piranhaLayout);
        title = (TextView) findViewById(R.id.textPiranha);

        title.setTypeface(Typo.getInstance().getTitle());

        success = MediaPlayer.create(getApplicationContext(), R.raw.fine);
        head = MediaPlayer.create(getApplicationContext(), R.raw.piranha_01);
        middle = MediaPlayer.create(getApplicationContext(), R.raw.piranha_02);
        tail = MediaPlayer.create(getApplicationContext(), R.raw.piranha_03);

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleTouch(event);
                return true;
            }
        });
    }

    private void handleTouch(MotionEvent e) {
        int pointerCount = e.getPointerCount();
        if (pointerCount == 1) {
            int action = e.getActionMasked();
            String actionStr;

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    globalCurrentX1 = (int) e.getX(0);

                    if(active && !found){
                        ((Vibrator)getSystemService(VIBRATOR_SERVICE)).cancel();
                        title.setText("Gotcha!");
                        success.start();
                        found = true;
                        Client.getInstance().send("fish_1");
                    }

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!active) {
                        globalCurrentX2 = (int) e.getX(0);

                        if (globalCurrentX1 < globalCurrentX2) {
                            count += (globalCurrentX2 - globalCurrentX1);
                        } else {
                            count += (globalCurrentX1 - globalCurrentX2);
                        }

                        if (count > 30000) {
                            active = true;
                            ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(20000);
                        }

                        globalCurrentX1 = globalCurrentX2;
                    }
                    break;
            }
            pointerCount = 0;
        } else {
            globalCurrentX1 = 0;
            globalCurrentX2 = 0;

            gestureDetector.onTouchEvent(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Client.getInstance().send("out_2");
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String str = (String) arg;
            if (ready) {
                if (str.contains("head") && !touchHead && !head.isPlaying() && !middle.isPlaying() && !tail.isPlaying()) {
                    Log.d("ClienteMensaje", str);
                    head.start();
                    touchHead = true;
                } else if (str.contains("middle") && !touchMiddle && !middle.isPlaying() && !head.isPlaying() && !tail.isPlaying()) {
                    Log.d("ClienteMensaje", str);
                    middle.start();
                    touchMiddle = true;
                } else if (str.contains("tail") && !touchTail && !tail.isPlaying() && !head.isPlaying() && !middle.isPlaying()) {
                    Log.d("ClienteMensaje", str);
                    tail.start();
                    touchTail = true;
                }
            }

            switch (str) {
                case "onfish_1":
                    ready = true;
                    break;
                case "offline":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Desconectado del servidor", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
            Log.d("ClienteMensajePuro", str);
        }
    }
}
