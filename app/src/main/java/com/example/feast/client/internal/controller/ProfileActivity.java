package com.example.feast.client.internal.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    
    private DrawerLayout drawerLayout;
    private ImageView ivProfile;
    private TextView tvUserName;
    private TextView tvEmail;

    private Model model;

    //<editor-fold desc="Overrides">
    /**
     * creates the activity and sets the views.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        model = Model.getInstance();

        setupView();

        setCurrentUser();

    }

    /**
     * sets up the toolbar.
     */
    @Override
    protected void onStart() {
        super.onStart();

        setupToolBar();

    }

    /**
     * checks if the toolbar is opened, and listens for a back button press.
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Setup">
    private void setCurrentUser() {
        FirebaseUser currentUser = model.getCurrentUser();
        tvUserName.setText(currentUser.getDisplayName());
        tvEmail.setText(currentUser.getEmail());

        Picasso.get().load(currentUser.getPhotoUrl()).into(ivProfile);
    }

    private void setupView() {
        tvUserName = findViewById(R.id.displayName);
        tvEmail = findViewById(R.id.displayEmail);
        drawerLayout = findViewById(R.id.drawLayout_profile);
        NavigationView navigationView = findViewById(R.id.navigation_view_profile);
        ivProfile = findViewById(R.id.profileImage);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_profile);
    }

    private void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }
    //</editor-fold>

    //<editor-fold desc="Navigation">
    /**
     * sets up the content in the toolbar.
     * <p>
     * either navigate to another layout, or shows message.
     *
     * @param item
     * @return
     */
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                Intent home_intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(home_intent);
                finish();
                break;
            case R.id.nav_addRecipe:
                Intent recipe_intent = new Intent(ProfileActivity.this, RecipesActivity.class);
                startActivity(recipe_intent);
                finish();
                break;
            case R.id.nav_profile:
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
