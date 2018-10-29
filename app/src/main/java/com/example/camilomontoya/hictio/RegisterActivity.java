package com.example.camilomontoya.hictio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.camilomontoya.hictio.Misc.BackgroundMusic;
import com.example.camilomontoya.hictio.Misc.User;

public class RegisterActivity extends AppCompatActivity {

    private Button registerBtn;
    private EditText registerID;

    private boolean mIsBound;
    private BackgroundMusic mServ;
    private ServiceConnection sCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundMusic.ServiceBinder binder = (BackgroundMusic.ServiceBinder) service;
            mServ = binder.getService();
            Log.d("SERVICE_MUSIC", "CONNECTED");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
            Log.d("SERVICE_MUSIC", "DISCONNECTED");
        }
    };

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String UID = "UID";

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        doBindService();

        Intent hictio = new Intent();
        hictio.setClass(getApplicationContext(), BackgroundMusic.class);
        startService(hictio);

        registerBtn = (Button) findViewById(R.id.registerBtn);
        registerID = (EditText) findViewById(R.id.uidText);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registerID.getText().toString().length() == 8) {
                    User.getRef().setUID(registerID.getText().toString());
                    saveUserData(registerID.getText().toString());
                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                } else if (registerID.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Escribe tu ID", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "ID no válido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadData();
    }

    private void saveUserData(String id) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(UID, id);

        editor.apply();
        Toast.makeText(getApplicationContext(), "Información guardada", Toast.LENGTH_SHORT).show();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        //registerID.setText(sharedPreferences.getString(UID, ""));

        userId = sharedPreferences.getString(UID, "");
        User.getRef().setActualContext(getApplicationContext());
        User.getRef().refreshFishes();
        if (userId.length() > 0) {
            User.setUID(userId);
            Intent i = new Intent(RegisterActivity.this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            doUnbindService();
            finish();
        }
    }

    void doBindService() {
        Intent hictio = new Intent(this,BackgroundMusic.class);
        bindService(hictio, sCon, Context.BIND_AUTO_CREATE);
        Log.d("BIND_SERVICE", String.valueOf(bindService(hictio, sCon, Context.BIND_AUTO_CREATE)));
        mIsBound = true;
        Log.d("BIND_SERVICE", "BIND");
    }

    void doUnbindService(){
        if(mIsBound){
            unbindService(sCon);
            Log.d("BIND_SERVICE", "UNBIND");
            mIsBound = false;
        }
    }
}
