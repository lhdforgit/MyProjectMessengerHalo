/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader;

/**
 * The listener for listening the downloading status changing.
 * <p>
 * This listener will be used when the file size of the task is greater than 1.99G.
 */
@SuppressWarnings({"WeakerAccess", "UnusedParameters"})
public abstract class FileDownloadLargeFileListener extends FileDownloadListener {

    public FileDownloadLargeFileListener() {
    }

    /**
     * @see #FileDownloadLargeFileListener()
     * @deprecated not handle priority any more
     */
    public FileDownloadLargeFileListener(int priority) {
        //noinspection deprecation
        super(priority);
    }

    /**
     * Entry queue, and pending
     *
     * @param task       Current task
     * @param soFarBytes Already downloaded bytes stored in the db
     * @param totalBytes Total bytes stored in the db
     */
    protected abstract void pending(BaseDownloadTask task, long soFarBytes,
                                    long totalBytes);

    /**
     * @param task       The task
     * @param soFarBytes Already downloaded bytes stored in the db
     * @param totalBytes Total bytes stored in the db
     * @deprecated replaced with {@link #pending(BaseDownloadTask, long, long)}
     */
    @Override
    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    }

    /**
     * Connected
     *
     * @param task       Current task
     * @param etag       ETag
     * @param isContinue Is resume from breakpoint
     * @param soFarBytes Number of bytes download so far
     * @param totalBytes Total size of the download in bytes
     */
    @SuppressWarnings("EmptyMethod")
    protected void connected(BaseDownloadTask task, String etag,
                             boolean isContinue,
                             long soFarBytes, long totalBytes) {
    }

    /**
     * @param task       The task
     * @param etag       ETag
     * @param isContinue Is resume from breakpoint
     * @param soFarBytes Number of bytes download so far
     * @param totalBytes Total size of the download in bytes
     * @deprecated replaced with {@link #connected(BaseDownloadTask, String, boolean, long, long)}
     */
    @Override
    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes,
                             int totalBytes) {
    }

    /**
     * @param task       Current task
     * @param soFarBytes Number of bytes download so far
     * @param totalBytes Total size of the download in bytes
     */
    protected abstract void progress(BaseDownloadTask task, long soFarBytes,
                                     long totalBytes);

    /**
     * @param task       The task
     * @param soFarBytes Number of bytes download so far
     * @param totalBytes Total size of the download in bytes
     * @deprecated replaced with {@link #progress(BaseDownloadTask, long, long)}
     */
    @Override
    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    }

    /**
     * Start Retry
     *
     * @param task          Current task
     * @param ex            why retry
     * @param retryingTimes How many times will retry
     * @param soFarBytes    Number of bytes download so far
     */
    @SuppressWarnings("EmptyMethod")
    protected void retry(BaseDownloadTask task, Throwable ex,
                         int retryingTimes, long soFarBytes) {
    }

    /**
     * @param task          The task
     * @param ex            Why retry
     * @param retryingTimes How many times will retry
     * @param soFarBytes    Number of bytes download so far
     * @deprecated replaced with {@link #retry(BaseDownloadTask, Throwable, int, long)}
     */
    @SuppressWarnings("EmptyMethod")
    @Override
    protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
    }

    /**
     * Download paused
     *
     * @param task       Current task
     * @param soFarBytes Number of bytes download so far
     * @param totalBytes Total size of the download in bytes
     */
    protected abstract void paused(BaseDownloadTask task, long soFarBytes,
                                   long totalBytes);

    /**
     * @param task       The task
     * @param soFarBytes Number of bytes download so far
     * @param totalBytes Total size of the download in bytes
     * @deprecated replaced with {@link #paused(BaseDownloadTask, long, long)}
     */
    @Override
    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    }
}
