package com.example.feast.data.external.firebase.repository;

import com.example.feast.core.data.adapter.IRecipeRepo;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class RecipeRepo implements IRecipeRepo {

    /**
     * reads all the recipes from the recipes collection
     *
     * @return
     */
    @Override
    public Task<QuerySnapshot> ReadAll() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("Recipes")
                .get();
    }

    /**
     * reads one recipe by a id
     *
     * @param id
     * @return
     */
    @Override
    public Task<DocumentSnapshot> Read(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("Recipes").document(id)
                .get();
    }
}
