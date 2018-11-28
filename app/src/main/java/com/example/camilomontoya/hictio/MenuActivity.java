package com.example.camilomontoya.hictio;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.camilomontoya.hictio.Misc.BackgroundMusic;
import com.example.camilomontoya.hictio.Misc.MenuFragment;
import com.example.camilomontoya.hictio.Misc.User;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private SliderPagerAdapter adapter;
    private boolean playInCreate;

    private MediaPlayer[] menuPlayer;

    private boolean mIsBound;
    private BackgroundMusic mServ;
    private ServiceConnection sCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServ = ((BackgroundMusic.ServiceBinder) service).getService();
            Log.d("SERVICE_MUSIC", "CONNECTED");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
            Log.d("SERVICE_MUSIC", "DISCONNECTED");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        doBindService();

        menuPlayer = new MediaPlayer[4];
        menuPlayer[0] = MediaPlayer.create(getApplicationContext(), R.raw.menu_explore);
        menuPlayer[1] = MediaPlayer.create(getApplicationContext(), R.raw.menu_album);
        menuPlayer[2] = MediaPlayer.create(getApplicationContext(), R.raw.menu_options);
        menuPlayer[3] = MediaPlayer.create(getApplicationContext(), R.raw.menu_about);

        viewPager = (ViewPager) findViewById(R.id.menuPager);
        final List<Fragment> fragments = new ArrayList<>();
        fragments.add(MenuFragment.newInstance(getResources().getString(R.string.title_explore), 0, getString(R.string.menu_explore), R.drawable.menu_explorar));
        fragments.add(MenuFragment.newInstance(getResources().getString(R.string.title_album), 1, getString(R.string.menu_album), R.drawable.menu_album));
        fragments.add(MenuFragment.newInstance(getResources().getString(R.string.title_options), 2, getString(R.string.menu_opciones), R.drawable.menu_opciones));
        fragments.add(MenuFragment.newInstance(getResources().getString(R.string.title_about), 3, getString(R.string.menu_about), R.drawable.menu_acerca));
        adapter = new SliderPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(fragments.size()*2);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int index = position % fragments.size();
                switch (index) {
                    case 0:
                        Log.d("Menu", "Navigation");
                        menuPlayer[0].start();
                        break;
                    case 1:
                        Log.d("Menu", "Album");
                        menuPlayer[1].start();
                        break;
                    case 2:
                        Log.d("Menu", "Collection");
                        menuPlayer[2].start();
                        break;
                    case 3:
                        Log.d("Menu", "About");
                        menuPlayer[3].start();
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
        menuPlayer[viewPager.getCurrentItem() % 4].start();
        if (User.getRef().isOutApp()) {
            mServ.resumeBackground();
            User.getRef().setOutApp(false);
        }
    }

    @Override
    protected void onPause() {
        Context c = getApplicationContext();
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfos = am.getRunningTasks(1);
        if (!taskInfos.isEmpty()) {
            ComponentName topActivity = taskInfos.get(0).topActivity;
            if (!topActivity.getPackageName().equals(getApplicationContext().getPackageName())) {
                //Salio de la app
                if(mServ != null) mServ.pauseBackground();
                User.getRef().setOutApp(true);
            } else {
                //Cambio de actividad
                doUnbindService();
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //mServ.stopBackground();
        stopService(new Intent(this, BackgroundMusic.class));
        mServ = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

    }

    void doBindService() {
        bindService(new Intent(getApplicationContext(), BackgroundMusic.class), sCon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        Log.d("BIND_SERVICE", "BIND");
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(sCon);
            mIsBound = false;
            Log.d("BIND_SERVICE", "UNBIND");
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
