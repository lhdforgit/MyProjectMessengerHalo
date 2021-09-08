/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.halo.okdownload.DownloadListener;
import com.halo.okdownload.DownloadTask;
import com.halo.okdownload.core.breakpoint.BreakpointInfo;
import com.halo.okdownload.core.cause.EndCause;
import com.halo.okdownload.core.cause.ResumeFailedCause;

import java.util.List;
import java.util.Map;

public class CompatListenerAdapter implements DownloadListener {

    @NonNull
    private final CompatListenerAssist listenerAssist;

    public CompatListenerAdapter(@NonNull CompatListenerAssist listenerAssist) {
        this.listenerAssist = listenerAssist;
    }

    @NonNull
    public CompatListenerAssist getListenerAssist() {
        return listenerAssist;
    }

    @Override
    public void taskStart(@NonNull DownloadTask task) {
        listenerAssist.taskStart(task);
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
    public void downloadFromBeginning(@NonNull DownloadTask task,
                                      @NonNull BreakpointInfo info,
                                      @NonNull ResumeFailedCause cause) {
        listenerAssist.setResumable(false);
    }

    @Override
    public void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {
        listenerAssist.setResumable(true);
        listenerAssist.setEtag(info.getEtag());
    }

    @Override
    public void connectStart(@NonNull DownloadTask task, int blockIndex,
                             @NonNull Map<String, List<String>> requestHeaderFields) {
        listenerAssist.connectStart(task);
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
        listenerAssist.fetchProgress(task, increaseBytes);
    }

    @Override
    public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {

    }

    @Override
    public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause,
                        @Nullable Exception realCause) {
        listenerAssist.taskEnd(task, cause, realCause);
    }

    public static CompatListenerAdapter create(@NonNull FileDownloadListener fileDownloadListener) {
        final CompatListenerAssist.CompatListenerAssistCallback callback =
                new CompatListenerAdaptee(fileDownloadListener);
        final CompatListenerAssist compatListenerAssist = new CompatListenerAssist(callback);
        return new CompatListenerAdapter(compatListenerAssist);
    }
}