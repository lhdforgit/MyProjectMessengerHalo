/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader;

import com.halo.filedownloader.event.IDownloadEvent;
import com.halo.filedownloader.event.IDownloadListener;

/**
 * The listener for listening whether the service establishes connection or disconnected.
 */
public abstract class FileDownloadConnectListener extends IDownloadListener {


    public FileDownloadConnectListener() {
    }

    @Override
    public boolean callback(IDownloadEvent event) {
        return false;
    }

    /**
     * connected file download service
     */
    public abstract void connected();

    /**
     * disconnected file download service
     */
    public abstract void disconnected();

}
