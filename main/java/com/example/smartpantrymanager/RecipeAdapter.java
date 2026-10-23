package com.example.smartpantrymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface RecipeActions {
        void onViewRecipe(Recipe recipe);
    }

    private final Context context;
    private final List<Recipe> recipes;
    private final RecipeActions actions;

    public RecipeAdapter(Context context, List<Recipe> recipes,
                         RecipeActions actions) {
        this.context = context;
        this.recipes = recipes;
        this.actions = actions;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_recipe, parent, false);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecipeViewHolder holder, int position) {

        Recipe recipe = recipes.get(position);

        holder.txtRecipeName.setText(recipe.getName());

        String instructions = recipe.getInstructions();

        if (instructions == null || instructions.trim().isEmpty()) {
            holder.txtRecipeDescription.setText(
                    "View this recipe for more details.");
        } else if (instructions.length() > 100) {
            holder.txtRecipeDescription.setText(
                    instructions.substring(0, 100) + "...");
        } else {
            holder.txtRecipeDescription.setText(instructions);
        }

        holder.btnViewRecipe.setOnClickListener(
                v -> actions.onViewRecipe(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void updateRecipes(List<Recipe> updatedRecipes) {
        recipes.clear();
        recipes.addAll(updatedRecipes);
        notifyDataSetChanged();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {

        TextView txtRecipeName;
        TextView txtRecipeDescription;
        Button btnViewRecipe;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            txtRecipeName =
                    itemView.findViewById(R.id.txtRecipeName);

            txtRecipeDescription =
                    itemView.findViewById(R.id.txtRecipeDescription);

            btnViewRecipe =
                    itemView.findViewById(R.id.btnViewRecipe);
        }
    }
}
