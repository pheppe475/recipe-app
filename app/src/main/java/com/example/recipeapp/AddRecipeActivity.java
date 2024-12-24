package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recipeapp.database.RecipeDatabaseManager;

public class AddRecipeActivity extends AppCompatActivity {
    private EditText nameEditText, descriptionEditText, categoryEditText;
    private Button saveButton;
    private RecipeDatabaseManager databaseManager;
    private int recipeId = -1; // Untuk mengidentifikasi apakah ini adalah update atau tambah resep

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        categoryEditText = findViewById(R.id.categoryEditText);
        saveButton = findViewById(R.id.saveButton);

        // Inisialisasi database manager
        databaseManager = new RecipeDatabaseManager(this);
        databaseManager.open();

        // Periksa jika ada data resep untuk diupdate
        Intent intent = getIntent();
        if (intent.hasExtra("recipeId")) {
            recipeId = intent.getIntExtra("recipeId", -1);
            Recipe recipe = databaseManager.getAllRecipes().get(recipeId);
            nameEditText.setText(recipe.getName());
            descriptionEditText.setText(recipe.getDescription());
            categoryEditText.setText(recipe.getCategory());
        }

        // Tombol untuk menyimpan resep
        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String category = categoryEditText.getText().toString();

            if (recipeId == -1) {
                // Menambah resep baru
                Recipe newRecipe = new Recipe(name, description, category);
                databaseManager.addRecipe(newRecipe);
            } else {
                // Mengupdate resep yang ada
                Recipe updatedRecipe = new Recipe(recipeId, name, description, category);
                databaseManager.updateRecipe(updatedRecipe);
            }

            // Kembali ke MainActivity
            Intent mainIntent = new Intent(AddRecipeActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseManager.close();
    }
}
