/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader.exception;

/**
 * Throwing this exception, when there are some security issues found on FileDownloader.
 */
public class FileDownloadSecurityException extends Exception {
    public FileDownloadSecurityException(String msg) {
        super(msg);
    }
}
