package com.example.camilomontoya.hictio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.camilomontoya.hictio.Misc.Typo;

public class OptionsActivity extends AppCompatActivity {

    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        title = (TextView) findViewById(R.id.title_options);

        title.setTypeface(Typo.getInstance().getTitle());
    }
}
