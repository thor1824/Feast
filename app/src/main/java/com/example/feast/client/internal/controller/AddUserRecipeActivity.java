package com.example.feast.client.internal.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.example.feast.core.entities.Ingredient;
import com.example.feast.core.entities.UserRecipe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddUserRecipeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private LinearLayout addIngLayout;
    private ScrollView scrollView;
    private EditText recipeNameField;
    private EditText estimatedTimeField;
    private EditText firstIngFiled;
    private EditText firstAmountField;
    private FloatingActionButton button;
    private int editFieldId = 0;
    private ArrayList<HashMap<String, Integer>> ingNameList;
    private Model model;
    private Button submitButton;
    private LinearLayout ingContainer;
    private ArrayList<LinearLayout> layouts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_recipe);

        //-------------------instantiate-------------------\\
        drawerLayout = findViewById(R.id.drawLayout_addRecipe);
        navigationView = findViewById(R.id.navigation_view_addRecipe);
        toolbar = findViewById(R.id.toolbar);
        scrollView = findViewById(R.id.scrollView);
        addIngLayout = findViewById(R.id.addIngLayout);
        recipeNameField = findViewById(R.id.editName);
        estimatedTimeField = findViewById(R.id.editTime);
        firstIngFiled = findViewById(R.id.editIng);
        firstAmountField = findViewById(R.id.editIngAmount);
        button = findViewById(R.id.addIngButton);
        ingNameList = new ArrayList<HashMap<String, Integer>>();
        model = Model.getInstance();
        submitButton = findViewById(R.id.submitBt);
        ingContainer = findViewById(R.id.linIngContainer);
        layouts = new ArrayList<>();

        //---------------------------------------------------\\
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIngIngredient();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserRecipe();
            }
        });
        toolbar.setTitle("");

    }

    public void addIngIngredient() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        final LinearLayout editContainer = new LinearLayout(this);
        editContainer.setLayoutParams(p);
        editContainer.setOrientation(LinearLayout.HORIZONTAL);
        addIngLayout.addView(editContainer);


        editFieldId++;
        final EditText ingName = new EditText(this);
        ingName.setHint("Ingredient");
        ingName.setLayoutParams(p);
        ingName.setId(editFieldId);
        ingName.setLayoutParams(new LinearLayout.LayoutParams(600, LinearLayout.LayoutParams.WRAP_CONTENT));
        editContainer.addView(ingName);

        int nameId = editFieldId;

        editFieldId++;
        final EditText ingAmount = new EditText(this);
        ingAmount.setHint("Amount");
        ingAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        ingAmount.setLayoutParams(p);
        ingAmount.setId(editFieldId);
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p2.leftMargin = 30;
        ingAmount.setLayoutParams(p2);
        editContainer.addView(ingAmount);

        int amountId = editFieldId;

        FloatingActionButton deleteIngButton = new FloatingActionButton(this);
        deleteIngButton.setImageResource(R.drawable.delete_icon);
        LinearLayout.LayoutParams p3 = (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        p3.leftMargin = 30;
        deleteIngButton.setLayoutParams(p3);
        deleteIngButton.setCompatElevation(0);
        editContainer.addView(deleteIngButton);


        HashMap<String, Integer> bob = new HashMap<String, Integer>();
        bob.put("name", nameId);
        bob.put("amount", amountId);
        ingNameList.add(bob);
        layouts.add(editContainer);
        deleteIngButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIngLayout.removeView(editContainer);

            }
        }));


    }


    private void saveUserRecipe() {

        String recipeName = recipeNameField.getText().toString();
        System.out.println(estimatedTimeField.getText().toString());
        long estimatedTime = Long.parseLong(estimatedTimeField.getText().toString());
        String firstIngName = firstIngFiled.getText().toString();
        Long firstAmount = Long.parseLong(firstAmountField.getText().toString());
        ArrayList<Ingredient> ingredients = new ArrayList<>();

        Ingredient firstIng = new Ingredient(firstIngName, firstAmount);

        ingredients.add(firstIng);


        for (HashMap<String, Integer> map : ingNameList) {
            EditText name = findViewById(map.get("name"));
            EditText amount = findViewById(map.get("amount"));

            String nameFromField = name.getText().toString();
            long amountFromField = Long.valueOf(amount.getText().toString());

            ingredients.add(new Ingredient(nameFromField, amountFromField));
        }

        UserRecipe recipe = new UserRecipe(ingredients, estimatedTime, recipeName, model.getCurrentUser().getUid());
        model.createUserRecipe(recipe);

        recipeNameField.getText().clear();
        recipeNameField.setHint("Recipe Name");
        estimatedTimeField.getText().clear();
        estimatedTimeField.setHint("Estimated Time");
        firstIngFiled.getText().clear();
        firstIngFiled.setHint("Ingredient");
        firstAmountField.getText().clear();
        firstAmountField.setHint("Amount");

        for (LinearLayout layout: layouts) {
            addIngLayout.removeView(layout);
        }


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
                Intent home_intent = new Intent(this, MainActivity.class);
                startActivity(home_intent);
                finish();
                break;
            case R.id.nav_addRecipe:
                Intent recipe_intent = new Intent(this, RecipesActivity.class);
                startActivity(recipe_intent);
                finish();
                break;
            case R.id.nav_profile:
                Intent profile_intent = new Intent(this, ProfileActivity.class);
                startActivity(profile_intent);
                finish();
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
