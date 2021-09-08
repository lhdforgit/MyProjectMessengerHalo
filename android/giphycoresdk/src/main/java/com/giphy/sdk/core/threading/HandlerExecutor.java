/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.threading;


import android.os.Handler;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Adapts an Android {@link android.os.Handler Handler} into a JRE {@link java.util.concurrent.Executor Executor}.
 * Runnables will be posted asynchronously.
 */
public class HandlerExecutor implements Executor {
    /**
     * Handler wrapped by this executor.
     */
    private Handler handler;

    /**
     * Construct a new executor wrapping the specified handler.
     *
     * @param handler Handler to wrap.
     */
    public HandlerExecutor(@NonNull Handler handler) {
        this.handler = handler;
    }

    /**
     * Execute a command, by posting it to the underlying handler.
     *
     * @param command Command to execute.
     */
    public void execute(Runnable command) {
        handler.post(command);
    }
}