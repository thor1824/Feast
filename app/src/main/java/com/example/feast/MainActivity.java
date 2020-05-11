package com.example.feast;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.varunest.sparkbutton.SparkButton;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner mainSpinner;
    SparkButton sparkButton;
    String valueFromSpinner;
    GoogleSignInClient mSignInClient;
    SignInButton signInButton;
    TextView test;
    private FirebaseAuth mAuth;

    private String TAG = "app";

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainSpinner = findViewById(R.id.spinnerMain);
        sparkButton = findViewById(R.id.spark_button);
        String[] spinnerValues = getResources().getStringArray(R.array.minutesForSpinner);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainSpinner.setAdapter(adapter);
        test = findViewById(R.id.test);
        test.setText("not Signed in");

        mAuth = FirebaseAuth.getInstance();


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }



}
