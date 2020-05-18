package com.example.feast.core.data.adapter;

import android.net.Uri;

import com.google.android.gms.tasks.Task;

public interface IImageRepo {
    Task<byte[]> getImage(String imgUrl);

    Task<Uri> saveImage(Uri imgUrl, String fileName);
}
