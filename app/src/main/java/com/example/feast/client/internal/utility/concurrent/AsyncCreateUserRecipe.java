package com.example.feast.client.internal.utility.concurrent;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.feast.client.internal.model.Model;
import com.example.feast.core.entities.UserRecipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;

import java.util.concurrent.CountDownLatch;

public class AsyncCreateUserRecipe extends AsyncUpdateTask<Void> {
    private static final String TAG = "UpdateUserRecipeTask";
    private Uri imageUri;
    private UserRecipe ur;
    private Context ctx;
    private boolean isSuccesfull;

    public AsyncCreateUserRecipe(Uri imageUrl, UserRecipe ur, Context ctx, AsyncUpdate<Void> listener) {
        super(listener);
        this.imageUri = imageUrl;
        this.ur = ur;
        this.ctx = ctx;
        this.isSuccesfull = false;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            final CountDownLatch latch = new CountDownLatch(2);
            final Model model = Model.getInstance();
            model.saveImage(imageUri, System.currentTimeMillis() + "." + model.getFileExt(imageUri, ctx)).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    latch.countDown();

                    Uri uri = task.getResult();
                    String url = uri.toString();

                    ur.setImageUrl(FirebaseStorage.getInstance().getReferenceFromUrl(url).toString());

                    model.createUserRecipe(ur).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            latch.countDown();
                        }
                    });
                }
            });
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
