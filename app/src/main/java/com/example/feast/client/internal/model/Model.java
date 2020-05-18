package com.example.feast.client.internal.model;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Model implements AsyncUpdate<RecipeContainer> {

    String TAG = "Model";

    private static Model model;
    private Listener<RecipeContainer> list;
    private AsyncGetAllRecipes task;
    private IUserRecipeService userRecipeService;
    private IRecipeService recipeService;
    private RecipeContainer recipeContainer;
    private IImageService imageService;
    private IAuthService authService;

    /**
     * constructor
     * @param userRecipeService
     * @param recipeService
     * @param imageService
     * @param authService
     */
    protected Model(IUserRecipeService userRecipeService, IRecipeService recipeService, IImageService imageService, IAuthService authService) {
        this.userRecipeService = userRecipeService;
        this.recipeService = recipeService;
        this.imageService = imageService;
        this.authService = authService;
    }

    /**
     * Singleton modelInstance
     * @return
     */
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

    /**
     * redirects to the userRecipeService with an update
     * @param ur
     * @return
     */
    public Task<Void> updateUserRecipe(UserRecipe ur) {
        Log.d(TAG, "updateUserRecipe: ");
        return userRecipeService.update(ur);
    }

    /**
     * redirects to the userRecipeService with a delete
     * @param id
     * @return
     */
    public Task<Void> deleteUserRecipe(String id) {
        return userRecipeService.delete(id);
    }

    /**
     * redirects to the userRecipeService with an create
     * @param ur
     * @return
     */
    public Task<DocumentReference> createUserRecipe(UserRecipe ur) {
        return userRecipeService.create(ur);
    }

    /**
     * gets all the recipes from firebase from both collections
     * @param userId
     * @param listener
     */
    public void getAllRecipes(String userId, Listener<RecipeContainer> listener) {
        this.list = listener;
        task = new AsyncGetAllRecipes(userId, userRecipeService, recipeService, this);
        if (task.isCompleted()) {
            update(recipeContainer);
        } else {
            task.execute();
        }
    }


    public void forceUpdate() {
        if (list != null) {
            task = new AsyncGetAllRecipes(getCurrentUser().getUid(), userRecipeService, recipeService, this);
            task.execute();
        }
    }

    /**
     * gets a random recipe from either userrecipes or recipes
     * @param estimatedTime
     * @return
     */
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

        if (size > 0) {
            if (size != 1) {
                Random rn = new Random();
                int randomInt = rn.nextInt(((recipes.size() - 1) + 1));
                return recipes.get(randomInt);
            } else {
                return recipes.get(0);
            }
        }
        return null;
    }

    /**
     * gets the image
     * @param imgUrl
     * @return
     */
    public Task<byte[]> getImage(String imgUrl) {
        return imageService.getImage(imgUrl);

    }

    /**
     * sets the image
     * @param imgUrl
     * @return
     */
    public Task<byte[]> setImage(String imgUrl) {
        return imageService.setImage(imgUrl);

    }

    /**
     * cancels all tasks
     */
    public void CancelTasks() {
        task.cancel(true);
    }

    /**
     * signs in with google
     * @param task
     * @return
     * @throws ApiException
     */
    public Task<AuthResult> singInWithGoogle(Task<GoogleSignInAccount> task) throws ApiException {
        return authService.singInWithGoogle(task);
    }

    /**
     * signs out
     */
    public void signOut() {
        authService.signOut();
    }

    /**
     * gets the current user
     * @return
     */
    public FirebaseUser getCurrentUser() {
        return authService.getCurrentUser();
    }

    /**
     * uploads an image to the firebase storage
     * @param imageUrl
     * @param ctx
     * @return
     * @throws InterruptedException
     */
    public Task<Uri> uploadImage(Uri imageUrl, Context ctx) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final StorageReference fileRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child("images")
                .child("recipe")
                .child(System.currentTimeMillis() + "." + getFileExt(imageUrl, ctx));

        fileRef.putFile(imageUrl).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                latch.countDown();
            }
        });
        latch.await();
        return fileRef.getDownloadUrl();
    }

    /**
     * gets the file extension
     * @param contentUri
     * @param ctx
     * @return
     */
    public String getFileExt(Uri contentUri, Context ctx) {
        ContentResolver c = ctx.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }
}
