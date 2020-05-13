package com.example.feast.core.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class RecipeContainer implements Serializable {
    private ArrayList<UserRecipe> userRecipes;
    private ArrayList<Recipe> recipes;

    public RecipeContainer(ArrayList<UserRecipe> userRecipes, ArrayList<Recipe> recipes) {
        this.userRecipes = userRecipes;
        this.recipes = recipes;
    }

    public ArrayList<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
    }

    public ArrayList<UserRecipe> getUserRecipes() {
        return userRecipes;
    }

    public void setUserRecipes(ArrayList<UserRecipe> userRecipes) {
        this.userRecipes = userRecipes;
    }
}
