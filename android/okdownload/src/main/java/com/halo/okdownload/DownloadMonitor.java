/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.halo.okdownload.core.breakpoint.BreakpointInfo;
import com.halo.okdownload.core.cause.EndCause;
import com.halo.okdownload.core.cause.ResumeFailedCause;

/**
 * If you set monitor to okdownload, the lifecycle of every task on okdownload will be caught and
 * callback to this monitor.
 *
 * @see OkDownload#setMonitor(DownloadMonitor)
 * @see OkDownload.Builder#setMonitor(DownloadMonitor)
 */
public interface DownloadMonitor {
    void taskStart(DownloadTask task);

    /**
     * Call this monitor function when the {@code task} just end trial connection, and its
     * {@code info} is ready and also certain this task will resume from the past breakpoint.
     *
     * @param task the target task.
     * @param info has certainly total-length and offset-length now.
     */
    void taskDownloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info);

    /**
     * Call this monitor function when the {@code task} just end trial connection, and its
     * {@code info} is ready and also certain this task will download from the very beginning.
     *
     * @param task  the target task.
     * @param info  has certainly total-length and offset-length now.
     * @param cause the cause of why download from the very beginning instead of from the past
     *              breakpoint.
     */
    void taskDownloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info,
                                   @Nullable ResumeFailedCause cause);

    void taskEnd(DownloadTask task, EndCause cause, @Nullable Exception realCause);
}
