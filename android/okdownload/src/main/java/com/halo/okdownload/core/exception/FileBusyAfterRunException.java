/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.exception;

import java.io.IOException;

public class FileBusyAfterRunException extends IOException {
    private FileBusyAfterRunException() {
        super("File busy after run");
    }

    public static final FileBusyAfterRunException HAHALOLO = new FileBusyAfterRunException() {
    };
}
