package com.example.feast.client.internal.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.feast.R;
import com.example.feast.client.internal.model.Model;
import com.example.feast.core.entities.IRecipe;
import com.example.feast.core.entities.Ingredient;
import com.example.feast.core.entities.Recipe;
import com.example.feast.core.entities.UserRecipe;
import com.example.feast.data.mock.DBInitializer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Random;


public class DisplayRecipeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    int estimatedTime;
    int randomInt;
    ImageView recipeImage;
    Recipe recipeToBeDisplayed;

    ArrayList<Recipe> recipesWithEstimatedTime = new ArrayList<Recipe>();
    ArrayList<UserRecipe> userRecipesWithEstimatedTime = new ArrayList<UserRecipe>();
    DBInitializer db = new DBInitializer();
    private Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe);

        //-------------------------logic for recipe list--------------------------------\\
        Intent intent = getIntent();

        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        estimatedTime = Integer.parseInt(message);

        ArrayList<Recipe> recipes = db.getRecipes();
        for (Recipe recipe : recipes) {
            if (recipe.getEstimatedTime() == estimatedTime) {
                recipesWithEstimatedTime.add(recipe);
            }
        }

        ArrayList<UserRecipe> userRecipes = db.getUserRecipes();

        for (UserRecipe userRecipe : userRecipes) {
            if (userRecipe.getEstimatedTime() == estimatedTime) {
                userRecipesWithEstimatedTime.add(userRecipe);
            }
        }

        int maxRandom = recipesWithEstimatedTime.size() + userRecipesWithEstimatedTime.size();
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(maxRandom);


        //---------------------instantiate----------------------\\
        drawerLayout = findViewById(R.id.drawLayout_display_recipe);
        navigationView = findViewById(R.id.navigation_view_display_recipe);
        toolbar = findViewById(R.id.toolbar);
        recipeImage = findViewById(R.id.imgRecipe);
        IRecipe recipeToBeDisplayed = model.getRandomRecipe(estimatedTime);
        IRecipe userRecipeToDisplay = model.getRandomRecipe(estimatedTime);
        LinearLayout layoutForName = findViewById(R.id.LinLayIngredients);
        TextView textView = findViewById(R.id.txtHeader);
        LinearLayout layoutForGram = findViewById(R.id.LinLayAmount);
        FloatingActionButton btnShare = findViewById(R.id.fab);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendIngredientsAsSMS();
            }
        });


        //--------------------setData--------------------------\\

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_profile);

        //------------------component generator-----------------\\
        intent = getIntent();
        message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        estimatedTime = Integer.parseInt(message);
        model = Model.getInstance();
        recipeToBeDisplayed = model.getRandomRecipe(estimatedTime);


        if (recipeToBeDisplayed != null) {
            textView.setText(recipeToBeDisplayed.getName());

            for (Ingredient s : recipeToBeDisplayed.getIngredients()) {
                TextView newTextViewIng = new TextView(this);
                TextView newTextViewAmount = new TextView(this);
                newTextViewIng.setText(s.getName());
                newTextViewAmount.setText(s.getAmount() + " Gram");

                layoutForName.addView(newTextViewIng);
                layoutForGram.addView(newTextViewAmount);
            }
        }

        if (userRecipeToDisplay != null) {
            for (Ingredient s : userRecipeToDisplay.getIngredients()) {
                TextView newTextViewIng = new TextView(this);
                TextView newTextViewAmount = new TextView(this);
                newTextViewIng.setText(s.getName());
                newTextViewAmount.setText(s.getAmount() + " Gram");

                layoutForName.addView(newTextViewIng);
                layoutForGram.addView(newTextViewAmount);
            }
        }

        StorageReference gsReference;
        if (shutItBeRecipe()) {
            gsReference = storage.getReferenceFromUrl(recipeToBeDisplayed.getImageUrl());
        } else {
            gsReference = storage.getReferenceFromUrl(userRecipeToDisplay.getImageUrl());
        }


        final long ONE_MEGABYTE = 1024 * 1024;
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                recipeImage.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                System.out.println("failed");
            }
        });
    }


    public boolean shutItBeRecipe() {
        if(randomInt <= recipesWithEstimatedTime.size()){
            return true;

    }
        else return false;
    }

    private void sendIngredientsAsSMS() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        StringBuilder builder = new StringBuilder();

        if (recipeToBeDisplayed != null) {
            builder.append(recipeToBeDisplayed.getName());
            builder.append("\n");
            for (Ingredient i : recipeToBeDisplayed.getIngredients()) {
                builder.append(i.toString());
                builder.append('\n');
            }
        }

        builder.append("Tak fordi du bruger Feast");

        sendIntent.putExtra("sms_body", builder.toString());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
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
                Intent home_intent = new Intent(DisplayRecipeActivity.this, MainActivity.class);
                startActivity(home_intent);
                break;
            case R.id.nav_addRecipe:
                Intent recipe_intent = new Intent(DisplayRecipeActivity.this, RecipesActivity.class);
                startActivity(recipe_intent);
                break;
            case R.id.nav_profile:
                Intent profile_intent = new Intent(DisplayRecipeActivity.this, ProfileActivity.class);
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
