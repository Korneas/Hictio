package com.example.camilomontoya.hictio.Fishes;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camilomontoya.hictio.Misc.BackgroundMusic;
import com.example.camilomontoya.hictio.Misc.CloseGesture;
import com.example.camilomontoya.hictio.Misc.Typo;
import com.example.camilomontoya.hictio.Misc.User;
import com.example.camilomontoya.hictio.Network.Client;
import com.example.camilomontoya.hictio.R;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import static com.example.camilomontoya.hictio.R.raw.oscar_03;

public class OscarActivity extends AppCompatActivity implements Observer {

    private static final int FISH_ID = 0, HEAD = 0, MIDDLE = 1, TAIL = 2;
    private ConstraintLayout cL;
    private TextView name, content;
    private boolean found;

    private int globalTouchPositionX, globalTouchCurrentPositionX;
    private int times = 0;
    private final static int MAX_TIMES = 3;
    private boolean ready;

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

    private MediaPlayer success, beforeSpeech, touchFish;
    private MediaPlayer[] oscarPlayer, oscarLearn;
    private boolean audioRunning;

    private ScaleGestureDetector gestureDetector;
    private CloseGesture closeGesture;

    private Handler handler, repeat;
    private Runnable outFish;
    private boolean outGesture, toAlbum, repeatOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oscar);

        doBindService();

        User.getRef().setActualContext(getApplicationContext());
        Client.getInstance().setObserver(this);
        success = MediaPlayer.create(getApplicationContext(), R.raw.fine);
        touchFish = MediaPlayer.create(getApplicationContext(), R.raw.touch_oscar);
        beforeSpeech = MediaPlayer.create(getApplicationContext(), R.raw.speech);
        oscarLearn = new MediaPlayer[6];

        if (User.getRef().getFishGesture(FISH_ID)) {
            oscarPlayer = new MediaPlayer[3];
            oscarPlayer[0] = MediaPlayer.create(this, R.raw.oscar_01);
            oscarPlayer[1] = MediaPlayer.create(this, R.raw.oscar_02);
            oscarPlayer[2] = MediaPlayer.create(this, R.raw.oscar_03);
            oscarLearn[2] = MediaPlayer.create(this, R.raw.inoscar);
            oscarLearn[3] = MediaPlayer.create(this, R.raw.oscar_before);
        } else {
            oscarLearn[2] = MediaPlayer.create(this, R.raw.oscar_close);
            oscarLearn[3] = MediaPlayer.create(this, R.raw.oscar_before);
        }

        oscarLearn[2].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                oscarLearn[3].start();
                repeat.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        repeatOn = true;
                    }
                }, oscarLearn[3].getDuration() + 2000);
            }
        });

        repeat = new Handler();
        handler = new Handler();
        outFish = new Runnable() {
            @Override
            public void run() {
                outGesture = true;
            }
        };

        Intent parent = getIntent();
        int mark = parent.getIntExtra("MARK", -1);
        if(mark != -1){
            oscarLearn[2].start();
        } else {
            Toast.makeText(this, "WHAT", Toast.LENGTH_SHORT).show();
        }

        closeGesture = new CloseGesture(this);
        gestureDetector = new ScaleGestureDetector(this, closeGesture);
        closeGesture.setGestureDetector(gestureDetector);

        cL = (ConstraintLayout) findViewById(R.id.oscar_layout);
        name = (TextView) findViewById(R.id.textOscar);
        content = (TextView) findViewById(R.id.contentOscar);

        name.setTypeface(Typo.getInstance().getTitle());
        content.setTypeface(Typo.getInstance().getContent());

        cL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleTouch(event);
                return true;
            }
        });
    }

    private void handleTouch(MotionEvent m) {
        int pointerCount = m.getPointerCount();
        if (pointerCount == 3) {
            int action = m.getActionMasked();
            int actionIndex = m.getActionIndex();
            String actionStr;

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    globalTouchPositionX = (int) m.getX(1);
                    break;
                case MotionEvent.ACTION_MOVE:
                    globalTouchCurrentPositionX = (int) m.getX(1);
                    int diff = globalTouchPositionX - globalTouchCurrentPositionX;
                    break;
                case MotionEvent.ACTION_UP:
                    globalTouchCurrentPositionX = 0;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    globalTouchPositionX = (int) m.getX(1);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    int finalDiff = globalTouchPositionX - globalTouchCurrentPositionX;
                    if (finalDiff < 0 && !found) {
                        times++;
                        actionStr = "Acaricialo " + (MAX_TIMES - times) + " veces más";
                        content.setText(actionStr);
                        if (touchFish != null) {
                            touchFish.release();
                            touchFish = MediaPlayer.create(getApplicationContext(), R.raw.touch_oscar);
                        }
                        touchFish.start();

                        if (times >= MAX_TIMES) {
                            found = true;
                            actionStr = "El pez ya siente amistad contigo\n¡Lo lograste!";
                            success.start();
                            content.setText(actionStr);
                            if (!User.getRef().getFishGesture(FISH_ID)) {
                                if (oscarLearn[0] == null)
                                    oscarLearn[0] = MediaPlayer.create(getApplicationContext(), R.raw.oscar_fine);
                                (new Handler()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        User.getRef().setFishGesture(FISH_ID, true);
                                        oscarLearn[0].start();
                                    }
                                }, 1000);

                                (new Handler()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        oscarLearn[0].release();
                                        finish();
                                    }
                                }, 1000 + oscarLearn[0].getDuration());
                            } else {
                                if (oscarLearn[1] == null)
                                    oscarLearn[1] = MediaPlayer.create(getApplicationContext(), R.raw.oscar_call);
                                oscarLearn[1].start();
                                oscarLearn[1].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        oscarLearn[1].release();
                                        ready = true;
                                    }
                                });
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        } else {
            globalTouchPositionX = 0;
            globalTouchCurrentPositionX = 0;

            if(m.getActionMasked() == MotionEvent.ACTION_UP && repeatOn && times == 0){
                oscarLearn[2].start();
                repeatOn = false;
            }

            if (outGesture && User.getRef().getFishState(FISH_ID)) {
                //gestureDetector.onTouchEvent(m);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String str = (String) arg;
            if (str.contains("oscar")) {
                String[] strOscar = str.split("-");
                if (ready && !strOscar[1].contains("piranha")) {
                    if (strOscar[1].contains("w") && !audioRunning) {
                        Log.d("OscarPlayer", "Audio 1");
                        audioRunning = true;
                        if (User.getRef().getFishGesture(FISH_ID)) {
                            oscarPlayer[0] = MediaPlayer.create(this, R.raw.oscar_01);
                        }
                        if (beforeSpeech != null) {
                            beforeSpeech.release();
                            beforeSpeech = MediaPlayer.create(getApplicationContext(), R.raw.speech);
                        }
                        beforeSpeech.start();
                        beforeSpeech.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                oscarPlayer[0].start();
                            }
                        });

                        oscarPlayer[0].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                audioRunning = false;
                                User.getRef().setFishes(FISH_ID, HEAD, true);
                                oscarPlayer[0].release();
                                if(User.getRef().getFishState(FISH_ID) && !toAlbum){
                                    if (oscarLearn[2] == null) oscarLearn[2] = MediaPlayer.create(getApplicationContext(), R.raw.addedtoalbum);
                                    audioRunning = true;
                                    oscarLearn[2].start();
                                    oscarLearn[2].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            audioRunning = false;
                                            oscarLearn[2].release();
                                            oscarLearn[2] = null;
                                        }
                                    });
                                    toAlbum = true;
                                }
                            }
                        });
                    } else if (strOscar[1].contains("a") && !audioRunning) {
                        Log.d("OscarPlayer", "Audio 2");
                        audioRunning = true;

                        if (User.getRef().getFishGesture(FISH_ID)) {
                            oscarPlayer[1] = MediaPlayer.create(this, R.raw.oscar_02);
                        }
                        if (beforeSpeech != null) {
                            beforeSpeech.release();
                            beforeSpeech = MediaPlayer.create(getApplicationContext(), R.raw.speech);
                        }
                        beforeSpeech.start();
                        beforeSpeech.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                oscarPlayer[1].start();
                            }
                        });
                        oscarPlayer[1].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                audioRunning = false;
                                User.getRef().setFishes(FISH_ID, MIDDLE, true);
                                oscarPlayer[1].release();
                                if(User.getRef().getFishState(FISH_ID) && !toAlbum){
                                    if (oscarLearn[2] == null) oscarLearn[2] = MediaPlayer.create(getApplicationContext(), R.raw.addedtoalbum);
                                    audioRunning = true;
                                    oscarLearn[2].start();
                                    oscarLearn[2].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            audioRunning = false;
                                            oscarLearn[2].release();
                                            oscarLearn[2] = null;
                                        }
                                    });
                                    toAlbum = true;
                                }
                            }
                        });
                    } else if (strOscar[1].contains("s") && !audioRunning) {
                        Log.d("OscarPlayer", "Audio 3");
                        audioRunning = true;

                        if (User.getRef().getFishGesture(FISH_ID)) {
                            oscarPlayer[2] = MediaPlayer.create(this, R.raw.oscar_03);
                        }
                        if (beforeSpeech != null) {
                            beforeSpeech.release();
                            beforeSpeech = MediaPlayer.create(getApplicationContext(), R.raw.speech);
                        }
                        beforeSpeech.start();
                        beforeSpeech.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                oscarPlayer[2].start();
                            }
                        });
                        oscarPlayer[2].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                audioRunning = false;
                                User.getRef().setFishes(FISH_ID, TAIL, true);
                                oscarPlayer[2].release();
                                if(User.getRef().getFishState(FISH_ID) && !toAlbum){
                                    if (oscarLearn[2] == null) oscarLearn[2] = MediaPlayer.create(getApplicationContext(), R.raw.addedtoalbum);
                                    audioRunning = true;
                                    oscarLearn[2].start();
                                    oscarLearn[2].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            audioRunning = false;
                                            oscarLearn[2].release();
                                            oscarLearn[2] = null;
                                        }
                                    });
                                    toAlbum = true;
                                }
                            }
                        });

                        handler.postDelayed(outFish, 1000);
                    }
                }

                switch (str) {
                    case "offline":
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Desconectado del servidor", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
                Log.d("ClienteMensajePuro", str);
            }
        }
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
    protected void onDestroy() {
        beforeSpeech.release();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //Do nothing bro
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
