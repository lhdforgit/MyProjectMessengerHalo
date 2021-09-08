/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.file;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface DownloadOutputStream {
    void write(byte[] b, int off, int len) throws IOException;

    void close() throws IOException;

    void flushAndSync() throws IOException;

    void seek(long offset) throws IOException;

    void setLength(long newLength) throws IOException;

    interface Factory {
        DownloadOutputStream create(Context context, File file, int flushBufferSize)
                throws FileNotFoundException;

        DownloadOutputStream create(Context context, Uri uri, int flushBufferSize)
                throws FileNotFoundException;

        boolean supportSeek();
    }
}
