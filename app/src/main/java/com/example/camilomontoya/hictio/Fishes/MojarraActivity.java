package com.example.camilomontoya.hictio.Fishes;

import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.camilomontoya.hictio.R;

public class MojarraActivity extends AppCompatActivity {

    private RelativeLayout rL;

    private int count;
    private boolean found;
    private MediaPlayer media, success;
    private PlaybackParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mojarra);

        rL = (RelativeLayout) findViewById(R.id.mojarra_layout);
        media = MediaPlayer.create(getApplicationContext(), R.raw.bubble01);
        success = MediaPlayer.create(getApplicationContext(), R.raw.success);
        params = new PlaybackParams();
        media.setVolume(0.8f, 0.8f);

        count = 0;

        rL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!found) {
                    count++;
                    if (count < 7) {
                        media.setVolume(0.2f, 0.2f);
                    } else if (count >= 7 && count < 15) {
                        media.setVolume(0.6f, 0.6f);
                    } else if (count >= 15) {
                        media.setVolume(1,1);
                    }
                    float r = (float) Math.random() + 0.5f;
                    params.setPitch(r);
                    media.setPlaybackParams(params);
                    media.start();
                    //Toast.makeText(getApplicationContext(), "Me tocaste: " + count, Toast.LENGTH_SHORT).show();
                    if (count >= 20) {
                        Toast.makeText(getApplicationContext(), "Prrro me tocaste 20 veces", Toast.LENGTH_SHORT).show();
                        found = true;
                        success.start();
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
}
