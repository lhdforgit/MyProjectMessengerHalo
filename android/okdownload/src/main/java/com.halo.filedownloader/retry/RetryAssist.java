/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader.retry;

import androidx.annotation.NonNull;

import com.halo.okdownload.DownloadTask;

import java.util.concurrent.atomic.AtomicInteger;

public class RetryAssist {

    @NonNull
    final AtomicInteger retriedTimes;
    final int retryTimes;

    public RetryAssist(int retryTimes) {
        this.retryTimes = retryTimes;
        this.retriedTimes = new AtomicInteger(0);
    }

    public void doRetry(@NonNull DownloadTask task) {
        final int retryingTime = retriedTimes.incrementAndGet();
        if (retryingTime > retryTimes) {
            throw new RuntimeException("retry has exceeded limit");
        }
        task.enqueue(task.getListener());
    }

    public boolean canRetry() {
        return retriedTimes.get() < retryTimes;
    }

    public int getRetriedTimes() {
        return retriedTimes.get();
    }
}
