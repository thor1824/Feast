package com.example.feast.client.internal.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.example.feast.client.internal.utility.globals.ExtraKeys;
import com.example.feast.core.entities.RecipeContainer;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.varunest.sparkbutton.SparkButton;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private Spinner spinnerEstimatedTime;
    private SparkButton btnRandomReButton;
    private TextView tvNamePlate;

    private Model model;

    //<editor-fold desc="Overrides">

    /**
     * creates the activity, and sets up the views.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        model = Model.getInstance();
        setupView();
        setupSpinner();
        setupListener();


    }

    /**
     * checks if there is  a user logged in or not.
     * if logged in, it gets all the recipes from firebase.
     */
    @Override
    protected void onStart() {
        super.onStart();
        loadRecipes();
        setupToolBar();
    }

    /**
     * checks if the toolbar is opened
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * when its destroyed it cancels all tasks.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.CancelTasks();
    }
    //</editor-fold>

    //<editor-fold desc="Setup">
    private void setupSpinner() {
        spinnerEstimatedTime = findViewById(R.id.spinnerMain);
        String[] spinnerValues = getResources().getStringArray(R.array.minutesForSpinner);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstimatedTime.setAdapter(adapter);

    }

    private void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();
    }

    private void setupView() {
        tvNamePlate = findViewById(R.id.test);
        tvNamePlate.setText("not Signed in");
        btnRandomReButton = findViewById(R.id.spark_button);
        drawerLayout = findViewById(R.id.drawLayout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
    }

    private void setupListener() {
        btnRandomReButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                btnRandomReButton.playAnimation();
                onDisplayRandomRecipe();
            }
        });
    }

    private void loadRecipes() {
        FirebaseUser currentUser = model.getCurrentUser();
        if (currentUser != null) {
            model.getAllRecipes(currentUser.getUid(), new Listener<RecipeContainer>() {
                @Override
                public void call(RecipeContainer entity) {
                    Log.d(TAG, "call: " + entity);
                }
            });
            tvNamePlate.setText(currentUser.getDisplayName());
        }
    }
    //</editor-fold>

    //<editor-fold desc="Button Actions">

    /**
     * takes the selected item in the spinner and parses it to the next activity
     */
    public void onDisplayRandomRecipe() {
        Intent intent = new Intent(this, DisplayRecipeActivity.class);
        spinnerEstimatedTime = findViewById(R.id.spinnerMain);
        String message = spinnerEstimatedTime.getSelectedItem().toString();
        intent.putExtra(ExtraKeys.EXTRA_KEY_TIME, message);
        startActivity(intent);
    }
    //</editor-fold>

    //<editor-fold desc="Navigation">

    /**
     * sets up the toolbar
     *
     * @param item
     * @return
     */
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
    //</editor-fold>


}
