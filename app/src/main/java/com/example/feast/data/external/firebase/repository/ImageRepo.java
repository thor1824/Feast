package com.example.feast.data.external.firebase.repository;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.feast.core.data.adapter.IImageRepo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ImageRepo implements IImageRepo {

    /**
     * Gets the image from firebase storage
     *
     * @param imgUrl
     * @return
     */
    @Override
    public Task<byte[]> getImage(String imgUrl) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference gsReference = storage.getReferenceFromUrl(imgUrl);
        final long ONE_MEGABYTE = 1024 * 1024;
        return gsReference.getBytes(ONE_MEGABYTE);
    }

    /**
     * Saves the image to firebase storage
     *
     * @param imgUrl
     * @param fileName
     * @return
     */
    public Task<Uri> saveImage(Uri imgUrl, String fileName) {
        final StorageReference fileRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child("images")
                .child("recipe")
                .child(fileName);

        return fileRef.putFile(imgUrl).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return fileRef.getDownloadUrl();
            }
        });
    }


}
