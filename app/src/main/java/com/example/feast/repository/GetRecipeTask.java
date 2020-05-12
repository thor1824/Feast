package com.example.feast.repository;

import android.os.AsyncTask;

import com.example.feast.Models.Recipes;
import com.example.feast.onGetRecipesComplete;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GetRecipeTask extends AsyncTask<Void, Void, ArrayList<Recipes>> {
    final String TAG = "repo";
    private onGetRecipesComplete listener;

    public GetRecipeTask(onGetRecipesComplete listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<Recipes> doInBackground(Void... params) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> next = db.collection("Recipes")
                .get();

        while (!next.isComplete()) {
        }

        ArrayList<Recipes> temp = new ArrayList<>();
        for (QueryDocumentSnapshot document : next.getResult()) {

            Recipes re = (Recipes) document.getData();
        }

        return temp;
    }

    protected void onPostExecute(ArrayList<Recipes> list) {
        // your stuff
        listener.onGetRecipesComplete(list);
    }

    // required methods

}