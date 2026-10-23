package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecipeAdapter adapter;

    private final List<Recipe> matchingRecipes = new ArrayList<>();

    private RecyclerView recyclerSuggestedRecipes;
    private TextView txtNoMatchingRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        databaseHelper = new DatabaseHelper(this);

        recyclerSuggestedRecipes =
                findViewById(R.id.recyclerSuggestedRecipes);

        txtNoMatchingRecipes =
                findViewById(R.id.txtNoMatchingRecipes);

        Button btnBackHome = findViewById(R.id.btnBackHome);

        recyclerSuggestedRecipes.setLayoutManager(
                new LinearLayoutManager(this));

        adapter = new RecipeAdapter(
                this,
                matchingRecipes,
                recipe -> {
                    Intent intent = new Intent(
                            SuggestedRecipesActivity.this,
                            RecipeDetailActivity.class);

                    intent.putExtra("recipe_id", recipe.getId());

                    startActivity(intent);
                });

        recyclerSuggestedRecipes.setAdapter(adapter);

        btnBackHome.setOnClickListener(v -> finish());

        loadMatchingRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && adapter != null) {
            loadMatchingRecipes();
        }
    }

    private void loadMatchingRecipes() {
        List<Ingredient> pantry =
                databaseHelper.getAllIngredients();

        List<Recipe> allRecipes =
                databaseHelper.getAllRecipes();

        List<RecipeIngredient> requirements =
                databaseHelper.getAllRecipeIngredients();

        List<Recipe> matches =
                RecipeMatcher.getMatchingRecipes(
                        pantry, allRecipes, requirements);

        adapter.updateRecipes(matches);

        if (matches.isEmpty()) {
            txtNoMatchingRecipes.setText(
                    "No recipes match your current pantry. "
                            + "Add more ingredients or increase their "
                            + "quantities to discover recipes.");
            txtNoMatchingRecipes.setVisibility(View.VISIBLE);
            recyclerSuggestedRecipes.setVisibility(View.GONE);
        } else {
            txtNoMatchingRecipes.setVisibility(View.GONE);
            recyclerSuggestedRecipes.setVisibility(View.VISIBLE);
        }
    }
}
