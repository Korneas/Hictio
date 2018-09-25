package com.example.camilomontoya.hictio.Fishes;

import android.media.MediaPlayer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camilomontoya.hictio.Misc.Typo;
import com.example.camilomontoya.hictio.Network.Client;
import com.example.camilomontoya.hictio.R;

import java.util.Observable;
import java.util.Observer;

public class OscarActivity extends AppCompatActivity implements Observer {

    private ConstraintLayout cL;
    private TextView name, content;
    private boolean found;

    private int globalTouchPositionX, globalTouchCurrentPositionX;
    private int times = 0;

    private MediaPlayer success, head, middle, tail, special;
    private boolean ready, touchHead, touchMiddle, touchTail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oscar);

        Client.getInstance().setObserver(this);

        cL = (ConstraintLayout) findViewById(R.id.oscar_layout);
        name = (TextView) findViewById(R.id.textOscar);
        content = (TextView) findViewById(R.id.contentOscar);
        success = MediaPlayer.create(getApplicationContext(), R.raw.fine);
        head = MediaPlayer.create(getApplicationContext(), R.raw.oscar_01);
        middle = MediaPlayer.create(getApplicationContext(), R.raw.oscar_02);
        tail = MediaPlayer.create(getApplicationContext(), R.raw.oscar_03);

        name.setTypeface(Typo.getInstance().getTitle());
        content.setTypeface(Typo.getInstance().getContent());

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touchHead = false;
                touchMiddle = false;
                touchTail = false;
                Log.i("Intento", "OMG");
            }
        });

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
                        if (!success.isPlaying()) {
                            success.start();
                        }

                        if (times >= 5) {
                            found = true;
                            actionStr = "El pez ya siente amistad contigo\n¡Lo lograste!";
                            content.setText(actionStr);
                            Client.getInstance().send("fish_0");
                        }
                    }
                    break;
                default:
                    actionStr = "";
            }
            pointerCount = 0;
        } else {
            globalTouchPositionX = 0;
            globalTouchCurrentPositionX = 0;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String str = (String) arg;
            if (ready) {
                if (str.contains("head") && !touchHead && !head.isPlaying() && !middle.isPlaying() && !tail.isPlaying()) {
                    Log.d("ClienteMensaje", str);
                    Log.i("ClienteMensaje", str);
                    head.start();
                    touchHead = true;
                } else if (str.contains("middle") && !touchMiddle && !middle.isPlaying() && !head.isPlaying() && !tail.isPlaying()) {
                    Log.d("ClienteMensaje", str);
                    Log.i("ClienteMensaje", str);
                    middle.start();
                    touchMiddle = true;
                } else if (str.contains("tail") && !touchTail && !tail.isPlaying() && !head.isPlaying() && !middle.isPlaying()) {
                    Log.d("ClienteMensaje", str);
                    Log.i("ClienteMensaje", str);
                    tail.start();
                    touchTail = true;
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
}
