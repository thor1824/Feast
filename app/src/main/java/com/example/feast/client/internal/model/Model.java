package com.example.feast.client.internal.model;

import com.example.feast.client.internal.utility.concurrent.AsyncGetAllRecipes;
import com.example.feast.client.internal.utility.concurrent.AsyncUpdate;
import com.example.feast.client.internal.utility.concurrent.Listener;
import com.example.feast.core.entities.IRecipe;
import com.example.feast.core.entities.RecipeContainer;
import com.example.feast.core.entities.UserRecipe;
import com.example.feast.core.services.IRecipeService;
import com.example.feast.core.services.IUserRecipeService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Model implements AsyncUpdate<RecipeContainer> {

    private static Model model;
    private Listener<RecipeContainer> list;
    private AsyncGetAllRecipes task;
    private IUserRecipeService userRecipeService;
    private IRecipeService recipeService;
    private RecipeContainer recipeContainer;

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
        recipeContainer = entity;
        if (list != null) {

            list.call(entity);
        }
    }

    public Task<Void> updateUserRecipe(UserRecipe ur) {
        return userRecipeService.update(ur);
    }

    public Task<Void> deleteUserRecipe(String id) {
        return userRecipeService.delete(id);
    }

    public Task<DocumentReference> createUserRecipe(UserRecipe ur) {
        return userRecipeService.create(ur);
    }

    public void readAllRecipes(String userId, Listener<RecipeContainer> listener) {
        this.list = listener;
        task = new AsyncGetAllRecipes(this, userId, userRecipeService, recipeService);
        task.execute();
    }

    public IRecipe getRandomRecipe(int estimatedTime) {

        List<IRecipe> recipes = new ArrayList<>();

        while (!task.isCompleted()) {
        }

        for (IRecipe re : recipeContainer.getRecipes()) {
            if (estimatedTime >= re.getEstimatedTime()) {
                recipes.add(re);
            }
        }
        for (IRecipe re : recipeContainer.getUserRecipes()) {
            if (estimatedTime >= re.getEstimatedTime()) {
                recipes.add(re);
            }
        }

        int size = recipes.size();

        if (size >= 0) {
            if (size != 0) {
                Random rn = new Random();
                int randomInt = rn.nextInt(((recipes.size() - 1) + 1));
                return recipes.get(randomInt);
            } else {
                return recipes.get(0);
            }
        }
        return null;
    }

    public void CancelTasks() {
        task.cancel(true);
    }
}
