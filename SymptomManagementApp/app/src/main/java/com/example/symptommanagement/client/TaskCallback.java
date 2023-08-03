package com.example.symptommanagement.client;

/**
 * Interface for handling the result or error of a background task.
 *
 * @param <T> The type of the result returned by the background task.
 */
public interface TaskCallback<T> {
    /**
     * Called when the background task is successfully completed.
     *
     * @param result The result returned by the background task.
     */
    void success(T result);

    /**
     * Called when the background task encounters an error.
     *
     * @param e The exception representing the error.
     */
    void error(Exception e);
}
