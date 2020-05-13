package com.example.feast.core.services;

import com.google.android.gms.tasks.Task;

public interface IImageService {
    Task<byte[]> getImage(String imgUrl);
}
