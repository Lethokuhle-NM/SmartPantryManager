package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnPantry;
    private Button btnSuggestedRecipes;
    private Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPantry = findViewById(R.id.btnPantry);
        btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);
        btnSettings = findViewById(R.id.btnSettings);

        btnPantry.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this, PantryActivity.class);
            startActivity(intent);
        });

        btnSuggestedRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this, SuggestedRecipesActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}
