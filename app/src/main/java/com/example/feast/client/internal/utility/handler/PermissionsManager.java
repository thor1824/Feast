package com.example.feast.client.internal.utility.handler;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class PermissionsManager {
    /**
     * checks if the phone api is 23 or above. then check if the permission is already given
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
     * Asks for permission based on what comes as a parameters
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
