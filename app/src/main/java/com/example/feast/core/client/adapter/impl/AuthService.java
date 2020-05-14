package com.example.feast.core.client.adapter.impl;

import com.example.feast.core.client.adapter.IAuthService;
import com.example.feast.core.data.adapter.IAuthRepo;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class AuthService implements IAuthService {

    private IAuthRepo authRepo;

    public AuthService(IAuthRepo authRepo) {

        this.authRepo = authRepo;
    }

    @Override
    public Task<AuthResult> singInWithGoogle(Task<GoogleSignInAccount> task) throws ApiException {
        return authRepo.singInWithGoogle(task);
    }

    @Override
    public void signOut() {
        authRepo.signOut();
    }

    @Override
    public FirebaseUser getCurrentUser() {
        return authRepo.getCurrentUser();
    }


}
