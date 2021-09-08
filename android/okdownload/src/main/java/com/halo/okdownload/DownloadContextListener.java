/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.halo.okdownload.core.cause.EndCause;

public interface DownloadContextListener {
    void taskEnd(@NonNull DownloadContext context, @NonNull DownloadTask task,
                 @NonNull EndCause cause, @Nullable Exception realCause, int remainCount);

    void queueEnd(@NonNull DownloadContext context);
}
