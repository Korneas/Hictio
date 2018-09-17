package com.example.camilomontoya.hictio.Misc;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.camilomontoya.hictio.R;

import static android.content.Context.VIBRATOR_SERVICE;

public class GestureControl extends GestureDetector.SimpleOnGestureListener {

    private Context c;
    private String type;

    //Para la interaccion del bocachico
    private boolean activeBocachico = true;
    private int count;
    private MediaPlayer dig, success;
    private PlaybackParams params;

    public GestureControl(Context c, String type) {
        this.c = c;
        this.type = type;

        dig = MediaPlayer.create(c, R.raw.digging01);
        success = MediaPlayer.create(c, R.raw.success);
        params = new PlaybackParams();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        switch (type) {
            case "bocachico":
                if (activeBocachico) {
                    if (e2.getY() > e1.getY()) {
                        count++;
                        ((Vibrator)c.getSystemService(VIBRATOR_SERVICE)).vibrate(150);
                        float r = (float) Math.random() + 0.5f;
                        params.setPitch(r);
                        dig.setPlaybackParams(params);
                        dig.start();
                    } else if (e2.getY() < e1.getY()) {
                        count++;
                        ((Vibrator)c.getSystemService(VIBRATOR_SERVICE)).vibrate(150);
                        float r = (float) Math.random() + 0.5f;
                        params.setPitch(r);
                        dig.setPlaybackParams(params);
                        dig.start();
                    }

                    if (count > 10) {
                        ((Vibrator)c.getSystemService(VIBRATOR_SERVICE)).vibrate(500);
                        Toast.makeText(c, "Prrro me escarbaste", Toast.LENGTH_SHORT).show();
                        activeBocachico = false;
                        success.start();
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }
}
