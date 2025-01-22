package com.grepguru.focuslock.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import java.util.HashSet;
import java.util.Set;

public class AppUtils {

    public static Set<String> getDefaultApps(Context context) {
        Set<String> defaultApps = new HashSet<>();
        PackageManager pm = context.getPackageManager();

        // Dialer/Call App
        String[] dialerApps = {"com.android.phone", "com.google.android.dialer", "com.samsung.android.dialer"};
        for (String pkg : dialerApps) {
            if (isPackageInstalled(pm, pkg)) {
                defaultApps.add(pkg);
                break; // Stop when the first available dialer is found
            }
        }

        // SMS App
        String[] smsApps = {"com.android.mms", "com.google.android.apps.messaging", "com.samsung.android.messaging"};
        for (String pkg : smsApps) {
            if (isPackageInstalled(pm, pkg)) {
                defaultApps.add(pkg);
                break;
            }
        }

        // Clock/Alarm App
        String[] clockApps = {"com.android.deskclock", "com.google.android.deskclock", "com.sec.android.app.clockpackage"};
        for (String pkg : clockApps) {
            if (isPackageInstalled(pm, pkg)) {
                defaultApps.add(pkg);
                break;
            }
        }

        return defaultApps;
    }


    private static boolean isPackageInstalled(PackageManager pm, String packageName) {
        try {
            pm.getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
