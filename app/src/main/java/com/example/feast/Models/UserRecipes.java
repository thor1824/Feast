package com.example.feast.Models;
import java.util.ArrayList;

public class UserRecipes {

    private ArrayList<String> ingredients;

    private Integer estimatedTime;

    private String id;


    public UserRecipes(ArrayList<String> listOfIngredients, String id, Integer time){
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

