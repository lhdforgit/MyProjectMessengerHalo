/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader.event;


/**
 * An atom event.
 */
@SuppressWarnings({"WeakerAccess", "CanBeFinal"})
public abstract class IDownloadEvent {
    public Runnable callback = null;

    public IDownloadEvent(final String id) {
        this.id = id;
    }

    /**
     * @see #IDownloadEvent(String)
     * @deprecated do not handle ORDER any more.
     */
    public IDownloadEvent(final String id, boolean order) {
        this.id = id;
    }

    @SuppressWarnings("WeakerAccess")
    protected final String id;

    public final String getId() {
        return this.id;
    }
}
