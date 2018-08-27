package com.example.camilomontoya.piscium.Fishes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.camilomontoya.piscium.R;

public class MojarraActivity extends AppCompatActivity {

    private RelativeLayout rL;

    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mojarra);

        rL = (RelativeLayout) findViewById(R.id.mojarra_layout);

        count = 0;

        rL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                //Toast.makeText(getApplicationContext(), "Me tocaste: " + count, Toast.LENGTH_SHORT).show();
                if (count == 20) {
                    Toast.makeText(getApplicationContext(), "Prrro me tocaste 20 veces", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
