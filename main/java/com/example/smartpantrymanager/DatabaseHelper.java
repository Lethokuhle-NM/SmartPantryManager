package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "expiry_date TEXT)"
        );

        db.execSQL(
                "CREATE TABLE recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "instructions TEXT NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE recipe_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "ingredient_name TEXT NOT NULL, " +
                        "required_quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(id) " +
                        "ON DELETE CASCADE)"
        );

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add database migration code here if the schema changes.
        // Avoid dropping tables because that would delete saved pantry data.
    }

    //Add a pantry ingredient;
    public long addIngredient(Ingredient ingredient) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", ingredient.getName());
        values.put("quantity", ingredient.getQuantity());
        values.put("unit", ingredient.getUnit());
        values.put("expiry_date", ingredient.getExpiryDate());

        return db.insert("ingredients", null, values);
    }

    //Retrieves every pantry ingredient.
    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                "ingredients",
                null,
                null,
                null,
                null,
                null,
                "name ASC")) {

            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id"));

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name"));

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("quantity"));

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow("unit"));

                String expiryDate = cursor.getString(
                        cursor.getColumnIndexOrThrow("expiry_date"));

                ingredients.add(new Ingredient(
                        id, name, quantity, unit, expiryDate));
            }
        }

        return ingredients;
    }

    //Retrieve one ingredient using itss ID.
    public Ingredient getIngredientById(int ingredientId) {
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                "ingredients",
                null,
                "id = ?",
                new String[]{String.valueOf(ingredientId)},
                null,
                null,
                null)) {

            if (cursor.moveToFirst()) {
                return new Ingredient(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                        cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"))
                );
            }
        }

        return null;
    }

    //Update an existing pantry ingredient.
    public int updateIngredient(Ingredient ingredient) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", ingredient.getName());
        values.put("quantity", ingredient.getQuantity());
        values.put("unit", ingredient.getUnit());
        values.put("expiry_date", ingredient.getExpiryDate());

        return db.update(
                "ingredients",
                values,
                "id = ?",
                new String[]{String.valueOf(ingredient.getId())}
        );
    }

    //Deletesa an ingredient.
    public int deleteIngredient(int ingredientId) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                "ingredients",
                "id = ?",
                new String[]{String.valueOf(ingredientId)}
        );
    }

    //This Retrieves every recipe.
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                "recipes",
                null,
                null,
                null,
                null,
                null,
                "name ASC")) {

            while (cursor.moveToNext()) {
                recipes.add(new Recipe(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("instructions"))
                ));
            }
        }

        return recipes;
    }

    // Retrieve one recipe using its ID.
    public Recipe getRecipeById(int recipeId) {
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                "recipes",
                null,
                "id = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                null)) {

            if (cursor.moveToFirst()) {
                return new Recipe(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("instructions"))
                );
            }
        }

        return null;
    }

    // Retrieve the ingredient requirements for all recipes.
    public List<RecipeIngredient> getAllRecipeIngredients() {
        List<RecipeIngredient> requirements = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                "recipe_ingredients",
                null,
                null,
                null,
                null,
                null,
                "recipe_id ASC")) {

            while (cursor.moveToNext()) {
                requirements.add(new RecipeIngredient(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("recipe_id")),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("ingredient_name")),
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow("required_quantity")),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("unit"))
                ));
            }
        }

        return requirements;
    }

    // Insert the starter recipes when the database is first created.
    private void seedRecipes(SQLiteDatabase db) {

        insertRecipe(db,
                "Tomato Omelette",
                "1. Beat the eggs. 2. Chop the tomato. " +
                        "3. Cook both in a pan with oil until the eggs are set.",
                new String[][]{
                        {"egg", "2", "piece"},
                        {"tomato", "1", "piece"},
                        {"oil", "10", "ml"}
                });

        insertRecipe(db,
                "Cheese Omelette",
                "1. Beat the eggs. 2. Cook in a pan with oil. " +
                        "3. Add cheese and fold the omelette.",
                new String[][]{
                        {"egg", "2", "piece"},
                        {"cheese", "30", "g"},
                        {"oil", "5", "ml"}
                });

        insertRecipe(db,
                "Tomato Salad",
                "1. Wash and chop the tomatoes and cucumber. " +
                        "2. Mix together and serve.",
                new String[][]{
                        {"tomato", "2", "piece"},
                        {"cucumber", "1", "piece"}
                });

        insertRecipe(db,
                "Garlic Pasta",
                "1. Cook the pasta. 2. Chop the garlic. " +
                        "3. Cook the garlic briefly in oil and mix with the pasta.",
                new String[][]{
                        {"pasta", "100", "g"},
                        {"garlic", "1", "piece"},
                        {"oil", "10", "ml"}
                });

        insertRecipe(db,
                "Cheese Pasta",
                "1. Cook and drain the pasta. " +
                        "2. Stir in the cheese until combined.",
                new String[][]{
                        {"pasta", "100", "g"},
                        {"cheese", "40", "g"}
                });

        insertRecipe(db,
                "Banana Oatmeal",
                "1. Heat the oats and milk gently. " +
                        "2. Slice the banana and add it before serving.",
                new String[][]{
                        {"oats", "50", "g"},
                        {"milk", "200", "ml"},
                        {"banana", "1", "piece"}
                });

        insertRecipe(db,
                "Peanut Butter Banana Toast",
                "1. Toast the bread. 2. Spread peanut butter. " +
                        "3. Slice the banana and place it on top.",
                new String[][]{
                        {"bread", "2", "piece"},
                        {"peanut butter", "20", "g"},
                        {"banana", "1", "piece"}
                });

        insertRecipe(db,
                "Apple Oatmeal",
                "1. Heat oats and milk gently. " +
                        "2. Chop the apple and add it before serving.",
                new String[][]{
                        {"oats", "50", "g"},
                        {"milk", "200", "ml"},
                        {"apple", "1", "piece"}
                });

        insertRecipe(db,
                "Potato Hash",
                "1. Dice the potatoes and onion. " +
                        "2. Fry in oil until tender and lightly browned.",
                new String[][]{
                        {"potato", "2", "piece"},
                        {"onion", "1", "piece"},
                        {"oil", "15", "ml"}
                });

        insertRecipe(db,
                "Garlic Rice",
                "1. Cook the rice. 2. Chop the garlic. " +
                        "3. Cook the garlic in oil and mix through the rice.",
                new String[][]{
                        {"rice", "100", "g"},
                        {"garlic", "1", "piece"},
                        {"oil", "10", "ml"}
                });

        insertRecipe(db,
                "Carrot Salad",
                "1. Wash and grate the carrots. " +
                        "2. Add lemon juice and mix.",
                new String[][]{
                        {"carrot", "2", "piece"},
                        {"lemon", "1", "piece"}
                });

        insertRecipe(db,
                "Cucumber Tomato Salad",
                "1. Wash and chop the cucumber and tomatoes. " +
                        "2. Mix and serve.",
                new String[][]{
                        {"cucumber", "1", "piece"},
                        {"tomato", "2", "piece"}
                });

        insertRecipe(db,
                "Egg Fried Rice",
                "1. Cook the rice. 2. Scramble the eggs in a pan. " +
                        "3. Add the rice and oil and stir-fry together.",
                new String[][]{
                        {"rice", "100", "g"},
                        {"egg", "2", "piece"},
                        {"oil", "10", "ml"}
                });

        insertRecipe(db,
                "Cheese Toast",
                "1. Place cheese on the bread. " +
                        "2. Toast in a pan or toaster oven until the cheese melts.",
                new String[][]{
                        {"bread", "2", "piece"},
                        {"cheese", "30", "g"}
                });

        insertRecipe(db,
                "Banana Milk Smoothie",
                "1. Peel the banana. " +
                        "2. Blend it with the milk until smooth.",
                new String[][]{
                        {"banana", "1", "piece"},
                        {"milk", "200", "ml"}
                });
    }

    private void insertRecipe(
            SQLiteDatabase db,
            String name,
            String instructions,
            String[][] ingredients) {

        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("instructions", instructions);

        long recipeId = db.insert("recipes", null, recipeValues);

        if (recipeId == -1) {
            return;
        }

        for (String[] ingredient : ingredients) {
            ContentValues requirementValues = new ContentValues();

            requirementValues.put("recipe_id", recipeId);
            requirementValues.put("ingredient_name", ingredient[0]);
            requirementValues.put(
                    "required_quantity",
                    Double.parseDouble(ingredient[1]));
            requirementValues.put("unit", ingredient[2]);

            db.insert("recipe_ingredients", null, requirementValues);
        }
    }
}
