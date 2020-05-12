package com.example.feast;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.feast.Models.Recipes;
import com.example.feast.repository.GetRecipeTask;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.varunest.sparkbutton.SparkButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, onGetRecipesComplete {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    Spinner mainSpinner;
    SparkButton sparkButton;
    String valueFromSpinner;

    TextView test;
    private FirebaseAuth mAuth;

    private String TAG = "app";

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainSpinner = findViewById(R.id.spinnerMain);
        sparkButton = findViewById(R.id.spark_button);
        drawerLayout = findViewById(R.id.drawLayout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        sparkButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                goToNextActivity();
            }
        });
        String[] spinnerValues = getResources().getStringArray(R.array.minutesForSpinner);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainSpinner.setAdapter(adapter);
        test = findViewById(R.id.test);
        test.setText("not Signed in");

        mAuth = FirebaseAuth.getInstance();

        AsyncTask<Void, Void, ArrayList<Recipes>> next = new GetRecipeTask(this);
        next.execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 10);
        } else {
            test.setText(mAuth.getCurrentUser().getDisplayName());
        }
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinnerMain) {
            this.valueFromSpinner = parent.getItemAtPosition(position).toString();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void goToNextActivity() {
        Intent intent = new Intent(this, DisplayRecipe.class);
        mainSpinner = findViewById(R.id.spinnerMain);
        String message = mainSpinner.getSelectedItem().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    public void onGetRecipesComplete(ArrayList<Recipes> list) {

    }
}
