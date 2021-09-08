/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader.progress;

import com.halo.filedownloader.CompatListenerAssist;
import com.halo.filedownloader.DownloadTaskAdapter;
import com.halo.okdownload.core.Util;

import java.util.concurrent.atomic.AtomicLong;

public class ProgressAssist {

    static final int CALLBACK_SAFE_MIN_INTERVAL_BYTES = 1;
    static final int NO_ANY_PROGRESS_CALLBACK = -1;
    private static final long TOTAL_VALUE_IN_CHUNKED_RESOURCE = -1;
    private static final String TAG = "ProgressAssist";

    private final int maxProgressCount;
    final AtomicLong sofarBytes;
    final AtomicLong incrementBytes;

    long callbackMinIntervalBytes = CALLBACK_SAFE_MIN_INTERVAL_BYTES;

    public ProgressAssist(int maxProgressCount) {
        this.maxProgressCount = maxProgressCount;
        sofarBytes = new AtomicLong(0);
        incrementBytes = new AtomicLong(0);
    }

    public void calculateCallbackMinIntervalBytes(final long contentLength) {
        if (maxProgressCount <= 0) {
            callbackMinIntervalBytes = NO_ANY_PROGRESS_CALLBACK;
        } else if (contentLength == TOTAL_VALUE_IN_CHUNKED_RESOURCE) {
            callbackMinIntervalBytes = CALLBACK_SAFE_MIN_INTERVAL_BYTES;
        } else {
            final long minIntervalBytes = contentLength / maxProgressCount;
            callbackMinIntervalBytes = minIntervalBytes <= 0 ? CALLBACK_SAFE_MIN_INTERVAL_BYTES
                    : minIntervalBytes;
        }
        Util.d(TAG, "contentLength: " + contentLength + " callbackMinIntervalBytes: "
                + callbackMinIntervalBytes);
    }


    public void onProgress(DownloadTaskAdapter downloadTaskAdapter, long increaseBytes,
                           CompatListenerAssist.CompatListenerAssistCallback callback) {
        final long sofar = sofarBytes.addAndGet(increaseBytes);
        if (canCallbackProgress(increaseBytes)) {
            callback.progress(downloadTaskAdapter,
                    sofar, downloadTaskAdapter.getTotalBytesInLong());
        }
    }

    boolean canCallbackProgress(long increaseBytes) {
        if (callbackMinIntervalBytes == NO_ANY_PROGRESS_CALLBACK) return false;
        final long increment = incrementBytes.addAndGet(increaseBytes);
        if (increment >= callbackMinIntervalBytes) {
            incrementBytes.addAndGet(-callbackMinIntervalBytes);
            return true;
        }
        return false;
    }

    public long getSofarBytes() {
        return sofarBytes.get();
    }

    public void clearProgress() {
        Util.d(TAG, "clear progress, sofar: " + sofarBytes.get()
                + " increment: " + incrementBytes.get());
        sofarBytes.set(0);
        incrementBytes.set(0);
    }

    public void initSofarBytes(long soFarBytes) {
        Util.d(TAG, "init sofar: " + soFarBytes);
        sofarBytes.set(soFarBytes);
    }
}
