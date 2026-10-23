package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchExpiryAlerts;
    private Spinner spinnerPreferredUnit;

    private SharedPreferences preferences;

    private final String[] units = {
            "piece", "g", "kg", "ml", "l"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);
        spinnerPreferredUnit = findViewById(R.id.spinnerPreferredUnit);

        Button btnSaveSettings = findViewById(R.id.btnSaveSettings);
        Button btnBackHome = findViewById(R.id.btnSettingsBackHome);

        preferences = getSharedPreferences(
                "SmartPantrySettings", MODE_PRIVATE);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Arrays.asList(units));

        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spinnerPreferredUnit.setAdapter(unitAdapter);

        loadSettings();

        btnSaveSettings.setOnClickListener(v -> saveSettings());

        btnBackHome.setOnClickListener(v -> finish());
    }

    private void loadSettings() {
        boolean alertsEnabled = preferences.getBoolean(
                "expiry_alerts", true);

        String savedUnit = preferences.getString(
                "preferred_unit", "piece");

        switchExpiryAlerts.setChecked(alertsEnabled);

        int position = Arrays.asList(units).indexOf(savedUnit);

        if (position >= 0) {
            spinnerPreferredUnit.setSelection(position);
        }
    }

    private void saveSettings() {
        boolean alertsEnabled = switchExpiryAlerts.isChecked();

        String preferredUnit =
                spinnerPreferredUnit.getSelectedItem().toString();

        preferences.edit()
                .putBoolean("expiry_alerts", alertsEnabled)
                .putString("preferred_unit", preferredUnit)
                .apply();

        Toast.makeText(this, "Settings saved",
                Toast.LENGTH_SHORT).show();
    }
}

