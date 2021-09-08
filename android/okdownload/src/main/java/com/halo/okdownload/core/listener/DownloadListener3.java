/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.listener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.halo.okdownload.DownloadTask;
import com.halo.okdownload.core.Util;
import com.halo.okdownload.core.cause.EndCause;
import com.halo.okdownload.core.listener.assist.Listener1Assist;

/**
 * started->connected->progress<-->progress(currentOffset)-> completed/canceled/error/warn
 */
public abstract class DownloadListener3 extends DownloadListener1 {
    @Override
    public final void taskStart(@NonNull DownloadTask task,
                                @NonNull Listener1Assist.Listener1Model model) {
        started(task);
    }

    @Override
    public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause,
                        @Nullable Exception realCause,
                        @NonNull Listener1Assist.Listener1Model model) {
        switch (cause) {
            case COMPLETED:
                completed(task);
                break;
            case CANCELED:
                canceled(task);
                break;
            case ERROR:
            case PRE_ALLOCATE_FAILED:
                error(task, realCause);
                break;
            case FILE_BUSY:
            case SAME_TASK_BUSY:
                warn(task);
                break;
            default:
                Util.w("DownloadListener3", "Don't support " + cause);
        }
    }

    protected abstract void started(@NonNull DownloadTask task);

    protected abstract void completed(@NonNull DownloadTask task);

    protected abstract void canceled(@NonNull DownloadTask task);

    protected abstract void error(@NonNull DownloadTask task, @NonNull Exception e);

    protected abstract void warn(@NonNull DownloadTask task);
}
