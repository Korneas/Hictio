package com.example.camilomontoya.hictio.Fishes;

import android.media.MediaPlayer;
import android.os.Handler;
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
import com.example.camilomontoya.hictio.Misc.Typo;
import com.example.camilomontoya.hictio.Misc.User;
import com.example.camilomontoya.hictio.Network.Client;
import com.example.camilomontoya.hictio.R;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class PiranhaActivity extends AppCompatActivity implements Observer {

    private final static int FISH_ID = 1, HEAD = 0, MIDDLE = 1, TAIL = 2;
    private boolean audioRunning;

    private ConstraintLayout layout;
    private TextView title;

    private MediaPlayer success, beforeSpeech;
    private MediaPlayer[] piranhaPlayer, piranhaLearn;

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
        piranhaLearn = new MediaPlayer[2];
        User.getRef().setActualContext(getApplicationContext());

        if (User.getRef().getFishGesture(FISH_ID)) {
            piranhaPlayer = new MediaPlayer[3];
            piranhaPlayer[0] = MediaPlayer.create(this, R.raw.piranha_01);
            piranhaPlayer[1] = MediaPlayer.create(this, R.raw.piranha_02);
            piranhaPlayer[2] = MediaPlayer.create(this, R.raw.piranha_03);
        }

        Client.getInstance().setObserver(this);
        success = MediaPlayer.create(getApplicationContext(), R.raw.fine);
        beforeSpeech = MediaPlayer.create(getApplicationContext(), R.raw.speech);

        closeGesture = new CloseGesture(this);
        gestureDetector = new ScaleGestureDetector(this, closeGesture);
        closeGesture.setGestureDetector(gestureDetector);

        layout = (ConstraintLayout) findViewById(R.id.piranhaLayout);
        title = (TextView) findViewById(R.id.textPiranha);

        title.setTypeface(Typo.getInstance().getTitle());

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
                        if (success != null) {
                            success.release();
                            success = MediaPlayer.create(getApplicationContext(), R.raw.fine);
                        }
                        success.start();
                        found = true;
                        if (!User.getRef().getFishGesture(FISH_ID)) {
                            if (piranhaLearn[0] == null)
                                piranhaLearn[0] = MediaPlayer.create(getApplicationContext(), R.raw.piranha_fine);
                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    User.getRef().setFishGesture(FISH_ID, true);

                                    piranhaLearn[0].start();
                                }
                            }, 1000);

                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 1000 + piranhaLearn[0].getDuration());
                        } else {
                            if (piranhaLearn[1] == null)
                                piranhaLearn[1] = MediaPlayer.create(getApplicationContext(), R.raw.piranha_call);
                            piranhaLearn[1].start();
                        }
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

                        if (count > 15000) {
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

            //gestureDetector.onTouchEvent(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String str = (String) arg;
            if (ready) {
                if (str.contains("head") && !audioRunning) {
                    Log.d("Piranha", "Audio 1");
                    audioRunning = true;

                    if (User.getRef().getFishGesture(FISH_ID)) {
                        piranhaPlayer[0].release();
                        piranhaPlayer[0] = MediaPlayer.create(this, R.raw.piranha_01);
                    }
                    if (beforeSpeech != null) {
                        beforeSpeech.release();
                        beforeSpeech = MediaPlayer.create(getApplicationContext(), R.raw.speech);
                    }
                    beforeSpeech.start();
                    beforeSpeech.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            piranhaPlayer[0].start();
                        }
                    });

                    piranhaPlayer[0].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            audioRunning = false;
                            User.getRef().setFishes(FISH_ID, HEAD, true);
                        }
                    });
                } else if (str.contains("middle") && !audioRunning) {
                    Log.d("Piranha", "Audio 2");
                    audioRunning = true;

                    if (User.getRef().getFishGesture(FISH_ID)) {
                        piranhaPlayer[1].release();
                        piranhaPlayer[1] = MediaPlayer.create(this, R.raw.piranha_02);
                    }
                    if (beforeSpeech != null) {
                        beforeSpeech.release();
                        beforeSpeech = MediaPlayer.create(getApplicationContext(), R.raw.speech);
                    }
                    beforeSpeech.start();
                    beforeSpeech.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            piranhaPlayer[1].start();
                        }
                    });

                    piranhaPlayer[1].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            audioRunning = false;
                            User.getRef().setFishes(FISH_ID, MIDDLE, true);
                        }
                    });

                } else if (str.contains("tail") && !audioRunning) {
                    Log.d("Piranha", "Audio 3");

                    if (User.getRef().getFishGesture(FISH_ID)) {
                        piranhaPlayer[2].release();
                        piranhaPlayer[2] = MediaPlayer.create(this, R.raw.piranha_03);
                    }
                    if (beforeSpeech != null) {
                        beforeSpeech.release();
                        beforeSpeech = MediaPlayer.create(getApplicationContext(), R.raw.speech);
                    }
                    beforeSpeech.start();
                    beforeSpeech.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            piranhaPlayer[2].start();
                        }
                    });

                    piranhaPlayer[2].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            audioRunning = false;
                            User.getRef().setFishes(FISH_ID, TAIL, true);
                        }
                    });
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



    @Override
    public void onBackPressed() {
        //Do nothing bro
    }
}
