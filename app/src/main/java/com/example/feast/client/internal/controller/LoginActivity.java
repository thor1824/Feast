package com.example.feast.client.internal.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.feast.R;
import com.example.feast.client.internal.model.Model;
import com.example.feast.client.internal.utility.globals.RequestCodes;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private  final String TAG = "LoginActivity";

    private GoogleSignInClient mSignInClient;

    private Model model;

    //<editor-fold desc="Overrides">

    /**
     * sets up the activity, with google signing
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        model = Model.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        navigateIfLoggedIn();

        setupSignIn();
    }

    /**
     * checks the result code, if the resultcode is "sign-in"
     * it logs in the user via firebase
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestCodes.RC_SIGN_IN: {
                if (resultCode == RESULT_OK) {
                    firebaseAuthWithGoogle(data);
                }
                break;
            }
            default: {
                Log.d(TAG, "onActivityResult not setup for Result Code " + resultCode);
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Setup">
    private void setupSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mSignInClient = GoogleSignIn.getClient(this, gso);
        SignInButton signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }
    //</editor-fold>

    //<editor-fold desc="Button Actions">

    /**
     * signs in the user
     */
    private void signIn() {
        Intent intent = mSignInClient.getSignInIntent();
        startActivityForResult(intent, RequestCodes.RC_SIGN_IN);
    }
    //</editor-fold>

    //<editor-fold desc="Helper Functions">

    /**
     * signs in the user via firebase auth.
     *
     * @param data
     */
    private void firebaseAuthWithGoogle(Intent data) {
        try {
            model.singInWithGoogle(GoogleSignIn.getSignedInAccountFromIntent(data))
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithCredential:success");
                                navigateIfLoggedIn();
                            } else {

                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                            }
                        }
                    });
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
        }

    }

    /**
     * Navigates the user (if logged in) to the main activity
     */
    public void navigateIfLoggedIn() {
        FirebaseUser currentUser = model.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        }
    }
    //</editor-fold>
}
