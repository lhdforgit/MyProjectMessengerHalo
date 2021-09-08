/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.threading;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.giphy.sdk.core.network.api.CompletionHandler;

import java.io.InterruptedIOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Represents a top level task returned by API calls that can be ran async or immediately
 * with an ExecutorService.
 *
 * @see Future
 * @see ExecutorService
 */
public class ApiTask<V> {

    /**
     * HTTP Executor settings borrowed from Bolts-Android's AndroidExecutors.newCachedThreadPool
     * https://github.com/BoltsFramework/Bolts-Android/blob/master/bolts-tasks/src/main/java/bolts/AndroidExecutors.java
     */
    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    public static final int THREAD_POOL_CORE_SIZE = CPU_COUNT + 2;
    public static final int THREAD_POOL_MAX_SIZE = CPU_COUNT * 2 + 2;
    public static final long THREAD_POOL_KEEP_ALIVE_TIME = 1L;

    private static ExecutorService NETWORK_REQUEST_EXECUTOR;

    public static ExecutorService getNetworkRequestExecutor() {
        if (NETWORK_REQUEST_EXECUTOR == null) {
            NETWORK_REQUEST_EXECUTOR = new ThreadPoolExecutor(
                    THREAD_POOL_CORE_SIZE,
                    THREAD_POOL_MAX_SIZE,
                    THREAD_POOL_KEEP_ALIVE_TIME,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>()
            );
        }
        return NETWORK_REQUEST_EXECUTOR;
    }

    private static Executor COMPLETION_EXECUTOR;

    public static Executor getCompletionExecutor() {
        if (COMPLETION_EXECUTOR == null) {
            COMPLETION_EXECUTOR = new HandlerExecutor(new Handler(Looper.getMainLooper()));
        }
        return COMPLETION_EXECUTOR;
    }

    private final Callable<V> callable;
    private final ExecutorService networkRequestExecutor;
    private final Executor completionExecutor;

    public ApiTask(Callable<V> callable) {
        this.callable = callable;
        this.networkRequestExecutor = getNetworkRequestExecutor();
        this.completionExecutor = getCompletionExecutor();
    }

    public ApiTask(Callable<V> callable, ExecutorService networkRequestExecutor, Executor completionExecutor) {
        this.callable = callable;
        this.networkRequestExecutor = networkRequestExecutor;
        this.completionExecutor = completionExecutor;
    }

    /**
     * Resolves the task on a shared thread pool executor service and returns the result using the
     * completionHandler
     *
     * @param completionHandler
     * @return
     */
    public Future executeAsyncTask(final CompletionHandler<V> completionHandler) {
        return networkRequestExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final V value = callable.call();

                    // If thread was interrupted, throw error
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

                    completionExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            completionHandler.onComplete(value, null);
                        }
                    });
                } catch (final ExecutionException e) {
                    Log.e(ApiTask.class.getName(), "Unable to perform async task, cancelling…", e);

                    completionExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            completionHandler.onComplete(null, e);
                        }
                    });
                } catch (InterruptedIOException | InterruptedException e) { // interrupts will naturally occur from cancelling
                } catch (final Throwable e) {
                    completionExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            completionHandler.onComplete(null, e);
                        }
                    });
                }
            }
        });
    }

    /**
     * Immediately resolves the task on the same thread.
     *
     * @return The resolved value from running the task.
     * @throws Exception
     */
    public V executeImmediately() throws Exception {
        return callable.call();
    }
}