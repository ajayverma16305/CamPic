package com.androidteam.campic.Helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by ajayverma on 16/03/18.
 */

public class PermissionManager {

    /**
     * Check for granted permission
     * @param permission
     * @return
     */
    public static boolean checkForStoragePermission(Context context ,String permission){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return (ContextCompat.checkSelfPermission(context, permission)) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }
}
