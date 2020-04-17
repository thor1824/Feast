package com.example.feast.Models;

import android.content.Intent;

import java.util.ArrayList;

public class Recipes {

    private ArrayList<String> ingredients;

    private Integer estimatedTime;

    private String id;


    public Recipes(ArrayList<String> listOfIngredients, String id, Integer time){
        this.estimatedTime = time;
        this.ingredients = listOfIngredients;
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public ArrayList<String> getIngredients(){
       return ingredients;
    }

    public Integer getTime(){
        return estimatedTime;
    }


}
