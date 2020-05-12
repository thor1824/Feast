package com.example.feast.repository;

import android.os.AsyncTask;

import com.example.feast.Models.Ingredient;
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
    private boolean isComplete = false;
    private boolean isSucces = false;
    private boolean isFailed = false;


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

            try {
                String id = document.getId();
                String name = (String) document.getData().get("name");
                ArrayList<Ingredient> ing = (ArrayList<Ingredient>) document.getData().get("ingredients");
                long estimatedTime = (long) document.getData().get("estimatedTime");
                temp.add(new Recipes(ing, id, estimatedTime, name));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return temp;
    }

    protected void onPostExecute(ArrayList<Recipes> list) {
        // your stuff
        listener.onGetRecipesComplete(list);
    }

    public boolean isComplete() {
        return isComplete;
    }

    public boolean isSucces() {
        return isSucces;
    }

    public boolean isFailed() {
        return isFailed;
    }

    // required methods

}