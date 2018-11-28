package com.example.camilomontoya.hictio;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camilomontoya.hictio.Misc.BackgroundMusic;
import com.example.camilomontoya.hictio.Misc.CloseGesture;
import com.example.camilomontoya.hictio.Misc.Typo;
import com.example.camilomontoya.hictio.Misc.User;
import com.example.camilomontoya.hictio.Network.Client;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ExploreActivity extends AppCompatActivity implements Observer {

    private final static String CHANNEL_ID = "Notifications";
    private static final int NOTIFICATION_ID = 0;
    private final static int NO_PATH = -1, OSCAR = 0, PIRANHA = 1;
    private final static int ANIM_TIME = 800, BEACON_TIME = 1200, LIGHT_ANIM = 15000;

    private MediaPlayer[] navPlayer, oscarPlayer;
    private MediaPlayer success, beforeSpeech, oscarBase;
    private boolean failed, audioRunning, availableOscar, touch, callingPI;
    private boolean isOscar, inModule, inTouchFish;

    private ConstraintLayout cL;
    private TextView aurora;
    private ImageView auroraProfile, auroraDialog;
    private ImageView beacon, beaconLine, beaconLine1, beaconLine2;
    private ImageView oscarSilhoutte, verticalScroll, oscarExplore;
    private ImageView infoSilhoutte, module, verticalUp, touchFish, bracelet;
    private ImageView oHead, oBody, oTail, light;
    private String[] auroraTxt;

    private ObjectAnimator auroraIn, auroraOut;
    private ObjectAnimator itemIn, itemOut;
    private ObjectAnimator lightAlpha, lightRot;

    private ScaleGestureDetector gestureDetector;
    private CloseGesture closeGesture;

    private float pointerY, pointerCurrentY;

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

    private int currentAudio, k;
    private ProgressDialog progress;
    private boolean audioPlaying;
    private boolean[] steps;
    private boolean narrativeAvailable, unlocked;

    private Handler repeatFish, inOscar, findNoFish, comError;
    private Runnable noFishFound, comFalse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        doBindService();

        steps = new boolean[3];
        auroraTxt = getResources().getStringArray(R.array.aurora_chat);

        navPlayer = new MediaPlayer[15];
        navPlayer[0] = MediaPlayer.create(getApplicationContext(), R.raw.com_error);
        navPlayer[1] = MediaPlayer.create(getApplicationContext(), R.raw.explore_guide);
        navPlayer[2] = MediaPlayer.create(getApplicationContext(), R.raw.explore_walk);
        navPlayer[3] = MediaPlayer.create(getApplicationContext(), R.raw.explore_remember);
        navPlayer[4] = MediaPlayer.create(getApplicationContext(), R.raw.explore_inoscar);
        navPlayer[5] = MediaPlayer.create(getApplicationContext(), R.raw.explore_getoscar);
        navPlayer[6] = MediaPlayer.create(getApplicationContext(), R.raw.explore_gotopi);
        navPlayer[7] = MediaPlayer.create(getApplicationContext(), R.raw.explore_gesturepi);
        navPlayer[8] = MediaPlayer.create(getApplicationContext(), R.raw.explore_soundpi);
        navPlayer[9] = MediaPlayer.create(getApplicationContext(), R.raw.explore_inpi);
        navPlayer[10] = MediaPlayer.create(getApplicationContext(), R.raw.explore_usepi);
        navPlayer[11] = MediaPlayer.create(getApplicationContext(), R.raw.pi_error);
        navPlayer[12] = MediaPlayer.create(getApplicationContext(), R.raw.explore_pioscar);
        navPlayer[13] = MediaPlayer.create(getApplicationContext(), R.raw.explore_pioscar_touch);
        navPlayer[14] = MediaPlayer.create(getApplicationContext(), R.raw.explore_bye);

        // Saludo -> Recorramos el lugar
        navPlayer[1].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                navPlayer[2].start();
                setAurora(1);
            }
        });

        // Recorramos -> Recuerda
        navPlayer[2].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                navPlayer[3].start();
                setAurora(2);
            }
        });

        navPlayer[3].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                steps[0] = true;
            }
        });

        // Aprender -> Buscar
        navPlayer[5].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                navPlayer[6].start();
                setAurora(6);
                itemAnimOut(oscarExplore);
                infoSilhoutte.setVisibility(View.VISIBLE);
                infoSilhoutte.setAlpha(1f);
                itemAnimIn(infoSilhoutte);
                callingPI = true;
            }
        });

        // Buscar -> Sonar
        navPlayer[6].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setAurora(7);
                navPlayer[7].start();
                verticalUp.setVisibility(View.VISIBLE);
                verticalUp.setAlpha(1f);
                itemAnimIn(verticalUp);
            }
        });

        navPlayer[7].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                steps[1] = true;
            }
        });

        // InPi -> Manilla
        navPlayer[9].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setAurora(11);
                navPlayer[10].start();
                itemAnimOut(module);
                bracelet.setVisibility(View.VISIBLE);
                bracelet.setAlpha(0f);
                itemAnimIn(bracelet);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setAurora(12);
                    }
                }, 4000);
                releasePlayer(0);
            }
        });

        // Manilla -> Error
        navPlayer[10].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                findNoFish.postDelayed(noFishFound, 30000);
            }
        });

        // Oscar -> Tocar figura
        navPlayer[12].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                touch = true;
            }
        });

        beforeSpeech = MediaPlayer.create(getApplicationContext(), R.raw.speech);

        oscarPlayer = new MediaPlayer[4];
        oscarBase = MediaPlayer.create(getApplicationContext(), R.raw.oscar_base);
        oscarPlayer[0] = MediaPlayer.create(getApplicationContext(), R.raw.oscar_head);
        oscarPlayer[1] = MediaPlayer.create(getApplicationContext(), R.raw.oscar_body);
        oscarPlayer[2] = MediaPlayer.create(getApplicationContext(), R.raw.oscar_tail);
        oscarPlayer[3] = MediaPlayer.create(getApplicationContext(), R.raw.explore_fulloscar);

        oscarPlayer[3].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                audioRunning = false;
            }
        });

        closeGesture = new CloseGesture(this);
        gestureDetector = new ScaleGestureDetector(this, closeGesture);
        closeGesture.setGestureDetector(gestureDetector);

        inOscar = new Handler();
        findNoFish = new Handler();
        noFishFound = new Runnable() {
            @Override
            public void run() {
                setAurora(22);
                navPlayer[11].start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setAurora(23);
                    }
                }, 2500);
            }
        };

        comError = new Handler();
        comFalse = new Runnable() {
            @Override
            public void run() {
                failed = true;
            }
        };

        cL = (ConstraintLayout) findViewById(R.id.nav_layout);
        aurora = (TextView) findViewById(R.id.aurora_text);
        auroraProfile = (ImageView) findViewById(R.id.aurora);
        auroraDialog = (ImageView) findViewById(R.id.dialog_box);

        aurora.setTypeface(Typo.getInstance().getContent());
        auroraProfile.setVisibility(View.INVISIBLE);
        auroraDialog.setVisibility(View.INVISIBLE);

        beacon = (ImageView) findViewById(R.id.beacon);
        beaconLine = (ImageView) findViewById(R.id.beacon_line);
        beaconLine1 = (ImageView) findViewById(R.id.beacon_line1);
        beaconLine2 = (ImageView) findViewById(R.id.beacon_line2);
        oscarSilhoutte = (ImageView) findViewById(R.id.oscar_silhoutte);
        verticalScroll = (ImageView) findViewById(R.id.verticalscroll);
        oscarExplore = (ImageView) findViewById(R.id.oscar_explore);
        infoSilhoutte = (ImageView) findViewById(R.id.info_silhoutte);
        module = (ImageView) findViewById(R.id.module);
        touchFish = (ImageView) findViewById(R.id.touch_fish);
        bracelet = (ImageView) findViewById(R.id.bracelet);
        verticalUp = (ImageView) findViewById(R.id.vertialup);
        light = (ImageView) findViewById(R.id.light_nav);

        oHead = (ImageView) findViewById(R.id.oscar_head);
        oBody = (ImageView) findViewById(R.id.oscar_body);
        oTail = (ImageView) findViewById(R.id.oscar_tail);

        cL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleTouch(event);
                return true;
            }
        });

        Client.getInstance().setObserver(this);
        Client.getInstance().startConection();

        new ClientConection().execute();
        comError.postDelayed(comFalse, 30000);
        createNotificationChannel();
        animLines();
        lightAnimation();

        auroraIn = ObjectAnimator.ofFloat(aurora, View.ALPHA, 0f, 1f);
        auroraOut = ObjectAnimator.ofFloat(aurora, View.ALPHA, 1f, 0f);
        auroraIn.setDuration(ANIM_TIME);
        auroraOut.setDuration(ANIM_TIME);
        auroraOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                auroraIn.start();
            }
        });
    }

    private void handleTouch(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    pointerY = event.getY(0);
//                    DisplayMetrics dm = new DisplayMetrics();
//                    getWindowManager().getDefaultDisplay().getMetrics(dm);
//                    com.dnkilic.waveform.WaveView waveView = (com.dnkilic.waveform.WaveView) findViewById(R.id.waveview);
//                    waveView.initialize(dm);
//                    waveView.speechStarted();
                    if(failed){
                        new ClientConection().execute();
                        comError.postDelayed(comFalse, 30000);
                        failed = false;
                        setAurora(25);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    pointerCurrentY = event.getY(0);
                    break;
                case MotionEvent.ACTION_UP:
                    float diff = pointerY - pointerCurrentY;
                    if (diff < 0) {
                        if (availableOscar) {
                            navPlayer[5].start();
                            setAurora(5);
                            itemAnimOut(oscarSilhoutte);
                            verticalScroll.setAlpha(1f);
                            itemAnimOut(verticalScroll);
                            oscarExplore.setVisibility(View.VISIBLE);
                            oscarExplore.setAlpha(1f);
                            itemAnimIn(oscarExplore);
                            User.getRef().setFishGesture(OSCAR, true);
                            availableOscar = false;
                        } else if (touch) {
                            navPlayer[13].start();
                            setAurora(14);
                            itemAnimOut(verticalScroll);
                            narrativeAvailable = true;
                            touch = false;
                        }
                    }
                    if (callingPI) {
                        if (diff >= 250) {
                            Client.getInstance().send("haptic");
                            if (!User.getRef().isHapticTutorial()) {
                                //FELICITAR POR EL TUTORIAL
                                (new Handler()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        navPlayer[8].start();
                                        setAurora(8);
                                    }
                                }, 1000);
                                callingPI = false;
                                User.getRef().setHapticTutorial(true);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Client.getInstance().setObserver(this);
        if (User.getRef().isOutApp()) {
            mServ.resumeBackground();
            User.getRef().setOutApp(false);

            if (!steps[0] && !steps[1]) {
                //navPlayer[1].start();
                //setAurora(0);
            }

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
                if (mServ != null) mServ.pauseBackground();
                User.getRef().setOutApp(true);
            } else {
                //Cambio de actividad
                doUnbindService();
            }
        }
        super.onPause();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {

            String str = (String) arg;
            if (str.contains("beacon")) {
                User.getRef().setNavTutorial(true);
                if (str.contains("oscar") && !User.getRef().getFishGesture(OSCAR) && !isOscar && steps[0]) {
                    if (User.getRef().isOutApp()) {
                        createNotification("Estas cerca del pez Oscar", "Ingresa a Hictio para aprenderlo a llamar");
                    } else {
                        navPlayer[4].start();
                        itemAnimOut(beacon);
                        itemAnimOut(beaconLine);
                        itemAnimOut(beaconLine1);
                        itemAnimOut(beaconLine2);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setAurora(3);
                                beaconLine.clearAnimation();
                                beaconLine1.clearAnimation();
                                beaconLine2.clearAnimation();
                                beaconLine.setVisibility(View.INVISIBLE);
                                beaconLine1.setVisibility(View.INVISIBLE);
                                beaconLine2.setVisibility(View.INVISIBLE);
                                inOscar.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAurora(4);
                                        availableOscar = true;
                                        verticalScroll.setVisibility(View.VISIBLE);
                                        verticalScroll.setAlpha(1f);
                                        itemAnimIn(verticalScroll);
                                    }
                                }, 2500);
                                oscarSilhoutte.setVisibility(View.VISIBLE);
                                oscarSilhoutte.setAlpha(1f);
                                itemAnimIn(oscarSilhoutte);
                            }
                        });
                    }
                    isOscar = true;
                } else if (str.contains("spot") && !inModule && steps[1]) {
                    navPlayer[9].start();
                    setAurora(9);
                    itemAnimOut(infoSilhoutte);
                    itemAnimOut(verticalUp);
                    module.setAlpha(0f);
                    itemAnimIn(module);
                    inModule = true;
                }
            }

            if (str.contains("active") && !inTouchFish) {
                if (User.getRef().getFishGesture(OSCAR)) {
                    Log.d("NFC", "Mensaje: Llego tarjeta");
                    navPlayer[12].start();
                    setAurora(13);
                    itemAnimOut(bracelet);
                    itemAnimIn(oscarSilhoutte);
                    itemAnimIn(verticalScroll);
                    findNoFish.removeCallbacks(noFishFound);
                    inTouchFish = true;
                }
            } else if (str.contains("oscar")) {
                String[] strOscar = str.split("_");
                if (narrativeAvailable) {
                    releasePlayer(1);
                    if (strOscar[1].contains("head") && !audioRunning) {
                        Log.d("OscarPlayer", "Audio 1");
                        audioRunning = true;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                oHead.setVisibility(View.VISIBLE);
                                setAurora(15);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAurora(16);
                                    }
                                }, 6500);
                            }
                        });

                        beforeSpeech.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                oscarBase.start();
                            }
                        });
                        beforeSpeech.start();
                        oscarBase.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                oscarPlayer[0].start();
                            }
                        });

                        oscarPlayer[0].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                audioRunning = false;
                                User.getRef().setFishes(OSCAR, 0, true);
                                if (User.getRef().getFishState(OSCAR) && !audioRunning && !unlocked) {
                                    lightRot.start();
                                    light.setAlpha(1f);
                                    lightAlpha.start();
                                    audioRunning = true;
                                    oscarPlayer[3].start();
                                    setAurora(20);
                                    unlocked = true;
                                }
                            }
                        });
                    } else if (strOscar[1].contains("body") && !audioRunning) {
                        Log.d("OscarPlayer", "Audio 2");
                        audioRunning = true;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                oBody.setVisibility(View.VISIBLE);
                                setAurora(17);
                            }
                        });

                        beforeSpeech.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                oscarBase.start();
                            }
                        });
                        beforeSpeech.start();
                        oscarBase.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                oscarPlayer[1].start();
                            }
                        });

                        oscarPlayer[1].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                audioRunning = false;
                                User.getRef().setFishes(OSCAR, 1, true);
                                if (User.getRef().getFishState(OSCAR) && !audioRunning) {
                                    lightRot.start();
                                    light.setAlpha(1f);
                                    lightAlpha.start();
                                    audioRunning = true;
                                    oscarPlayer[3].start();
                                    setAurora(20);
                                    unlocked = true;
                                }
                            }
                        });
                    } else if (strOscar[1].contains("tail") && !audioRunning) {
                        Log.d("OscarPlayer", "Audio 2");
                        audioRunning = true;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                oTail.setVisibility(View.VISIBLE);
                                setAurora(18);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAurora(19);
                                    }
                                }, 5700);
                            }
                        });

                        beforeSpeech.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                oscarBase.start();
                            }
                        });
                        beforeSpeech.start();

                        oscarBase.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                oscarPlayer[2].start();
                            }
                        });

                        oscarPlayer[2].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                audioRunning = false;
                                User.getRef().setFishes(OSCAR, 2, true);

                                if (User.getRef().getFishState(OSCAR) && !audioRunning && !unlocked) {
                                    lightRot.start();
                                    light.setAlpha(1f);
                                    lightAlpha.start();
                                    audioRunning = true;
                                    oscarPlayer[3].start();
                                    setAurora(20);
                                    unlocked = true;
                                }
                            }
                        });
                    }

                }
            }

            if (str.contains("acuario")) {
                if (User.getRef().isOutApp())
                    createNotification("Estas acercándote al Acuario", "Ingresa a Hictio para explorar las especies del lugar");
            }

            if (str.contains("x")) {
                navPlayer[14].start();
                setAurora(21);
            }

            Log.d("Cliente_Navegacion", "Mensaje: " + str);
        }
    }

    @Override
    protected void onDestroy() {
        //mServ.stopBackground();
        stopService(new Intent(this, BackgroundMusic.class));
        mServ = null;
        super.onDestroy();
    }

    private void setAurora(final int num) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                auroraOut.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        aurora.setText(auroraTxt[num]);
                    }
                }, ANIM_TIME);
            }
        });
    }

    private void animLines() {
        //Anim Line 1
        final ScaleAnimation beaconLineAnim = new ScaleAnimation(0.7f, 1f, 0.7f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        beaconLineAnim.setDuration(BEACON_TIME);
        beaconLineAnim.setFillAfter(true);
        beaconLineAnim.setRepeatMode(Animation.RESTART);
        beaconLineAnim.setRepeatCount(Animation.INFINITE);
        final ObjectAnimator beaconLineAlphaIn = ObjectAnimator.ofFloat(beaconLine, View.ALPHA, 0f, 1f);
        final ObjectAnimator beaconLineAlphaOut = ObjectAnimator.ofFloat(beaconLine, View.ALPHA, 1f, 0f);
        beaconLineAlphaIn.setDuration((BEACON_TIME / 2));
        beaconLineAlphaOut.setDuration((BEACON_TIME / 2));
        beaconLineAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                beaconLineAlphaIn.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                beaconLineAlphaIn.start();
            }
        });

        beaconLineAlphaIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                beaconLineAlphaOut.start();
            }
        });

        beaconLine.startAnimation(beaconLineAnim);

        //Anim line 2
        final ScaleAnimation beaconLineAnim1 = new ScaleAnimation(0.7f, 1f, 0.7f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        beaconLineAnim1.setDuration(BEACON_TIME);
        beaconLineAnim1.setFillAfter(true);
        beaconLineAnim1.setRepeatMode(Animation.RESTART);
        beaconLineAnim1.setRepeatCount(Animation.INFINITE);
        final ObjectAnimator beaconLineAlphaIn1 = ObjectAnimator.ofFloat(beaconLine1, View.ALPHA, 0f, 1f);
        final ObjectAnimator beaconLineAlphaOut1 = ObjectAnimator.ofFloat(beaconLine1, View.ALPHA, 1f, 0f);
        beaconLineAlphaIn1.setDuration((BEACON_TIME / 2));
        beaconLineAlphaOut1.setDuration((BEACON_TIME / 2));
        beaconLineAnim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                beaconLineAlphaIn1.start();
                introAurora();
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                beaconLineAlphaIn1.start();
            }
        });

        beaconLineAlphaIn1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                beaconLineAlphaOut1.start();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                beaconLine1.startAnimation(beaconLineAnim1);
            }
        }, 500);

        //Anim line 3
        final ScaleAnimation beaconLineAnim2 = new ScaleAnimation(0.7f, 1f, 0.7f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        beaconLineAnim2.setDuration(BEACON_TIME);
        beaconLineAnim2.setFillAfter(true);
        beaconLineAnim2.setRepeatMode(Animation.RESTART);
        beaconLineAnim2.setRepeatCount(Animation.INFINITE);
        final ObjectAnimator beaconLineAlphaIn2 = ObjectAnimator.ofFloat(beaconLine2, View.ALPHA, 0f, 1f);
        final ObjectAnimator beaconLineAlphaOut2 = ObjectAnimator.ofFloat(beaconLine2, View.ALPHA, 1f, 0f);
        beaconLineAlphaIn2.setDuration((BEACON_TIME / 2));
        beaconLineAlphaOut2.setDuration((BEACON_TIME / 2));
        beaconLineAnim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                beaconLineAlphaIn2.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                beaconLineAlphaIn2.start();
            }
        });

        beaconLineAlphaIn2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                beaconLineAlphaOut2.start();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                beaconLine2.startAnimation(beaconLineAnim2);
            }
        }, 1000);
    }

    private void introAurora() {
        aurora.setText("Buscando exhibiciones...");
        auroraProfile.setVisibility(View.VISIBLE);
        auroraDialog.setVisibility(View.VISIBLE);
        aurora.setAlpha(0f);
        auroraProfile.setAlpha(0f);
        auroraDialog.setAlpha(0f);

        textAnimIn(aurora);
        imageAnimIn(auroraProfile);
        imageAnimIn(auroraDialog);
    }

    private void lightAnimation(){
        lightRot = ObjectAnimator.ofFloat(light, "rotation", 0f, 360f);
        lightRot.setDuration(LIGHT_ANIM);
        lightRot.setInterpolator(new LinearInterpolator());
        lightRot.setRepeatMode(ValueAnimator.RESTART);
        lightRot.setRepeatCount(ValueAnimator.INFINITE);
        lightAlpha = ObjectAnimator.ofFloat(light, View.ALPHA, 0f, 1f);
        lightAlpha.setDuration(ANIM_TIME);
    }

    private void textAnimIn(TextView txt) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(txt, View.ALPHA, 0f, 1f);
        anim.setDuration(ANIM_TIME);
        anim.start();
    }

    private void imageAnimIn(final ImageView img) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator anim = ObjectAnimator.ofFloat(img, View.ALPHA, 0f, 1f);
                anim.setDuration(ANIM_TIME);
                anim.start();
            }
        });
    }

    private void itemAnimIn(final ImageView img) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itemIn = ObjectAnimator.ofFloat(img, View.ALPHA, 0f, 1f);
                itemIn.setDuration(ANIM_TIME);
                itemIn.start();
            }
        });
    }

    private void itemAnimOut(final ImageView img) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itemOut = ObjectAnimator.ofFloat(img, View.ALPHA, 1f, 0f);
                itemOut.setDuration(ANIM_TIME);
                itemOut.start();
            }
        });

    }

    private void releasePlayer(int num) {
        if(num == 0) {
            if (navPlayer[0] != null) {
                for (int i = 0; i < 8; i++) {
                    navPlayer[i].release();
                    navPlayer[i] = null;
                }
            }
        } else {
            if(navPlayer[8] != null) {
                for (int i = 8; i < 13; i++) {
                    navPlayer[i].release();
                    navPlayer[i] = null;
                }
            }
        }
    }

    private class ClientConection extends AsyncTask<Void, Void, Void> {

        private boolean connected = false;
        private boolean life = true;

        @Override
        protected void onPreExecute() {
            //progress = ProgressDialog.show(ExploreActivity.this, "Conectando...", "Acércate a una exhibición para comenzar la experiencia");
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (life) {
                if (Client.getInstance().isConnected()) {
                    connected = true;
                    life = false;
                } else if(failed){
                    connected = false;
                    life = false;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!connected) {
                navPlayer[0].start();
                setAurora(24);
            } else {
                if (!User.getRef().isOutApp()) navPlayer[1].start();
                setAurora(0);
                comError.removeCallbacks(comFalse);
            }
            //progress.dismiss();
        }
    }

    private void toastPost(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void createNotification(String title, String subtitle) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_directions_boat_black_24dp);
        builder.setContentTitle(title);
        builder.setContentText(subtitle);
        builder.setColor(getResources().getColor(R.color.colorAccentHictio));
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setAutoCancel(true);

        Intent explore = new Intent(this, ExploreActivity.class);

        explore.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, explore, PendingIntent.FLAG_CANCEL_CURRENT));

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

    }

    @Override
    public void onBackPressed() {
        //DoNothingBro
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
