package com.example.feast.data.external.firebase.repository;

import com.example.feast.core.data.adapter.IUserRecipeRepo;
import com.example.feast.core.entities.UserRecipe;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserRecipeRepo implements IUserRecipeRepo {

    @Override
    public Task<QuerySnapshot> readByUserId(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("UserRecipe")
                .whereEqualTo("userId", userId)
                .get();
    }

    @Override
    public Task<DocumentReference> create(UserRecipe re) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("UserRecipe").add(re);

    }

    @Override
    public Task<Void> delete(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("UserRecipe").document(id).delete();

    }

    @Override
    public Task<Void> update(UserRecipe re) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", re.getName());
        data.put("ingredients", re.getIngredients());
        data.put("estimatedTime", re.getEstimatedTime());
        data.put("userId", re.getUserId());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("UserRecipe").document(re.getId()).update(data);
    }
}
