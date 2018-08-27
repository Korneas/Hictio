package com.example.camilomontoya.piscium.Fishes;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.example.camilomontoya.piscium.Misc.GestureControl;
import com.example.camilomontoya.piscium.R;

public class BocachicoActivity extends AppCompatActivity {

    private GestureDetectorCompat gesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bocachico);

        gesture = new GestureDetectorCompat(this, new GestureControl(getApplicationContext(), "bocachico"));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gesture.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


}
