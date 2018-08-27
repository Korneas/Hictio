package com.example.camilomontoya.piscium.Fishes;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.camilomontoya.piscium.R;

public class FantasmaActivity extends AppCompatActivity {

    private Vibrator vib;
    private VibrationEffect vibEf;
    private final long time = 350;

    private RelativeLayout rL;

    private int tries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fantasma);

        vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        rL = (RelativeLayout) findViewById(R.id.fantasma_layout);

        tries = (int) (Math.random() * 6);

        rL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vib.vibrate(time);
                int chance = (int) (Math.random() * 6);
                if (chance == tries) {
                    Toast.makeText(getApplicationContext(), "Prrro me encontraste", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Prrro esta no fue | " + chance + " : " + tries, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
