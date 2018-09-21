package com.example.camilomontoya.hictio.Fishes;

import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camilomontoya.hictio.Misc.Typo;
import com.example.camilomontoya.hictio.R;

public class MojarraActivity extends AppCompatActivity {

    private ConstraintLayout cL;
    private TextView title;

    private int times;
    private boolean found;

    private MediaPlayer bubbles, success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mojarra);

        cL = (ConstraintLayout) findViewById(R.id.mojarra_layout);
        title = (TextView) findViewById(R.id.textMojarra);

        title.setTypeface(Typo.getInstance().getTitle());

        bubbles = MediaPlayer.create(getApplicationContext(), R.raw.bubble01);
        success = MediaPlayer.create(getApplicationContext(), R.raw.success);
        bubbles.setVolume(0.8f, 0.8f);

        cL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleTouch(event);
                return true;
            }
        });
    }

    private void handleTouch(MotionEvent e){
        int pointerCount = e.getPointerCount();
        if(pointerCount == 3){
            int action = e.getActionMasked();

            switch (action){
                case MotionEvent.ACTION_UP:
                    if(!bubbles.isPlaying() && !found){
                        bubbles.start();
                        times++;
                    }

                    if(times > 10 && !found){
                        found = true;
                        success.start();
                    }

                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    if(!bubbles.isPlaying() && !found){
                        bubbles.start();
                        times++;
                    }

                    if(times > 10 && !found){
                        found = true;
                        success.start();
                    }

                    break;
                default:
                    break;
            }
        }
    }
}
