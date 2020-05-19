package com.example.feast.client.internal.utility.handler;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class PermissionsManager {

    /**
     * Checks if the permissions are granted for the app to be usable
     *
     * @param permission
     * @param act
     * @return
     */
    public static boolean isGrantedPermission(String permission, Activity act) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permissionResult = act.checkSelfPermission(permission);
            return permissionResult == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    /**
     * asks permission from the user
     *
     * @param Permissions
     * @param RequestCode
     * @param act
     */
    public static void askPermission(String[] Permissions, int RequestCode, Activity act) {
        ActivityCompat.requestPermissions(act,
                Permissions,
                RequestCode);

    }

}
