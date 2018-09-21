package com.example.camilomontoya.hictio.Fishes;

import android.media.MediaPlayer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.camilomontoya.hictio.Misc.Typo;
import com.example.camilomontoya.hictio.R;

public class DiscoActivity extends AppCompatActivity {

    private ConstraintLayout cL;
    private TextView title;
    private MediaPlayer success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disco);

        cL = (ConstraintLayout) findViewById(R.id.disco_layout);
        title = (TextView) findViewById(R.id.textDisco);
        success = MediaPlayer.create(getApplicationContext(), R.raw.fine);

        title.setTypeface(Typo.getInstance().getTitle());
    }
}
