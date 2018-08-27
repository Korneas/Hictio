package com.example.camilomontoya.piscium.Fishes;

import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.camilomontoya.piscium.R;

public class OscarActivity extends AppCompatActivity {

    private RelativeLayout rL;
    private int k = 0;
    private long[] pattern = { 0, 500, 500, 500, 500};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oscar);

        rL = (RelativeLayout) findViewById(R.id.oscar_layout);

        rL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                k++;
                Handler handler = new Handler();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        k = 0;
                    }
                };

                if(k == 1){
                    ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(100);
                    handler.postDelayed(r, 600);
                } else if(k == 2){
                    Toast.makeText(getApplicationContext(), "Buenas prrro, que se le ofrece?", Toast.LENGTH_SHORT).show();
                    ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(100);
                    k = 0;
                }

            }
        });
    }
}
