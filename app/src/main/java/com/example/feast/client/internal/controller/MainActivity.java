package com.example.feast.client.internal.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.example.feast.client.internal.utility.concurrent.Listener;
import com.example.feast.core.entities.RecipeContainer;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.varunest.sparkbutton.SparkButton;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private Spinner mainSpinner;
    private SparkButton sparkButton;
    private String valueFromSpinner;
    private Model model;
    private TextView test;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private String TAG = "app";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        model = Model.getInstance();

        mainSpinner = findViewById(R.id.spinnerMain);
        sparkButton = findViewById(R.id.spark_button);
        drawerLayout = findViewById(R.id.drawLayout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");


        sparkButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                sparkButton.playAnimation();
                goToNextActivity();
            }
        });
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        String[] spinnerValues = getResources().getStringArray(R.array.minutesForSpinner);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainSpinner.setAdapter(adapter);
        test = findViewById(R.id.test);
        test.setText("not Signed in");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = model.getCurrentUser();
        if (currentUser != null) {
            model.getAllRecipes(currentUser.getUid(), new Listener<RecipeContainer>() {
                @Override
                public void call(RecipeContainer entity) {
                    Log.d(TAG, "call: " + entity);
                }
            });
            test.setText(currentUser.getDisplayName());
        }

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinnerMain) {
            this.valueFromSpinner = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void goToNextActivity() {
        Intent intent = new Intent(this, DisplayRecipeActivity.class);
        mainSpinner = findViewById(R.id.spinnerMain);
        String message = mainSpinner.getSelectedItem().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_addRecipe:
                Intent recipe_intent = new Intent(MainActivity.this, RecipesActivity.class);
                startActivity(recipe_intent);
                break;
            case R.id.nav_profile:
                Intent profile_intent = new Intent(MainActivity.this, ProfileActivity.class);
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
            case R.id.nav_logOut:
                model.signOut();
                Intent signOutIntent = new Intent(this, LoginActivity.class);
                startActivity(signOutIntent);
                finishAffinity();

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       model.CancelTasks();
    }
}
