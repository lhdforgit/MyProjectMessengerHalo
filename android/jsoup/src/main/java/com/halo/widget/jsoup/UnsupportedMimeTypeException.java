/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.jsoup;

import java.io.IOException;

/**
 * Hahalolo that a HTTP response returned a mime type that is not supported.
 */
public class UnsupportedMimeTypeException extends IOException {
    private String mimeType;
    private String url;

    public UnsupportedMimeTypeException(String message, String mimeType, String url) {
        super(message);
        this.mimeType = mimeType;
        this.url = url;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return super.toString() + ". Mimetype=" + mimeType + ", URL="+url;
    }
}
