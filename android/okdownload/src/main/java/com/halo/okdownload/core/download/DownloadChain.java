/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.download;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.halo.okdownload.DownloadTask;
import com.halo.okdownload.OkDownload;
import com.halo.okdownload.core.Util;
import com.halo.okdownload.core.breakpoint.BreakpointInfo;
import com.halo.okdownload.core.breakpoint.DownloadStore;
import com.halo.okdownload.core.connection.DownloadConnection;
import com.halo.okdownload.core.dispatcher.CallbackDispatcher;
import com.halo.okdownload.core.exception.InterruptException;
import com.halo.okdownload.core.file.MultiPointOutputStream;
import com.halo.okdownload.core.interceptor.BreakpointInterceptor;
import com.halo.okdownload.core.interceptor.FetchDataInterceptor;
import com.halo.okdownload.core.interceptor.Interceptor;
import com.halo.okdownload.core.interceptor.RetryInterceptor;
import com.halo.okdownload.core.interceptor.connect.CallServerInterceptor;
import com.halo.okdownload.core.interceptor.connect.HeaderInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DownloadChain implements Runnable {

    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
            Util.threadFactory("OkDownload Cancel Block", false));

    private static final String TAG = "DownloadChain";

    private final int blockIndex;

    @NonNull
    private final DownloadTask task;
    @NonNull
    private final BreakpointInfo info;
    @NonNull
    private final DownloadCache cache;

    final List<Interceptor.Connect> connectInterceptorList = new ArrayList<>();
    final List<Interceptor.Fetch> fetchInterceptorList = new ArrayList<>();
    int connectIndex = 0;
    int fetchIndex = 0;

    private long responseContentLength;
    private volatile DownloadConnection connection;

    long noCallbackIncreaseBytes;
    volatile Thread currentThread;

    private final CallbackDispatcher callbackDispatcher;

    @NonNull
    private final DownloadStore store;

    static DownloadChain createChain(int blockIndex, DownloadTask task,
                                     @NonNull BreakpointInfo info,
                                     @NonNull DownloadCache cache,
                                     @NonNull DownloadStore store) {
        return new DownloadChain(blockIndex, task, info, cache, store);
    }

    private DownloadChain(int blockIndex, @NonNull DownloadTask task, @NonNull BreakpointInfo info,
                          @NonNull DownloadCache cache, @NonNull DownloadStore store) {
        this.blockIndex = blockIndex;
        this.task = task;
        this.cache = cache;
        this.info = info;
        this.store = store;
        this.callbackDispatcher = OkDownload.with().callbackDispatcher();
    }

    public long getResponseContentLength() {
        return responseContentLength;
    }

    public void setResponseContentLength(long responseContentLength) {
        this.responseContentLength = responseContentLength;
    }

    public void cancel() {
        if (finished.get() || this.currentThread == null) return;

        currentThread.interrupt();
    }

    @NonNull
    public DownloadTask getTask() {
        return task;
    }

    @NonNull
    public BreakpointInfo getInfo() {
        return this.info;
    }

    public int getBlockIndex() {
        return blockIndex;
    }

    public synchronized void setConnection(@NonNull DownloadConnection connection) {
        this.connection = connection;
    }

    @NonNull
    public DownloadCache getCache() {
        return cache;
    }

    public void setRedirectLocation(String location) {
        this.cache.setRedirectLocation(location);
    }

    public MultiPointOutputStream getOutputStream() {
        return this.cache.getOutputStream();
    }

    @Nullable
    public synchronized DownloadConnection getConnection() {
        return this.connection;
    }

    @NonNull
    public synchronized DownloadConnection getConnectionOrCreate() throws IOException {
        if (cache.isInterrupt()) throw InterruptException.HAHALOLO;

        if (connection == null) {
            final String url;
            final String redirectLocation = cache.getRedirectLocation();
            if (redirectLocation != null) {
                url = redirectLocation;
            } else {
                url = info.getUrl();
            }

            Util.d(TAG, "create connection on url: " + url);

            connection = OkDownload.with().connectionFactory().create(url);
        }
        return connection;
    }

    public void increaseCallbackBytes(long increaseBytes) {
        this.noCallbackIncreaseBytes += increaseBytes;
    }

    public void flushNoCallbackIncreaseBytes() {
        if (noCallbackIncreaseBytes == 0) return;

        callbackDispatcher.dispatch().fetchProgress(task, blockIndex, noCallbackIncreaseBytes);
        noCallbackIncreaseBytes = 0;
    }

    void start() throws IOException {
        final CallbackDispatcher dispatcher = OkDownload.with().callbackDispatcher();
        // connect chain
        final RetryInterceptor retryInterceptor = new RetryInterceptor();
        final BreakpointInterceptor breakpointInterceptor = new BreakpointInterceptor();
        connectInterceptorList.add(retryInterceptor);
        connectInterceptorList.add(breakpointInterceptor);
        connectInterceptorList.add(new HeaderInterceptor());
        connectInterceptorList.add(new CallServerInterceptor());

        connectIndex = 0;
        final DownloadConnection.Connected connected = processConnect();
        if (cache.isInterrupt()) {
            throw InterruptException.HAHALOLO;
        }

        dispatcher.dispatch().fetchStart(task, blockIndex, getResponseContentLength());
        // fetch chain
        final FetchDataInterceptor fetchDataInterceptor =
                new FetchDataInterceptor(blockIndex, connected.getInputStream(),
                        getOutputStream(), task);
        fetchInterceptorList.add(retryInterceptor);
        fetchInterceptorList.add(breakpointInterceptor);
        fetchInterceptorList.add(fetchDataInterceptor);

        fetchIndex = 0;
        final long totalFetchedBytes = processFetch();
        dispatcher.dispatch().fetchEnd(task, blockIndex, totalFetchedBytes);
    }

    public void resetConnectForRetry() {
        connectIndex = 1;
        releaseConnection();
    }

    public synchronized void releaseConnection() {
        if (connection != null) {
            connection.release();
            Util.d(TAG, "release connection " + connection + " task[" + task.getId()
                    + "] block[" + blockIndex + "]");
        }
        connection = null;
    }

    public DownloadConnection.Connected processConnect() throws IOException {
        if (cache.isInterrupt()) throw InterruptException.HAHALOLO;
        return connectInterceptorList.get(connectIndex++).interceptConnect(this);
    }

    public long processFetch() throws IOException {
        if (cache.isInterrupt()) throw InterruptException.HAHALOLO;
        return fetchInterceptorList.get(fetchIndex++).interceptFetch(this);
    }

    public long loopFetch() throws IOException {
        if (fetchIndex == fetchInterceptorList.size()) {
            // last one is fetch data interceptor
            fetchIndex--;
        }
        return processFetch();
    }

    final AtomicBoolean finished = new AtomicBoolean(false);

    boolean isFinished() {
        return finished.get();
    }

    @NonNull
    public DownloadStore getDownloadStore() {
        return store;
    }

    @Override
    public void run() {
        if (isFinished()) {
            throw new IllegalAccessError("The chain has been finished!");
        }
        this.currentThread = Thread.currentThread();

        try {
            start();
        } catch (IOException ignored) {
            // interrupt.
        } finally {
            finished.set(true);
            releaseConnectionAsync();
        }
    }

    void releaseConnectionAsync() {
        EXECUTOR.execute(releaseConnectionRunnable);
    }

    private final Runnable releaseConnectionRunnable = new Runnable() {
        @Override
        public void run() {
            releaseConnection();
        }
    };
}