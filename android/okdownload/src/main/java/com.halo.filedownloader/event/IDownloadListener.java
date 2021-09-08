/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader.event;

/**
 * The listener is used to listen the publish event from Event Pool.
 *
 * @see IDownloadEvent
 */
public abstract class IDownloadListener {

    public abstract boolean callback(IDownloadEvent event);

}
