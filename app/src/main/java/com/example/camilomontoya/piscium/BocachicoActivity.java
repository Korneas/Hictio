package com.example.camilomontoya.piscium;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BocachicoActivity extends AppCompatActivity {

    private GestureDetectorCompat gesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bocachico);

        gesture = new GestureDetectorCompat(this, new GestureControl());
    }


}
