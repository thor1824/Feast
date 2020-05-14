package com.example.feast.client.internal.utility.concurrent;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.feast.core.entities.Ingredient;
import com.example.feast.core.entities.Recipe;
import com.example.feast.core.entities.RecipeContainer;
import com.example.feast.core.entities.UserRecipe;
import com.example.feast.core.services.IRecipeService;
import com.example.feast.core.services.IUserRecipeService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class AsyncGetAllRecipes extends AsyncUpdateTask<RecipeContainer> {


    private static final String TAG = "GetRecipeTask";
    private final String USER_ID;
    private final IUserRecipeService urServ;
    private final IRecipeService reServ;
    private boolean isComplete;

    public AsyncGetAllRecipes(AsyncUpdate<RecipeContainer> listener, String userId, IUserRecipeService urServ, IRecipeService reServ) {
        super(listener);
        this.USER_ID = userId;
        this.urServ = urServ;
        this.reServ = reServ;
        isComplete = false;
    }

    @Override
    protected RecipeContainer doInBackground(Void... voids) {
        isComplete = false;
        final RecipeContainer rc = new RecipeContainer(new ArrayList<UserRecipe>(), new ArrayList<Recipe>());
        try {
            final CountDownLatch latch = new CountDownLatch(2);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Task<QuerySnapshot> task1 = reServ.ReadAll()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<Recipe> list = convertToRecipes(task.getResult());
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
                                ArrayList<UserRecipe> list = convertToUserRecipes(task.getResult());
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
            isComplete = true;
            return rc;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<UserRecipe> convertToUserRecipes(QuerySnapshot result) {
        ArrayList<UserRecipe> list = new ArrayList<>();
        for (QueryDocumentSnapshot document : result) {

            try {
                String id = document.getId();
                String name = (String) document.getData().get("name");
                ArrayList<Ingredient> ing = convertToIngredients((ArrayList<HashMap<String, Object>>) document.getData().get("ingredients"));
                long estimatedTime = (long) document.getData().get("estimatedTime");
                String userId = (String) document.getData().get("userId");
                list.add(new UserRecipe(ing, id, estimatedTime, name, userId));
                Log.d(TAG, "convertToUserRecipes: Converted " + name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private ArrayList<Recipe> convertToRecipes(QuerySnapshot result) {
        ArrayList<Recipe> list = new ArrayList<>();
        for (QueryDocumentSnapshot document : result) {

            try {
                String id = document.getId();
                String name = (String) document.getData().get("name");
                ArrayList<Ingredient> ing = convertToIngredients((ArrayList<HashMap<String, Object>>) document.getData().get("ingredients"));
                long estimatedTime = (long) document.getData().get("estimatedTime");
                String imageUrl = (String) document.getData().get("imageUrl");
                list.add(new Recipe(ing, id, estimatedTime, name, imageUrl));
                Log.d(TAG, "convertToRecipes: Converted " + name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private ArrayList<Ingredient> convertToIngredients(ArrayList<HashMap<String, Object>> list) {
        ArrayList<Ingredient> ing = new ArrayList<>();
        for (HashMap<String, Object> map : list) {
            String name = (String) map.get("name");
            long amount = (long) map.get("amount");
            ing.add(new Ingredient(name, amount));
        }
        return ing;
    }

    public boolean isCompleted() {
        return isComplete;
    }
}
