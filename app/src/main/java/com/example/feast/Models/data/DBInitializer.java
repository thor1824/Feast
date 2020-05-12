package com.example.feast.Models.data;

import com.example.feast.Models.Ingredient;
import com.example.feast.Models.Recipes;
import com.example.feast.Models.User;

import java.util.ArrayList;

public class DBInitializer {

    private ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
    private ArrayList<Recipes> recipes = new ArrayList<Recipes>();

    public DBInitializer() {
        generateIngredients();
        generateRecipes();
    }

    private void generateRecipes() {
        Recipes recipe1 = new Recipes(ingredients, "someID", 20, "Flue i suppe");
        Recipes recipe2 = new Recipes(ingredients, "nextID", 30, "lort i nutella");
        Recipes recipe3 = new Recipes(ingredients, "AfterThatId", 20, "Skipperlapskov");
        Recipes recipe4 = new Recipes(ingredients, "WOWid", 40, "Fransk løgsuppe");
        Recipes recipe5 = new Recipes(ingredients, "WOWeeeeeid", 20, "Knepkager");
        Recipes recipe6 = new Recipes(ingredients, "WOWqweqweid", 30, "Arme riddere");
        Recipes recipe7 = new Recipes(ingredients, "WOwqerohkjgfWid", 40, "Benløse fugle");
        Recipes recipe8 = new Recipes(ingredients, "WOWioashfhd", 20, "Bøf Stroganoff");
        Recipes recipe9 = new Recipes(ingredients, "WOWiyasdfiuaod", 15, "Klapsammen mad");
        Recipes recipe10 = new Recipes(ingredients, "WOWbnasdfjgid", 10, "Bare kaffe");
        Recipes recipe11 = new Recipes(ingredients, "WOWijahgsdfhlaid", 60, "Italiansk pizza snitter");
        Recipes recipe12 = new Recipes(ingredients, "WOWijsghfahjugsdfd", 60, "After 8 Snitter");
        Recipes recipe13 = new Recipes(ingredients, "WOuhafgsukgasfWid", 20, "Elgiganten er klam");

        recipes.add(recipe1);
        recipes.add(recipe2);
        recipes.add(recipe3);
        recipes.add(recipe4);
        recipes.add(recipe5);
        recipes.add(recipe6);
        recipes.add(recipe7);
        recipes.add(recipe8);
        recipes.add(recipe9);
        recipes.add(recipe10);
        recipes.add(recipe11);
        recipes.add(recipe12);
        recipes.add(recipe13);
    }

    private void generateIngredients() {
        ingredients.add(new Ingredient("Hvad er Mock?", 2));
        ingredients.add(new Ingredient("noget der simulere noget andet?", 4));
        ingredients.add(new Ingredient("nå ja!", 100));
    }

    public ArrayList<Recipes> getRecipes() {
        return recipes;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }


    public User getUser(){
        User user = new User("Bob", "someId", "someEmail");
        return user;
    }
}
