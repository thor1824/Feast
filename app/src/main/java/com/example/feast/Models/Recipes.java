package com.example.feast.Models;

import android.content.Intent;

import java.util.ArrayList;

public class Recipes {

    private ArrayList<String> ingredients;

    private Integer estimatedTime;

    private String id;

    private String name;


    public Recipes(ArrayList<String> listOfIngredients, String id, Integer time, String name) {
        this.estimatedTime = time;
        this.ingredients = listOfIngredients;
        this.id = id;
        this.name = name;
    }

    public String getId(){
        return id;
    }

    public ArrayList<String> getIngredients(){
       return ingredients;
    }

    public Integer getEstimatedTime(){
        return estimatedTime;
    }

    public String getName() { return name; }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
