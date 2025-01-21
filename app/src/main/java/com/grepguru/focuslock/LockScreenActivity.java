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

        // Retrieve saved lock end time
        long lockEndTime = preferences.getLong("lockEndTime", 0);
        long currentTime = System.currentTimeMillis();

        // If no active lock or timer already expired, exit lock screen
        if (!preferences.getBoolean("isLocked", false) || currentTime >= lockEndTime) {
            finish();
            return;
        }

        setContentView(R.layout.activity_lock_screen);

        // Initializing UI Components
        pinInput = findViewById(R.id.pinInput);
        Button unlockButton = findViewById(R.id.unlockButton);
        TextView timerCountdown = findViewById(R.id.timerCountdown);
//        TextView lockMessage = findViewById(R.id.lockscreenMessage);
        Button unlockPromptButton = findViewById(R.id.unlockPromptButton);
        LinearLayout unlockInputsContainer = findViewById(R.id.unlockInputsContainer);
        LinearLayout emergencyAppsContainer = findViewById(R.id.emergencyAppsContainer);

        // Start countdown timer with remaining time
        long remainingTimeMillis = lockEndTime - currentTime;
        if (remainingTimeMillis <= 0) {
            finish();
            return;
        }

        // -----------------------------------------------------------
        // Setting up Click Listeners

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

        unlockButton.setOnClickListener(v -> checkPinAndUnlock());

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

        // Start Countdown Timer
        startCountdownTimer(remainingTimeMillis , timerCountdown);
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

    private void startCountdownTimer(long remainingTimeMillis, TextView timerCountdown) {
        new android.os.CountDownTimer(remainingTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                timerCountdown.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isLocked", false);
                editor.remove("lockEndTime"); // Remove saved lock end time
                editor.apply();

                Toast.makeText(LockScreenActivity.this, "Time's up! Focus Mode Ended.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }.start();
    }


}
