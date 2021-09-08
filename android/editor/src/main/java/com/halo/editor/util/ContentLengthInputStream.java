/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Create by ndn
 * Create on 4/9/20
 * com.halo.editor.util
 */
public class ContentLengthInputStream extends FilterInputStream {

    private long bytesRemaining;

    public ContentLengthInputStream(InputStream inputStream, long contentLength) {
        super(inputStream);
        this.bytesRemaining = contentLength;
    }

    @Override
    public int read() throws IOException {
        if (bytesRemaining == 0) return -1;
        int result = super.read();
        bytesRemaining--;

        return result;
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        if (bytesRemaining == 0) return -1;

        int result = super.read(buffer, offset, Math.min(length, Util.toIntExact(bytesRemaining)));

        bytesRemaining -= result;
        return result;
    }

}
