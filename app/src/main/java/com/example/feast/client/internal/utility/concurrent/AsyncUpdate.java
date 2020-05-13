package com.example.feast.client.internal.utility.concurrent;

public interface AsyncUpdate<T> {

    void update(T entity);
}
