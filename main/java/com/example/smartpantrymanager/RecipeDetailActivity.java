package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private TextView txtRecipeDetailName;
    private TextView txtRecipeDetailIngredients;
    private TextView txtRecipeDetailInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        databaseHelper = new DatabaseHelper(this);

        txtRecipeDetailName =
                findViewById(R.id.txtRecipeDetailName);

        txtRecipeDetailIngredients =
                findViewById(R.id.txtRecipeDetailIngredients);

        txtRecipeDetailInstructions =
                findViewById(R.id.txtRecipeDetailInstructions);

        Button btnBackRecipes = findViewById(R.id.btnBackRecipes);

        btnBackRecipes.setOnClickListener(v -> finish());

        int recipeId = getIntent().getIntExtra("recipe_id", -1);

        if (recipeId == -1) {
            Toast.makeText(this, "Recipe not found",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayRecipe(recipeId);
    }

    private void displayRecipe(int recipeId) {
        Recipe recipe = databaseHelper.getRecipeById(recipeId);

        if (recipe == null) {
            Toast.makeText(this, "Recipe not found",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        txtRecipeDetailName.setText(recipe.getName());

        txtRecipeDetailInstructions.setText(
                recipe.getInstructions());

        StringBuilder ingredientText = new StringBuilder();

        List<RecipeIngredient> requirements =
                databaseHelper.getAllRecipeIngredients();

        for (RecipeIngredient requirement : requirements) {
            if (requirement.getRecipeId() == recipeId) {
                ingredientText.append("• ")
                        .append(requirement.getIngredientName())
                        .append(" — ")
                        .append(String.format(
                                Locale.getDefault(),
                                "%s",
                                requirement.getRequiredQuantity()))
                        .append(" ")
                        .append(requirement.getUnit())
                        .append("\n");
            }
        }

        if (ingredientText.length() == 0) {
            ingredientText.append("No ingredients listed.");
        }

        txtRecipeDetailIngredients.setText(ingredientText.toString());
    }
}
