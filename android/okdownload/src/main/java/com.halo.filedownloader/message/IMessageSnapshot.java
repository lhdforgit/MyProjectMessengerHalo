/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader.message;

/**
 * The snapshot interface.
 */
interface IMessageSnapshot {
    /**
     * @return The download identify.
     */
    int getId();

    /**
     * @return The current downloading status.
     * @see com.halo.filedownloader.model.FileDownloadStatus
     */
    byte getStatus();

    /**
     * @return The error cause.
     */
    Throwable getThrowable();

    /**
     * @return The currently retrying times.
     */
    int getRetryingTimes();

    /**
     * @return {@code true} if the downloading is resuming from the breakpoint, otherwise the
     * downloading is from the beginning.
     */
    boolean isResuming();

    /**
     * @return the Etag from the response's header.
     */
    String getEtag();

    /**
     * This method will be used when the downloading file is a large file.
     *
     * @return The so far downloaded bytes.
     * @see #isLargeFile()
     */
    long getLargeSofarBytes();

    /**
     * This method will be used when the downloading file is a large file.
     *
     * @return The total bytes of the downloading file.
     * @see #isLargeFile()
     */
    long getLargeTotalBytes();

    /**
     * This method will be used when the downloading file isn't a large file.
     *
     * @return The so far downloaded bytes.
     * @see #isLargeFile()
     */
    int getSmallSofarBytes();

    /**
     * This method will be used when the downloading file isn't a large file.
     *
     * @return The total bytes of the downloading file.
     * @see #isLargeFile()
     */
    int getSmallTotalBytes();

    /**
     * @return {@code true} if the task isn't real started, and we find the target file is already
     * exist, so the task will receive the completed callback directly, {@code false} otherwise.
     */
    boolean isReusedDownloadedFile();

    /**
     * @return {@code true} if the length of the file is more than 1.99G, {@code false} otherwise.
     */
    boolean isLargeFile();

    /**
     * @return The filename.
     */
    String getFileName();
}