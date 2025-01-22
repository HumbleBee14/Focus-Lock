package com.grepguru.focuslock;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends Activity {

    private EditText pinInput;
    private Button saveButton;
    private SwitchCompat superStrictModeToggle;

    private Button whitelistButton;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        pinInput = findViewById(R.id.pinInput);
        saveButton = findViewById(R.id.saveButton);
        superStrictModeToggle = findViewById(R.id.superStrictModeToggle);
        whitelistButton = findViewById(R.id.whitelistButton);

        preferences = getSharedPreferences("FocusLockPrefs", MODE_PRIVATE);

        // Load existing credentials
        pinInput.setText(preferences.getString("lock_pin", ""));
        superStrictModeToggle.setChecked(preferences.getBoolean("super_strict_mode", false));

        saveButton.setOnClickListener(v -> saveCredentials()); // Save credentials

        // Toggle Super Strict Mode
        superStrictModeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("super_strict_mode", isChecked);
            editor.apply();
        });

        // Navigate to Whitelist Management
        whitelistButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, WhitelistActivity.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            } else if (id == R.id.settings) {
                return true;
            } else if (id == R.id.analytics) {
                Toast.makeText(this, "Analytics Coming Soon", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.schedule) {
                Toast.makeText(this, "Schedule Coming Soon", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });
    }

    private void saveCredentials() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lock_pin", pinInput.getText().toString());
        editor.apply();

        Toast.makeText(this, "Settings Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
