package com.example.feast.core.client.adapter.impl;

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

}
