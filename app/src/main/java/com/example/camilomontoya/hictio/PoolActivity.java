package com.example.camilomontoya.hictio;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.camilomontoya.hictio.Fishes.BocachicoActivity;
import com.example.camilomontoya.hictio.Fishes.DiscoActivity;
import com.example.camilomontoya.hictio.Fishes.FantasmaActivity;
import com.example.camilomontoya.hictio.Fishes.LeporinoActivity;
import com.example.camilomontoya.hictio.Fishes.MojarraActivity;
import com.example.camilomontoya.hictio.Fishes.MonedaActivity;
import com.example.camilomontoya.hictio.Fishes.OscarActivity;
import com.example.camilomontoya.hictio.Fishes.PiranhaActivity;
import com.example.camilomontoya.hictio.Fishes.RollizoActivity;
import com.example.camilomontoya.hictio.Fishes.SapoaraActivity;
import com.example.camilomontoya.hictio.Misc.FishFragment;
import com.example.camilomontoya.hictio.Network.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class PoolActivity extends AppCompatActivity implements Observer {

    private ListView fishList;
    private ArrayList activityList, activityNames;

    private ViewPager poolPager;
    private SliderPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool);

        fishList = (ListView) findViewById(R.id.fishList);
        activityList = new ArrayList<>();
        activityNames = new ArrayList<>();

        //Iniciar Conexion
        Client.getInstance().setObserver(this);
        Client.getInstance().startConection();

        poolPager = (ViewPager) findViewById(R.id.poolPager);
        final List<Fragment> fishFrags = new ArrayList<>();
        fishFrags.add(FishFragment.newInstance("Oscar", 0, 0));
        fishFrags.add(FishFragment.newInstance("Piraña", 1, 0));
        fishFrags.add(FishFragment.newInstance("Fantasma\nNegro", 2, 0));
        fishFrags.add(FishFragment.newInstance("Pez Moneda", 3, 0));

        adapter = new SliderPagerAdapter(getSupportFragmentManager(), fishFrags);
        poolPager.setAdapter(adapter);
        poolPager.setCurrentItem(fishFrags.size());

        poolPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Pool","OnResume");
        Client.getInstance().setObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String str = (String) arg;
            if (str.contains("acuario")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Conectado al servidor", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    static class SliderPagerAdapter extends FragmentStatePagerAdapter {

        List<Fragment> mFrags = new ArrayList<>();

        public SliderPagerAdapter(FragmentManager fm, List<Fragment> frags) {
            super(fm);
            mFrags = frags;
        }

        @Override
        public Fragment getItem(int position) {
            int index = position%mFrags.size();
            return mFrags.get(index);
        }

        @Override
        public int getCount() {
            return mFrags.size()*10;
        }

        int getRealCount() {
            return mFrags.size();
        }
    }
}
