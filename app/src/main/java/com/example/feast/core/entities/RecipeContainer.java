package com.example.feast.core.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class RecipeContainer implements Serializable {
    private ArrayList<UserRecipes> userRecipes;
    private ArrayList<Recipes> recipes;

    public RecipeContainer(ArrayList<UserRecipes> userRecipes, ArrayList<Recipes> recipes) {
        this.userRecipes = userRecipes;
        this.recipes = recipes;
    }

    public ArrayList<Recipes> getRecipes() {
        return recipes;
    }

    public void setRecipes(ArrayList<Recipes> recipes) {
        this.recipes = recipes;
    }

    public ArrayList<UserRecipes> getUserRecipes() {
        return userRecipes;
    }

    public void setUserRecipes(ArrayList<UserRecipes> userRecipes) {
        this.userRecipes = userRecipes;
    }
}
