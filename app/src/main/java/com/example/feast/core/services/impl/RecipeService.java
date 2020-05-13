package com.example.feast.core.services.impl;

import com.example.feast.core.data.adapter.IRecipeRepo;
import com.example.feast.core.services.IRecipeService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RecipeService implements IRecipeService {

    private IRecipeRepo _repo;

    public RecipeService(IRecipeRepo repo) {
        _repo = repo;
    }

    @Override
    public Task<QuerySnapshot> ReadAll() {
        return _repo.ReadAll();
    }

    @Override
    public Task<DocumentSnapshot> Read(String id) {
        return _repo.Read(id);

    }
}
