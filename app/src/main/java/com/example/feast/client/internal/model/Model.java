package com.example.feast.client.internal.model;

import com.example.feast.client.internal.utility.concurrent.AsyncGetAllRecipes;
import com.example.feast.client.internal.utility.concurrent.AsyncUpdate;
import com.example.feast.client.internal.utility.concurrent.Listener;
import com.example.feast.core.client.adapter.IAuthService;
import com.example.feast.core.client.adapter.IImageService;
import com.example.feast.core.client.adapter.IRecipeService;
import com.example.feast.core.client.adapter.IUserRecipeService;
import com.example.feast.core.entities.IRecipe;
import com.example.feast.core.entities.RecipeContainer;
import com.example.feast.core.entities.UserRecipe;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
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
    private IImageService imageService;
    private IAuthService authService;

    protected Model(IUserRecipeService userRecipeService, IRecipeService recipeService, IImageService imageService, IAuthService authService) {
        this.userRecipeService = userRecipeService;
        this.recipeService = recipeService;
        this.imageService = imageService;
        this.authService = authService;
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

    public Task<byte[]> getImage(String imgUrl) {
        return imageService.getImage(imgUrl);

    }

    public void CancelTasks() {
        task.cancel(true);
    }

    public Task<AuthResult> singInWithGoogle(Task<GoogleSignInAccount> task) throws ApiException {
        return authService.singInWithGoogle(task);
    }

    public void signOut() {
        authService.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return authService.getCurrentUser();
    }
}
