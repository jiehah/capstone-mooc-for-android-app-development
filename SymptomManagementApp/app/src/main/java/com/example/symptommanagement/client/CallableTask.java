package com.example.symptommanagement.client;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.Callable;

/**
 * AsyncTask that executes a given Callable in the background and provides a callback
 * mechanism to handle the result or error in the main/UI thread.
 *
 * @param <T> The type of the result returned by the Callable.
 */
public class CallableTask<T> extends AsyncTask<Void, Double, T> {

    private static final String TAG = CallableTask.class.getName();

    /**
     * Invokes the given Callable using a CallableTask instance and executes it asynchronously.
     *
     * @param call     The Callable to be executed.
     * @param callback The callback interface to handle the result or error of the Callable.
     * @param <V>      The type of the result returned by the Callable.
     */
    public static <V> void invoke(Callable<V> call, TaskCallback<V> callback) {
        new CallableTask<>(call, callback).execute();
    }

    private final Callable<T> callable;

    private final TaskCallback<T> callback;

    private Exception error;

    /**
     * Constructs a new CallableTask with the given Callable and TaskCallback.
     *
     * @param callable The Callable to be executed.
     * @param callback The callback interface to handle the result or error of the Callable.
     */
    public CallableTask(Callable<T> callable, TaskCallback<T> callback) {
        this.callable = callable;
        this.callback = callback;
    }

    /**
     * Executes the Callable in the background thread.
     * This method is automatically called by the AsyncTask framework.
     *
     * @param ts Not used in this implementation.
     * @return The result returned by the Callable, or null in case of an error.
     */
    @Override
    protected T doInBackground(Void... ts) {
        T result = null;
        try {
            result = callable.call();
        } catch (Exception e) {
            Log.e(TAG, "Error invoking callable in AsyncTask callable: " + callable, e);
            error = e;
        }
        return result;
    }

    /**
     * Handles the result of the background execution.
     * This method is automatically called by the AsyncTask framework.
     *
     * @param r The result returned by doInBackground or null in case of an error.
     */
    @Override
    protected void onPostExecute(T r) {
        if (error != null) {
            callback.error(error);
        } else {
            callback.success(r);
        }
    }
}
