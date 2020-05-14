package com.example.feast.data.external.firebase.repository;

import com.google.android.gms.tasks.Task;

public interface IImageRepo {
    Task<byte[]> getImage(String imgUrl);
}
