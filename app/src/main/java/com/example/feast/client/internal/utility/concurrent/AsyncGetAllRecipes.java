package com.example.feast.client.internal.utility.concurrent;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.feast.core.entities.Ingredient;
import com.example.feast.core.entities.RecipeContainer;
import com.example.feast.core.entities.Recipes;
import com.example.feast.core.entities.UserRecipes;
import com.example.feast.core.services.IRecipeService;
import com.example.feast.core.services.IUserRecipeService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class AsyncGetAllRecipes extends AsyncUpdateTask<RecipeContainer> {


    private static final String TAG = "GetRecipeTask";
    private final String USER_ID;
    private final IUserRecipeService urServ;
    private final IRecipeService reServ;

    public AsyncGetAllRecipes(AsyncUpdate<RecipeContainer> listener, String userId, IUserRecipeService urServ, IRecipeService reServ) {
        super(listener);
        this.USER_ID = userId;
        this.urServ = urServ;
        this.reServ = reServ;
    }

    @Override
    protected RecipeContainer doInBackground(Void... voids) {
        final RecipeContainer rc = new RecipeContainer(new ArrayList<UserRecipes>(), new ArrayList<Recipes>());
        try {
            final CountDownLatch latch = new CountDownLatch(2);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Task<QuerySnapshot> task1 = reServ.ReadAll()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<Recipes> list = convertToRecipes(task.getResult());
                                rc.setRecipes(list);
                                latch.countDown();
                                Log.d(TAG, "onComplete: Recipe succesfully converted");
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
            Task<QuerySnapshot> task2 = urServ.readByUserId(USER_ID)
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<UserRecipes> list = convertToUserRecipes(task.getResult());
                                rc.setUserRecipes(list);
                                latch.countDown();
                                Log.d(TAG, "onComplete: UserRecipes succesfully converted");
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
            Log.d(TAG, "doInBackground: Waiting for latch to open");
            latch.await();
            Log.d(TAG, "doInBackground: latch is open");

            return rc;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<UserRecipes> convertToUserRecipes(QuerySnapshot result) {
        ArrayList<UserRecipes> list = new ArrayList<>();
        for (QueryDocumentSnapshot document : result) {

            try {
                String id = document.getId();
                String name = (String) document.getData().get("name");
                ArrayList<Ingredient> ing = (ArrayList<Ingredient>) document.getData().get("ingredients");
                long estimatedTime = (long) document.getData().get("estimatedTime");
                String userId = (String) document.getData().get("userId");
                list.add(new UserRecipes(ing, id, estimatedTime, name, userId));
                Log.d(TAG, "convertToUserRecipes: Converted " + name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private ArrayList<Recipes> convertToRecipes(QuerySnapshot result) {
        ArrayList<Recipes> list = new ArrayList<>();
        for (QueryDocumentSnapshot document : result) {

            try {
                String id = document.getId();
                String name = (String) document.getData().get("name");
                ArrayList<Ingredient> ing = (ArrayList<Ingredient>) document.getData().get("ingredients");
                long estimatedTime = (long) document.getData().get("estimatedTime");
                String imageUrl = (String) document.getData().get("imageUrl");
                list.add(new Recipes(ing, id, estimatedTime, name, imageUrl));
                Log.d(TAG, "convertToRecipes: Converted " + name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
