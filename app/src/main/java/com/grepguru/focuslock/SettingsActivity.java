package com.grepguru.focuslock;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    private EditText pinInput, passwordInput, emailInput;
    private Button saveButton;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        pinInput = findViewById(R.id.pinInput);
        passwordInput = findViewById(R.id.passwordInput);
        emailInput = findViewById(R.id.emailInput);
        saveButton = findViewById(R.id.saveButton);

        preferences = getSharedPreferences("FocusLockPrefs", MODE_PRIVATE);

        // Load existing credentials if available
        pinInput.setText(preferences.getString("lock_pin", ""));
        passwordInput.setText(preferences.getString("lock_password", ""));
        emailInput.setText(preferences.getString("recovery_email", ""));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCredentials();
            }
        });
    }

    private void saveCredentials() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lock_pin", pinInput.getText().toString());
        editor.putString("lock_password", passwordInput.getText().toString());
        editor.putString("recovery_email", emailInput.getText().toString());
        editor.apply();

        Toast.makeText(this, "Settings Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
