package com.example.apputils;

/**
 * Interface for async callback, required for constructor of AsyncHttpGetTask
 */
public interface ResponseListener{
    /**
     * This function is called when AsyncHttpGetTask completes
     * @param output Object returned by the async task, usually a String
     */
    void taskComplete(Object output);
}