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

import com.example.camilomontoya.hictio.Misc.HictioPlayer;
import com.example.camilomontoya.hictio.Misc.MenuFragment;
import com.example.camilomontoya.hictio.Misc.SlideAdapter;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private SliderPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        HictioPlayer.getRef().setMenuContext(this);
        HictioPlayer.getRef().playResumeMenu(0);

        viewPager = (ViewPager) findViewById(R.id.menuPager);
        final List<Fragment> fragments = new ArrayList<>();
        fragments.add(MenuFragment.newInstance(getResources().getString(R.string.title_navigate), 0));
        fragments.add(MenuFragment.newInstance(getResources().getString(R.string.title_album), 1));
        fragments.add(MenuFragment.newInstance(getResources().getString(R.string.title_options), 2));
        fragments.add(MenuFragment.newInstance(getResources().getString(R.string.title_about), 3));
        adapter = new SliderPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(fragments.size());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int index = position%fragments.size();
                switch (index){
                    case 0:
                        Log.d("Menu", "Navigation");
                        HictioPlayer.getRef().playMenu(0);
                        break;
                    case 1:
                        Log.d("Menu", "Album");
                        HictioPlayer.getRef().playMenu(1);
                        break;
                    case 2:
                        Log.d("Menu", "Collection");
                        HictioPlayer.getRef().playMenu(2);
                        break;
                    case 3:
                        Log.d("Menu", "About");
                        HictioPlayer.getRef().playMenu(3);
                        break;
                    default:
                        Log.d("Menu", "Default");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        HictioPlayer.getRef().playResumeMenu(viewPager.getCurrentItem()%4);

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
