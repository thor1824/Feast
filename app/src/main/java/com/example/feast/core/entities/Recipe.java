package com.example.feast.core.entities;

import java.util.ArrayList;

public class Recipe implements IRecipe {

    private ArrayList<Ingredient> ingredients;

    private long estimatedTime;

    private String id;

    private String name;


    public Recipe(ArrayList<Ingredient> listOfIngredients, String id, long time, String name) {
        this.estimatedTime = time;
        this.ingredients = listOfIngredients;
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public long getEstimatedTime() {
        return estimatedTime;
    }

    @Override
    public void setEstimatedTime(long estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
