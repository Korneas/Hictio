package com.example.camilomontoya.hictio.Fishes;

import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camilomontoya.hictio.Misc.Typo;
import com.example.camilomontoya.hictio.R;

public class RollizoActivity extends AppCompatActivity {

    private ConstraintLayout cL;
    private TextView title;

    private int globalTouchPosY, globalCurrentPosY;
    private int times;
    private boolean found;

    private MediaPlayer success, cut;
    private PlaybackParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rollizo);

        cL = (ConstraintLayout) findViewById(R.id.rollizoLayout);
        title = (TextView) findViewById(R.id.textRollizo);

        title.setTypeface(Typo.getInstance().getTitle());
        success = MediaPlayer.create(this, R.raw.fine);
        cut = MediaPlayer.create(this, R.raw.plant01);

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

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                globalTouchPosY = (int) e.getY(0);
                Log.d("Rollizo", "Inicio corte");
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                globalTouchPosY = (int) e.getY(0);
                Log.d("Rollizo", "Inicio corte");
                break;
            case MotionEvent.ACTION_MOVE:
                globalCurrentPosY = (int) e.getY(0);
                break;
            case MotionEvent.ACTION_UP:
                int finalDiff = globalTouchPosY - globalCurrentPosY;
                if (finalDiff > 0 && !found) {
                    times++;
                    cut.start();
                }

                if (times > 15 && !found) {
                    success.start();
                    found = true;
                }

                Log.d("Rollizo", "Termino corte");
                break;
            case MotionEvent.ACTION_POINTER_UP:
                int finalDiffPointer = globalTouchPosY - globalCurrentPosY;
                if (finalDiffPointer > 0 && !found) {
                    times++;
                    cut.start();
                }

                if (times > 15 && !found) {
                    success.start();
                    found = true;
                }

                Log.d("Rollizo", "Termino corte");
                break;
            default:
                break;
        }

    }
}
