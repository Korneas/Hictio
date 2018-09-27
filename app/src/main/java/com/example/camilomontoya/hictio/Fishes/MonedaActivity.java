package com.example.camilomontoya.hictio.Fishes;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

public class MonedaActivity extends AppCompatActivity implements Observer{

    private ConstraintLayout cL;
    private TextView title, textAngle;

    private int globalTouchPosX, globalTouchPosY, globalTouchCurrentX, globalTouchCurrentY;
    private float middleX, middleY;
    private int angle1, angle2, totalAngle;

    private boolean ready, touchHead, touchMiddle, touchTail;
    private MediaPlayer success, head, tail, middle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moneda);

        Client.getInstance().setObserver(this);

        cL = (ConstraintLayout) findViewById(R.id.monedaLayout);
        title = (TextView) findViewById(R.id.textMoneda);
        textAngle = (TextView) findViewById(R.id.angleMoneda);

        success = MediaPlayer.create(getApplicationContext(), R.raw.fine);
        head = MediaPlayer.create(getApplicationContext(), R.raw.moneda_01);
        middle = MediaPlayer.create(getApplicationContext(), R.raw.moneda_02);
        tail = MediaPlayer.create(getApplicationContext(), R.raw.moneda_03);

        title.setTypeface(Typo.getInstance().getTitle());
        textAngle.setTypeface(Typo.getInstance().getContent());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        middleX = width / 2f;
        middleY = height / 2f;

        cL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleTouch(event);
                return true;
            }
        });
    }

    private void handleTouch(MotionEvent e) {
        int action = e.getActionMasked();
        int actionIndex = e.getActionIndex();
        String str;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                globalTouchPosX = (int) e.getX(0);
                globalTouchPosY = (int) e.getY(0);

                angle1 = (int) ((Math.atan2(middleY - globalTouchPosY, globalTouchPosX - middleX) * 180) / Math.PI);
                if (angle1 < 0) {
                    angle1 = angle1 + 360;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!ready) {
                    globalTouchCurrentX = (int) e.getX(0);
                    globalTouchCurrentY = (int) e.getY(0);

                    angle2 = (int) ((Math.atan2(middleY - globalTouchCurrentY, globalTouchCurrentX - middleX) * 180) / Math.PI);
                    if (angle2 < 0) {
                        angle2 = angle2 + 360;
                    }

                    if (angle1 < angle2) {
                        totalAngle += (angle2 - angle1);
                    } else if (angle1 > angle2) {
                        totalAngle += (angle1 - angle2);
                    }

                    if(totalAngle > 12000){
                        success.start();
                        ready = true;
                        Client.getInstance().send("fish_2");
                    }

                    //MAPEAR ANGULO CON EL VOLUMEN DEL MEDIAPLAYER

                    textAngle.setText("Angulo: " + totalAngle + " : " + angle1 + "=" + angle2);

                    if (angle1 != angle2) {
                        angle1 = angle2;
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                globalTouchPosX = (int) e.getX(0);
                globalTouchPosY = (int) e.getY(0);

                angle1 = (int) ((Math.atan2(middleY - globalTouchPosY, globalTouchPosX - middleX) * 180) / Math.PI);
                if (angle1 < 0) {
                    angle1 = angle1 + 360;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            default:
                break;
        }
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
                case "onfish_2":
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
