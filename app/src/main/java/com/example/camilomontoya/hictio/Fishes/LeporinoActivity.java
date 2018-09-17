package com.example.camilomontoya.hictio.Fishes;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.camilomontoya.hictio.R;

public class LeporinoActivity extends AppCompatActivity implements View.OnTouchListener {

    private RelativeLayout rL;

    private boolean found, active;
    private MediaPlayer success;

    private Handler handler;
    private Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leporino);

        rL = (RelativeLayout) findViewById(R.id.leporino_layout);
        rL.setOnTouchListener(this);
        success = MediaPlayer.create(getApplicationContext(), R.raw.success);

        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                active = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Activo", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        handler.postDelayed(r, 8000);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case (MotionEvent.ACTION_DOWN):
                if (active && !found) {
                    Toast.makeText(getApplicationContext(), "Posicion: " + x + " : " + y, Toast.LENGTH_SHORT).show();
                    found = true;
                    success.start();
                }
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }
}
