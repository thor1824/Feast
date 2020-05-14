package com.example.feast.data.external.firebase.repository;

import com.example.feast.core.data.adapter.IImageRepo;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageRepo implements IImageRepo {

    @Override
    public Task<byte[]> getImage(String imgUrl) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference gsReference = storage.getReferenceFromUrl(imgUrl);
        final long ONE_MEGABYTE = 1024 * 1024;
        return gsReference.getBytes(ONE_MEGABYTE);
    }

}
