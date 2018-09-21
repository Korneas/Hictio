package com.example.camilomontoya.hictio;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camilomontoya.hictio.Misc.Typo;

public class AboutActivity extends AppCompatActivity {

    private ScaleGestureDetector gestureDetector;
    private float scaleFactor = 1.0f;
    private float sF1 = 1.0f, sF2 = 1.0f;
    private ConstraintLayout layout;

    private TextView title, about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        gestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        title = (TextView) findViewById(R.id.title_about);
        about = (TextView) findViewById(R.id.hictio_about);
        layout = (ConstraintLayout) findViewById(R.id.about_layout);

        title.setTypeface(Typo.getInstance().getTitle());
        about.setTypeface(Typo.getInstance().getContent());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= gestureDetector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.f));
            about.setText("Escala: " + scaleFactor);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            Log.d("Pressed", "Button pressed");
            if(scaleFactor != 1.0f){
                sF1 = scaleFactor;
            }
            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            Log.d("Released", "Button released");
            sF2 = scaleFactor;
            if(sF1 < sF2){
                Toast.makeText(getApplicationContext(), "Plus", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Minus", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
