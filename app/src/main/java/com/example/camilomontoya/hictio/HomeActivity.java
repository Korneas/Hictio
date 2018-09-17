package com.example.camilomontoya.hictio;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.camilomontoya.hictio.Fishes.OscarActivity;
import com.example.camilomontoya.hictio.Misc.Typo;

public class HomeActivity extends AppCompatActivity {

    private TextView title, subtitle;
    private Button start;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Typo.getInstance().setTitle(Typeface.createFromAsset(getAssets(), "fonts/Muli-Black.ttf"));
        Typo.getInstance().setSpecial(Typeface.createFromAsset(getAssets(), "fonts/Muli-SemiBold.ttf"));
        Typo.getInstance().setContent(Typeface.createFromAsset(getAssets(), "fonts/Muli-Regular.ttf"));

        title = (TextView) findViewById(R.id.textTitle);
        subtitle = (TextView) findViewById(R.id.textSubtitle);

        title.setTypeface(Typo.getInstance().getTitle());
        subtitle.setTypeface(Typo.getInstance().getSpecial());

        start = (Button) findViewById(R.id.btn_start);
        start.setTypeface(Typo.getInstance().getContent());

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,ListActivity.class));
                finish();
            }
        });
    }
}
