package com.example.feast.data.mock;

import com.example.feast.core.entities.Ingredient;
import com.example.feast.core.entities.Recipes;
import com.example.feast.core.entities.UserRecipes;

import java.util.ArrayList;

public class DBInitializer {

    private ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
    private ArrayList<Recipes> recipes = new ArrayList<Recipes>();
    private ArrayList<UserRecipes> userRecipes = new ArrayList<UserRecipes>();

    public DBInitializer() {
        generateIngredients();
        generateRecipes();
        generateUserRecipes();
    }

    private void generateRecipes() {
        Recipes recipe1 = new Recipes(ingredients, "someID", 20, "Ikke Flue i suppe", "gs://feast-782f3.appspot.com/images/recipe/salt-i-kaffe.jpg");
        Recipes recipe2 = new Recipes(ingredients, "nextID", 30, "lort i nutella", "gs://feast-782f3.appspot.com/images/recipe/salt-i-kaffe.jpg");
        Recipes recipe3 = new Recipes(ingredients, "AfterThatId", 20, "Skipperlapskov", "gs://feast-782f3.appspot.com/images/recipe/salt-i-kaffe.jpg");
        Recipes recipe4 = new Recipes(ingredients, "WOWid", 40, "Fransk løgsuppe", "gs://feast-782f3.appspot.com/images/recipe/salt-i-kaffe.jpg");
        Recipes recipe5 = new Recipes(ingredients, "WOWeeeeeid", 20, "Knepkager", "gs://feast-782f3.appspot.com/images/recipe/salt-i-kaffe.jpg");
        Recipes recipe6 = new Recipes(ingredients, "WOWqweqweid", 30, "Arme riddere", "gs://feast-782f3.appspot.com/images/recipe/lars bilde.PNG");
        Recipes recipe7 = new Recipes(ingredients, "WOwqerohkjgfWid", 40, "Benløse fugle", "gs://feast-782f3.appspot.com/images/recipe/lars bilde.PNG");
        Recipes recipe8 = new Recipes(ingredients, "WOWioashfhd", 30, "Bøf Stroganoff", "gs://feast-782f3.appspot.com/images/recipe/lars bilde.PNG");
        Recipes recipe9 = new Recipes(ingredients, "WOWiyasdfiuaod", 15, "Klapsammen mad", "gs://feast-782f3.appspot.com/images/recipe/gmod.png");
        Recipes recipe10 = new Recipes(ingredients, "WOWbnasdfjgid", 30, "Bare kaffe", "gs://feast-782f3.appspot.com/images/recipe/gmod.png");
        Recipes recipe11 = new Recipes(ingredients, "WOWijahgsdfhlaid", 60, "Italiansk pizza snitter", "gs://feast-782f3.appspot.com/images/recipe/gmod.png");
        Recipes recipe12 = new Recipes(ingredients, "WOWijsghfahjugsdfd", 60, "After 8 Snitter", "gs://feast-782f3.appspot.com/images/recipe/gmod.png");
        Recipes recipe13 = new Recipes(ingredients, "WOuhafgsukgasfWid", 20, "Elgiganten er klam", "gs://feast-782f3.appspot.com/images/recipe/gmod.png");

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

    public void generateUserRecipes() {

        UserRecipes recipe1 = new UserRecipes(ingredients, "someID1", 10, "Flue i suppe", "1");
        UserRecipes recipe2 = new UserRecipes(ingredients, "someID2", 15, "Flue i suppe", "2");
        UserRecipes recipe3 = new UserRecipes(ingredients, "someID3", 20, "Flue i suppe", "3");
        UserRecipes recipe4 = new UserRecipes(ingredients, "someID4", 25, "Flue i suppe", "4");
        UserRecipes recipe5 = new UserRecipes(ingredients, "someID5", 30, "Flue i suppe", "5");
        UserRecipes recipe6 = new UserRecipes(ingredients, "someID6", 45, "Flue i suppe", "6");
        UserRecipes recipe7 = new UserRecipes(ingredients, "someID7", 50, "Flue i suppe", "7");
        UserRecipes recipe8 = new UserRecipes(ingredients, "someID8", 20, "Flue i suppe", "8");
        UserRecipes recipe9 = new UserRecipes(ingredients, "someID9", 20, "Flue i suppe", "9");
        UserRecipes recipe10 = new UserRecipes(ingredients, "someID10", 20, "Flue i suppe", "10");

        userRecipes.add(recipe1);
        userRecipes.add(recipe2);
        userRecipes.add(recipe3);
        userRecipes.add(recipe4);
        userRecipes.add(recipe5);
        userRecipes.add(recipe6);
        userRecipes.add(recipe7);
        userRecipes.add(recipe8);
        userRecipes.add(recipe9);
        userRecipes.add(recipe10);


    }

    private void generateIngredients() {
        ingredients.add(new Ingredient("Hvad er Mock?", 2));
        ingredients.add(new Ingredient("noget der simulere noget andet?", 4));
        ingredients.add(new Ingredient("nå ja!", 100));
        ingredients.add(new Ingredient("nå ja!", 100));
        ingredients.add(new Ingredient("nå ja!", 100));
        ingredients.add(new Ingredient("nå ja!", 100));
        ingredients.add(new Ingredient("nå ja!", 100));
        ingredients.add(new Ingredient("nå ja!", 100));
        ingredients.add(new Ingredient("nå ja!", 100));
        ingredients.add(new Ingredient("nå ja!", 100));
        ingredients.add(new Ingredient("nå ja!", 100));
        ingredients.add(new Ingredient("nå ja!", 100));
        ingredients.add(new Ingredient("nå ja!", 100));

    }

    public ArrayList<Recipes> getRecipes() {
        return recipes;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<UserRecipes> getUserRecipes() {
        return userRecipes;
    }

}
