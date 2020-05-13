package com.example.feast.core.services.impl;

import com.example.feast.core.data.adapter.IUserRecipeRepo;
import com.example.feast.core.entities.UserRecipes;
import com.example.feast.core.services.IUserRecipeService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

public class UserRecipeService implements IUserRecipeService {

    private IUserRecipeRepo _repo;

    public UserRecipeService(IUserRecipeRepo _repo) {
        this._repo = _repo;
    }

    @Override
    public Task<QuerySnapshot> readByUserId(String userId) {
        return _repo.readByUserId(userId);
    }

    @Override
    public Task<DocumentReference> create(UserRecipes re) {
        return _repo.create(re);
    }

    @Override
    public Task<Void> delete(String id) {
        return _repo.delete(id);
    }

    @Override
    public Task<Void> update(UserRecipes re) {
        return _repo.update(re);
    }


}
