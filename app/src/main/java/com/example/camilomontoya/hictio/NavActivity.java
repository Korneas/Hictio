package com.example.camilomontoya.hictio;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.camilomontoya.hictio.Misc.CloseGesture;

public class NavActivity extends AppCompatActivity {

    private ConstraintLayout cL;

    private ScaleGestureDetector gestureDetector;
    private CloseGesture closeGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        closeGesture = new CloseGesture(this);
        gestureDetector = new ScaleGestureDetector(this, closeGesture);
        closeGesture.setGestureDetector(gestureDetector);

        cL = (ConstraintLayout) findViewById(R.id.nav_layout);

        cL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }
}
