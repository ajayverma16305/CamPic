package com.androidteam.campic.MainModule.AppApplication;

import android.app.Application;
import android.content.Context;
import com.androidteam.campic.MainModule.imageloader.FrescoImageLoader;
import com.androidteam.campic.R;
import com.imnjh.imagepicker.PickerConfig;
import com.imnjh.imagepicker.SImagePicker;

import java.lang.ref.WeakReference;

/**
 * Created by Ajay Verma on 3/16/2018.
 */
public class CamPicApplication extends Application {

    private static WeakReference<Application> instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = new WeakReference<Application>(this);
        SImagePicker.init(new PickerConfig.Builder()
                .setAppContext(this)
                .setImageLoader(new FrescoImageLoader())
                .setToolbaseColor(getResources().getColor(R.color.colorPrimaryDark))
                .build());
    }

    public static Context getAppContext() {
        return instance.get();
    }
}
