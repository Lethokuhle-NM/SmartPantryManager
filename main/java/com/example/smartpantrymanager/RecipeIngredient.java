package com.example.smartpantrymanager;

public class RecipeIngredient {

    private int recipeId;
    private String ingredientName;
    private double requiredQuantity;
    private String unit;

    public RecipeIngredient(int recipeId, String ingredientName,
                            double requiredQuantity, String unit) {
        this.recipeId = recipeId;
        this.ingredientName = ingredientName;
        this.requiredQuantity = requiredQuantity;
        this.unit = unit;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public double getRequiredQuantity() {
        return requiredQuantity;
    }

    public String getUnit() {
        return unit;
    }
}
