package com.example.feast.client.internal.controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.feast.R;
import com.example.feast.client.internal.model.Model;
import com.example.feast.core.entities.IRecipe;
import com.example.feast.core.entities.Ingredient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;


public class DisplayRecipeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CODE_SHARE = 106;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private int estimatedTime;
    private ImageView recipeImage;
    private IRecipe recipeToBeDisplayed;
    private TextView textView;
    private LinearLayout layoutForGram;
    private LinearLayout layoutForName;

    private Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe);

        //---------------------instantiate----------------------\\
        drawerLayout = findViewById(R.id.drawLayout_display_recipe);
        navigationView = findViewById(R.id.navigation_view_display_recipe);
        toolbar = findViewById(R.id.toolbar);
        recipeImage = findViewById(R.id.imgRecipe);
        layoutForName = findViewById(R.id.LinLayIngredients);
        textView = findViewById(R.id.txtHeader);
        layoutForGram = findViewById(R.id.LinLayAmount);
        FloatingActionButton btnShare = findViewById(R.id.fab);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askSharePermissions();
            }
        });
        final Button btnPick = findViewById(R.id.button_pickAnother);
        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewRandomRecipe();
            }
        });



        //--------------------setData--------------------------\\

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_profile);
        toolbar.setTitle("");

        //------------------component generator-----------------\\
        getNewRandomRecipe();

    }

    private void setRecipe(IRecipe recipe) {
        layoutForName.removeAllViews();
        layoutForGram.removeAllViews();
        if (recipe != null) {
            textView.setText(recipe.getName());

            for (Ingredient s : recipe.getIngredients()) {
                TextView newTextViewIng = new TextView(this);
                TextView newTextViewAmount = new TextView(this);
                newTextViewIng.setText(s.getName());
                newTextViewAmount.setText(s.getAmount() + " Gram");

                layoutForName.addView(newTextViewIng);
                layoutForGram.addView(newTextViewAmount);
            }

            if (recipe.getImageUrl() != null && recipe.getImageUrl().length() > 0) {
                model.getImage(recipe.getImageUrl()).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
        } else {
            textView.setText("No Recipe Found");
        }
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
                finish();
                break;
            case R.id.nav_addRecipe:
                Intent recipe_intent = new Intent(DisplayRecipeActivity.this, RecipesActivity.class);
                startActivity(recipe_intent);
                finish();
                break;
            case R.id.nav_profile:
                Intent profile_intent = new Intent(DisplayRecipeActivity.this, ProfileActivity.class);
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


    private void askSharePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_CODE_SHARE);
        } else {
            sendIngredientsAsSMS();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_SHARE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendIngredientsAsSMS();
            } else {
                Toast.makeText(this, "Permission TO Share Is Required For Sending A Recipe", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getNewRandomRecipe() {
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        assert message != null;
        estimatedTime = Integer.parseInt(message);
        model = Model.getInstance();
        recipeToBeDisplayed = model.getRandomRecipe(estimatedTime);

        setRecipe(recipeToBeDisplayed);
    }


}
