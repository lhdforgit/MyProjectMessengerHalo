/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.listener;

import androidx.annotation.NonNull;

import com.halo.okdownload.DownloadListener;
import com.halo.okdownload.DownloadTask;
import com.halo.okdownload.core.breakpoint.BreakpointInfo;
import com.halo.okdownload.core.cause.ResumeFailedCause;

import java.util.List;
import java.util.Map;

/**
 * taskStart->taskEnd
 */
public abstract class DownloadListener2 implements DownloadListener {
    @Override
    public void connectTrialStart(@NonNull DownloadTask task,
                                  @NonNull Map<String, List<String>> requestHeaderFields) {
    }

    @Override
    public void connectTrialEnd(@NonNull DownloadTask task, int responseCode,
                                @NonNull Map<String, List<String>> responseHeaderFields) {
    }

    @Override
    public void downloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info,
                                      @NonNull ResumeFailedCause cause) {
    }

    @Override
    public void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {
    }

    @Override
    public void connectStart(@NonNull DownloadTask task, int blockIndex,
                             @NonNull Map<String, List<String>> requestHeaderFields) {
    }

    @Override
    public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode,
                           @NonNull Map<String, List<String>> responseHeaderFields) {
    }

    @Override
    public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {
    }

    @Override
    public void fetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes) {
    }

    @Override
    public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {
    }
}
