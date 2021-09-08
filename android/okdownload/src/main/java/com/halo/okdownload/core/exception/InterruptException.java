/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.exception;

import java.io.IOException;

public class InterruptException extends IOException {
    private InterruptException() {
        super("Interrupted");
    }

    public static final InterruptException HAHALOLO = new InterruptException() {

        @Override
        public void printStackTrace() {
            throw new IllegalAccessError("Stack is ignored for signal");
        }
    };
}
