/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.util;

/**
 * Create by ndn
 * Create on 4/9/20
 * com.halo.editor.util
 */
public class InvalidMacException extends Exception {

    public InvalidMacException(String detailMessage) {
        super(detailMessage);
    }

    public InvalidMacException(Throwable throwable) {
        super(throwable);
    }
}
