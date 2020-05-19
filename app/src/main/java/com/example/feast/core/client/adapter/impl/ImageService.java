package com.example.feast.core.client.adapter.impl;

import android.net.Uri;

import com.example.feast.core.client.adapter.IImageService;
import com.example.feast.core.data.adapter.IImageRepo;
import com.google.android.gms.tasks.Task;

public class ImageService implements IImageService {

    private IImageRepo imageRepo;

    /**
     * Constructor
     *
     * @param imageRepo
     */
    public ImageService(IImageRepo imageRepo) {
        this.imageRepo = imageRepo;
    }

    /**
     * gets the image from the repository
     *
     * @param imgUrl
     * @return
     */
    @Override
    public Task<byte[]> getImage(String imgUrl) {
        return imageRepo.getImage(imgUrl);
    }

    /**
     * Saves the image to the repository
     *
     * @param uri
     * @param fileName
     * @return
     */
    @Override
    public Task<Uri> saveImage(Uri uri, String fileName) {
        return imageRepo.saveImage(uri, fileName);
    }

}
