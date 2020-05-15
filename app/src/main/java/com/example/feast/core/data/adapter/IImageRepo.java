package com.example.feast.core.data.adapter;

import com.google.android.gms.tasks.Task;

public interface IImageRepo {
    Task<byte[]> getImage(String imgUrl);

    Task<byte[]> saveImage(String imgUrl);
}
