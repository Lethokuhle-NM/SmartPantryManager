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

    private static final String TABLE_INGREDIENTS = "ingredients";
    private static final String TABLE_RECIPES = "recipes";
    private static final String TABLE_REQUIREMENTS = "recipe_ingredients";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "expiry_date TEXT)");

        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "instructions TEXT NOT NULL)");

        db.execSQL("CREATE TABLE recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, " +
                "ingredient_name TEXT NOT NULL, " +
                "required_quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id) " +
                "ON DELETE CASCADE)");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        // Add database migration code here when the schema changes.
    }

    // CREATE: Add an ingredient to the pantry.
    public long addIngredient(Ingredient ingredient) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", ingredient.getName());
        values.put("quantity", ingredient.getQuantity());
        values.put("unit", ingredient.getUnit());
        values.put("expiry_date", ingredient.getExpiryDate());

        return db.insert(TABLE_INGREDIENTS, null, values);
    }

    // READ: Retrieve all pantry ingredients.
    public List<Ingredient> getAllIngredients() {

        List<Ingredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_INGREDIENTS,
                null, null, null, null, null, "name ASC")) {

            while (cursor.moveToNext()) {
                ingredients.add(new Ingredient(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                        cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"))
                ));
            }
        }

        return ingredients;
    }

    // UPDATE: Edit an existing ingredient.
    public int updateIngredient(Ingredient ingredient) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", ingredient.getName());
        values.put("quantity", ingredient.getQuantity());
        values.put("unit", ingredient.getUnit());
        values.put("expiry_date", ingredient.getExpiryDate());

        return db.update(
                TABLE_INGREDIENTS,
                values,
                "id = ?",
                new String[]{String.valueOf(ingredient.getId())}
        );
    }

    // DELETE: Remove an ingredient from the pantry.
    public int deleteIngredient(int id) {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_INGREDIENTS,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // READ: Retrieve a single ingredient by its ID.
    public Ingredient getIngredientById(int id) {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_INGREDIENTS,
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null, null, null)) {

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

    public List<Recipe> getAllRecipes() {

        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_RECIPES, null, null, null,
                null, null, "name ASC")) {

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

    public List<RecipeIngredient> getAllRecipeIngredients() {

        List<RecipeIngredient> requirements = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_REQUIREMENTS, null, null, null,
                null, null, null)) {

            while (cursor.moveToNext()) {
                requirements.add(new RecipeIngredient(
                        cursor.getInt(cursor.getColumnIndexOrThrow("recipe_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ingredient_name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("required_quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit"))
                ));
            }
        }

        return requirements;
    }

    public Recipe getRecipeById(int id) {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_RECIPES, null, "id = ?",
                new String[]{String.valueOf(id)},
                null, null, null)) {

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

    private void seedRecipes(SQLiteDatabase db) {

        insertRecipe(db, "Tomato Omelette",
                "Beat the eggs. Chop the tomato. Cook both in a lightly oiled pan.",
                new String[]{"egg", "tomato", "oil"},
                new double[]{2, 1, 10},
                new String[]{"piece", "piece", "ml"});

        insertRecipe(db, "Cheese Omelette",
                "Beat the eggs, cook in a pan and add the cheese before folding.",
                new String[]{"egg", "cheese", "oil"},
                new double[]{2, 30, 5},
                new String[]{"piece", "g", "ml"});

        insertRecipe(db, "Tomato Salad",
                "Chop the tomatoes and cucumber. Mix and serve.",
                new String[]{"tomato", "cucumber"},
                new double[]{2, 1},
                new String[]{"piece", "piece"});

        insertRecipe(db, "Garlic Pasta",
                "Boil pasta. Cook garlic in oil, combine and serve.",
                new String[]{"pasta", "garlic", "oil"},
                new double[]{100, 1, 10},
                new String[]{"g", "piece", "ml"});

        insertRecipe(db, "Cheese Pasta",
                "Boil pasta, drain and mix with grated cheese.",
                new String[]{"pasta", "cheese"},
                new double[]{100, 40},
                new String[]{"g", "g"});

        insertRecipe(db, "Banana Oatmeal",
                "Cook oats in milk and serve with sliced banana.",
                new String[]{"oats", "milk", "banana"},
                new double[]{50, 200, 1},
                new String[]{"g", "ml", "piece"});

        insertRecipe(db, "Peanut Butter Banana Toast",
                "Toast the bread and spread peanut butter. Add sliced banana.",
                new String[]{"bread", "peanut butter", "banana"},
                new double[]{2, 20, 1},
                new String[]{"piece", "g", "piece"});

        insertRecipe(db, "Apple Oatmeal",
                "Cook oats in milk and top with chopped apple.",
                new String[]{"oats", "milk", "apple"},
                new double[]{50, 200, 1},
                new String[]{"g", "ml", "piece"});

        insertRecipe(db, "Potato Hash",
                "Dice potatoes and onion. Fry in oil until tender.",
                new String[]{"potato", "onion", "oil"},
                new double[]{2, 1, 15},
                new String[]{"piece", "piece", "ml"});

        insertRecipe(db, "Garlic Rice",
                "Cook rice. Fry chopped garlic in oil and mix through the rice.",
                new String[]{"rice", "garlic", "oil"},
                new double[]{100, 1, 10},
                new String[]{"g", "piece", "ml"});

        insertRecipe(db, "Carrot Salad",
                "Grate the carrots and mix with lemon juice.",
                new String[]{"carrot", "lemon"},
                new double[]{2, 1},
                new String[]{"piece", "piece"});

        insertRecipe(db, "Cucumber Tomato Salad",
                "Slice the cucumber and tomatoes and mix together.",
                new String[]{"cucumber", "tomato"},
                new double[]{1, 2},
                new String[]{"piece", "piece"});

        insertRecipe(db, "Egg Fried Rice",
                "Cook the rice. Scramble the eggs in oil and stir in the rice.",
                new String[]{"rice", "egg", "oil"},
                new double[]{100, 2, 10},
                new String[]{"g", "piece", "ml"});

        insertRecipe(db, "Cheese Toast",
                "Place cheese on bread and toast until the cheese melts.",
                new String[]{"bread", "cheese"},
                new double[]{2, 30},
                new String[]{"piece", "g"});

        insertRecipe(db, "Banana Milk Smoothie",
                "Blend the banana and milk until smooth.",
                new String[]{"banana", "milk"},
                new double[]{1, 200},
                new String[]{"piece", "ml"});
    }

    private void insertRecipe(SQLiteDatabase db,
                              String name,
                              String instructions,
                              String[] ingredientNames,
                              double[] quantities,
                              String[] units) {

        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("instructions", instructions);

        long recipeId = db.insert(TABLE_RECIPES, null, recipeValues);

        if (recipeId == -1) {
            return;
        }

        for (int i = 0; i < ingredientNames.length; i++) {

            ContentValues values = new ContentValues();
            values.put("recipe_id", recipeId);
            values.put("ingredient_name", ingredientNames[i]);
            values.put("required_quantity", quantities[i]);
            values.put("unit", units[i]);

            db.insert(TABLE_REQUIREMENTS, null, values);
        }
    }
}
