/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.interceptor;

import androidx.annotation.NonNull;

import com.halo.okdownload.DownloadTask;
import com.halo.okdownload.OkDownload;
import com.halo.okdownload.core.dispatcher.CallbackDispatcher;
import com.halo.okdownload.core.download.DownloadChain;
import com.halo.okdownload.core.exception.InterruptException;
import com.halo.okdownload.core.file.MultiPointOutputStream;

import java.io.IOException;
import java.io.InputStream;

public class FetchDataInterceptor implements Interceptor.Fetch {

    private final InputStream inputStream;

    private final byte[] readBuffer;
    private final MultiPointOutputStream outputStream;
    private final int blockIndex;
    private final DownloadTask task;
    private final CallbackDispatcher dispatcher;

    public FetchDataInterceptor(int blockIndex,
                                @NonNull InputStream inputStream,
                                @NonNull MultiPointOutputStream outputStream,
                                DownloadTask task) {
        this.blockIndex = blockIndex;
        this.inputStream = inputStream;
        this.readBuffer = new byte[task.getReadBufferSize()];
        this.outputStream = outputStream;

        this.task = task;
        this.dispatcher = OkDownload.with().callbackDispatcher();
    }

    @Override
    public long interceptFetch(DownloadChain chain) throws IOException {
        if (chain.getCache().isInterrupt()) {
            throw InterruptException.HAHALOLO;
        }

        OkDownload.with().downloadStrategy().inspectNetworkOnWifi(chain.getTask());
        // fetch
        int fetchLength = inputStream.read(readBuffer);
        if (fetchLength == -1) {
            return fetchLength;
        }

        // write to file
        outputStream.write(blockIndex, readBuffer, fetchLength);

        chain.increaseCallbackBytes(fetchLength);
        if (this.dispatcher.isFetchProcessMoment(task)) {
            chain.flushNoCallbackIncreaseBytes();
        }

        return fetchLength;
    }
}
