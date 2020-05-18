package com.example.feast.client.internal.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.feast.R;
import com.example.feast.client.internal.model.Model;
import com.example.feast.client.internal.utility.concurrent.Listener;
import com.example.feast.core.entities.RecipeContainer;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "login";

    private GoogleSignInClient mSignInClient;
    private SignInButton signInButton;
    private Model model;

    /**
     * sets up the activity, with google signing
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        model = Model.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    /**
     * check if the user isn't null.
     * if the user isn't null it makes a new intent, and starts the main activity.
     */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = model.getCurrentUser();
        Log.d(TAG, "onStart: " + currentUser);
        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivityForResult(intent, 10);
            finish();
        }
    }

    /**
     * signs in the user
     */
    private void signIn() {
        Intent intent = mSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    /**
     * checks the result code, if the resultcode is "sign-in"
     * it logs in the user via firebase
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            firebaseAuthWithGoogle(data);
        }
    }

    /**
     * signs in the user via firebase auth.
     * @param data
     */
    private void firebaseAuthWithGoogle(Intent data) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            model.singInWithGoogle(GoogleSignIn.getSignedInAccountFromIntent(data))
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = model.getCurrentUser();
                                backToMain();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                            }

                            // ...
                        }
                    });
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
        }

    }

    /**
     * returns the user to the main activity
     */
    private void backToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, 10);
    }
}
