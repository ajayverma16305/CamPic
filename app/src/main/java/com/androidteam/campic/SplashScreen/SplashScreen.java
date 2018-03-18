package com.androidteam.campic.SplashScreen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.androidteam.campic.Helper.FirstLaunchPrefManager;
import com.androidteam.campic.MainModule.HomeActivity;
import com.androidteam.campic.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Checking for first time launch - before calling setContentView()
        FirstLaunchPrefManager prefManager = new FirstLaunchPrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            startActivity(new Intent(this,HomeActivity.class));
        } else {
            startActivity(new Intent(this,PermissionActivity.class));
        }

        finish();

        /*
        * https://github.com/ParkSangGwon/TedBottomPicker
        * https://github.com/jaydeepw/poly-picker
        * https://github.com/RameshBhupathi/ImagePicker-OLX?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=5987

        * https://github.com/linchaolong/ImagePicker/blob/master/README_en.md
        * https://github.com/martin90s/ImagePicker?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=5129
        * https://github.com/Tofira/ImagePickerWithCrop?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=4760
        * https://android-arsenal.com/details/1/4618
        * https://android-arsenal.com/details/1/3092
        * https://android-arsenal.com/details/1/2472
        * https://github.com/ogaclejapan/SmartTabLayout
        * https://android-arsenal.com/details/1/6668
        * https://android-arsenal.com/details/1/6576
        * https://android-arsenal.com/details/1/6487
        * */
    }
}
