package com.grepguru.focuslock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grepguru.focuslock.model.*;
import com.grepguru.focuslock.ui.adapter.*;
import com.grepguru.focuslock.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class WhitelistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button saveButton;
    private List<SelectableAppModel> appList = new ArrayList<>();
    private Set<String> defaultApps = new HashSet<>();
    private Set<String> selectedApps = new HashSet<>();
    //  private static final int MAX_SELECTION = 5; // Max additional apps

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whitelist);

        recyclerView = findViewById(R.id.whitelistRecyclerView);
        saveButton = findViewById(R.id.saveButton);

        // Define default apps (Call, SMS, Alarm)
        defaultApps = AppUtils.getDefaultApps(this);
        selectedApps.addAll(defaultApps); // Pre-select default apps

        // Load all installed apps dynamically
        loadInstalledApps();

        // Setup RecyclerView
        WhitelistAdapter adapter = new WhitelistAdapter(appList, selectedApps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Save Button Click Listener
        saveButton.setOnClickListener(v -> {
            saveWhitelist();
        });
    }

    private void loadInstalledApps() {
        PackageManager pm = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // Get only third-party apps with a launcher icon
        List<ResolveInfo> resolvedApps = pm.queryIntentActivities(mainIntent, 0);

        for (ResolveInfo resolveInfo : resolvedApps) {
            String packageName = resolveInfo.activityInfo.packageName;
            boolean isDefault = defaultApps.contains(packageName);
            boolean isSelected = selectedApps.contains(packageName); // Preserve selection
            Drawable icon;
            try {
                icon = pm.getApplicationIcon(packageName);
            } catch (PackageManager.NameNotFoundException e) {
                icon = getDrawable(R.drawable.default_app_icon); // Fallback icon
            }

            // FILTER SYSTEM APPS HERE
            ApplicationInfo appInfo;
            try {
                appInfo = pm.getApplicationInfo(packageName, 0);
                if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { // Only include non-system apps
                    appList.add(new SelectableAppModel(packageName, resolveInfo.activityInfo.loadLabel(pm).toString(), isDefault, isSelected, icon));
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    private void saveWhitelist() {
        SharedPreferences preferences = getSharedPreferences("FocusLockPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("whitelisted_apps", selectedApps);
        editor.apply();

        Toast.makeText(this, "Whitelist Updated!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
