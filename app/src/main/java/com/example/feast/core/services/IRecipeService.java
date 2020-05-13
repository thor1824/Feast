package com.example.feast.core.services;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public interface IRecipeService {

    Task<QuerySnapshot> ReadAll();

    Task<DocumentSnapshot> Read(String id);
}
