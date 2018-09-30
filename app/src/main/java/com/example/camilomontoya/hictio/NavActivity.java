package com.example.camilomontoya.hictio;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.camilomontoya.hictio.Fishes.OscarActivity;
import com.example.camilomontoya.hictio.Misc.CloseGesture;
import com.example.camilomontoya.hictio.Misc.HictioPlayer;
import com.example.camilomontoya.hictio.Misc.User;
import com.example.camilomontoya.hictio.Network.Client;

import java.util.Observable;
import java.util.Observer;

public class NavActivity extends AppCompatActivity implements Observer {

    private final static int OSCAR = 0, PIRANHA = 1;

    private ConstraintLayout cL;

    private ScaleGestureDetector gestureDetector;
    private CloseGesture closeGesture;

    private float pointerY, pointerCurrentY;

    private int currentFish = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        Client.getInstance().setObserver(this);
        Client.getInstance().startConection();
        HictioPlayer.getRef().setNavigateContext(this);

        closeGesture = new CloseGesture(this);
        gestureDetector = new ScaleGestureDetector(this, closeGesture);
        closeGesture.setGestureDetector(gestureDetector);

        cL = (ConstraintLayout) findViewById(R.id.nav_layout);

        cL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleTouch(event);
                return true;
            }
        });
    }

    private void handleTouch(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            switch (event.getActionMasked()){
                case MotionEvent.ACTION_DOWN:
                    pointerY = event.getY(0);
                    break;
                case MotionEvent.ACTION_MOVE:
                    pointerCurrentY = event.getY(0);
                    break;
                case MotionEvent.ACTION_UP:
                    float diff = pointerY - pointerCurrentY;
                    if(diff >= 0){
                        Client.getInstance().send("haptic");
                    }
                    break;
                default:
                    break;
            }
        } else {
            gestureDetector.onTouchEvent(event);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String str = (String) arg;
            if (str.contains("beacon") && User.getRef().getFishGesture(currentFish)) {
                if (str.contains("oscar") && !User.getRef().getFishGesture(OSCAR)) {
                    currentFish = OSCAR;
                    HictioPlayer.getRef().playNavigateGesture(OSCAR);
                    startActivity(new Intent(NavActivity.this, OscarActivity.class));
                } else if (str.contains("piranha") && !User.getRef().getFishGesture(PIRANHA)) {
                    currentFish = PIRANHA;
                    HictioPlayer.getRef().playNavigateGesture(PIRANHA);
                    startActivity(new Intent(NavActivity.this, OscarActivity.class));
                }
            }
        }
    }
}
