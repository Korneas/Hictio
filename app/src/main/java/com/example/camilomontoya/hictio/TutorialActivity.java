package com.example.camilomontoya.hictio;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.camilomontoya.hictio.Misc.Typo;

public class TutorialActivity extends AppCompatActivity {

    private ConstraintLayout cL;
    private ImageView auroraFull, auroraBox, light;
    private TextView aurora;

    private final static long LIGHT_ANIM = 15000;
    private final static long SCALE_ANIM = 3000;
    private final static long ANIM_LONG_TIME = 3000;
    private final static long ANIM_SHORT_TIME = 1200;

    private AnimatorSet animatorSet;
    private int actualTxt;

    private ObjectAnimator textAlphaIn, textAlphaOut;
    private ObjectAnimator animatorRot, lightAlpha, boxAnimatorAlpha, auroraAlpha, auroraAlphaOut;
    private ScaleAnimation auroraScale, auroraScaleOut, boxAnimatorScale;

    private float pointerX, currentPointerX;
    private int k;
    private Handler handler;
    private Runnable oneTap;

    private boolean touch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        cL = (ConstraintLayout) findViewById(R.id.tutorial_layout);
        auroraFull = (ImageView) findViewById(R.id.aurora_full);
        auroraBox = (ImageView) findViewById(R.id.textbox);
        light = (ImageView) findViewById(R.id.light);
        aurora = (TextView) findViewById(R.id.aurora_tutorial_text);

        aurora.setTypeface(Typo.getInstance().getContent());
        final String[] auroraTxt = getResources().getStringArray(R.array.aurora_tutorial);

        cL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleTouch(event);
                return true;
            }
        });

        handler = new Handler();
        oneTap = new Runnable() {
            @Override
            public void run() {
                k = 0;
            }
        };

        //Light Animation

        animatorRot = ObjectAnimator.ofFloat(light, "rotation", 0f, 360f);
        animatorRot.setDuration(LIGHT_ANIM);
        animatorRot.setInterpolator(new LinearInterpolator());
        lightAlpha = ObjectAnimator.ofFloat(light, View.ALPHA, 0f, 1f);
        lightAlpha.setDuration(ANIM_SHORT_TIME);
        animatorSet = new AnimatorSet();
        animatorSet.play(animatorRot).after(0);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorSet.start();
            }
        });

        //Aurora animation

        auroraScale = new ScaleAnimation(0.9f, 1f, 0.9f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        auroraScale.setFillAfter(true);
        auroraScale.setDuration(SCALE_ANIM);
        auroraAlpha = ObjectAnimator.ofFloat(auroraFull, View.ALPHA, 0f, 1f);
        auroraAlpha.setDuration(ANIM_SHORT_TIME);

        auroraScaleOut = new ScaleAnimation(1f, 0.9f, 1f, 0.9f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        auroraScaleOut.setFillAfter(true);
        auroraScaleOut.setDuration(SCALE_ANIM);
        auroraAlphaOut = ObjectAnimator.ofFloat(auroraFull, View.ALPHA, 1f, 0f);
        auroraAlphaOut.setDuration(ANIM_SHORT_TIME);

        //Text box Animation

        boxAnimatorAlpha = ObjectAnimator.ofFloat(auroraBox, View.ALPHA, 0f, 1f);
        boxAnimatorAlpha.setDuration(ANIM_LONG_TIME);
        boxAnimatorAlpha.start();
        boxAnimatorAlpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                textAlphaIn.start();
                auroraAlpha.start();
                auroraFull.startAnimation(auroraScale);
                animatorSet.start();
                lightAlpha.start();
            }
        });

        boxAnimatorScale = new ScaleAnimation(0.95f, 1f, 0.95f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        boxAnimatorScale.setFillAfter(true);
        boxAnimatorScale.setDuration(ANIM_LONG_TIME);
        auroraBox.startAnimation(boxAnimatorScale);

        //Text animation

        actualTxt = 0;
        aurora.setText(auroraTxt[actualTxt]);

        textAlphaIn = ObjectAnimator.ofFloat(aurora, View.ALPHA, 0f, 1f);
        textAlphaIn.setDuration(ANIM_SHORT_TIME);

        textAlphaOut = ObjectAnimator.ofFloat(aurora, View.ALPHA, 1f, 0f);
        textAlphaOut.setDuration(ANIM_SHORT_TIME);

        textAlphaOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                actualTxt++;
                aurora.setText(auroraTxt[actualTxt]);
                textAlphaIn.start();
            }
        });

        textAlphaIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                touch = true;
                Log.d("TUTORIAL", "AVAILABLE");
            }
        });
    }

    private void handleTouch(MotionEvent event) {
        if (touch) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    switch (actualTxt) {
                        case 0:
                            auroraScaleOut.start();
                            auroraAlphaOut.start();
                            textAlphaOut.start();
                            touch = false;
                            break;
                        case 1:
                            textAlphaOut.start();
                            touch = false;
                            break;
                        case 2:
                            pointerX = event.getX();
                            break;
                        case 3:
                            textAlphaOut.start();
                            touch = false;
                            break;
                        case 4:
                            k++;
                            if (k == 1) {
                                handler.postDelayed(oneTap, 350);
                            } else if (k >= 2) {
                                handler.removeCallbacks(oneTap);
                                Intent i = new Intent(TutorialActivity.this, MenuActivity.class);
                                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                //overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                                k = 0;
                                touch = false;
                                finish();
                            }
                            break;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    switch (actualTxt) {
                        case 2:
                            currentPointerX = event.getX();
                            break;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    float diff = pointerX - currentPointerX;
                    if (actualTxt == 2 && (diff > 250 || diff < -250)) {
                        Log.d("TUTORIAL", "HUMMMM YES " + diff);
                        textAlphaOut.start();
                        touch = false;
                    }
                    break;
            }
        }
    }


}
