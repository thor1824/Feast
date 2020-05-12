package com.example.feast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.feast.Models.Ingredient;
import com.example.feast.Models.Recipes;
import com.example.feast.Models.data.DBInitializer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class DisplayRecipe extends AppCompatActivity {

    DBInitializer db = new DBInitializer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe);

        Intent intent = getIntent();

        FloatingActionButton btnShare = findViewById(R.id.fab);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSMS();
            }
        });

        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        Recipes recipeToBeDisplayed = getRecipe(message);

        TextView textView = findViewById(R.id.txtHeader);
        if (recipeToBeDisplayed != null) {
            textView.setText(recipeToBeDisplayed.getName());
        } else {
            textView.setText("No Recipe Found");
        }

        LinearLayout layoutForName = findViewById(R.id.LinLayIngredients);
        LinearLayout layoutForGram = findViewById(R.id.LinLayAmount);


        for (Ingredient s : recipeToBeDisplayed.getIngredients()) {
            TextView newTextViewIng = new TextView(this);
            TextView newTextViewAmount = new TextView(this);

            newTextViewIng.setText(s.getName());
            newTextViewAmount.setText(Integer.toString(s.getAmount()) + " Gram");

            layoutForName.addView(newTextViewIng);
            layoutForGram.addView(newTextViewAmount);
        }
    }

    public Recipes getRecipe(String eTimeFromPrevActivity) {

        int estimatedTime = Integer.parseInt(eTimeFromPrevActivity);
        ArrayList<Recipes> recipes = db.getRecipes();
        ArrayList<Recipes> recipesWithEstimatedTime = new ArrayList<Recipes>();

        for (Recipes recipe : recipes) {
            if (recipe.getEstimatedTime() == estimatedTime) {
                recipesWithEstimatedTime.add(recipe);
            }
        }

        if (recipesWithEstimatedTime.isEmpty()) {
            return null;
        } else {
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(recipesWithEstimatedTime.size());

            return recipesWithEstimatedTime.get(randomInt);
        }
    }

    private void goToSMS() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_MESSAGING);
        startActivity(intent);
        //TODO
    }
}
