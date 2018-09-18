package com.example.camilomontoya.hictio;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.camilomontoya.hictio.Misc.Typo;

public class HomeActivity extends AppCompatActivity {

    private TextView subtitle, versionApp;
    private ConstraintLayout home;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Typo.getInstance().setTitle(Typeface.createFromAsset(getAssets(), "fonts/BarlowCondensed-Bold.ttf"));
        Typo.getInstance().setSpecial(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf"));
        Typo.getInstance().setContent(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf"));

        subtitle = (TextView) findViewById(R.id.textSubtitle);
        versionApp = (TextView) findViewById(R.id.versionApp);
        home = (ConstraintLayout) findViewById(R.id.homeLayout);

        subtitle.setTypeface(Typo.getInstance().getContent());
        versionApp.setTypeface(Typo.getInstance().getContent());

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MenuActivity.class));
            }
        });

    }
}
