package com.example.camilomontoya.hictio.Fishes;

import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.camilomontoya.hictio.Misc.GestureControl;
import com.example.camilomontoya.hictio.Misc.Typo;
import com.example.camilomontoya.hictio.R;

public class BocachicoActivity extends AppCompatActivity {

    private ConstraintLayout cL;
    private TextView title;

    private int globalTouchPosY, globalCurrentPosY;
    private int times;
    private boolean found;

    private MediaPlayer success, dig;
    private PlaybackParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bocachico);

        cL = (ConstraintLayout) findViewById(R.id.bocachicoLayout);
        title = (TextView) findViewById(R.id.textBoca);

        success = MediaPlayer.create(this, R.raw.fine);
        dig = MediaPlayer.create(this, R.raw.digging01);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            params = new PlaybackParams();
        }

        title.setTypeface(Typo.getInstance().getTitle());

        cL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleTouch(event);
                return true;
            }
        });
    }

    private void handleTouch(MotionEvent e) {
        int pointerCount = e.getPointerCount();
        if (pointerCount == 2) {
            int action = e.getActionMasked();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    globalTouchPosY = (int) e.getY(0);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    globalTouchPosY = (int) e.getY(0);
                    break;
                case MotionEvent.ACTION_MOVE:
                    globalCurrentPosY = (int) e.getY(0);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    int finalDiff = globalCurrentPosY - globalTouchPosY;
                    if(finalDiff > 0 && !found){
                        times++;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            params.setPitch((float)Math.random() + 0.5f);
                            dig.setPlaybackParams(params);
                        }
                        dig.start();
                    }

                    if(times > 10 && !found){
                        success.start();
                        found = true;
                    }

                    break;
                default:
                    break;
            }
        }
    }

}
