package com.grepguru.focuslock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class LockScreenActivity extends AppCompatActivity {

    private EditText pinInput;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("FocusLockPrefs", Context.MODE_PRIVATE);

        // Check if focus lock is active
        if (!preferences.getBoolean("isLocked", false)) {
            finish(); // If not locked, close this screen
            return;
        }

        setContentView(R.layout.activity_lock_screen);

        pinInput = findViewById(R.id.pinInput);
        Button unlockButton = findViewById(R.id.unlockButton);

        TextView timerCountdown = findViewById(R.id.timerCountdown);
        TextView lockMessage = findViewById(R.id.lockscreenMessage);
        Button unlockPromptButton = findViewById(R.id.unlockPromptButton);
        LinearLayout unlockInputsContainer = findViewById(R.id.unlockInputsContainer);
        LinearLayout emergencyAppsContainer = findViewById(R.id.emergencyAppsContainer);

        // Initially Hide PIN Input and Keep Emergency Apps Visible
        unlockInputsContainer.setVisibility(View.GONE);
        emergencyAppsContainer.setVisibility(View.VISIBLE);

        unlockPromptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show PIN Input when unlocking and hide emergency buttons
                unlockInputsContainer.setVisibility(View.VISIBLE);
                unlockPromptButton.setVisibility(View.GONE);
                emergencyAppsContainer.setVisibility(View.GONE);
            }
        });

        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPinAndUnlock();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Check if unlock input screen is visible
                if (unlockInputsContainer.getVisibility() == View.VISIBLE) {
                    // Hide unlock inputs and show main lock screen
                    unlockInputsContainer.setVisibility(View.GONE);
                    unlockPromptButton.setVisibility(View.VISIBLE);
                    emergencyAppsContainer.setVisibility(View.VISIBLE);

                    pinInput.setText("");
                } else {
                    // Prevent exiting the app
                    Toast.makeText(LockScreenActivity.this, "Cannot exit Focus Mode!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkPinAndUnlock() {
        String enteredPin = pinInput.getText().toString();
        String savedPin = preferences.getString("lock_pin", "");

        if (enteredPin.equals(savedPin)) {
            Toast.makeText(this, "Unlocked!", Toast.LENGTH_SHORT).show();

            // Reset lock state
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLocked", false); // Mark as unlocked
            editor.apply();

            // Return to MainActivity
            Intent intent = new Intent(LockScreenActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Incorrect PIN!", Toast.LENGTH_SHORT).show();
        }
    }
}
