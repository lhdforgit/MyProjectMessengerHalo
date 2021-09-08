/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.interceptor;

import androidx.annotation.NonNull;

import com.halo.okdownload.core.connection.DownloadConnection;
import com.halo.okdownload.core.download.DownloadChain;

import java.io.IOException;

public interface Interceptor {
    interface Connect {
        @NonNull
        DownloadConnection.Connected interceptConnect(DownloadChain chain)
                throws IOException;
    }

    interface Fetch {
        long interceptFetch(DownloadChain chain) throws IOException;
    }
}
