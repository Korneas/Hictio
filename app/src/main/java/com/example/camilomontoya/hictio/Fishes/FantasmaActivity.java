package com.example.camilomontoya.hictio.Fishes;

import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.camilomontoya.hictio.Network.Client;
import com.example.camilomontoya.hictio.R;

import java.util.Observable;
import java.util.Observer;

public class FantasmaActivity extends AppCompatActivity implements Observer{

    private Vibrator vib;
    private final long time = 350;
    private MediaPlayer media, success;
    private PlaybackParams params;

    private RelativeLayout rL;

    private int tries;
    private boolean found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fantasma);

        Client.getInstance().setObserver(this);

        vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        media = MediaPlayer.create(getApplicationContext(), R.raw.echo);
        success = MediaPlayer.create(getApplicationContext(), R.raw.success);
        rL = (RelativeLayout) findViewById(R.id.fantasma_layout);

        tries = (int) (Math.random() * 8);

        rL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!media.isPlaying() && !found) {
                    vib.vibrate(time);
                    int chance = (int) (Math.random() * 8);
                    if (chance == tries) {
                        found = true;
                        success.start();
                    } else {
                        media.start();
                    }
                }
            }
        });
    }

    /**
     * Metodo para salir de la app si se esta en la actividad y devolverlo a la lista
     */
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
