package com.example.camilomontoya.hictio.Fishes;

import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camilomontoya.hictio.Misc.CloseGesture;
import com.example.camilomontoya.hictio.Misc.HictioPlayer;
import com.example.camilomontoya.hictio.Misc.Typo;
import com.example.camilomontoya.hictio.Misc.User;
import com.example.camilomontoya.hictio.Network.Client;
import com.example.camilomontoya.hictio.R;

import java.util.Observable;
import java.util.Observer;

public class PiranhaActivity extends AppCompatActivity implements Observer {

    private final static int FISH_ID = 1, HEAD = 0, MIDDLE = 1, TAIL = 2;

    private ConstraintLayout layout;
    private TextView title;

    private MediaPlayer success;

    private int globalCurrentX1, globalCurrentX2;
    private int count;
    private boolean ready, active, found, touchHead, touchMiddle, touchTail;

    private ScaleGestureDetector gestureDetector;
    private CloseGesture closeGesture;

    private int times;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piranha);

        HictioPlayer.getRef().setFishContext(this, 1);
        HictioPlayer.getRef().setBeforeSpeech(this);

        Client.getInstance().setObserver(this);

        closeGesture = new CloseGesture(this);
        gestureDetector = new ScaleGestureDetector(this, closeGesture);
        closeGesture.setGestureDetector(gestureDetector);

        layout = (ConstraintLayout) findViewById(R.id.piranhaLayout);
        title = (TextView) findViewById(R.id.textPiranha);

        title.setTypeface(Typo.getInstance().getTitle());

        success = MediaPlayer.create(getApplicationContext(), R.raw.fine);

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

                    if (active && !found) {
                        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).cancel();
                        title.setText("Gotcha!");
                        success.start();
                        found = true;
                        User.getRef().setFishGesture(FISH_ID, true);
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
                            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(20000);
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
                if (str.contains("head") || str.contains("middle") || str.contains("tail")) {
                    if (!User.getRef().getFishAudioState(FISH_ID, HEAD)) {
                        Log.d("ClienteMensaje", str);
                        HictioPlayer.getRef().playSample(0);
                        touchHead = true;
                    } else if (touchHead && !touchMiddle && !HictioPlayer.getRef().getPlaying(0)) {
                        Log.d("ClienteMensaje", str);
                        HictioPlayer.getRef().playSample(1);
                        touchMiddle = true;
                    } else if (str.contains("tail") && touchHead && touchMiddle && !touchTail && !HictioPlayer.getRef().getPlaying(1)) {
                        Log.d("ClienteMensaje", str);
                        HictioPlayer.getRef().playSample(2);
                        touchTail = true;
                    }
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
