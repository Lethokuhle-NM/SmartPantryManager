package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PantryActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private IngredientAdapter adapter;
    private final List<Ingredient> ingredientList = new ArrayList<>();

    private RecyclerView recyclerPantry;
    private TextView txtEmptyPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        databaseHelper = new DatabaseHelper(this);

        recyclerPantry = findViewById(R.id.recyclerPantry);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);

        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);
        Button btnBackHome = findViewById(R.id.btnBackHome);

        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));

        adapter = new IngredientAdapter(this, ingredientList,
                new IngredientAdapter.IngredientActions() {

                    @Override
                    public void onEdit(Ingredient ingredient) {
                        Intent intent = new Intent(
                                PantryActivity.this, IngredientFormActivity.class);
                        intent.putExtra("ingredient_id", ingredient.getId());
                        startActivity(intent);
                    }

                    @Override
                    public void onDelete(Ingredient ingredient) {
                        databaseHelper.deleteIngredient(ingredient.getId());
                        loadIngredients();

                        Toast.makeText(PantryActivity.this,
                                "Ingredient deleted", Toast.LENGTH_SHORT).show();
                    }
                });

        recyclerPantry.setAdapter(adapter);

        btnAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PantryActivity.this, IngredientFormActivity.class);
            startActivity(intent);
        });

        btnBackHome.setOnClickListener(v -> finish());

        loadIngredients();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && adapter != null) {
            loadIngredients();
        }
    }

    private void loadIngredients() {
        List<Ingredient> savedIngredients = databaseHelper.getAllIngredients();

        adapter.updateIngredients(savedIngredients);

        if (savedIngredients.isEmpty()) {
            txtEmptyPantry.setVisibility(View.VISIBLE);
            recyclerPantry.setVisibility(View.GONE);
        } else {
            txtEmptyPantry.setVisibility(View.GONE);
            recyclerPantry.setVisibility(View.VISIBLE);
        }
    }
}
