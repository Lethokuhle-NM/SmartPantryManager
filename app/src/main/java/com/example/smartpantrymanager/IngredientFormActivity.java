package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class IngredientFormActivity extends AppCompatActivity {

    private EditText editName, editQuantity, editExpiry;
    private Spinner spinnerUnit;
    private TextView txtFormTitle;

    private DatabaseHelper databaseHelper;
    private int ingredientId = -1;

    private final String[] units = {"piece", "g", "kg", "ml", "l"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_form);

        databaseHelper = new DatabaseHelper(this);

        txtFormTitle = findViewById(R.id.txtFormTitle);
        editName = findViewById(R.id.editIngredientName);
        editQuantity = findViewById(R.id.editIngredientQuantity);
        editExpiry = findViewById(R.id.editIngredientExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);

        Button btnSave = findViewById(R.id.btnSaveIngredient);
        Button btnCancel = findViewById(R.id.btnCancelIngredient);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Arrays.asList(units)
        );

        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerUnit.setAdapter(unitAdapter);

        ingredientId = getIntent().getIntExtra("ingredient_id", -1);

        if (ingredientId != -1) {
            loadIngredientForEditing();
        } else {
            txtFormTitle.setText("Add Ingredient");
        }

        btnSave.setOnClickListener(v -> saveIngredient());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadIngredientForEditing() {
        Ingredient ingredient = databaseHelper.getIngredientById(ingredientId);

        if (ingredient == null) {
            Toast.makeText(this, "Ingredient not found",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        txtFormTitle.setText("Edit Ingredient");
        editName.setText(ingredient.getName());
        editQuantity.setText(String.valueOf(ingredient.getQuantity()));
        editExpiry.setText(ingredient.getExpiryDate());

        int unitPosition = Arrays.asList(units)
                .indexOf(ingredient.getUnit());

        if (unitPosition >= 0) {
            spinnerUnit.setSelection(unitPosition);
        }
    }

    private void saveIngredient() {
        String name = editName.getText().toString().trim();
        String quantityText = editQuantity.getText().toString().trim();
        String expiryDate = editExpiry.getText().toString().trim();
        String unit = spinnerUnit.getSelectedItem().toString();

        if (name.isEmpty()) {
            editName.setError("Enter an ingredient name");
            editName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            editQuantity.setError("Enter a quantity");
            editQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            editQuantity.setError("Enter a valid quantity");
            editQuantity.requestFocus();
            return;
        }

        if (quantity <= 0 || Double.isInfinite(quantity)
                || Double.isNaN(quantity)) {
            editQuantity.setError("Quantity must be greater than zero");
            editQuantity.requestFocus();
            return;
        }

        if (!expiryDate.isEmpty() && !isValidDate(expiryDate)) {
            editExpiry.setError("Use a valid date in YYYY-MM-DD format");
            editExpiry.requestFocus();
            return;
        }

        Ingredient ingredient = new Ingredient(
                name, quantity, unit, expiryDate
        );

        if (ingredientId == -1) {
            long result = databaseHelper.addIngredient(ingredient);

            if (result != -1) {
                Toast.makeText(this, "Ingredient added successfully",
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Could not save ingredient",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            ingredient.setId(ingredientId);

            int result = databaseHelper.updateIngredient(ingredient);

            if (result > 0) {
                Toast.makeText(this, "Ingredient updated successfully",
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Could not update ingredient",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isValidDate(String dateText) {
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.ROOT
        );

        format.setLenient(false);

        try {
            Date date = format.parse(dateText);
            return date != null && format.format(date).equals(dateText);
        } catch (ParseException e) {
            return false;
        }
    }
}
