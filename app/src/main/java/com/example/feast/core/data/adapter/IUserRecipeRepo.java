package com.example.feast.core.data.adapter;

import com.example.feast.core.entities.UserRecipe;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

public interface IUserRecipeRepo {

    Task<QuerySnapshot> readByUserId(String userId);

    Task<DocumentReference> create(UserRecipe re);

    Task<Void> delete(String id);

    Task<Void> update(UserRecipe re);
}
