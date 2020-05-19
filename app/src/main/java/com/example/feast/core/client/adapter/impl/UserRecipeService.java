package com.example.feast.core.client.adapter.impl;

import com.example.feast.core.client.adapter.IUserRecipeService;
import com.example.feast.core.data.adapter.IUserRecipeRepo;
import com.example.feast.core.entities.UserRecipe;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

public class UserRecipeService implements IUserRecipeService {

    private IUserRecipeRepo _repo;

    /**
     * Constructor
     *
     * @param _repo
     */
    public UserRecipeService(IUserRecipeRepo _repo) {
        this._repo = _repo;
    }

    /**
     * Gets a user by the id
     *
     * @param userId
     * @return
     */
    @Override
    public Task<QuerySnapshot> readByUserId(String userId) {
        return _repo.readByUserId(userId);
    }

    /**
     * creates a userRecipe in the userRepository
     *
     * @param re
     * @return
     */
    @Override
    public Task<DocumentReference> create(UserRecipe re) {
        return _repo.create(re);
    }

    /**
     * Deletes a userRecipe by id
     *
     * @param id
     * @return
     */
    @Override
    public Task<Void> delete(String id) {
        return _repo.delete(id);
    }

    /**
     * Updates a userRecipe in the repository
     *
     * @param re
     * @return
     */
    @Override
    public Task<Void> update(UserRecipe re) {
        return _repo.update(re);
    }


}
