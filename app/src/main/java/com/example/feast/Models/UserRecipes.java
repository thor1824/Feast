package com.example.feast.Models;

import java.util.ArrayList;

public class UserRecipes {

    private ArrayList<Ingredient> ingredients;

    private Integer estimatedTime;

    private String id;

    private String name;


    public UserRecipes(ArrayList<Ingredient> listOfIngredients, String id, Integer time, String name) {
        this.estimatedTime = time;
        this.ingredients = listOfIngredients;
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public Integer getTime() {
        return estimatedTime;
    }

    public String getName(){ return name; }

}

