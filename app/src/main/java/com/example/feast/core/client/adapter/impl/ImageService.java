package com.example.feast.core.client.adapter.impl;

import android.net.Uri;

import com.example.feast.core.client.adapter.IImageService;
import com.example.feast.core.data.adapter.IImageRepo;
import com.google.android.gms.tasks.Task;

public class ImageService implements IImageService {

    private IImageRepo imageRepo;

    public ImageService(IImageRepo imageRepo) {
        this.imageRepo = imageRepo;
    }

    @Override
    public Task<byte[]> getImage(String imgUrl) {
        return imageRepo.getImage(imgUrl);
    }

    @Override
    public Task<Uri> saveImage(Uri uri, String fileName) {
        return imageRepo.saveImage(uri, fileName);
    }

}
