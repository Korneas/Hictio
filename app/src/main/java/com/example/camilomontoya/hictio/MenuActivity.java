package com.example.camilomontoya.hictio;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.example.camilomontoya.hictio.Misc.MenuFragment;
import com.example.camilomontoya.hictio.Misc.SlideAdapter;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewGroup frameLayout;
    private SliderPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(MenuFragment.newInstance("Navegar", 0));
        fragments.add(MenuFragment.newInstance("Estanque", 1));
        fragments.add(MenuFragment.newInstance("Opciones", 2));
        fragments.add(MenuFragment.newInstance("Acerca de\nHictio", 3));
        adapter = new SliderPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(fragments.size());

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
