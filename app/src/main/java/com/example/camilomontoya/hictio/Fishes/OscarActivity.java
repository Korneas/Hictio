package com.example.camilomontoya.hictio.Fishes;

import android.os.Handler;
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

public class OscarActivity extends AppCompatActivity implements Observer {

    private static final int FISH_ID = 0, HEAD = 0, MIDDLE = 1, TAIL = 2;
    private ConstraintLayout cL;
    private TextView name, content;
    private boolean found;

    private int globalTouchPositionX, globalTouchCurrentPositionX;
    private int times = 0;
    private boolean ready;

    private ScaleGestureDetector gestureDetector;
    private CloseGesture closeGesture;

    private Handler handler;
    private Runnable outFish;
    private boolean outGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oscar);

        HictioPlayer.getRef().setFishContext(this, 0);
        HictioPlayer.getRef().setBeforeSpeech(this);

        Client.getInstance().setObserver(this);

        handler = new Handler();
        outFish = new Runnable() {
            @Override
            public void run() {
                outGesture = true;
            }
        };

        closeGesture = new CloseGesture(this);
        gestureDetector = new ScaleGestureDetector(this, closeGesture);
        closeGesture.setGestureDetector(gestureDetector);

        cL = (ConstraintLayout) findViewById(R.id.oscar_layout);
        name = (TextView) findViewById(R.id.textOscar);
        content = (TextView) findViewById(R.id.contentOscar);

        name.setTypeface(Typo.getInstance().getTitle());
        content.setTypeface(Typo.getInstance().getContent());

        cL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleTouch(event);
                return true;
            }
        });
    }

    private void handleTouch(MotionEvent m) {
        int pointerCount = m.getPointerCount();
        if (pointerCount == 3) {
            int action = m.getActionMasked();
            int actionIndex = m.getActionIndex();
            String actionStr;

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    globalTouchPositionX = (int) m.getX(1);
                    break;
                case MotionEvent.ACTION_MOVE:
                    globalTouchCurrentPositionX = (int) m.getX(1);
                    int diff = globalTouchPositionX - globalTouchCurrentPositionX;
                    break;
                case MotionEvent.ACTION_UP:
                    globalTouchCurrentPositionX = 0;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    globalTouchPositionX = (int) m.getX(1);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    int finalDiff = globalTouchPositionX - globalTouchCurrentPositionX;
                    if (finalDiff < 0 && !found) {
                        times++;
                        actionStr = "Acaricialo " + (5 - times) + " veces más";
                        content.setText(actionStr);
                        HictioPlayer.getRef().playSuccess(this);

                        if (times >= 5) {
                            found = true;
                            User.getRef().setFishGesture(FISH_ID, true);
                            actionStr = "El pez ya siente amistad contigo\n¡Lo lograste!";
                            content.setText(actionStr);
                            Client.getInstance().send("fish_0");
                        }
                    }
                    break;
                default:
                    break;
            }
        } else {
            globalTouchPositionX = 0;
            globalTouchCurrentPositionX = 0;

            if (outGesture && User.getRef().getFishState(FISH_ID)) {
                gestureDetector.onTouchEvent(m);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String str = (String) arg;
            if (ready) {
                if (str.contains("head") || str.contains("middle") || str.contains("tail")) {
                    if (!User.getRef().getFishAudioState(FISH_ID, HEAD)) {
                        Log.d("ClienteMensaje", str);
                        HictioPlayer.getRef().playSample(HEAD);
                        User.getRef().setFishes(FISH_ID, HEAD, true);
                    } else if (User.getRef().getFishAudioState(FISH_ID, HEAD)
                            && !User.getRef().getFishAudioState(FISH_ID, MIDDLE)
                            && !HictioPlayer.getRef().getPlaying(HEAD)) {
                        Log.d("ClienteMensaje", str);
                        HictioPlayer.getRef().playSample(MIDDLE);
                        User.getRef().setFishes(FISH_ID, MIDDLE, true);
                    } else if (User.getRef().getFishAudioState(FISH_ID, HEAD)
                            && User.getRef().getFishAudioState(FISH_ID, MIDDLE)
                            && !User.getRef().getFishAudioState(FISH_ID, TAIL)
                            && !HictioPlayer.getRef().getPlaying(MIDDLE)) {
                        Log.d("ClienteMensaje", str);
                        HictioPlayer.getRef().playSample(TAIL);
                        User.getRef().setFishes(FISH_ID, TAIL, true);

                        handler.postDelayed(outFish, 1000);
                    }
                }
            }

            switch (str) {
                case "onfish_0":
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
            Log.i("ClienteMensajePuro", str);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Client.getInstance().send("out_0");
    }
}
