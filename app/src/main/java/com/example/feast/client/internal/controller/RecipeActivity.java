package com.example.feast.client.internal.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.feast.R;
import com.example.feast.client.internal.model.Model;
import com.example.feast.client.internal.utility.concurrent.AsyncUpdate;
import com.example.feast.client.internal.utility.concurrent.AsyncUpdateUserRecipe;
import com.example.feast.client.internal.utility.globals.RequestCodes;
import com.example.feast.core.entities.Ingredient;
import com.example.feast.core.entities.UserRecipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RecipeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "RecipeActivity";
    private final String KEY_NAME = "name";
    private final String KEY_AMOUNT = "amount";

    private UserRecipe mainRecipe;
    private ArrayList<View> views;
    private ArrayList<HashMap<String, EditText>> Ingredients;
    private boolean edit;
    private boolean wasUpdated;
    private Model model;
    private String currentPhotoPath;
    private Uri imageUrl;

    private EditText etName, etTime;
    private ImageButton btnAddIng, imageViewButton;
    private ImageView imgRecipe;
    private Toolbar toolbar;
    private ConstraintLayout mainLayout;
    private TableLayout tableIng;
    private TableRow addRow;
    private Button btnEdit, btnback, btnGallery;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private boolean isImageUpdated;


    //<editor-fold desc="Override Methods">
    /**
     * Creates the Activity and sets up the views.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        model = Model.getInstance();
        Ingredients = new ArrayList<>();
        mainRecipe = (UserRecipe) getIntent().getSerializableExtra("ur");
        views = new ArrayList<>();
        edit = false;
        wasUpdated = false;

        setUpViews();
        setUpListeners();
        setRecipe(mainRecipe);
        switchActivation(edit);
    }

    /**
     * Checks the resultcode, and sets a property on a recipe.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.USER_REQUEST_CODE && resultCode == RESULT_OK) {

            File file = new File(currentPhotoPath);
            imgRecipe.setImageURI(Uri.fromFile(file));
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            imageUrl = contentUri;

            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
            isImageUpdated = true;
        }

        if (requestCode == RequestCodes.REQUEST_CODE_GET_FROM_GALLERY) {

            if (resultCode == Activity.RESULT_OK) {
                imageUrl = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + model.getFileExt(imageUrl, this);//todo put
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " + imageFileName);
                imgRecipe.setImageURI(imageUrl);
                isImageUpdated = true;
            }
        }
    }

    /**
     * sets up the toolbar
     */
    @Override
    protected void onStart() {
        super.onStart();

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * checks if there is permission to use the camera or not.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RequestCodes.REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
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
    //</editor-fold>


    //<editor-fold desc="Setup">
    /**
     * sets up the views.
     */
    private void setUpViews() {
        drawerLayout = findViewById(R.id.drawLayout_recipe);
        navigationView = findViewById(R.id.navigation_recipe);
        btnback = findViewById(R.id.button_back);
        toolbar = findViewById(R.id.toolbar);
        imageViewButton = findViewById(R.id.imageButton);
        mainLayout = findViewById(R.id.main);
        etName = findViewById(R.id.et_ur_name);
        etTime = findViewById(R.id.et_ur_time);
        imgRecipe = findViewById(R.id.imgRecipe);
        btnEdit = findViewById(R.id.button_edit);
        btnAddIng = findViewById(R.id.button_add_ing);
        btnGallery = findViewById(R.id.button_gallery);
        addRow = findViewById(R.id.tr_add_ing);
        tableIng = findViewById(R.id.table_ing);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_addRecipe);

        mainLayout.removeView(imageViewButton);
        mainLayout.removeView(btnGallery);
        tableIng.removeView(addRow);

        views.add(btnAddIng);
        views.add(etName);
        views.add(etTime);
    }

    /**
     * sets up the Listeners on buttons.
     */
    private void setUpListeners() {
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEdit();
            }
        });
        btnAddIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredient(null);
            }
        });
        imageViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Image");
                askCameraPermissions();
            }
        });
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPictureFromGallery();
            }
        });

    }

    /**
     * sets the recipe properties in the views.
     * @param ur
     */
    @SuppressLint("SetTextI18n")
    private void setRecipe(UserRecipe ur) {
        etName.setText(ur.getName());
        etTime.setText("" + ur.getEstimatedTime());

        if (ur.getImageUrl() != null && ur.getImageUrl().length() > 0) {

            Log.d("Imagefile", ur.getImageUrl());

            model.getImage(ur.getImageUrl()).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgRecipe.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    System.out.println("failed");
                }
            });
        }
        tableIng.removeAllViews();
        for (Ingredient ing : ur.getIngredients()) {
            addIngredient(ing);
        }
    }
    //</editor-fold>

    //<editor-fold desc="ButtonActions">

    /**
     * sets the views to be editable, and sets the save button and cancel in the layout
     * @param edit
     */
    @SuppressLint("SetTextI18n")
    private void switchActivation(boolean edit) {
        for (View v : views) {
            v.setEnabled(edit);
            v.setAlpha(1);
        }
        if (edit) {
            btnEdit.setText("Save");
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSave();
                }
            });
            btnback.setText("Back");
            btnback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCancel();
                }
            });

            tableIng.addView(addRow);
            mainLayout.addView(imageViewButton);
            mainLayout.addView(btnGallery);
        } else {
            btnEdit.setText("Edit");
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEdit();
                }
            });
            btnback.setText("Back");
            btnback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBack();
                }
            });
            tableIng.removeView(addRow);
            mainLayout.removeView(imageViewButton);
            mainLayout.removeView(btnGallery);

        }

    }

    /**
     * sets the recipe when clicked.
     */
    private void onCancel() {
        setRecipe(mainRecipe);
    }

    /**
     * creates a new set of editTextViews, and sets the ingriedient to the recipes arraylist
     * @param ing
     */
    private void addIngredient(Ingredient ing) {
        EditText etName = new EditText(this);
        etName.setText(ing != null ? ing.getName() : "");
        etName.setEnabled(edit);
        views.add(etName);

        EditText etAmount = new EditText(this);
        etAmount.setText(ing != null ? ing.getAmount() + "" : "");
        etAmount.setEnabled(edit);
        views.add(etAmount);


        ImageButton btnDeleteIng = new ImageButton(this);
        views.add(btnDeleteIng);

        btnDeleteIng.setImageResource(R.drawable.delete_icon);
        btnDeleteIng.setBackgroundResource(R.color.transparant);
        btnDeleteIng.setEnabled(edit);
        views.add(btnDeleteIng);

        TableRow tr = new TableRow(this);
        tr.addView(etName);
        tr.addView(etAmount);
        tr.addView(btnDeleteIng);

        Log.d(TAG, "addIngredient: " + tableIng.getChildCount());
        tableIng.addView(tr, tableIng.getChildCount() >= 2 ? tableIng.getChildCount() - 1 : 0);

        HashMap<String, EditText> temp = new HashMap<>();
        temp.put(KEY_NAME, etName);
        temp.put(KEY_AMOUNT, etAmount);
        Ingredients.add(temp);
    }

    /**
     * sets the editState
     */
    private void onEdit() {
        edit = true;
        switchActivation(edit);
    }

    /**
     * saves the changes to the recipe and updates the firebase
     */
    @SuppressWarnings("ConstantConditions")
    private void onSave() {
        String id = mainRecipe.getId();
        String name = etName.getText().toString();
        long cookTime = Long.parseLong(etTime.getText().toString());
        String userId = mainRecipe.getUserId();

        ArrayList<Ingredient> ingredients = new ArrayList<>();
        for (HashMap<String, EditText> map : Ingredients) {
            String ingName = map.get(KEY_NAME).getText().toString();
            long amount = Long.parseLong(map.get(KEY_AMOUNT).getText().toString());

            ingredients.add(new Ingredient(ingName, amount));
        }

        UserRecipe ur = new UserRecipe(ingredients, id, cookTime, name, userId, "");
        if (isImageUpdated) {
            updateUserRecipeWithImage(ur);
        } else {
            updateUserRecipeNoImage(ur);
        }
        wasUpdated = true;

    }

    /**
     * saves the recipe without a image
     * @param ur
     */
    private void updateUserRecipeNoImage(UserRecipe ur) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Updating");
        pd.show();
        model.updateUserRecipe(ur).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: ");
                pd.dismiss();
                edit = false;
                switchActivation(edit);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: ", e);
                pd.dismiss();
                edit = true;
                switchActivation(edit);
            }
        });
    }

    /**
     * saves the recipe with an image.
     * @param recipe
     */
    private void updateUserRecipeWithImage(final UserRecipe recipe) {
        if (imageUrl != null) {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Updating");
            pd.show();

            AsyncUpdateUserRecipe a = new AsyncUpdateUserRecipe(imageUrl, recipe, this, new AsyncUpdate<Void>() {
                @Override
                public void update(Void entity) {
                    pd.dismiss();
                    edit = false;
                    switchActivation(edit);
                }
            });

            a.execute();

        }

    }

    /**
     * checks if the recipe wasUpdated and finishes the edit.
     */
    private void onBack() {
        Log.d(TAG, "onBack: GO BACK");
        if (wasUpdated) {
            setResult(RESULT_OK, null);
            finish();
        } else {
            finish();
        }
    }
    //</editor-fold>


    //<editor-fold desc="Camera and Gallery">

    /**
     * checks if the permission to the camera is granted
     */
    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, RequestCodes.REQUEST_CODE_CAMERA);
        } else {
            openCamera();
        }

    }

    /**
     * gets an image from the gallery.
     */
    private void getPictureFromGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, RequestCodes.REQUEST_CODE_GET_FROM_GALLERY);
    }

    /**
     * opens the camera and creates a new image.
     */
    private void openCamera() {
        Intent cameraIntend = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntend.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntend, RequestCodes.USER_REQUEST_CODE);
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("----------TAG----------", "openCamera: " + ex);
                ex.printStackTrace();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.feast.android.fileProvider",
                        photoFile);
                cameraIntend.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntend, RequestCodes.USER_REQUEST_CODE);
            }

        }
    }

    /**
     * creates the image which has been taken
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "createImageFile: " + image.getAbsolutePath());
        return image;
    }
    //</editor-fold>

    //<editor-fold desc="Navigation">

    /**
     * sets up the navigation in the toolbar
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


}
