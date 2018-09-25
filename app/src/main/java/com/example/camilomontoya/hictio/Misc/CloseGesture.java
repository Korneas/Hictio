package com.example.camilomontoya.hictio.Misc;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.widget.Toast;

public class CloseGesture extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    private float scaleFactor;
    private float scale1, scale2;
    private Context c;
    private ScaleGestureDetector gestureDetector;

    public CloseGesture(Context c) {
        this.c = c;
        scaleFactor = 1.0f;
        scale1 = 1.0f;
        scale2 = 1.0f;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        scaleFactor *= gestureDetector.getScaleFactor();
        scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.f));
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        if(scaleFactor != 1.0f){
            scale1 = scaleFactor;
        }
        Log.d("Scale","Scale Begin");
        return super.onScaleBegin(detector);
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        scale2 = scaleFactor;
        if(scale1 > scale2){
            ((Activity)c).finish();
        }
        Log.d("Scale","Scale End");
    }

    public void setGestureDetector(ScaleGestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }
}
