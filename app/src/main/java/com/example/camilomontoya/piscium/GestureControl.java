package com.example.camilomontoya.piscium;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureControl extends GestureDetector.SimpleOnGestureListener {

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if(e2.getX() > e1.getX()){

        } else if(e2.getX() < e1.getX()){

        }

        return super.onFling(e1, e2, velocityX, velocityY);
    }
}
