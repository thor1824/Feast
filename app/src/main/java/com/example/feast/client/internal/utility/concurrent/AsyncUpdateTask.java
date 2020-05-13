package com.example.feast.client.internal.utility.concurrent;

import android.os.AsyncTask;

public abstract class AsyncUpdateTask<T> extends AsyncTask<Void, Void, T> {

    private final AsyncUpdate<T> listener;

    public AsyncUpdateTask(AsyncUpdate<T> listener) {
        this.listener = listener;
    }

    @Override
    protected abstract T doInBackground(Void... voids);

    protected void onPostExecute(T entity) {
        listener.update(entity);
    }
}
