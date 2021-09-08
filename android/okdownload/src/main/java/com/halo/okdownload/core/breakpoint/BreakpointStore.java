/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.breakpoint;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.halo.okdownload.DownloadTask;

import java.io.IOException;

public interface BreakpointStore {

    @Nullable
    BreakpointInfo get(int id);

    @NonNull
    BreakpointInfo createAndInsert(@NonNull DownloadTask task) throws IOException;

    int findOrCreateId(@NonNull DownloadTask task);

    boolean update(@NonNull BreakpointInfo breakpointInfo) throws IOException;

    void remove(int id);

    @Nullable
    String getResponseFilename(String url);

    @Nullable
    BreakpointInfo findAnotherInfoFromCompare(@NonNull DownloadTask task,
                                              @NonNull BreakpointInfo ignored);

    /**
     * Whether only store breakpoint on memory cache.
     *
     * @return {@code true} if breakpoint on this store is only store on the memory cache.
     */
    boolean isOnlyMemoryCache();

    /**
     * Whether the file relate to the task id {@code id} is dirty, which means the file isn't
     * complete download yet.
     *
     * @param id the task id.
     * @return {@code true} the file relate to {@code id} is dirty
     */
    boolean isFileDirty(int id);
}
