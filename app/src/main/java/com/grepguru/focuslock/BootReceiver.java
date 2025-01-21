package com.grepguru.focuslock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences preferences = context.getSharedPreferences("FocusLockPrefs", Context.MODE_PRIVATE);

            // Reset Lock State after Reboot
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("wasDeviceRestarted", true); // Mark Restart
            editor.putBoolean("isLocked", false);
            editor.remove("lockEndTime");
            editor.apply();
        }
    }

}
