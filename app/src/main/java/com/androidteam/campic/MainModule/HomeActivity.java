package com.androidteam.campic.MainModule;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidteam.campic.MainModule.adapters.ViewPagerAdapter;
import com.androidteam.campic.MainModule.fragments.AppInfoFragment;
import com.androidteam.campic.MainModule.fragments.CameraFragment;
import com.androidteam.campic.MainModule.fragments.GalleryFragment;
import com.androidteam.campic.R;
import com.imnjh.imagepicker.activity.PickerPreviewActivity;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        tabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        viewPager = (ViewPager) findViewById(R.id.mainViewPager);
        setupViewPager(viewPager);

    }

    final GalleryFragment galleryFragment = new GalleryFragment();

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        CameraFragment cameraFragment = new CameraFragment();

        cameraFragment.setPictureTakenListener(new CameraFragment.OnPictureTakenListener() {
            @Override
            public void pictureTaken() {
                galleryFragment.refreshLoader();
            }
        });
        adapter.addFragment(galleryFragment, "Gallery");
        adapter.addFragment(cameraFragment, "Camera");
        adapter.addFragment(new AppInfoFragment(), "Info");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
        makeTabLayoutGone();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                    if(position == 1){
                        makeTabLayoutGone();
                    } else {
                        makeTabLayoutVisible();
                    }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TextView galleryTxt = (TextView) (((LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(0)).getChildAt(1));
        galleryTxt.setScaleY(-1);
        TextView cameraTxt = (TextView) (((LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(1)).getChildAt(1));
        cameraTxt.setScaleY(-1);
        TextView infoTxt = (TextView) (((LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(2)).getChildAt(1));
        infoTxt.setScaleY(-1);
    }

    private void makeTabLayoutVisible() {
        TranslateAnimation animate = new TranslateAnimation(0,0,tabLayout.getMeasuredHeight(),0);
        animate.setDuration(200);
        animate.setFillAfter(false);
        tabLayout.startAnimation(animate);
        tabLayout.setVisibility(View.VISIBLE);
    }

    private void makeTabLayoutGone() {
        TranslateAnimation animate = new TranslateAnimation(0,0,0,tabLayout.getMeasuredHeight());
        animate.setDuration(200);
        animate.setFillAfter(false);
        tabLayout.startAnimation(animate);
        tabLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(null != data && null != data.getAction() && data.getAction().equals(PickerPreviewActivity.KEY_DELETED)){
            galleryFragment.refreshLoader();
        }
    }
}
