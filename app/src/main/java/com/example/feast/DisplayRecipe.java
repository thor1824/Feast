package com.example.feast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.feast.Models.Recipes;
import com.example.feast.Models.data.DBInitializer;

import java.util.ArrayList;
import java.util.Random;

public class DisplayRecipe extends AppCompatActivity {

    DBInitializer db = new DBInitializer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        Recipes recipeToBeDisplayed = getRecipe(message);

        TextView textView = findViewById(R.id.TextView);
        if(recipeToBeDisplayed != null){
            textView.setText(recipeToBeDisplayed.getName());
        } else {
            textView.setText("No Recipe Found");
        }

    }

    public Recipes getRecipe(String eTimeFromPrevActivity) {

        int estimatedTime = Integer.parseInt(eTimeFromPrevActivity);
        ArrayList<Recipes> recipes = db.getRecipes();
        ArrayList<Recipes> recipesWithEstimatedTime = new ArrayList<Recipes>();

        for (Recipes recipe : recipes)
        {
            if (recipe.getEstimatedTime() == estimatedTime) {
                recipesWithEstimatedTime.add(recipe);
            }
        }

        if (recipesWithEstimatedTime.isEmpty()){
            return null;
        } else {
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(recipesWithEstimatedTime.size());

            return recipesWithEstimatedTime.get(randomInt);
        }
    }
}
