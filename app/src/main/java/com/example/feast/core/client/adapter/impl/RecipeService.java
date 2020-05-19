package com.example.feast.core.client.adapter.impl;

import com.example.feast.core.client.adapter.IRecipeService;
import com.example.feast.core.data.adapter.IRecipeRepo;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RecipeService implements IRecipeService {

    private IRecipeRepo _repo;

    /**
     * Constructor
     *
     * @param repo
     */
    public RecipeService(IRecipeRepo repo) {
        _repo = repo;
    }

    /**
     * Gets all the recipes from the repository
     *
     * @return
     */
    @Override
    public Task<QuerySnapshot> ReadAll() {
        return _repo.ReadAll();
    }

    /**
     * gets on recipe by the id from the repository
     *
     * @param id
     * @return
     */
    @Override
    public Task<DocumentSnapshot> Read(String id) {
        return _repo.Read(id);

    }
}
