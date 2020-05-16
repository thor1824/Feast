package com.example.feast.client.internal.utility.concurrent;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.feast.client.internal.model.Model;
import com.example.feast.core.entities.UserRecipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.concurrent.CountDownLatch;

public class AsyncUpdateUserRecipe extends AsyncUpdateTask<Void> {
    private static final String TAG = "UpdateUserRecipeTask";
    private Uri imageUrl;
    private UserRecipe ur;
    private Context ctx;
    private boolean isSuccesfull;

    public AsyncUpdateUserRecipe(Uri imageUrl, UserRecipe ur, Context ctx, AsyncUpdate<Void> listener) {
        super(listener);
        this.imageUrl = imageUrl;
        this.ur = ur;
        this.ctx = ctx;
        this.isSuccesfull = false;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            final CountDownLatch latch = new CountDownLatch(2);
            final Model model = Model.getInstance();
            final StorageReference fileRef = FirebaseStorage
                    .getInstance()
                    .getReference()
                    .child("images")
                    .child("recipe")
                    .child(System.currentTimeMillis() + "." + model.getFileExt(imageUrl, ctx));

            fileRef.putFile(imageUrl).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    latch.countDown();
                                    String url = uri.toString();
                                    Log.d(TAG, "onSuccess: url:" +  url);
                                    ur.setImageUrl(FirebaseStorage.getInstance().getReferenceFromUrl(url).toString());

                                    model.updateUserRecipe(ur).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            isSuccesfull = true;
                                            latch.countDown();
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    cancel(true);
                                }
                            });
                }
            });
            Log.d(TAG, "doInBackground: Waiting for latch to open");

            latch.await();
            Log.d(TAG, "doInBackground: latch is open");
            Log.d(TAG, "doInBackground: Action Was A success = " + isSuccesfull);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
