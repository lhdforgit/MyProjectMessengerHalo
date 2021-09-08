/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.halo.okdownload.DownloadListener;
import com.halo.okdownload.DownloadTask;
import com.halo.okdownload.core.breakpoint.BreakpointInfo;
import com.halo.okdownload.core.cause.EndCause;
import com.halo.okdownload.core.cause.ResumeFailedCause;

import java.util.List;
import java.util.Map;
/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/6/2018.
 */
public interface CustomDownloadListener extends DownloadListener {

    @Override
    void taskStart(@NonNull DownloadTask task);

    @Override
    default void connectTrialStart(@NonNull DownloadTask task, @NonNull Map<String, List<String>> requestHeaderFields) {
    }

    @Override
    default void connectTrialEnd(@NonNull DownloadTask task, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
    }

    @Override
    default void downloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info, @NonNull ResumeFailedCause cause) {
    }

    @Override
    default void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {
    }

    @Override
    default void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {
    }

    @Override
    default void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
    }

    @Override
    default void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {
    }

    @Override
    void fetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes);

    @Override
    default void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {
    }

    @Override
    void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause);
}
