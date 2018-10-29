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
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.example.camilomontoya.hictio.Fishes.OscarActivity;
import com.example.camilomontoya.hictio.Fishes.PiranhaActivity;
import com.example.camilomontoya.hictio.Misc.BackgroundMusic;
import com.example.camilomontoya.hictio.Misc.CloseGesture;
import com.example.camilomontoya.hictio.Misc.User;
import com.example.camilomontoya.hictio.Network.Client;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ExploreActivity extends AppCompatActivity implements Observer {

    private final static String CHANNEL_ID = "Notifications";
    private static final int NOTIFICATION_ID = 0;
    private final static int NO_PATH = -1, OSCAR = 0, PIRANHA = 1;
    private MediaPlayer[] navPlayer;

    private ConstraintLayout cL;

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

    private int currentFish = 10;
    private int currentAudio;
    private int k;
    private ProgressDialog progress;
    private boolean audioPlaying;
    private boolean[] steps;
    private final static String MARK = "MARK";
    private final static int[] MARK_TYPE = {0, 1, 2};

    private Handler repeatFish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        doBindService();

        steps = new boolean[3];
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
                steps[0] = true;
            }
        });

        /*for (int i = 1; i < navPlayer.length; i++) {
            navPlayer[i].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    navPlayer[1].stop();
                    navPlayer[1].release();
                    navPlayer[1] = null;
                }
            });
        }*/

        closeGesture = new CloseGesture(this);
        gestureDetector = new ScaleGestureDetector(this, closeGesture);
        closeGesture.setGestureDetector(gestureDetector);

        cL = (ConstraintLayout) findViewById(R.id.nav_layout);

        cL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //repeate();
            }
        });

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
                    break;
                case MotionEvent.ACTION_MOVE:
                    pointerCurrentY = event.getY(0);
                    break;
                case MotionEvent.ACTION_UP:
                    float diff = pointerY - pointerCurrentY;
                    if (diff >= 30) {
                        Client.getInstance().send("haptic");
                        if (!User.getRef().isHapticTutorial() && User.getRef().getFishGesture(OSCAR)) {
                            //FELICITAR POR EL TUTORIAL
                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    navPlayer[4].start();
                                }
                            }, 1000);
                            User.getRef().setHapticTutorial(true);
                        }
                    }
                    break;
                default:
                    break;
            }
        } else {
            if (User.getRef().getFishState(OSCAR) && User.getRef().getFishState(PIRANHA)) {
                //gestureDetector.onTouchEvent(event);
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
                    currentFish = OSCAR;
                    if (User.getRef().isOutApp()) {
                        createNotification("Estas cerca del pez Oscar", "Ingresa a Hictio para aprenderlo a llamar", OSCAR);
                    } else {
                        Intent fish = new Intent(this, OscarActivity.class);
                        fish.putExtra(MARK, MARK_TYPE[0]);
                        startActivity(fish);
                    }
                    ;
                } else if (str.contains("piranha") && !User.getRef().getFishGesture(PIRANHA)) {

                } else if (str.contains("spot")) {
                    navPlayer[3].start();
                }
            }

            if (str.contains("oscar-piranha")) {
                if (User.getRef().getFishGesture(OSCAR)) {
                    Log.d("NFC", "Mensaje: Llego tarjeta");
                    Intent fish = new Intent(this, OscarActivity.class);
                    fish.putExtra(MARK, MARK_TYPE[1]);
                    startActivity(fish);
                }
            }

            if (str.contains("acuario")) {
                if (User.getRef().isOutApp())
                    createNotification("Estas acercándote al Acuario", "Ingresa a Hictio para explorar las especies del lugar", NO_PATH);
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
                if (!User.getRef().isOutApp()) navPlayer[0].start();
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

    public void createNotification(String title, String subtitle, int path) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_directions_boat_black_24dp);
        builder.setContentTitle(title);
        builder.setContentText(subtitle);
        builder.setColor(getResources().getColor(R.color.colorAccentHictio));
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setAutoCancel(true);

        Intent explore = new Intent(this, ExploreActivity.class);

        switch (path){
            case 0:
                explore = new Intent(this, OscarActivity.class);
                explore.putExtra(MARK, MARK_TYPE[0]);
                break;
        }

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
