package com.example.recipeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.database.RecipeDatabaseManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText nameEditText, descriptionEditText, categoryEditText;
    private Button addRecipeButton;
    private RecyclerView recipeRecyclerView;
    private RecipeAdapter recipeAdapter;
    private RecipeDatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi UI dan database manager
        initUI();
        dbManager = new RecipeDatabaseManager(this);

        // Atur RecyclerView
        setupRecyclerView();

        // Tombol untuk menambahkan resep
        addRecipeButton.setOnClickListener(v -> handleAddRecipe());

        // Muat data resep
        loadRecipes();
    }

    private void initUI() {
        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        categoryEditText = findViewById(R.id.categoryEditText);
        addRecipeButton = findViewById(R.id.addRecipeButton);
        recipeRecyclerView = findViewById(R.id.recipeRecyclerView);
    }

    private void setupRecyclerView() {
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeAdapter = new RecipeAdapter(this, null); // Inisialisasi adapter dengan data null
        recipeRecyclerView.setAdapter(recipeAdapter);
    }

    private void handleAddRecipe() {
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String category = categoryEditText.getText().toString().trim();

        if (validateInput(name, description, category)) {
            Recipe recipe = new Recipe(name, description, category);

            dbManager.open();
            try {
                dbManager.addRecipe(recipe);
                Toast.makeText(this, "Recipe added!", Toast.LENGTH_SHORT).show();
            } finally {
                dbManager.close();
            }

            clearInputFields();
            loadRecipes(); // Refresh data
        }
    }

    private boolean validateInput(String name, String description, String category) {
        if (name.isEmpty() || description.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void clearInputFields() {
        nameEditText.setText("");
        descriptionEditText.setText("");
        categoryEditText.setText("");
    }

    private void loadRecipes() {
        List<Recipe> recipes;

        dbManager.open();
        try {
            recipes = dbManager.getAllRecipes();
        } finally {
            dbManager.close();
        }

        // Perbarui dataset adapter
        if (recipeAdapter != null) {
            recipeAdapter.updateRecipes(recipes);
        }
    }
}
