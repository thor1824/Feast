package com.example.feast.client.internal.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.feast.R;

public class RecipesActivity extends AppCompatActivity {

    private Button goToAddRecipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        goToAddRecipe = findViewById(R.id.btGoToAddRecipe);
        goToAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGoToAddRecipe();
            }
        });
    }


    public void getGoToAddRecipe() {
        Intent GotoAddRecipe_intent = new Intent(this, AddUserRecipeActivity.class);
        startActivity(GotoAddRecipe_intent);
    }
}
