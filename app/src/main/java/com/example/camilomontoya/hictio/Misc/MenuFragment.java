package com.example.camilomontoya.hictio.Misc;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.camilomontoya.hictio.AboutActivity;
import com.example.camilomontoya.hictio.PoolActivity;
import com.example.camilomontoya.hictio.ExploreActivity;
import com.example.camilomontoya.hictio.OptionsActivity;
import com.example.camilomontoya.hictio.R;

import java.util.ArrayList;

public class MenuFragment extends Fragment {

    private static final String PARAM1 = "Titulo";
    private static final String PARAM2 = "Tipo";
    private static final String PARAM3 = "Descripcion";
    private static final String PARAM4 = "Icon";
    private String titulo, descripcion;
    private int type, icon;
    private int k;
    private Handler handler;
    private Runnable oneTap;

    private MediaPlayer[] menuPlayer;

    private ArrayList activityList;

    public MenuFragment() {

        activityList = new ArrayList<>();
        activityList.add(ExploreActivity.class);
        activityList.add(PoolActivity.class);
        activityList.add(OptionsActivity.class);
        activityList.add(AboutActivity.class);
    }

    public static MenuFragment newInstance(String titulo, int type, String des, int drawable) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(PARAM1, titulo);
        args.putInt(PARAM2, type);
        args.putString(PARAM3, des);
        args.putInt(PARAM4, drawable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            titulo = getArguments().getString(PARAM1);
            type = getArguments().getInt(PARAM2);
            descripcion = getArguments().getString(PARAM3);
            icon = getArguments().getInt(PARAM4);

            menuPlayer = new MediaPlayer[4];
            menuPlayer[0] = MediaPlayer.create(getContext(), R.raw.navigate);
            menuPlayer[1] = MediaPlayer.create(getContext(), R.raw.album);
            menuPlayer[2] = MediaPlayer.create(getContext(), R.raw.options);
            menuPlayer[3] = MediaPlayer.create(getContext(), R.raw.about);

            handler = new Handler();
            oneTap = new Runnable() {
                @Override
                public void run() {
                    k = 0;
                    menuPlayer[type].start();
                }
            };
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.menu_slide, container, false);
        ConstraintLayout layout = (ConstraintLayout) v.findViewById(R.id.menu_slide_layout);
        TextView txt = (TextView) v.findViewById(R.id.menu_title);
        txt.setText(titulo);
        txt.setTypeface(Typo.getInstance().getTitle());
        TextView description = (TextView) v.findViewById(R.id.menu_description);
        description.setText(descripcion);
        description.setTypeface(Typo.getInstance().getContent());

        ImageView menuIcon = (ImageView) v.findViewById(R.id.menu_icon);
        menuIcon.setImageResource(icon);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                k++;
                if (k == 1) {
                    handler.postDelayed(oneTap, 350);
                } else if(k >= 2){
                    handler.removeCallbacks(oneTap);
                    startActivity(new Intent(getContext(), (Class) activityList.get(type)));
                    k = 0;
                }
            }
        });

        return v;
    }

    public String getTitulo() {
        return titulo;
    }
}
