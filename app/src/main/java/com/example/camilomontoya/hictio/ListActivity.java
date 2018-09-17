package com.example.camilomontoya.hictio;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ListView fishList;
    private ArrayList activityList, activityNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        fishList = (ListView) findViewById(R.id.fishList);
        activityList = new ArrayList<>();
        activityNames = new ArrayList<>();

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
                startActivity(new Intent(ListActivity.this, (Class) activityList.get(position)));
            }
        });

    }
}
