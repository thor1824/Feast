package com.example.feast.client.internal.model;

import android.os.AsyncTask;

import com.example.feast.client.internal.utility.concurrent.AsyncGetAllRecipes;
import com.example.feast.client.internal.utility.concurrent.AsyncUpdate;
import com.example.feast.client.internal.utility.concurrent.Listener;
import com.example.feast.core.entities.RecipeContainer;
import com.example.feast.core.entities.UserRecipes;
import com.example.feast.core.services.IRecipeService;
import com.example.feast.core.services.IUserRecipeService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

public class Model implements AsyncUpdate<RecipeContainer> {

    private static Model model;
    private Listener<RecipeContainer> list;
    private AsyncTask<Void, Void, RecipeContainer> task;
    private IUserRecipeService userRecipeService;
    private IRecipeService recipeService;

    protected Model(IUserRecipeService userRecipeService, IRecipeService recipeService) {
        this.userRecipeService = userRecipeService;
        this.recipeService = recipeService;
    }

    public static Model getInstance() {
        if (model == null) {
            model = BuildFactory.BuildModel();
        }
        return model;
    }

    @Override
    public void update(RecipeContainer entity) {
        if (list != null) {
            list.call(entity);
        }
    }

    public Task<Void> updateUserRecipe(UserRecipes ur) {
        return userRecipeService.update(ur);
    }

    public Task<Void> deleteUserRecipe(String id) {
        return userRecipeService.delete(id);
    }

    public Task<DocumentReference> createUserRecipe(UserRecipes ur) {
        return userRecipeService.create(ur);
    }

    public void readAllRecipes(String userId, Listener<RecipeContainer> listener) {
        this.list = listener;
        AsyncTask<Void, Void, RecipeContainer> next = new AsyncGetAllRecipes(this, userId, userRecipeService, recipeService);
        next.execute();
    }
}
