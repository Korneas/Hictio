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

        

        //Agregar las clases para los intent al tocar un elemento de la lista
        activityList.add(BocachicoActivity.class);
        activityList.add(DiscoActivity.class);
        activityList.add(FantasmaActivity.class);
        activityList.add(LeporinoActivity.class);
        activityList.add(MojarraActivity.class);
        activityList.add(MonedaActivity.class);
        activityList.add(OscarActivity.class);
        activityList.add(PiranhaActivity.class);
        activityList.add(RollizoActivity.class);
        activityList.add(SapoaraActivity.class);

        //Agregar los nombres al seleccionarlos
        activityNames.add("Bocachico");
        activityNames.add("Pez Disco");
        activityNames.add("Fantasma Negro");
        activityNames.add("Leporino Listado");
        activityNames.add("Mojarra Luminosa");
        activityNames.add("Pez Moneda");
        activityNames.add("Oscar");
        activityNames.add("Piraña");
        activityNames.add("Rollizo");
        activityNames.add("Sapoara");


        ArrayAdapter adapt = new ArrayAdapter(this, R.layout.list_item, activityNames);
        fishList.setAdapter(adapt);

        fishList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Elegiste al item: " + activityNames.get(position).toString(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PoolActivity.this, (Class) activityList.get(position)));
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
