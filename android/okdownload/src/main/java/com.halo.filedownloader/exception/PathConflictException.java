/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader.exception;


import java.util.Locale;

/**
 * Throw this exception, when there is an another running task with the same path with the current
 * task, so if the current task is started, the path of the file is sure to be written by multiple
 * tasks, it is wrong, then we through this exception to avoid such conflict.
 */

public class PathConflictException extends IllegalAccessException {

    private final String mDownloadingConflictPath;
    private final String mTargetFilePath;
    private final int mAnotherSamePathTaskId;

    public PathConflictException(final int anotherSamePathTaskId, final String conflictPath,
                                 final String targetFilePath) {
        super(String.format(Locale.ENGLISH, "There is an another running task(%d) with the"
                        + " same downloading path(%s), because of they are with the same "
                        + "target-file-path(%s), so if the current task is started, the path of the"
                        + " file is sure to be written by multiple tasks, it is wrong, then you "
                        + "receive this exception to avoid such conflict.",
                anotherSamePathTaskId, conflictPath, targetFilePath));

        mAnotherSamePathTaskId = anotherSamePathTaskId;
        mDownloadingConflictPath = conflictPath;
        mTargetFilePath = targetFilePath;
    }

    /**
     * Get the conflict downloading file path, normally, this path is used for store the downloading
     * file relate with the {@link #mTargetFilePath}
     *
     * @return the conflict downloading file path.
     */
    public String getDownloadingConflictPath() {
        return mDownloadingConflictPath;
    }

    /**
     * Get the target file path, which downloading file path is conflict when downloading the task.
     *
     * @return the target file path, which downloading file path is conflict when downloading the
     * task.
     */
    public String getTargetFilePath() {
        return mTargetFilePath;
    }

    /**
     * Get the identify of another task which has the same path with the current task and its target
     * file path is the same to the current task too.
     *
     * @return the identify of another task which has the same path with the current task and its
     * target file path is the same to the current task too.
     */
    public int getAnotherSamePathTaskId() {
        return mAnotherSamePathTaskId;
    }
}
