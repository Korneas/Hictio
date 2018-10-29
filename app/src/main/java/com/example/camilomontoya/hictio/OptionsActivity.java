package com.example.camilomontoya.hictio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camilomontoya.hictio.Misc.OptionsFragment;
import com.example.camilomontoya.hictio.Misc.Typo;
import com.example.camilomontoya.hictio.Misc.User;

import java.util.ArrayList;
import java.util.List;

public class OptionsActivity extends AppCompatActivity {

    private ViewPager optionsPager;
    private SliderPagerAdapter adapter;

    private TextView title, uidOptions;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        title = (TextView) findViewById(R.id.title_options);
        uidOptions = (TextView) findViewById(R.id.uidOptions);
        //logout = (Button) findViewById(R.id.logout);

        title.setTypeface(Typo.getInstance().getTitle());
        uidOptions.setTypeface(Typo.getInstance().getContent());
        uidOptions.setText("UID: " + User.getUID());

        optionsPager = (ViewPager) findViewById(R.id.optionsPager);
        final List<Fragment> optionsFrags = new ArrayList<>();
        optionsFrags.add(OptionsFragment.newInstance(getResources().getString(R.string.title_volume_music), 0));
        optionsFrags.add(OptionsFragment.newInstance(getResources().getString(R.string.title_volume_guide), 1));
        optionsFrags.add(OptionsFragment.newInstance(getResources().getString(R.string.title_voice_guide), 2));
        optionsFrags.add(OptionsFragment.newInstance(getResources().getString(R.string.title_logout), 3));
        adapter = new SliderPagerAdapter(getSupportFragmentManager(), optionsFrags);
        optionsPager.setAdapter(adapter);

        Log.d("Pager", "Total array: " + optionsPager.getAdapter().getCount());

        optionsPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int index = position % optionsFrags.size();
                switch (index) {
                    case 0:
                        Log.d("Options", "Volume Music");
                        break;
                    case 1:
                        Log.d("Options", "Volume Guide");
                        break;
                    case 2:
                        Log.d("Options", "Voice");
                        break;
                    case 3:
                        Log.d("Options", "Logout");
                        break;
                    default:
                        Log.d("Options", "Default");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        /*logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.clear();
                editor.apply();

                Toast.makeText(getApplicationContext(), "Cerraste sesión", Toast.LENGTH_SHORT).show();
                User.setUID("");
                startActivity(new Intent(OptionsActivity.this, RegisterActivity.class));
            }
        });*/
    }

    static class SliderPagerAdapter extends FragmentStatePagerAdapter {

        List<Fragment> mFrags = new ArrayList<>();

        public SliderPagerAdapter(FragmentManager fm, List<Fragment> frags) {
            super(fm);
            mFrags = frags;
        }

        @Override
        public Fragment getItem(int position) {
            int index = position % mFrags.size();
            return mFrags.get(index);
        }

        @Override
        public int getCount() {
            return mFrags.size() * 10;
        }

        int getRealCount() {
            return mFrags.size();
        }
    }
}
