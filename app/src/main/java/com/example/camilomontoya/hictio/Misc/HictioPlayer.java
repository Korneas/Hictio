package com.example.camilomontoya.hictio.Misc;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import com.example.camilomontoya.hictio.R;

public class HictioPlayer {

    public static HictioPlayer ref;
    private MediaPlayer success, beforeSpeech, afterSpeech;
    private MediaPlayer[] menuPlayer, poolPlayer, fishPlayer, closeFish;
    private Context menuContext, poolContext, currentFishContext;
    private Handler handler;
    private Runnable outNarrative;

    private HictioPlayer() {
        menuPlayer = new MediaPlayer[5];
        poolPlayer = new MediaPlayer[4];
        fishPlayer = new MediaPlayer[3];
        closeFish = new MediaPlayer[2];

        handler = new Handler();
    }

    public static HictioPlayer getRef() {
        if (ref == null) {
            ref = new HictioPlayer();
        }
        return ref;
    }

    public void playSample(final int audio) {
        beforeSpeech.start();
        beforeSpeech.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                fishPlayer[audio].start();
            }
        });
        Log.d("HictioPlayer", "Audio de Pez: " + audio);
    }

    public boolean getPlaying(int audio) {
        return fishPlayer[audio].isPlaying();
    }

    public void playSuccess(Context c) {
        success = MediaPlayer.create(c, R.raw.fine);
        success.start();
    }

    public void playMenu(int value) {
        menuPlayer[value].start();
    }

    public void playPool(int value) {
        poolPlayer[value].start();
    }

    public void playResumeMenu(final int value) {
        /*menuPlayer[4].start();
        menuPlayer[4].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                menuPlayer[value].start();
            }
        });*/
        menuPlayer[value].start();
    }

    public void playResumePool(int value) {
        poolPlayer[value].start();
    }

    public void setMenuContext(Context c) {
        this.menuContext = c;
        menuPlayer[0] = MediaPlayer.create(menuContext, R.raw.navigate);
        menuPlayer[1] = MediaPlayer.create(menuContext, R.raw.album);
        menuPlayer[2] = MediaPlayer.create(menuContext, R.raw.options);
        menuPlayer[3] = MediaPlayer.create(menuContext, R.raw.about);
        //menuPlayer[4] = MediaPlayer.create(menuContext, null);
    }

    public void setPoolContext(Context c) {
        this.poolContext = c;
        poolPlayer[0] = MediaPlayer.create(poolContext, R.raw.oscar_name);
        poolPlayer[1] = MediaPlayer.create(poolContext, R.raw.piranha_name);
        poolPlayer[2] = MediaPlayer.create(poolContext, R.raw.blackghost_name);
        poolPlayer[3] = MediaPlayer.create(poolContext, R.raw.moneda_name);
    }

    public void setFishContext(Context c, int fish) {

        if (fishPlayer[2] != null) {
            fishPlayer[0].release();
            fishPlayer[1].release();
            fishPlayer[2].release();
        }

        switch (fish) {
            case 0:
                fishPlayer[0] = MediaPlayer.create(c, R.raw.oscar_01);
                fishPlayer[1] = MediaPlayer.create(c, R.raw.oscar_02);
                fishPlayer[2] = MediaPlayer.create(c, R.raw.oscar_03);
                break;
            case 1:
                fishPlayer[0] = MediaPlayer.create(c, R.raw.piranha_01);
                fishPlayer[1] = MediaPlayer.create(c, R.raw.piranha_02);
                fishPlayer[2] = MediaPlayer.create(c, R.raw.piranha_03);
                break;
            case 2:
                break;
        }

        currentFishContext = c;
        Log.d("HictioPlayer", "Carga de Narrtiva");
    }

    public void setBeforeSpeech(Context c) {
        if (beforeSpeech != null) {
            beforeSpeech.release();
        }

        beforeSpeech = MediaPlayer.create(c, R.raw.speech);
    }

    public void setNavigateContext(Context c){
        closeFish[0] = MediaPlayer.create(c, R.raw.oscar_close);
        closeFish[1] = MediaPlayer.create(c, R.raw.piranha_close);
    }

    public void playNavigateGesture(final int audio){
        closeFish[audio].start();
    }
}
