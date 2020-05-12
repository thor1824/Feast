package com.example.feast;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.feast.Models.Ingredient;
import com.example.feast.Models.Recipes;
import com.example.feast.Models.UserRecipes;
import com.example.feast.Models.data.DBInitializer;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Random;

public class DisplayRecipe extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DBInitializer db = new DBInitializer();
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    int estimatedTime;
    int randomInt;

    ArrayList<Recipes> recipesWithEstimatedTime = new ArrayList<Recipes>();
    ArrayList<UserRecipes> userRecipesWithEstimatedTime = new ArrayList<UserRecipes>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe);


        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        estimatedTime = Integer.parseInt(message);

        ArrayList<Recipes> recipes = db.getRecipes();
        for (Recipes recipe : recipes) {
            if (recipe.getEstimatedTime() == estimatedTime) {
                recipesWithEstimatedTime.add(recipe);
            }
        }

        ArrayList<UserRecipes> userRecipes = db.getUserRecipes();

        for (UserRecipes userRecipe : userRecipes) {
            if (userRecipe.getTime() == estimatedTime) {
                userRecipesWithEstimatedTime.add(userRecipe);
            }
        }

        int maxRandom = recipesWithEstimatedTime.size() + userRecipesWithEstimatedTime.size();
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(maxRandom + 1);


        //---------------------instantiate----------------------\\
        drawerLayout = findViewById(R.id.drawLayout_display_recipe);
        navigationView = findViewById(R.id.navigation_view_display_recipe);
        toolbar = findViewById(R.id.toolbar);
        Recipes recipeToBeDisplayed = getRecipe();
        UserRecipes userRecipeToDisplay = getUserRecipe();
        LinearLayout layoutForName = findViewById(R.id.LinLayIngredients);
        TextView textView = findViewById(R.id.txtHeader);


        //--------------------setData--------------------------\\

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_profile);



        //------------------component generator-----------------\\


        if (recipeToBeDisplayed != null) {
            textView.setText(recipeToBeDisplayed.getName());
        } else if (userRecipeToDisplay != null) {
            textView.setText(userRecipeToDisplay.getName());
        } else {
            textView.setText("No Recipe Found");
        }

        if (recipeToBeDisplayed != null) {
            for (Ingredient s : recipeToBeDisplayed.getIngredients()) {
                TextView newTextView = new TextView(this);

                newTextView.setText(s.getName() + s.getAmount());

                layoutForName.addView(newTextView);
            }
        }

        if (userRecipeToDisplay != null) {
            for (Ingredient s : userRecipeToDisplay.getIngredients()) {
                TextView newTextView = new TextView(this);

                newTextView.setText(s.getName() + s.getAmount());

                layoutForName.addView(newTextView);
            }
        }

    }


    public boolean shutItBeRecipe() {
        if (randomInt < recipesWithEstimatedTime.size()) {
            return true;
        }
        return false;
    }


    public Recipes getRecipe() {
        if (shutItBeRecipe()) {
            return recipesWithEstimatedTime.get(randomInt);
        }

        return null;
    }

    public UserRecipes getUserRecipe() {
        if (!shutItBeRecipe()) {
            return userRecipesWithEstimatedTime.get(randomInt - recipesWithEstimatedTime.size());
        }

        return null;
    }


    @Override
    protected void onStart() {
        super.onStart();

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                Intent home_intent = new Intent(DisplayRecipe.this, MainActivity.class);
                startActivity(home_intent);
                break;
            case R.id.nav_addRecipe:
                Intent recipe_intent = new Intent(DisplayRecipe.this, RecipesActivity.class);
                startActivity(recipe_intent);
                break;
            case R.id.nav_profile:
                Intent profile_intent = new Intent(DisplayRecipe.this, ProfileActivity.class);
                startActivity(profile_intent);
                break;

            case R.id.nav_settings:
                Toast.makeText(this, "We Have No Settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_contact:
                Toast.makeText(this, "We Can't Be Contacted", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_rating:
                Toast.makeText(this, "You Have Rated Us 5 Stars. Thank You <3", Toast.LENGTH_SHORT).show();
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
