package com.example.camilomontoya.hictio.Misc;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.example.camilomontoya.hictio.R;

public class HictioPlayer {

    public static HictioPlayer ref;
    private MediaPlayer fishPlayer, success;
    private MediaPlayer[] menuPlayer;
    private Context menuContext;

    private HictioPlayer() {
        menuPlayer = new MediaPlayer[5];
    }

    public static HictioPlayer getRef() {
        if (ref == null) {
            ref = new HictioPlayer();
        }
        return ref;
    }

    public void playSample(Context c, String fish, int audio) {
        if (fishPlayer != null) {
            if (!fishPlayer.isPlaying()) {
                fishPlayer.release();
                Uri u = Uri.parse("R.raw." + fish + "_0" + audio);
                fishPlayer = MediaPlayer.create(c, u);
                fishPlayer.start();
            }
        }
    }

    public void playSuccess(Context c) {
        success = MediaPlayer.create(c, R.raw.fine);
        success.start();
    }

    public boolean isPlay() {
        if (fishPlayer != null) {
            return fishPlayer.isPlaying();
        }
        return false;
    }

    public void playMenu(int value) {
        menuPlayer[value].start();
    }

    public void playResumeMenu(final int value){
        menuPlayer[4].start();
        menuPlayer[4].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                menuPlayer[value].start();
            }
        });
    }

    public void setMenuContext(Context c){
        this.menuContext = c;
        menuPlayer[0] = MediaPlayer.create(menuContext, R.raw.navigate);
        menuPlayer[1] = MediaPlayer.create(menuContext, R.raw.album);
        menuPlayer[2] = MediaPlayer.create(menuContext, R.raw.options);
        menuPlayer[3] = MediaPlayer.create(menuContext, R.raw.about);
        menuPlayer[4] = MediaPlayer.create(menuContext, null);
    }

}
