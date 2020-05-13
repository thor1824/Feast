package com.example.feast.client.internal.utility.concurrent;

public interface Listener<T> {
    void call(T entity);
}
