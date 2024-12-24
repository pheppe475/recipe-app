package com.example.recipeapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.recipeapp.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeDatabaseManager {

    private static final String DATABASE_NAME = "recipes.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "recipes";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CATEGORY = "category";

    private static final String TAG = "RecipeDatabaseManager";

    private SQLiteDatabase database;
    private final RecipeDatabaseHelper dbHelper;

    public RecipeDatabaseManager(Context context) {
        dbHelper = new RecipeDatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    public void addRecipe(Recipe recipe) {
        if (recipe == null || recipe.getName().isEmpty() || recipe.getDescription().isEmpty() || recipe.getCategory().isEmpty()) {
            Log.e(TAG, "Invalid recipe data. Cannot insert.");
            return;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, recipe.getName());
        values.put(COLUMN_DESCRIPTION, recipe.getDescription());
        values.put(COLUMN_CATEGORY, recipe.getCategory());

        long result = database.insert(TABLE_NAME, null, values);
        if (result == -1) {
            Log.e(TAG, "Failed to insert recipe.");
        } else {
            Log.d(TAG, "Recipe added with ID: " + result);
        }
    }

    public void updateRecipe(Recipe recipe) {
        if (recipe == null || recipe.getId() <= 0) {
            Log.e(TAG, "Invalid recipe data. Cannot update.");
            return;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, recipe.getName());
        values.put(COLUMN_DESCRIPTION, recipe.getDescription());
        values.put(COLUMN_CATEGORY, recipe.getCategory());

        int rowsAffected = database.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(recipe.getId())});
        Log.d(TAG, "Rows updated: " + rowsAffected);
    }

    public void deleteRecipe(int recipeId) {
        if (recipeId <= 0) {
            Log.e(TAG, "Invalid recipe ID. Cannot delete.");
            return;
        }

        int rowsDeleted = database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(recipeId)});
        Log.d(TAG, "Rows deleted: " + rowsDeleted);
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = database.query(TABLE_NAME, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    Recipe recipe = parseCursorToRecipe(cursor);
                    recipes.add(recipe);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching recipes: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return recipes;
    }

    public List<Recipe> searchRecipes(String query) {
        List<Recipe> recipes = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = database.query(TABLE_NAME, null, COLUMN_NAME + " LIKE ?", new String[]{"%" + query + "%"}, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    Recipe recipe = parseCursorToRecipe(cursor);
                    recipes.add(recipe);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error searching recipes: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return recipes;
    }

    private Recipe parseCursorToRecipe(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
        return new Recipe(id, name, description, category);
    }

    private static class RecipeDatabaseHelper extends SQLiteOpenHelper {

        public RecipeDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                    COLUMN_CATEGORY + " TEXT NOT NULL)";
            db.execSQL(createTableQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
