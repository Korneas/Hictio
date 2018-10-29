package com.example.camilomontoya.hictio.Misc;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.example.camilomontoya.hictio.R;
import com.example.camilomontoya.hictio.RegisterActivity;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class OptionsFragment extends Fragment {

    public static final String SHARED_PREFS = "sharedPrefs";
    private static final String PARAM1 = "Titulo";
    private static final String PARAM2 = "Tipo";
    private static final String PARAM3 = "Ilustracion";
    private String title;
    private int type, draw;
    private MediaPlayer[] optionsPlayer;

    private ArrayList activityList;

    private Handler handler;
    private Runnable oneTap;
    private int k;

    private ScaleGestureDetector gestureDetector;
    private CloseGesture closeGesture;

    public OptionsFragment(){

    }

    public static OptionsFragment newInstance(String title, int type){
        OptionsFragment fragment = new OptionsFragment();
        Bundle args = new Bundle();
        args.putString(PARAM1, title);
        args.putInt(PARAM2, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){

            title = getArguments().getString(PARAM1);
            type = getArguments().getInt(PARAM2);

            handler = new Handler();
            oneTap = new Runnable() {
                @Override
                public void run() {
                    k = 0;
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
        View v = inflater.inflate(R.layout.option_slide, container, false);
        ConstraintLayout layout = (ConstraintLayout) v.findViewById(R.id.optionsLayout);
        TextView txt = (TextView) v.findViewById(R.id.optionsText);
        txt.setTypeface(Typo.getInstance().getTitle());
        txt.setText(title);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                k++;
                if (k == 1) {
                    handler.postDelayed(oneTap, 350);
                } else if (k >= 2) {
                    handler.removeCallbacks(oneTap);
                    //startActivity(new Intent(getContext(), (Class) activityList.get(type)));
                    k = 0;
                    if(type == 3){
                        logout();
                    }
                }
                Log.d("FishFragment", "El valor es: " + k);
            }
        });

        layout.setOnTouchListener(new View.OnTouchListener() {
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

    public String getTitle(){
        return title;
    }

    public void logout(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();

        Toast.makeText(getContext(), "Cerraste sesión", Toast.LENGTH_SHORT).show();
        User.setUID("");
        Intent i = new Intent(getActivity(), RegisterActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        getActivity().finish();

    }

}
