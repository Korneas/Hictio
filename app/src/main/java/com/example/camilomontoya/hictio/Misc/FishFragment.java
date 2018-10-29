package com.example.camilomontoya.hictio.Misc;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.camilomontoya.hictio.Fishes.FantasmaActivity;
import com.example.camilomontoya.hictio.Fishes.MonedaActivity;
import com.example.camilomontoya.hictio.Fishes.OscarActivity;
import com.example.camilomontoya.hictio.Fishes.PiranhaActivity;
import com.example.camilomontoya.hictio.R;

import java.util.ArrayList;

public class FishFragment extends Fragment {

    private static final String PARAM1 = "Titulo";
    private static final String PARAM2 = "Tipo";
    private static final String PARAM3 = "Ilustracion";
    private String title;
    private int type, draw;
    private MediaPlayer[] poolPlayer;

    private ArrayList activityList;

    private Handler handler;
    private Runnable oneTap;
    private int k;

    private ScaleGestureDetector gestureDetector;
    private CloseGesture closeGesture;

    public FishFragment() {
        activityList = new ArrayList<>();
        activityList.add(OscarActivity.class);
        activityList.add(PiranhaActivity.class);
        activityList.add(FantasmaActivity.class);
        activityList.add(MonedaActivity.class);
    }

    public static FishFragment newInstance(String title, int type, int draw) {
        FishFragment fragment = new FishFragment();
        Bundle args = new Bundle();
        args.putString(PARAM1, title);
        args.putInt(PARAM2, type);
        args.putInt(PARAM3, draw);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(PARAM1);
            type = getArguments().getInt(PARAM2);
            draw = getArguments().getInt(PARAM3);

            poolPlayer = new MediaPlayer[4];
            poolPlayer[0] = MediaPlayer.create(getContext(), R.raw.oscar_name);
            poolPlayer[1] = MediaPlayer.create(getContext(), R.raw.piranha_name);
            poolPlayer[2] = MediaPlayer.create(getContext(), R.raw.blackghost_name);
            poolPlayer[3] = MediaPlayer.create(getContext(), R.raw.moneda_name);

            handler = new Handler();
            oneTap = new Runnable() {
                @Override
                public void run() {
                    k = 0;
                    poolPlayer[type].start();
                }
            };

            closeGesture = new CloseGesture(getContext());
            gestureDetector = new ScaleGestureDetector(getContext(), closeGesture);
            closeGesture.setGestureDetector(gestureDetector);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fish_slide, container, false);
        ConstraintLayout fishLayout = (ConstraintLayout) v.findViewById(R.id.fishSlideLayout);
        TextView txt = (TextView) v.findViewById(R.id.fishText);
        txt.setText(title);
        txt.setTypeface(Typo.getInstance().getTitle());

        fishLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                k++;
                if (k == 1) {
                    handler.postDelayed(oneTap, 350);
                } else if (k >= 2) {
                    handler.removeCallbacks(oneTap);
                    startActivity(new Intent(getContext(), (Class) activityList.get(type)));
                    k = 0;
                }
                Log.d("FishFragment", "El valor es: " + k);
            }
        });

        fishLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handler.removeCallbacks(oneTap);
                if (event.getPointerCount() >= 2) {
                    gestureDetector.onTouchEvent(event);
                }
                return false;
            }
        });

        return v;
    }

    public String getTitle() {
        return title;
    }
}
