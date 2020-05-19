package com.example.feast.data.external.firebase.repository;

import android.util.Log;

import com.example.feast.core.data.adapter.IUserRecipeRepo;
import com.example.feast.core.entities.UserRecipe;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserRecipeRepo implements IUserRecipeRepo {

    /**
     * Gets the userRecipes for the current user
     *
     * @param userId
     * @return
     */
    @Override
    public Task<QuerySnapshot> readByUserId(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("UserRecipe")
                .whereEqualTo("userId", userId)
                .get();
    }

    /**
     * creates a userRecipe on firebase
     *
     * @param re
     * @return
     */
    @Override
    public Task<DocumentReference> create(UserRecipe re) {

        Log.d("CreateUserRecipe", re.getImageUrl());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("UserRecipe").add(re);

    }

    /**
     * deletes a recipe by the id given in the params
     *
     * @param id
     * @return
     */
    @Override
    public Task<Void> delete(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("UserRecipe").document(id).delete();

    }

    /**
     * updates a userRecipe in the firebase
     *
     * @param re
     * @return
     */
    @Override
    public Task<Void> update(UserRecipe re) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", re.getName());
        data.put("ingredients", re.getIngredients());
        data.put("estimatedTime", re.getEstimatedTime());
        data.put("userId", re.getUserId());
        data.put("imageUrl", re.getImageUrl());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("UserRecipe").document(re.getId()).update(data);
    }
}
