/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.glide;

import java.util.List;

/**
 * Create by ndn
 * Create on 4/9/20
 * com.halo.editor.util
 */
public class InvalidMessageException extends Exception {

    public InvalidMessageException() {}

    public InvalidMessageException(String detailMessage) {
        super(detailMessage);
    }

    public InvalidMessageException(Throwable throwable) {
        super(throwable);
    }

    public InvalidMessageException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InvalidMessageException(String detailMessage, List<Exception> exceptions) {
        super(detailMessage, exceptions.get(0));
    }
}
