package com.example.feast.core.data.adapter;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public interface IRecipeRepo {
    Task<QuerySnapshot> ReadAll();

    Task<DocumentSnapshot> Read(String id);
}
