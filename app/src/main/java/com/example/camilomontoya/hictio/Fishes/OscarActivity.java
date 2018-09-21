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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oscar);

        Client.getInstance().setObserver(this);

        cL = (ConstraintLayout) findViewById(R.id.oscar_layout);
        name = (TextView) findViewById(R.id.textOscar);
        content = (TextView) findViewById(R.id.contentOscar);
        success = MediaPlayer.create(getApplicationContext(), R.raw.fine);
        head = MediaPlayer.create(getApplicationContext(), R.raw.voz_cabeza);
        middle = MediaPlayer.create(getApplicationContext(), R.raw.voz_cuerpo);
        tail = MediaPlayer.create(getApplicationContext(), R.raw.voz_cola);
        special = MediaPlayer.create(getApplicationContext(), R.raw.voz_rika);

        name.setTypeface(Typo.getInstance().getTitle());
        content.setTypeface(Typo.getInstance().getContent());

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!special.isPlaying()) {
                    special.start();
                }
                Client.getInstance().send("oscar");
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
                    actionStr = "Abajo" + " ahora " + globalTouchCurrentPositionX + " antes " + globalTouchPositionX;
                    //content.setText(actionStr);
                    break;
                case MotionEvent.ACTION_MOVE:
                    globalTouchCurrentPositionX = (int) m.getX(1);
                    int diff = globalTouchPositionX - globalTouchCurrentPositionX;
                    //actionStr = "Diferencia" + diff + " ahora " + globalTouchCurrentPositionX + " antes " + globalTouchPositionX;
                    //content.setText(actionStr);
                    break;
                case MotionEvent.ACTION_UP:
                    globalTouchCurrentPositionX = 0;
                    actionStr = "Arriba" + " ahora " + globalTouchCurrentPositionX + " antes " + globalTouchPositionX;
                    content.setText(actionStr);
                    Toast.makeText(getApplicationContext(), "Made it!", Toast.LENGTH_SHORT).show();
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    globalTouchPositionX = (int) m.getX(1);
                    actionStr = "Abajo" + " ahora " + globalTouchCurrentPositionX + " antes " + globalTouchPositionX;
                    content.setText(actionStr);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    int finalDiff = globalTouchPositionX - globalTouchCurrentPositionX;
                    if (finalDiff < 0) {
                        times++;
                        actionStr = "Lo acarisiaste " + times + " veces";
                        content.setText(actionStr);
                        if (!success.isPlaying()) {
                            success.start();
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
            if (str.contains("head")) {
                Log.d("ClienteMensaje", str);
                head.start();
            } else if (str.contains("middle")) {
                Log.d("ClienteMensaje", str);
                middle.start();
            } else if (str.contains("tail")) {
                Log.d("ClienteMensaje", str);
                tail.start();
            } else if (str.contains("offline")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Desconectado del servidor", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.d("ClienteMensaje", str);
            }
        }
    }
}
