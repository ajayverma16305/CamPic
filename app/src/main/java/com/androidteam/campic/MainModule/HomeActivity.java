package com.androidteam.campic.MainModule;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import com.androidteam.campic.MainModule.adapters.ViewPagerAdapter;
import com.androidteam.campic.MainModule.fragments.AppInfoFragment;
import com.androidteam.campic.MainModule.fragments.CameraFragment;
import com.androidteam.campic.MainModule.fragments.GalleryFragment;
import com.androidteam.campic.R;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        viewPager = (ViewPager) findViewById(R.id.mainViewPager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment(), "Gallery");
        adapter.addFragment(new CameraFragment(), "Camera");
        adapter.addFragment(new AppInfoFragment(), "Info");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
