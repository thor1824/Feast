package com.example.feast.client.internal.controller;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import com.example.feast.client.internal.utility.globals.RequestCodes;
import com.example.feast.core.entities.Ingredient;
import com.example.feast.core.entities.Recipe;
import com.example.feast.core.entities.UserRecipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.BlockingDeque;


public class AddUserRecipeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
    private Uri imageUrl;
    private ArrayList<EditText> editTexts;


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
        editTexts = new ArrayList<>();

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

        editTexts.add(recipeNameField);
        editTexts.add(estimatedTimeField);
        editTexts.add(firstIngFiled);
        editTexts.add(firstAmountField);


    }

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
        deleteButton.setOnClickListener((new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                addIngLayout.removeView(editContainer);

            }
        }));

        int nameId = editFieldId;
        int amountId = editFieldId;

        HashMap<String, Integer> ingredientsMap = new HashMap<String, Integer>();
        ingredientsMap.put("name", nameId);
        ingredientsMap.put("amount", amountId);
        ingNameList.add(ingredientsMap);
        layouts.add(editContainer);


    }

    private void deleteIng(){

    }

    private TextWatcher recipeTxtWatcher = new TextWatcher() {

        private boolean isValid() {

            for (EditText editText : editTexts) {

                if (editText.getText().toString().isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            submitButton.setEnabled(isValid());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void saveUserRecipe() {


        final String recipeName = recipeNameField.getText().toString();
        final long estimatedTime = Long.parseLong(estimatedTimeField.getText().toString());
        final String firstIngName = firstIngFiled.getText().toString();
        final long firstAmount = Long.parseLong(firstAmountField.getText().toString());
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

        UserRecipe recipe = new UserRecipe(ingredients, estimatedTime, recipeName, model.getCurrentUser().getUid(), "");

        if (imageUrl != null) {
            uploadImage(recipe);
        } else {
            model.createUserRecipe(recipe).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    clearFields();
                }
            });
        }

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
            setResult(RESULT_OK);
            finish();
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, RequestCodes.REQUEST_CODE_CAMERA);
        } else {
            openCamera();
        }

    }

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

    private void getPictureFromGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, RequestCodes.REQUEST_CODE_GET_FROM_GALLERY);
    }

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

    private void uploadImage(final UserRecipe recipe) {
        if (imageUrl != null) {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Uploading");
            pd.show();

            final StorageReference fileRef = FirebaseStorage
                    .getInstance()
                    .getReference()
                    .child("images")
                    .child("recipe")
                    .child(System.currentTimeMillis() + "." + getFileExt(imageUrl));

            fileRef.putFile(imageUrl).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            recipe.setImageUrl(FirebaseStorage.getInstance().getReferenceFromUrl(url).toString());

                            model.createUserRecipe(recipe);

                            pd.dismiss();
                            imageView.setImageResource(R.drawable.camara_icon);
                            clearFields();
                            Toast.makeText(AddUserRecipeActivity.this, "image was uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            pd.dismiss();
                        }
                    });
                }
            });
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.USER_REQUEST_CODE && resultCode == RESULT_OK) {
            File file = new File(currentPhotoPath);
            imageView.setImageURI(Uri.fromFile(file));
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            imageUrl = contentUri;
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }

        if (requestCode == RequestCodes.REQUEST_CODE_GET_FROM_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                imageUrl = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(imageUrl);
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " + imageFileName);
                imageView.setImageURI(imageUrl);
            }
        }
    }

    private void clearFields() {
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


    private EditText ingredientName() {
        editFieldId++;
        EditText ingName = new EditText(this);
        ingName.setHint("Ingredient");
        ingName.setId(editFieldId);
        ingName.setLayoutParams(new LinearLayout.LayoutParams(710, LinearLayout.LayoutParams.WRAP_CONTENT));

        editTexts.add(ingName);
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
        ingAmount.addTextChangedListener(recipeTxtWatcher);
        editTexts.add(ingAmount);
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


}