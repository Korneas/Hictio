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

import com.example.camilomontoya.hictio.Misc.CloseGesture;
import com.example.camilomontoya.hictio.Misc.Typo;

public class AboutActivity extends AppCompatActivity {

    private ConstraintLayout layout;

    private TextView title, about;

    private ScaleGestureDetector gestureDetector;
    private CloseGesture closeGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        closeGesture = new CloseGesture(this);
        gestureDetector = new ScaleGestureDetector(this, closeGesture);
        closeGesture.setGestureDetector(gestureDetector);

        title = (TextView) findViewById(R.id.title_about);
        about = (TextView) findViewById(R.id.hictio_about);
        layout = (ConstraintLayout) findViewById(R.id.about_layout);

        title.setTypeface(Typo.getInstance().getTitle());
        about.setTypeface(Typo.getInstance().getContent());

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }
}
