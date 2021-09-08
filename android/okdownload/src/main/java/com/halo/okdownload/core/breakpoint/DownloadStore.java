/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.breakpoint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.halo.okdownload.core.cause.EndCause;

import java.io.IOException;

public interface DownloadStore extends BreakpointStore {

    void onSyncToFilesystemSuccess(@NonNull BreakpointInfo info, int blockIndex,
                                   long increaseLength) throws IOException;

    void onTaskStart(int id);

    void onTaskEnd(int id, @NonNull EndCause cause, @Nullable Exception exception);

    /**
     * Get the breakpoint info after the {@code id} task has been completed, this function is just
     * for ignore real operation and just return {@code null} directly since on some logic model we
     * already delete info from store after task has been completed to enhance performance.
     */
    @Nullable
    BreakpointInfo getAfterCompleted(int id);

    /**
     * Mark the file relate to the {@code id} is dirty state.
     *
     * @param id the task id
     */
    boolean markFileDirty(int id);

    /**
     * Mark the file relate to the {@code id} is clear state.
     * <p>
     * Normally, which means the task is completed download.
     *
     * @param id the task id
     */
    boolean markFileClear(int id);
}
