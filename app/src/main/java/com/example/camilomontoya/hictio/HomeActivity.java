package com.example.camilomontoya.hictio;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camilomontoya.hictio.Misc.BackgroundMusic;
import com.example.camilomontoya.hictio.Misc.Typo;
import com.example.camilomontoya.hictio.Misc.User;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";

    private MediaPlayer intro;

    private boolean mIsBound;
    private BackgroundMusic mServ;
    private ServiceConnection sCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServ = ((BackgroundMusic.ServiceBinder) service).getService();
            Log.d("SERVICE_MUSIC", "CONNECTED");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
            Log.d("SERVICE_MUSIC", "DISCONNECTED");
        }
    };

    private TextView subtitle, versionApp;
    private ConstraintLayout home;
    private boolean isUserNew;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        doBindService();

        /*
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
        */

        intro = MediaPlayer.create(getApplicationContext(), R.raw.home_start);

        Typo.getInstance().setTitle(Typeface.createFromAsset(getAssets(), "fonts/BarlowCondensed-Bold.ttf"));
        Typo.getInstance().setSpecial(Typeface.createFromAsset(getAssets(), "fonts/Pangolin-Regular.ttf"));
        Typo.getInstance().setContent(Typeface.createFromAsset(getAssets(), "fonts/Pangolin-Regular.ttf"));

        subtitle = (TextView) findViewById(R.id.textSubtitle);
        home = (ConstraintLayout) findViewById(R.id.homeLayout);

        subtitle.setTypeface(Typo.getInstance().getContent());

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, MenuActivity.class);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                intro.stop();
                intro.release();
                intro = null;
                //finish();
            }
        });

        AccessibilityManager am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        if(!am.isTouchExplorationEnabled()){
            intro.start();
        }
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

    private void newUser() {
        int totalConocidos = 0;
        for (int i = 0; i < User.getRef().getFishStateList(); i++) {
            if (User.getRef().getFishState(i)) {
                totalConocidos++;
            }
        }
        if (totalConocidos == 0) {
            isUserNew = true;
        }
        User.getRef().setUserNew(isUserNew);
    }

    public void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();

        Toast.makeText(getApplicationContext(), "Cerraste sesión", Toast.LENGTH_SHORT).show();
        User.setUID("");
        Intent i = new Intent(HomeActivity.this, RegisterActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        //logout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (User.getRef().isOutApp()) {
            mServ.resumeBackground();
            User.getRef().setOutApp(false);
        }
    }

    @Override
    protected void onPause() {
        Context c = getApplicationContext();
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfos = am.getRunningTasks(1);
        if (!taskInfos.isEmpty()) {
            ComponentName topActivity = taskInfos.get(0).topActivity;
            if (!topActivity.getPackageName().equals(getApplicationContext().getPackageName())) {
                //Salio de la app
                if(mServ != null) mServ.pauseBackground();
                User.getRef().setOutApp(true);
            } else {
                //Cambio de actividad
                doUnbindService();
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mServ.stopBackground();
        stopService(new Intent(this, BackgroundMusic.class));
        mServ = null;
        super.onDestroy();
    }

    void doBindService() {
        bindService(new Intent(getApplicationContext(), BackgroundMusic.class), sCon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        Log.d("BIND_SERVICE", "BIND");
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(sCon);
            mIsBound = false;
            Log.d("BIND_SERVICE", "UNBIND");
        }
    }
}
