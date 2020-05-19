package com.example.feast.client.internal.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.feast.R;
import com.example.feast.client.internal.model.Model;
import com.example.feast.client.internal.utility.adapter.UserRecipeArrayAdapter;
import com.example.feast.client.internal.utility.concurrent.Listener;
import com.example.feast.client.internal.utility.globals.RequestCodes;
import com.example.feast.core.entities.RecipeContainer;
import com.example.feast.core.entities.UserRecipe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class RecipesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "RecipesActivity";

    private ListView lvUserRecipe;
    private DrawerLayout mainView;
    private FloatingActionButton btnAddRecipe;

    private Model model;

    //<editor-fold desc="Overrides">

    /**
     * sets up the activity, and sets the views.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        model = Model.getInstance();

        setupViews();
        setupNavigation();
        setupListener();
    }

    /**
     * Starts the toolbar.
     */
    @Override
    protected void onStart() {
        super.onStart();

        setupToolBar();
    }

    /**
     * checks the resultcodes from an activity
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestCodes.RC_UPDATE: {
                if (RESULT_OK == resultCode) {
                    model.forceUpdate();
                }
            }
        }

    }

    /**
     * checkes if the toolbar is opened
     */
    @Override
    public void onBackPressed() {
        if (mainView.isDrawerOpen(GravityCompat.START)) {
            mainView.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Setup">
    private void setupNavigation() {
        NavigationView navigationView = findViewById(R.id.navigation_recipes);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
        navigationView.setCheckedItem(R.id.nav_addRecipe);
    }

    private void setupListener() {
        btnAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onNewRecipe();
            }
        });

        model.getAllRecipes(model.getCurrentUser().getUid(), new Listener<RecipeContainer>() {
            @Override
            public void call(RecipeContainer entity) {
                setUpListView(entity.getUserRecipes());
            }
        });
    }

    /**
     * sets up most views
     */
    private void setupViews() {
        mainView = findViewById(R.id.drawLayout_recipes);
        btnAddRecipe = findViewById(R.id.button_add_ur);
    }

    private void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mainView, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mainView.addDrawerListener(toggle);

        toggle.syncState();
    }

    /**
     * sets up the list views for userRecipes
     *
     * @param urs
     */
    private void setUpListView(ArrayList<UserRecipe> urs) {
        lvUserRecipe = findViewById(R.id.listView_userRecipe);
        UserRecipeArrayAdapter adapter = new UserRecipeArrayAdapter(this, R.layout.list_item_user_recipe, urs, lvUserRecipe);
        lvUserRecipe.setAdapter(adapter);
        lvUserRecipe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserRecipe ur = (UserRecipe) lvUserRecipe.getItemAtPosition(position);
                onToUserRecipe(ur);
            }
        });

    }
    //</editor-fold>

    //<editor-fold desc="Button Actions">

    /**
     * starts a activity to update userRecipe
     *
     * @param ur
     */
    private void onToUserRecipe(UserRecipe ur) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("ur", ur);
        startActivityForResult(intent, RequestCodes.RC_UPDATE);
    }


    /**
     * opens the addRecipeActivity
     */
    private void onNewRecipe() {
        Intent GotoAddRecipe_intent = new Intent(this, AddUserRecipeActivity.class);
        startActivityForResult(GotoAddRecipe_intent, RequestCodes.RC_UPDATE);
    }
    //</editor-fold>

    //<editor-fold desc="Navigation">

    /**
     * sets up the toolbar with navigation or messages
     *
     * @param item
     * @return
     */
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

            case R.id.nav_logOut:
                model.signOut();
                Intent signOutIntent = new Intent(this, LoginActivity.class);
                startActivity(signOutIntent);
                finishAffinity();

        }
        mainView.closeDrawer(GravityCompat.START);
        return true;
    }
    //</editor-fold>

}
