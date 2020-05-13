package com.example.feast.core.services;

import com.example.feast.core.entities.UserRecipes;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

public interface IUserRecipeService {
    Task<QuerySnapshot> readByUserId(String userId);

    Task<DocumentReference> create(UserRecipes re);

    Task<Void> delete(String id);

    Task<Void> update(UserRecipes re);
}
