/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.jsoup;

import java.io.IOException;

public class UncheckedIOException extends RuntimeException {
    public UncheckedIOException(IOException cause) {
        super(cause);
    }

    public IOException ioException() {
        return (IOException) getCause();
    }
}
