package com.example.feast.core.client.adapter;

import com.google.android.gms.tasks.Task;

public interface IImageService {
    Task<byte[]> getImage(String imgUrl);

    Task<byte[]> setImage(String imgUrl);
}
