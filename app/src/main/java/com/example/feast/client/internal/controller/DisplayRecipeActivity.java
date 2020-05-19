package com.example.feast.client.internal.controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.feast.R;
import com.example.feast.client.internal.model.Model;
import com.example.feast.client.internal.utility.globals.ExtraKeys;
import com.example.feast.client.internal.utility.globals.RequestCodes;
import com.example.feast.client.internal.utility.handler.PermissionsManager;
import com.example.feast.core.entities.IRecipe;
import com.example.feast.core.entities.Ingredient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;


public class DisplayRecipeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private  final String TAG = "DisplayRecipeActivity";

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ImageView recipeImage;
    private IRecipe recipeToBeDisplayed;
    private TextView textView;
    private LinearLayout layoutForGram, layoutForName;
    private FloatingActionButton btnShare;
    private Button btnPick;

    private Model model;

    //<editor-fold desc="Overrides">
    /**
     * sets up the toolbar
     */
    /**
     * creates the activity and setup up the views.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe);

        model = Model.getInstance();

        setupViews();
        setupListener();
        onNewRandomRecipe();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupToolBar();
    }


    /**
     * checkes if the toolbar is opened
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
     * checks the result of the permissions.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCodes.RC_SHARE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onShare();
                } else {
                    Toast.makeText(this, "Permission TO Share Is Required For Sending A Recipe", Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Setup">
    private void setupListener() {
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShare();
            }
        });

        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNewRandomRecipe();
            }
        });
    }

    private void setupViews() {
        drawerLayout = findViewById(R.id.drawLayout_display_recipe);
        NavigationView navigationView = findViewById(R.id.navigation_view_display_recipe);
        toolbar = findViewById(R.id.toolbar);
        recipeImage = findViewById(R.id.imgRecipe);
        layoutForName = findViewById(R.id.LinLayIngredients);
        textView = findViewById(R.id.txtHeader);
        layoutForGram = findViewById(R.id.LinLayAmount);
        btnShare = findViewById(R.id.fab);
        btnPick = findViewById(R.id.button_pickAnother);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_profile);
    }

    private void setupToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
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
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="Button Actions">
    public void onShare() {
        if (!PermissionsManager.isGrantedPermission(Manifest.permission.SEND_SMS, this)) {
            PermissionsManager.askPermission(
                    new String[]{
                            Manifest.permission.SEND_SMS
                    },
                    RequestCodes.RC_SHARE,
                    this
            );
        } else {
            sendIngredientsAsSMS();
        }
    }

    /**
     * gets a random recipe from firebase
     */
    public void onNewRandomRecipe() {
        Intent intent = getIntent();
        String message = intent.getStringExtra(ExtraKeys.EXTRA_KEY_TIME);
        assert message != null;
        int estimatedTime = Integer.parseInt(message);
        recipeToBeDisplayed = model.getRandomRecipe(estimatedTime);
        if(recipeToBeDisplayed.getImageUrl() == "" || recipeToBeDisplayed.getImageUrl() == null){
            recipeImage.setImageResource(R.drawable.foodnotfound);
        }
        setRecipe(recipeToBeDisplayed);

    }
    //</editor-fold>

    //<editor-fold desc="Helper Functions">
    /**
     * sets the recipe in the views, and checks if the recipe has a picture.
     *
     * @param recipe
     */
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

                Log.d("Imagefile", recipe.getImageUrl());

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

    /**
     * sends the recipe to the SMS app on the phone
     */
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

        Intent shareIntent = Intent.createChooser(sendIntent, "Choose your character");
        startActivity(shareIntent);
    }
    //</editor-fold>


}
