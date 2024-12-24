package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.database.RecipeDatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipeList;
    private Context context;

    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList != null ? recipeList : new ArrayList<>();
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.recipeName.setText(recipe.getName());
        holder.recipeDescription.setText(recipe.getDescription());
        holder.recipeCategory.setText(recipe.getCategory());

        // Menangani aksi tombol delete
        holder.deleteButton.setOnClickListener(v -> {
            deleteRecipe(position, recipe);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    // Metode untuk memperbarui dataset
    public void updateRecipes(List<Recipe> newRecipes) {
        this.recipeList = newRecipes != null ? newRecipes : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Metode untuk menghapus item dari dataset
    private void deleteRecipe(int position, Recipe recipe) {
        RecipeDatabaseManager dbManager = new RecipeDatabaseManager(context);
        dbManager.open();
        try {
            dbManager.deleteRecipe(recipe.getId());
            recipeList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Recipe deleted", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Error deleting recipe", Toast.LENGTH_SHORT).show();
        } finally {
            dbManager.close();
        }
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {

        TextView recipeName, recipeDescription, recipeCategory;
        Button deleteButton;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeName);
            recipeDescription = itemView.findViewById(R.id.recipeDescription);
            recipeCategory = itemView.findViewById(R.id.recipeCategory);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
