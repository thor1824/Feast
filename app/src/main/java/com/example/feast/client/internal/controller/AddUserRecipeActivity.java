package com.example.feast.client.internal.controller;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.feast.client.internal.utility.CustomTextWatcher.ValidationTextWatcher;
import com.example.feast.client.internal.utility.concurrent.AsyncUpdate;
import com.example.feast.client.internal.utility.globals.RequestCodes;
import com.example.feast.client.internal.utility.handler.PermissionsManager;
import com.example.feast.core.entities.Ingredient;
import com.example.feast.core.entities.UserRecipe;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;


public class AddUserRecipeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "AddUserRecipeActivity";
    private final String KEY_NAME = "name";
    private final String KEY_AMOUNT = "amount";

    private EditText firstIngFiled, firstAmountField, estimatedTimeField, recipeNameField;
    private Button submitButton, takePickButton, picFromGalleryButton;
    private DrawerLayout drawerLayout;
    private LinearLayout addIngLayout;
    private FloatingActionButton button;
    private ImageView imageView;
    private ValidationTextWatcher txtWatcher;
    private ArrayList<LinearLayout> layouts;

    private int editFieldId = 0;
    private ArrayList<HashMap<String, Integer>> ingNameList;
    private Uri imageUri;
    private Model model;

    //<editor-fold desc="Overrides">

    /**
     * Creates the activity and sets up the views, with buttons etc.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_recipe);

        model = Model.getInstance();
        ingNameList = new ArrayList<>();
        layouts = new ArrayList<>();

        setupViews();

        txtWatcher = new ValidationTextWatcher(submitButton);
        
        setupListener();
    }

    /**
     * onStart checks whether the toolbar is opened or not
     */
    @Override
    protected void onStart() {
        super.onStart();

        setupToolBar();
    }

    /**
     * when the back button is pressed, it checks if the toolbar is open of closed
     * if it is open it closes and sets the result to "OK"
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    /**
     * Checks the resultcodes for if it is chosen by the gallery or a new image
     * thereafter saves it to the internal storage.
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
                        imageView.setImageBitmap(thumbnail);
                        /*imageurl = getRealPathFromURI(imageUri);*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case RequestCodes.RC_READ_FROM_GALLERY: {
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    imageView.setImageURI(imageUri);
                }
                break;
            }
            default: {
                Log.d(TAG, "onActivityResult: not setup for Request code " + requestCode);
            }
        }
    }

    /**
     * Gets the result of camera permissions. if false, it tells the user it needs permission.
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
                    onClickCamera();
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
    //</editor-fold>

    //<editor-fold desc="Setup">
    public void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupViews() {
        drawerLayout = findViewById(R.id.drawLayout_addRecipe);
        addIngLayout = findViewById(R.id.addIngLayout);
        recipeNameField = findViewById(R.id.editName);
        estimatedTimeField = findViewById(R.id.editTime);
        firstIngFiled = findViewById(R.id.editIng);
        firstAmountField = findViewById(R.id.editIngAmount);
        button = findViewById(R.id.addIngButton);
        takePickButton = findViewById(R.id.bt_TakePic);
        picFromGalleryButton = findViewById(R.id.bt_picFromGalleri);
        submitButton = findViewById(R.id.submitBt);
        imageView = findViewById(R.id.img_picContainer);

    }

    public void setupListener() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addIngIngredient();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddRecipe();
            }
        });
        takePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCamera();
            }
        });
        picFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFromGallery();
            }
        });

        txtWatcher.getEditTexts().add(recipeNameField);
        txtWatcher.getEditTexts().add(estimatedTimeField);
        txtWatcher.getEditTexts().add(firstIngFiled);
        txtWatcher.getEditTexts().add(firstAmountField);

    }
    //</editor-fold>

    //<editor-fold desc="Button Actions">

    /**
     * Checks if the camera and external write permission is granted, if false open the permission survey.
     * if true, opens the camera
     */
    public void onClickCamera() {
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

    public void onFromGallery() {
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

    /**
     * takes all the views content, and creates a new userRecipe,
     * if the userRecipe has an image, the image is uploaded.
     * when the createUserRecipe is succeeded the method clears all the views
     * for a new recipe to be made.
     */
    public void onAddRecipe() {
        UserRecipe recipe = extractUserRecipe();

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Creating");
        pd.show();
        if (imageUri != null) {
            model.createUserRecipeWithImage(imageUri, recipe, this, new AsyncUpdate<Void>() {
                @Override
                public void update(Void entity) {
                    Toast.makeText(AddUserRecipeActivity.this, "Recipe was Created", Toast.LENGTH_SHORT).show();
                    pd.dismiss();

                    clearFields();
                }
            });
        } else {
            model.createUserRecipe(recipe).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {

                    Toast.makeText(AddUserRecipeActivity.this, "Recipe was Created", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    clearFields();
                }
            });
        }
    }


    //</editor-fold>

    //<editor-fold desc="Helper functions">

    private UserRecipe extractUserRecipe() {
        final String recipeName = recipeNameField.getText().toString();
        final long estimatedTime = Long.parseLong(estimatedTimeField.getText().toString());
        final String firstIngName = firstIngFiled.getText().toString();
        final long firstAmount = Long.parseLong(firstAmountField.getText().toString());
        ArrayList<Ingredient> ingredients = new ArrayList<>();

        Ingredient firstIng = new Ingredient(firstIngName, firstAmount);

        ingredients.add(firstIng);

        for (HashMap<String, Integer> map : ingNameList) {
            EditText name = findViewById(map.get(KEY_NAME));
            EditText amount = findViewById(map.get(KEY_AMOUNT));
            String nameFromField = name.getText().toString();
            long amountFromField = Long.valueOf(amount.getText().toString());

            ingredients.add(new Ingredient(nameFromField, amountFromField));
        }

        return new UserRecipe(ingredients, estimatedTime, recipeName, model.getCurrentUser().getUid(), "");
    }

    /**
     * Clears all the views
     */
    private void clearFields() {
        imageView.setImageResource(R.drawable.camara_icon);
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

    /**
     * Creates a new LinearLayout, and sets 2 editText views, which contains the information
     * for user recipes.
     */
    public void addIngIngredient() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final LinearLayout editContainer = new LinearLayout(this);
        editContainer.setLayoutParams(p);
        editContainer.setOrientation(LinearLayout.HORIZONTAL);
        addIngLayout.addView(editContainer);

        editContainer.addView(ingredientName());
        editContainer.addView(ingredientAmount());
        TextView textView = new TextView(this);
        textView.setText("g");
        editContainer.addView(textView);

        FloatingActionButton deleteButton = deleteButton();
        editContainer.addView(deleteButton);

        int nameId = editFieldId;
        int amountId = editFieldId;

        HashMap<String, Integer> ingredientsMap = new HashMap<String, Integer>();
        ingredientsMap.put(KEY_NAME, nameId);
        ingredientsMap.put(KEY_AMOUNT, amountId);
        ingNameList.add(ingredientsMap);
        layouts.add(editContainer);
        final int index = ingNameList.size() - 1;
        deleteButton.setOnClickListener((new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ingNameList.remove(index);
                addIngLayout.removeView(editContainer);

            }
        }));

    }

    private EditText ingredientName() {
        editFieldId++;
        EditText ingName = new EditText(this);
        ingName.setHint("Ingredient");
        ingName.setId(editFieldId);
        ingName.setLayoutParams(new LinearLayout.LayoutParams(710, LinearLayout.LayoutParams.WRAP_CONTENT));

        txtWatcher.getEditTexts().add(ingName);
        return ingName;
    }

    private EditText ingredientAmount() {
        editFieldId++;
        EditText ingAmount = new EditText(this);
        ingAmount.setHint("Amount");
        ingAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        ingAmount.setId(editFieldId);
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p2.leftMargin = 90;
        ingAmount.setLayoutParams(p2);
        ingAmount.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        ingAmount.addTextChangedListener(txtWatcher);
        txtWatcher.getEditTexts().add(ingAmount);
        return ingAmount;
    }

    private FloatingActionButton deleteButton() {

        FloatingActionButton deleteIngButton = new FloatingActionButton(this);
        deleteIngButton.setImageResource(R.drawable.delete_icon);
        LinearLayout.LayoutParams p3 = (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        p3.leftMargin = 60;
        p3.topMargin = 30;
        deleteIngButton.setLayoutParams(p3);
        deleteIngButton.setCompatElevation(0);
        return deleteIngButton;


    }
    //</editor-fold>

    //<editor-fold desc="Navigation">

    /**
     * Navigation is setup through a switch statement, either displays a message or
     * navigate to another activity
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

    //<editor-fold desc="to be removed">

    /**
     * gets the files extension (Jpg, Png, etc)
     *
     * @param contentUri
     * @return
     */
    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    /**
     * check whether the imageUrl on a recipe is null, if false,
     * it opens a progressDialog, gets a firebase Reference, and upload the file to firebase firestore.
     * when it is done it closes the ProgressDialog.
     * <p>
     * if it fails it prints the Stacktrace to the Console.
     *
     * @param recipe
     */
    private void uploadImage(final UserRecipe recipe) {
        if (imageUri != null) {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Uploading");
            pd.show();

            model.createUserRecipeWithImage(imageUri, recipe, this, new AsyncUpdate<Void>() {
                @Override
                public void update(Void entity) {
                    Toast.makeText(AddUserRecipeActivity.this, "image was uploaded", Toast.LENGTH_SHORT).show();
                    pd.dismiss();

                    clearFields();
                }
            });
        }
    }
    //</editor-fold>
}
