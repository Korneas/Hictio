package com.example.camilomontoya.piscium.Misc;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import static android.content.Context.VIBRATOR_SERVICE;

public class GestureControl extends GestureDetector.SimpleOnGestureListener {

    private Context c;
    private String type;

    //Para la interaccion del bocachico
    private boolean activeBocachico = true;
    private int count;
    private MediaPlayer dig;

    public GestureControl(Context c, String type) {
        this.c = c;
        this.type = type;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        switch (type) {
            case "bocachico":
                if (activeBocachico) {
                    if (e2.getY() > e1.getY()) {
                        count++;
                        ((Vibrator)c.getSystemService(VIBRATOR_SERVICE)).vibrate(150);
                    } else if (e2.getY() < e1.getY()) {
                        count++;
                        ((Vibrator)c.getSystemService(VIBRATOR_SERVICE)).vibrate(150);
                    }

                    if (count > 10) {
                        ((Vibrator)c.getSystemService(VIBRATOR_SERVICE)).vibrate(500);
                        Toast.makeText(c, "Prrro me escarbaste", Toast.LENGTH_SHORT).show();
                        activeBocachico = false;
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }
}
