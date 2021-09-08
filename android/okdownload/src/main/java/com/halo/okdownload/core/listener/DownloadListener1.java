/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.listener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.halo.okdownload.DownloadListener;
import com.halo.okdownload.DownloadTask;
import com.halo.okdownload.core.breakpoint.BreakpointInfo;
import com.halo.okdownload.core.cause.EndCause;
import com.halo.okdownload.core.cause.ResumeFailedCause;
import com.halo.okdownload.core.listener.assist.Listener1Assist;
import com.halo.okdownload.core.listener.assist.ListenerAssist;

import java.util.List;
import java.util.Map;

/**
 * taskStart->(retry)->connect->progress<-->progress(currentOffset)->taskEnd
 */
public abstract class DownloadListener1 implements DownloadListener,
        Listener1Assist.Listener1Callback, ListenerAssist {
    final Listener1Assist assist;

    DownloadListener1(Listener1Assist assist) {
        this.assist = assist;
        assist.setCallback(this);
    }

    public DownloadListener1() {
        this(new Listener1Assist());
    }

    @Override
    public boolean isAlwaysRecoverAssistModel() {
        return assist.isAlwaysRecoverAssistModel();
    }

    @Override
    public void setAlwaysRecoverAssistModel(boolean isAlwaysRecoverAssistModel) {
        assist.setAlwaysRecoverAssistModel(isAlwaysRecoverAssistModel);
    }

    @Override
    public void setAlwaysRecoverAssistModelIfNotSet(boolean isAlwaysRecoverAssistModel) {
        assist.setAlwaysRecoverAssistModelIfNotSet(isAlwaysRecoverAssistModel);
    }

    @Override
    public final void taskStart(@NonNull DownloadTask task) {
        assist.taskStart(task);
    }

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
        assist.downloadFromBeginning(task, info, cause);
    }

    @Override
    public void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {
        assist.downloadFromBreakpoint(task, info);
    }

    @Override
    public void connectStart(@NonNull DownloadTask task, int blockIndex,
                             @NonNull Map<String, List<String>> requestHeaderFields) {
    }

    @Override
    public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode,
                           @NonNull Map<String, List<String>> responseHeaderFields) {
        assist.connectEnd(task);
    }

    @Override
    public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {
    }

    @Override
    public void fetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes) {
        assist.fetchProgress(task, increaseBytes);
    }

    @Override
    public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {
    }

    @Override
    public final void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause,
                              @Nullable Exception realCause) {
        assist.taskEnd(task, cause, realCause);
    }
}

