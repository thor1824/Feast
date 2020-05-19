package com.example.feast.core.client.adapter;

import android.net.Uri;

import com.google.android.gms.tasks.Task;

public interface IImageService {
    Task<byte[]> getImage(String imgUrl);

    Task<Uri> saveImage(Uri imgUrl, String fileName, String userId);
}
