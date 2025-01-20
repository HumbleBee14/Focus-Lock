package com.grepguru.focuslock;

import android.app.Activity;
import android.app.KeyguardManager;
import android.os.Bundle;

public class LockScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Modern way to show over lock screen in API 27+
        setShowWhenLocked(true);
        setTurnScreenOn(true);

        // Dismiss keyguard for unlocked interaction
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (keyguardManager != null) {
            keyguardManager.requestDismissKeyguard(this, null);
        }

        setContentView(R.layout.activity_lock_screen);
    }

}
