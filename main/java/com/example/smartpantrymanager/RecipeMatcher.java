package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeMatcher {

    public static boolean matches(
            List<Ingredient> pantry,
            List<RecipeIngredient> requirements) {

        if (requirements == null || requirements.isEmpty()) {
            return false;
        }

        for (RecipeIngredient required : requirements) {

            Ingredient available = findIngredient(
                    pantry, required.getIngredientName());

            if (available == null) {
                return false;
            }

            if (!unitsAreCompatible(
                    available.getUnit(), required.getUnit())) {
                return false;
            }

            if (available.getQuantity()
                    < required.getRequiredQuantity()) {
                return false;
            }
        }

        return true;
    }

    private static Ingredient findIngredient(
            List<Ingredient> pantry, String requiredName) {

        for (Ingredient ingredient : pantry) {
            if (normaliseName(ingredient.getName())
                    .equals(normaliseName(requiredName))) {
                return ingredient;
            }
        }

        return null;
    }

    private static String normaliseName(String name) {

        String normalised = name.trim()
                .toLowerCase(Locale.ROOT);

        if (normalised.endsWith("ies")
                && normalised.length() > 3) {
            normalised = normalised.substring(
                    0, normalised.length() - 3) + "y";
        } else if (normalised.endsWith("oes")
                && normalised.length() > 3) {
            normalised = normalised.substring(
                    0, normalised.length() - 2);
        } else if (normalised.endsWith("s")
                && !normalised.endsWith("ss")
                && normalised.length() > 1) {
            normalised = normalised.substring(
                    0, normalised.length() - 1);
        }

        return normalised;
    }

    private static boolean unitsAreCompatible(
            String availableUnit, String requiredUnit) {

        String available = normaliseUnit(availableUnit);
        String required = normaliseUnit(requiredUnit);

        return available.equals(required);
    }

    private static String normaliseUnit(String unit) {

        String value = unit.trim().toLowerCase(Locale.ROOT);

        switch (value) {
            case "grams":
                return "g";
            case "kilograms":
            case "kilogram":
                return "kg";
            case "millilitres":
            case "milliliters":
                return "ml";
            case "litres":
            case "liters":
                return "l";
            case "pieces":
            case "piece":
            case "pcs":
                return "piece";
            default:
                return value;
        }
    }

    public static List<Recipe> getMatchingRecipes(
            List<Ingredient> pantry,
            List<Recipe> recipes,
            List<RecipeIngredient> allRequirements) {

        List<Recipe> matchingRecipes = new ArrayList<>();

        for (Recipe recipe : recipes) {

            List<RecipeIngredient> requirements = new ArrayList<>();

            for (RecipeIngredient item : allRequirements) {
                if (item.getRecipeId() == recipe.getId()) {
                    requirements.add(item);
                }
            }

            if (matches(pantry, requirements)) {
                matchingRecipes.add(recipe);
            }
        }

        return matchingRecipes;
    }
}

