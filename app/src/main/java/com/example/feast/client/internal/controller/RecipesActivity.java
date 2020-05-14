package com.example.feast.client.internal.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.feast.R;
import com.example.feast.client.internal.model.Model;
import com.example.feast.client.internal.utility.adapter.UserRecipeArrayAdapter;
import com.example.feast.client.internal.utility.concurrent.Listener;
import com.example.feast.core.entities.RecipeContainer;
import com.example.feast.core.entities.UserRecipe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class RecipesActivity extends AppCompatActivity {
    private ListView lvUserRecipe;
    private FloatingActionButton goToAddRecipe;
    private Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        model = Model.getInstance();
        model.getAllRecipes(model.getCurrentUser().getUid(), new Listener<RecipeContainer>() {
            @Override
            public void call(RecipeContainer entity) {
                setUpListView(entity.getUserRecipes());
            }
        });

        goToAddRecipe = findViewById(R.id.button_add_ur);
        goToAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGoToAddRecipe();
            }
        });
    }

    private void setUpListView(ArrayList<UserRecipe> ur) {
        lvUserRecipe = findViewById(R.id.listView_userRecipe);
        UserRecipeArrayAdapter adapter = new UserRecipeArrayAdapter(this, R.layout.list_item_user_recipe, ur, lvUserRecipe);
        lvUserRecipe.setAdapter(adapter);
    }


    public void getGoToAddRecipe() {
        Intent GotoAddRecipe_intent = new Intent(this, AddUserRecipeActivity.class);
        startActivity(GotoAddRecipe_intent);
    }
}
