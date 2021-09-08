/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.interceptor;

import androidx.annotation.NonNull;

import com.halo.okdownload.core.connection.DownloadConnection;
import com.halo.okdownload.core.download.DownloadCache;
import com.halo.okdownload.core.download.DownloadChain;
import com.halo.okdownload.core.exception.InterruptException;
import com.halo.okdownload.core.exception.RetryException;

import java.io.IOException;

public class RetryInterceptor implements Interceptor.Connect, Interceptor.Fetch {

    @NonNull
    @Override
    public DownloadConnection.Connected interceptConnect(DownloadChain chain) throws IOException {
        final DownloadCache cache = chain.getCache();

        while (true) {
            try {
                if (cache.isInterrupt()) {
                    throw InterruptException.HAHALOLO;
                }
                return chain.processConnect();
            } catch (IOException e) {
                if (e instanceof RetryException) {
                    chain.resetConnectForRetry();
                    continue;
                }

                chain.getCache().catchException(e);
                chain.getOutputStream().catchBlockConnectException(chain.getBlockIndex());
                throw e;
            }
        }
    }

    @Override
    public long interceptFetch(DownloadChain chain) throws IOException {
        try {
            return chain.processFetch();
        } catch (IOException e) {
            chain.getCache().catchException(e);
            throw e;
        }
    }
}
