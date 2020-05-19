package com.example.feast.client.internal.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.feast.R;
import com.example.feast.client.internal.model.Model;
import com.example.feast.client.internal.utility.concurrent.AsyncUpdate;
import com.example.feast.client.internal.utility.globals.RequestCodes;
import com.example.feast.client.internal.utility.handler.PermissionsManager;
import com.example.feast.core.entities.Ingredient;
import com.example.feast.core.entities.UserRecipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RecipeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "RecipeActivity";
    private final String KEY_NAME = "name";
    private final String KEY_AMOUNT = "amount";
    private final String KEY_URI = "imageUri";

    private EditText etName, etTime;
    private ImageButton btnAddIngredients, btnTakePicture;
    private ImageView ivRecipe;
    private Toolbar toolbar;
    private ConstraintLayout mainLayout;
    private TableLayout tableIng;
    private TableRow addRow;
    private Button btnEdit, btnback, btnGallery;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Uri imageUri;

    private UserRecipe mainRecipe;
    private ArrayList<View> views;
    private ArrayList<HashMap<String, EditText>> Ingredients;
    private boolean edit;
    private boolean wasUpdated;
    private boolean isImageUpdated;
    private Model model;



    //<editor-fold desc="Override Methods">

    /**
     * Creates the Activity and sets up the views.
     *
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
        setupNavigation();
        setUpListeners();
        setRecipe(mainRecipe);
        switchActivation(edit);

        if (imageUri != null) {
            Log.d(TAG, "onCreate: get state");
            imageUri = (Uri) savedInstanceState.getParcelable(KEY_URI);
            Bitmap thumbnail = null;
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ivRecipe.setImageBitmap(thumbnail);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        state.putParcelable(KEY_URI, imageUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(TAG, "Inside of onRestoreInstanceState");
        imageUri = (Uri) savedInstanceState.getParcelable(KEY_URI);
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
     * Checks the resultcode, and sets a property on a recipe.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestCodes.RC_IMAGE_CAPTURE: {
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                        ivRecipe.setImageBitmap(thumbnail);
                        isImageUpdated = true;
                        wasUpdated = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case RequestCodes.RC_READ_FROM_GALLERY: {
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    ivRecipe.setImageURI(imageUri);
                    isImageUpdated = true;
                    wasUpdated = true;
                }
                break;
            }
            default: {
                Log.d(TAG, "onActivityResult: not setup for Request code " + requestCode);
            }
        }

    }

    /**
     * checks if there is permission to use the camera or not.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCodes.RC_CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onTakePicture();
                } else {
                    Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case RequestCodes.RC_EXTERNAL_READ_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onFromGallery();
                } else {
                    Toast.makeText(this, "External Read Permission is Required to access Gallery.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            default: {
                Log.d(TAG, "onPermissionRequestResult not setup for" + permissions[0]);
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
        btnTakePicture = findViewById(R.id.imageButton);
        mainLayout = findViewById(R.id.main);
        etName = findViewById(R.id.et_ur_name);
        etTime = findViewById(R.id.et_ur_time);
        ivRecipe = findViewById(R.id.imgRecipe);
        btnEdit = findViewById(R.id.button_edit);
        btnAddIngredients = findViewById(R.id.button_add_ing);
        btnGallery = findViewById(R.id.button_gallery);
        addRow = findViewById(R.id.tr_add_ing);
        tableIng = findViewById(R.id.table_ing);

        mainLayout.removeView(btnTakePicture);
        mainLayout.removeView(btnGallery);
        tableIng.removeView(addRow);

        views.add(btnAddIngredients);
        views.add(etName);
        views.add(etTime);
    }

    private void setupNavigation() {
        navigationView = findViewById(R.id.navigation_recipe);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_addRecipe);
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

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });

        btnAddIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddIngredient(null);
            }
        });

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Image");
                onTakePicture();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFromGallery();
            }
        });

    }

    /**
     * sets the recipe properties in the views.
     *
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
                    ivRecipe.setImageBitmap(bitmap);
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
            onAddIngredient(ing);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Button Actions">

    /**
     * sets the editState
     */
    private void onEdit() {
        edit = true;
        switchActivation(edit);
    }

    /**
     * checks if the recipe wasUpdated and finishes the edit.
     */
    private void onBack() {
        if (wasUpdated) {
            setResult(RESULT_OK, null);
            finish();
        } else {
            finish();
        }
    }

    /**
     * saves the changes to the recipe and updates the firebase
     */
    @SuppressWarnings("ConstantConditions")
    private void onSave() {
        UserRecipe ur = extractUserRecipe();
        if (isImageUpdated) {
            updateUserRecipeWithImage(ur);
        } else {
            updateUserRecipeNoImage(ur);
        }
        wasUpdated = true;
    }


    /**
     * sets the recipe when clicked.
     */
    private void onCancel() {
        setRecipe(mainRecipe);
        edit = false;
        switchActivation(edit);
    }

    /**
     * creates a new set of editTextViews, and sets the ingredients to the recipes arraylist
     *
     * @param ing
     */
    private void onAddIngredient(Ingredient ing) {
        EditText etName = createIngredientEditText(ing != null ? ing.getName() : "");
        views.add(etName);

        EditText etAmount = createIngredientEditText(ing != null ? ing.getAmount() + "" : "");
        views.add(etAmount);

        ImageButton btnDeleteIngredient = new ImageButton(this);
        btnDeleteIngredient.setImageResource(R.drawable.delete_icon);
        btnDeleteIngredient.setBackgroundResource(R.color.transparant);
        btnDeleteIngredient.setEnabled(edit);

        views.add(btnDeleteIngredient);

        final TableRow trIngredient = new TableRow(this);
        trIngredient.addView(etName);
        trIngredient.addView(etAmount);
        trIngredient.addView(btnDeleteIngredient);

        btnDeleteIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableIng.removeView(trIngredient);
            }
        });

        //places the EditTexts into a HashMap and then places it into an array
        HashMap<String, EditText> temp = new HashMap<>();
        temp.put(KEY_NAME, etName);
        temp.put(KEY_AMOUNT, etAmount);
        Ingredients.add(temp);

        // Places the view one index beneath the addIngredients button
        int index = tableIng.getChildCount() >= 2 ? tableIng.getChildCount() - 1 : 0;
        tableIng.addView(trIngredient, index);
    }

    private void onTakePicture() {
        if (!PermissionsManager.isGrantedPermission(Manifest.permission.CAMERA, this)
                && !PermissionsManager.isGrantedPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this)) {

            PermissionsManager.askPermission(
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    },
                    RequestCodes.RC_CAMERA_PERMISSION,
                    this
            );
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, System.currentTimeMillis() + "");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, RequestCodes.RC_IMAGE_CAPTURE);
            }
        }
    }

    private void onFromGallery() {
        if (!PermissionsManager.isGrantedPermission(Manifest.permission.READ_EXTERNAL_STORAGE, this)) {
            PermissionsManager.askPermission(
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    RequestCodes.RC_EXTERNAL_READ_PERMISSION,
                    this
            );
        } else {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, RequestCodes.RC_READ_FROM_GALLERY);
        }

    }
    //</editor-fold>

    //<editor-fold desc="Helper Functions">
    private UserRecipe extractUserRecipe() {
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

        return new UserRecipe(ingredients, id, cookTime, name, userId, "");
    }

    private EditText createIngredientEditText(String value) {
        EditText et = new EditText(this);
        et.setText(value);
        et.setEnabled(edit);
        return et;
    }

    /**
     * sets the views to be editable, and sets the save button and cancel in the layout
     *
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
            btnback.setText("Cancel");
            btnback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCancel();
                }
            });

            tableIng.addView(addRow);
            mainLayout.addView(btnTakePicture);
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
            mainLayout.removeView(btnTakePicture);
            mainLayout.removeView(btnGallery);

        }

    }

    /**
     * saves the recipe without a image
     *
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
     *
     * @param recipe
     */
    private void updateUserRecipeWithImage(final UserRecipe recipe) {
        if (imageUri != null) {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Updating");
            pd.show();

            model.updateUserRecipeWithImage(imageUri, recipe, this, new AsyncUpdate<Void>() {
                @Override
                public void update(Void entity) {
                    pd.dismiss();
                    edit = false;
                    switchActivation(edit);
                }
            });
        }
    }
    //</editor-fold>

    //<editor-fold desc="Navigation">

    /**
     * sets up the navigation in the toolbar
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
}
