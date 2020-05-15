package com.example.feast.client.internal.controller;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.feast.R;
import com.example.feast.client.internal.model.Model;
import com.example.feast.core.entities.Ingredient;
import com.example.feast.core.entities.UserRecipe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.BlockingDeque;


public class AddUserRecipeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CODE_CAMERA = 101;
    public static final int USER_REQUEST_CODE = 102;
    public static final int REQUEST_CODE_GET_FROM_GALLERY = 103;
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
    private Button submitButton, takePickButton, picFromGalleryButton;
    private LinearLayout ingContainer;
    private ArrayList<LinearLayout> layouts;
    private ImageView imageView;
    private String currentPhotoPath;


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
        takePickButton = findViewById(R.id.bt_TakePic);
        picFromGalleryButton = findViewById(R.id.bt_picFromGalleri);
        ingNameList = new ArrayList<HashMap<String, Integer>>();
        model = Model.getInstance();
        submitButton = findViewById(R.id.submitBt);
        ingContainer = findViewById(R.id.linIngContainer);
        layouts = new ArrayList<>();
        imageView = findViewById(R.id.img_picContainer);

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
        takePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });
        picFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPictureFromGallery();
            }
        });

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
        ingName.setLayoutParams(new LinearLayout.LayoutParams(710, LinearLayout.LayoutParams.WRAP_CONTENT));
        editContainer.addView(ingName);

        int nameId = editFieldId;

        editFieldId++;
        final EditText ingAmount = new EditText(this);
        ingAmount.setHint("Amount");
        ingAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        ingAmount.setLayoutParams(p);
        ingAmount.setId(editFieldId);
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p2.leftMargin = 90;
        ingAmount.setLayoutParams(p2);
        ingAmount.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        editContainer.addView(ingAmount);

        int amountId = editFieldId;

        final TextView textView = new TextView(this);
        textView.setText("g");
        editContainer.addView(textView);

        FloatingActionButton deleteIngButton = new FloatingActionButton(this);
        deleteIngButton.setImageResource(R.drawable.delete_icon);
        LinearLayout.LayoutParams p3 = (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        p3.leftMargin = 60;
        p3.topMargin = 30;
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

        for (LinearLayout layout : layouts) {
            addIngLayout.removeView(layout);
        }


    }

    private void getPictureFromGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, REQUEST_CODE_GET_FROM_GALLERY);
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
            case R.id.nav_logOut:
                model.signOut();
                Intent signOutIntent = new Intent(this, LoginActivity.class);
                startActivity(signOutIntent);
                finishAffinity();

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        } else {
            openCamera();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent cameraIntend = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntend.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntend, USER_REQUEST_CODE);
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("----------TAG----------", "openCamera: " + ex);
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.feast.android.fileProvider",
                        photoFile);
                cameraIntend.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntend, USER_REQUEST_CODE);
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USER_REQUEST_CODE && resultCode == RESULT_OK) {
            File file = new File(currentPhotoPath);
            imageView.setImageURI(Uri.fromFile(file));

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }

        if (requestCode == REQUEST_CODE_GET_FROM_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " + imageFileName);
                imageView.setImageURI(contentUri);

            }
        }
    }


    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        Log.d("//////////////////", "createImageFile: " + image.getAbsolutePath());
        return image;
    }


}


