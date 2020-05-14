package com.example.feast.core.client.adapter;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public interface IAuthService {
    Task<AuthResult> singInWithGoogle(Task<GoogleSignInAccount> task) throws ApiException;

    void signOut();

    FirebaseUser getCurrentUser();
}
