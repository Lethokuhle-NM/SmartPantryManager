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
            String requiredName =
                    normaliseName(required.getIngredientName());

            String requiredUnit =
                    normaliseUnit(required.getUnit());

            double requiredAmount = required.getRequiredQuantity();

            if (requiredName.isEmpty() || requiredAmount <= 0) {
                return false;
            }

            boolean requirementSatisfied = false;

            for (Ingredient available : pantry) {
                String availableName =
                        normaliseName(available.getName());

                if (!availableName.equals(requiredName)) {
                    continue;
                }

                String availableUnit =
                        normaliseUnit(available.getUnit());

                if (!availableUnit.equals(requiredUnit)) {
                    continue;
                }

                double availableAmount = toBaseQuantity(
                        available.getQuantity(), availableUnit);

                double neededAmount = toBaseQuantity(
                        requiredAmount, requiredUnit);

                if (availableAmount >= neededAmount) {
                    requirementSatisfied = true;
                    break;
                }
            }

            if (!requirementSatisfied) {
                return false;
            }
        }

        return true;
    }

    public static List<Recipe> getMatchingRecipes(
            List<Ingredient> pantry,
            List<Recipe> recipes,
            List<RecipeIngredient> allRequirements) {

        List<Recipe> matches = new ArrayList<>();

        for (Recipe recipe : recipes) {
            List<RecipeIngredient> recipeRequirements =
                    new ArrayList<>();

            for (RecipeIngredient requirement : allRequirements) {
                if (requirement.getRecipeId() == recipe.getId()) {
                    recipeRequirements.add(requirement);
                }
            }

            if (matches(pantry, recipeRequirements)) {
                matches.add(recipe);
            }
        }

        return matches;
    }

    public static String normaliseName(String name) {
        if (name == null) {
            return "";
        }

        String normalised = name.trim().toLowerCase(Locale.ROOT);

        if (normalised.endsWith("ies") && normalised.length() > 3) {
            return normalised.substring(0, normalised.length() - 3) + "y";
        }

        if (normalised.endsWith("oes") && normalised.length() > 3) {
            return normalised.substring(0, normalised.length() - 2);
        }

        if (normalised.endsWith("s")
                && !normalised.endsWith("ss")
                && normalised.length() > 1) {
            return normalised.substring(0, normalised.length() - 1);
        }

        return normalised;
    }

    public static String normaliseUnit(String unit) {
        if (unit == null) {
            return "";
        }

        String normalised = unit.trim().toLowerCase(Locale.ROOT);

        switch (normalised) {
            case "gram":
            case "grams":
                return "g";

            case "kilogram":
            case "kilograms":
                return "kg";

            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "ml";

            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "l";

            case "piece":
            case "pieces":
            case "pc":
            case "pcs":
                return "piece";

            default:
                return normalised;
        }
    }

    private static double toBaseQuantity(
            double quantity, String unit) {

        switch (unit) {
            case "kg":
                return quantity * 1000.0;

            case "l":
                return quantity * 1000.0;

            default:
                return quantity;
        }
    }
}
