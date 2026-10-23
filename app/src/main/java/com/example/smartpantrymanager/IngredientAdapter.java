package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    public interface IngredientActions {
        void onEdit(Ingredient ingredient);
        void onDelete(Ingredient ingredient);
    }

    private final Context context;
    private final List<Ingredient> ingredients;
    private final IngredientActions actions;

    public IngredientAdapter(Context context, List<Ingredient> ingredients,
                             IngredientActions actions) {
        this.context = context;
        this.ingredients = ingredients;
        this.actions = actions;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_ingredient, parent, false);

        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);

        holder.txtName.setText(ingredient.getName());
        holder.txtQuantity.setText(
                "Quantity: " + ingredient.getQuantity() + " " + ingredient.getUnit()
        );

        String expiryDate = ingredient.getExpiryDate();

        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            holder.txtExpiry.setText("Expiry date: Not specified");
        } else {
            holder.txtExpiry.setText("Expiry date: " + expiryDate);
        }

        holder.btnEdit.setOnClickListener(v -> actions.onEdit(ingredient));

        holder.btnDelete.setOnClickListener(v ->
                new AlertDialog.Builder(context)
                        .setTitle("Delete ingredient")
                        .setMessage("Are you sure you want to delete "
                                + ingredient.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) ->
                                actions.onDelete(ingredient))
                        .setNegativeButton("Cancel", null)
                        .show()
        );
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public void updateIngredients(List<Ingredient> updatedIngredients) {
        ingredients.clear();
        ingredients.addAll(updatedIngredients);
        notifyDataSetChanged();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtQuantity, txtExpiry;
        Button btnEdit, btnDelete;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtIngredientName);
            txtQuantity = itemView.findViewById(R.id.txtIngredientQuantity);
            txtExpiry = itemView.findViewById(R.id.txtIngredientExpiry);
            btnEdit = itemView.findViewById(R.id.btnEditIngredient);
            btnDelete = itemView.findViewById(R.id.btnDeleteIngredient);
        }
    }
}


