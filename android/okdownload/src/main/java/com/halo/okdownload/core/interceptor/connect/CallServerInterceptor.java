/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.interceptor.connect;

import androidx.annotation.NonNull;

import com.halo.okdownload.OkDownload;
import com.halo.okdownload.core.connection.DownloadConnection;
import com.halo.okdownload.core.download.DownloadChain;
import com.halo.okdownload.core.interceptor.Interceptor;

import java.io.IOException;

public class CallServerInterceptor implements Interceptor.Connect {
    @NonNull
    @Override
    public DownloadConnection.Connected interceptConnect(DownloadChain chain) throws IOException {
        OkDownload.with().downloadStrategy().inspectNetworkOnWifi(chain.getTask());
        OkDownload.with().downloadStrategy().inspectNetworkAvailable();

        return chain.getConnectionOrCreate().execute();
    }
}
