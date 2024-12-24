package com.example.recipeapp;

public class Recipe {

    private int id;
    private String name;
    private String description;
    private String category;

    // Constructor untuk resep baru (tanpa ID)
    public Recipe(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    // Constructor untuk resep dengan ID (untuk update)
    public Recipe(int id, String name, String description, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }
}
