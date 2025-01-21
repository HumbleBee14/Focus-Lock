package com.grepguru.focuslock;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private NumberPicker hoursPicker, minutesPicker;
    private TextView selectedTimeDisplay;
    private Button enableLockButton;
    private int selectedHours = 0, selectedMinutes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        hoursPicker = findViewById(R.id.hoursPicker);
        minutesPicker = findViewById(R.id.minutesPicker);
        selectedTimeDisplay = findViewById(R.id.selectedTimeDisplay);
        enableLockButton = findViewById(R.id.enableLockButton);


        // Setup Number Pickers
        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(23);

        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(6);
        String[] minuteValues = {"0", "1", "10", "20", "30", "40", "50"};
        minutesPicker.setDisplayedValues(minuteValues);

        // Listen for changes
        hoursPicker.setOnValueChangedListener((picker, oldVal, newVal) -> updateSelectedTime());
        minutesPicker.setOnValueChangedListener((picker, oldVal, newVal) -> updateSelectedTime());

        // Default Values to 0h 30m on App Start
        hoursPicker.setValue(0);
        minutesPicker.setValue(1); // 30 minutes corresponds to index 3 in minuteValues
        updateSelectedTime();

        // Set a click listener for the button
        enableLockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndStartLockService();
            }
        });
    }

    private void updateSelectedTime() {
        selectedHours = hoursPicker.getValue();
        int minuteIndex = minutesPicker.getValue();
        int[] minuteValues = {0, 1, 10, 20, 30, 40, 50};
        selectedMinutes = minuteValues[minuteIndex];

        selectedTimeDisplay.setText("Lock Time: " + selectedHours + " hrs " + selectedMinutes + " mins");

        // Disable button if time is 0h 0m
        if (selectedHours == 0 && selectedMinutes == 0) {
            enableLockButton.setEnabled(false);
            enableLockButton.setAlpha(0.5f); // Greyed out effect
        } else {
            enableLockButton.setEnabled(true);
            enableLockButton.setAlpha(1f);
        }
    }

    private void checkAndStartLockService() {
        if (!isAccessibilityPermissionGranted()) {
            // Redirect user to enable Accessibility permission first
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "Please enable Accessibility for Focus Lock", Toast.LENGTH_SHORT).show();

            return;
        } else {
            // Save Lock State and Timer Duration in SharedPreferences
            SharedPreferences preferences = getSharedPreferences("FocusLockPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            long lockDurationMillis = (selectedHours * 3600 + selectedMinutes * 60) * 1000;

            if (lockDurationMillis <= 0) {
                Toast.makeText(this, "Please select a valid lock time!", Toast.LENGTH_SHORT).show();
                return;
            }

            long lockEndTime = System.currentTimeMillis() + lockDurationMillis;
            System.out.println("lockEndTime : " + lockEndTime);

            editor.putBoolean("isLocked", true);
            editor.putLong("lockEndTime", lockEndTime);  // Save lock expiration time
            editor.apply();

            // Start LockScreenActivity with selected duration
            Intent intent = new Intent(MainActivity.this, LockScreenActivity.class);
            intent.putExtra("lockDuration", lockDurationMillis);
            startActivity(intent);
//            finish();
        }
    }

    private boolean isAccessibilityPermissionGranted() {
        AccessibilityManager am = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (am != null) {
            for (AccessibilityServiceInfo service : am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)) {
                if (service.getId().contains(getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
