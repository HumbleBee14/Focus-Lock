package com.grepguru.focuslock;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class WhitelistActivity extends Activity {

    private CheckBox phoneCheckBox, smsCheckBox, emailCheckBox;
    private Button saveButton;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whitelist);

        phoneCheckBox = findViewById(R.id.phoneCheckBox);
        smsCheckBox = findViewById(R.id.smsCheckBox);
        emailCheckBox = findViewById(R.id.emailCheckBox);
        saveButton = findViewById(R.id.saveButton);

        preferences = getSharedPreferences("FocusLockPrefs", MODE_PRIVATE);

        // Load saved values
        phoneCheckBox.setChecked(preferences.getBoolean("allow_phone", false));
        smsCheckBox.setChecked(preferences.getBoolean("allow_sms", false));
        emailCheckBox.setChecked(preferences.getBoolean("allow_email", false));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWhitelist();
            }
        });
    }

    private void saveWhitelist() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("allow_phone", phoneCheckBox.isChecked());
        editor.putBoolean("allow_sms", smsCheckBox.isChecked());
        editor.putBoolean("allow_email", emailCheckBox.isChecked());
        editor.apply();

        Toast.makeText(this, "Whitelist Updated!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
