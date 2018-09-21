package com.example.camilomontoya.hictio;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.camilomontoya.hictio.Misc.Typo;

import java.text.Format;

public class HomeActivity extends AppCompatActivity {

    private TextView subtitle, versionApp;
    private ConstraintLayout home;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        WindowManager manager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (40 * getResources()
                .getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        CustomViewGroup view = new CustomViewGroup(this);

        manager.addView(view, localLayoutParams);

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
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
            }
        });
    }

    public class CustomViewGroup extends ViewGroup {

        public CustomViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            Log.v("customViewGroup", "**********Intercepted");
            return true;
        }
    }
}
