package com.example.camilomontoya.hictio;

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

    private MediaPlayer[] navPlayer, oscarPlayer, oscarLearn;
    private MediaPlayer success, beforeSpeech, touchFish;
    private boolean audioRunning, availableOscar, touch, callingPI;

    private ConstraintLayout cL;
    private TextView aurora;
    private ImageView auroraProfile, auroraDialog;
    private ImageView oscarSilhoutte, verticalScroll, oscarExplore;
    private ImageView infoSilhoutte, verticalUp;
    private ImageView oHead, oBody, oTail;
    private String[] auroraTxt;

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

    private Handler repeatFish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        doBindService();

        steps = new boolean[3];
        auroraTxt = getResources().getStringArray(R.array.aurora_chat);
        navPlayer = new MediaPlayer[5];
        navPlayer[0] = MediaPlayer.create(getApplicationContext(), R.raw.hello_test);
        navPlayer[1] = MediaPlayer.create(getApplicationContext(), R.raw.search_test);
        navPlayer[2] = MediaPlayer.create(getApplicationContext(), R.raw.tospot);
        navPlayer[3] = MediaPlayer.create(getApplicationContext(), R.raw.inspot);
        navPlayer[4] = MediaPlayer.create(getApplicationContext(), R.raw.tutorialspot);

        navPlayer[0].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                navPlayer[1].start();
                aurora.setText(auroraTxt[1]);
            }
        });

        navPlayer[1].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                aurora.setText(auroraTxt[2]);
            }
        });

        navPlayer[2].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                aurora.setText(auroraTxt[7]);
                verticalUp.setVisibility(View.VISIBLE);
            }
        });

        navPlayer[3].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                aurora.setText(auroraTxt[10]);
            }
        });

        oscarPlayer = new MediaPlayer[3];
        oscarLearn = new MediaPlayer[5];
        oscarPlayer[0] = MediaPlayer.create(getApplicationContext(), R.raw.oscar_01);
        oscarPlayer[1] = MediaPlayer.create(getApplicationContext(), R.raw.oscar_02);
        oscarPlayer[2] = MediaPlayer.create(getApplicationContext(), R.raw.oscar_03);
        oscarLearn[0] = MediaPlayer.create(getApplicationContext(), R.raw.oscar_fine);
        oscarLearn[1] = MediaPlayer.create(getApplicationContext(), R.raw.oscar_call);
        oscarLearn[2] = MediaPlayer.create(getApplicationContext(), R.raw.inoscar);
        oscarLearn[3] = MediaPlayer.create(getApplicationContext(), R.raw.oscar_before);
        oscarLearn[4] = MediaPlayer.create(getApplicationContext(), R.raw.addedtoalbum);

        oscarLearn[0].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                aurora.setText(auroraTxt[6]);
                navPlayer[2].start();
                oscarExplore.setVisibility(View.INVISIBLE);
                infoSilhoutte.setVisibility(View.VISIBLE);
                callingPI = true;
            }
        });

        oscarLearn[2].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                oscarLearn[3].start();
                aurora.setText(auroraTxt[4]);
                availableOscar = true;
                verticalScroll.setVisibility(View.VISIBLE);
            }
        });

        oscarLearn[3].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                touch = true;
            }
        });

        closeGesture = new CloseGesture(this);
        gestureDetector = new ScaleGestureDetector(this, closeGesture);
        closeGesture.setGestureDetector(gestureDetector);

        cL = (ConstraintLayout) findViewById(R.id.nav_layout);
        aurora = (TextView) findViewById(R.id.aurora_text);
        auroraProfile = (ImageView) findViewById(R.id.aurora);
        auroraDialog = (ImageView) findViewById(R.id.dialog_box);

        aurora.setTypeface(Typo.getInstance().getContent());
        auroraProfile.setVisibility(View.INVISIBLE);
        auroraDialog.setVisibility(View.INVISIBLE);

        oscarSilhoutte = (ImageView) findViewById(R.id.oscar_silhoutte);
        verticalScroll = (ImageView) findViewById(R.id.verticalscroll);

        oscarSilhoutte.setVisibility(View.INVISIBLE);
        verticalScroll.setVisibility(View.INVISIBLE);

        oscarExplore = (ImageView) findViewById(R.id.oscar_explore);

        oscarExplore.setVisibility(View.INVISIBLE);

        infoSilhoutte = (ImageView) findViewById(R.id.info_silhoutte);
        verticalUp = (ImageView) findViewById(R.id.vertialup);

        infoSilhoutte.setVisibility(View.INVISIBLE);
        verticalUp.setVisibility(View.INVISIBLE);

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
        createNotificationChannel();
    }

    private void handleTouch(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    pointerY = event.getY(0);
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    com.dnkilic.waveform.WaveView waveView = (com.dnkilic.waveform.WaveView) findViewById(R.id.waveview);
                    waveView.initialize(dm);
                    waveView.speechStarted();
                    break;
                case MotionEvent.ACTION_MOVE:
                    pointerCurrentY = event.getY(0);
                    break;
                case MotionEvent.ACTION_UP:
                    float diff = pointerY - pointerCurrentY;
                    if(diff < 0){
                        if(availableOscar){
                            oscarLearn[0].start();
                            aurora.setText(auroraTxt[5]);
                            verticalScroll.setVisibility(View.INVISIBLE);
                            oscarSilhoutte.setVisibility(View.INVISIBLE);
                            oscarExplore.setVisibility(View.VISIBLE);
                            availableOscar = false;
                        } else if(touch){
                            aurora.setText(auroraTxt[14]);
                            touch = false;
                        }
                    }
                    if(callingPI) {
                        if (diff >= 250) {
                            Client.getInstance().send("haptic");
                            if (!User.getRef().isHapticTutorial()) {
                                //FELICITAR POR EL TUTORIAL
                                (new Handler()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        navPlayer[4].start();
                                        aurora.setText(auroraTxt[8]);
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
        } else {
            if (User.getRef().getFishState(OSCAR) && User.getRef().getFishState(PIRANHA)) {
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
                navPlayer[0].start();
                aurora.setText(auroraTxt[0]);
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
                if (str.contains("oscar") && !User.getRef().getFishGesture(OSCAR)) {
                    if (User.getRef().isOutApp()) {
                        createNotification("Estas cerca del pez Oscar", "Ingresa a Hictio para aprenderlo a llamar");
                    } else {
                        oscarLearn[2].start();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                aurora.setText(auroraTxt[3]);
                                oscarSilhoutte.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    ;
                } else if (str.contains("piranha") && !User.getRef().getFishGesture(PIRANHA)) {

                } else if (str.contains("spot")) {
                    navPlayer[3].start();
                    aurora.setText(auroraTxt[9]);
                    infoSilhoutte.setImageResource(R.drawable.modulo);
                    infoSilhoutte.setVisibility(View.VISIBLE);
                }
            }

            if (str.contains("oscar-piranha")) {
                if (User.getRef().getFishGesture(OSCAR)) {
                    Log.d("NFC", "Mensaje: Llego tarjeta");
                    oscarLearn[2].start();
                    aurora.setText(auroraTxt[13]);
                    infoSilhoutte.setVisibility(View.INVISIBLE);
                    oscarSilhoutte.setVisibility(View.VISIBLE);
                    verticalScroll.setVisibility(View.VISIBLE);
                }
            } else if(str.contains("oscar")){
                String[] strOscar = str.split("-");
                if (touch && !strOscar[1].contains("piranha")) {
                    verticalScroll.setVisibility(View.INVISIBLE);
                    if (strOscar[1].contains("w") && !audioRunning) {
                        Log.d("OscarPlayer", "Audio 1");
                        audioRunning = true;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                oHead.setVisibility(View.VISIBLE);
                                aurora.setText(auroraTxt[15]);
                            }
                        });

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
                                User.getRef().setFishes(OSCAR, 0, true);
                                oscarPlayer[0].release();
                                if(User.getRef().getFishState(OSCAR) && !audioRunning){
                                    audioRunning = true;
                                    oscarLearn[4].start();
                                    aurora.setText(auroraTxt[20]);
                                }
                            }
                        });
                    } else if(strOscar[1].contains("a") && !audioRunning){
                        Log.d("OscarPlayer", "Audio 2");
                        audioRunning = true;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                oBody.setVisibility(View.VISIBLE);
                                aurora.setText(auroraTxt[16]);
                            }
                        });

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
                                User.getRef().setFishes(OSCAR, 1, true);
                                aurora.setText(auroraTxt[17]);
                                oscarPlayer[1].release();
                                if(User.getRef().getFishState(OSCAR) && !audioRunning){
                                    audioRunning = true;
                                    oscarLearn[4].start();
                                    aurora.setText(auroraTxt[20]);
                                }
                            }
                        });
                    } else if(strOscar[1].contains("s") && !audioRunning){
                        Log.d("OscarPlayer", "Audio 2");
                        audioRunning = true;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                oTail.setVisibility(View.VISIBLE);
                                aurora.setText(auroraTxt[18]);
                            }
                        });

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
                                User.getRef().setFishes(OSCAR, 2, true);
                                aurora.setText(auroraTxt[19]);
                                oscarPlayer[2].release();
                                if(User.getRef().getFishState(OSCAR) && !audioRunning){
                                    audioRunning = true;
                                    oscarLearn[4].start();
                                    aurora.setText(auroraTxt[20]);
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

            if(str.contains("x")){

                aurora.setText(auroraTxt[21]);
            }

            Log.d("Cliente_Navegacion", "Mensaje: " + str);
        }
    }

    @Override
    protected void onDestroy() {
        releaseNavigate();
        mServ.stopBackground();
        stopService(new Intent(this, BackgroundMusic.class));
        mServ = null;
        super.onDestroy();
    }

    public void repeat() {
        navPlayer[currentAudio].start();
    }

    private void releaseNavigate() {
    }

    private class ClientConection extends AsyncTask<Void, Void, Void> {

        private boolean connected = false;
        private boolean life = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ExploreActivity.this, "Conectando...", "Acércate a una exhibición para comenzar la experiencia");
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (life) {
                if (Client.getInstance().isConnected()) {
                    connected = true;
                    life = false;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!connected) {
                toastPost("Conexion fallida");
            } else {
                toastPost("Conexion completa");
                auroraProfile.setVisibility(View.VISIBLE);
                auroraDialog.setVisibility(View.VISIBLE);
                if (!User.getRef().isOutApp()) navPlayer[0].start();
                aurora.setText(auroraTxt[0]);
            }
            progress.dismiss();
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
