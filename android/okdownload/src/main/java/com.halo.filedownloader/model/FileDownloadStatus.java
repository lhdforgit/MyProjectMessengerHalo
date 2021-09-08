/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader.model;


@SuppressWarnings({"checkstyle:linelength", "checkstyle:constantname"})
/**
 * The downloading status.
 *
 * @see com.halo.filedownloader.IFileDownloadMessenger
 */
public class FileDownloadStatus {
    // [-2^7, 2^7 -1]
    // by very beginning
    /**
     * When the task on {@code toLaunchPool} status, it means that the task is just into the
     * LaunchPool and is scheduled for launch.
     * <p>
     * The task is scheduled for launch and it isn't on the FileDownloadService yet.
     */
    public static final byte toLaunchPool = 10;
    /**
     * When the task on {@code toFileDownloadService} status, it means that the task is just post to
     * the FileDownloadService.
     * <p>
     * The task is posting to the FileDownloadService and after this status, this task can start.
     */
    public static final byte toFileDownloadService = 11;

    // by FileDownloadService
    /**
     * When the task on {@code pending} status, it means that the task is in the list on the
     * FileDownloadService and just waiting for start.
     * <p>
     * The task is waiting on the FileDownloadService.
     * <p>
     * The count of downloading simultaneously, you can configure in filedownloader.properties.
     */
    public static final byte pending = 1;
    /**
     * When the task on {@code started} status, it means that the network access thread of
     * downloading this task is started.
     * <p>
     * The task is downloading on the FileDownloadService.
     */
    public static final byte started = 6;
    /**
     * When the task on {@code connected} status, it means that the task is successfully connected
     * to the back-end.
     * <p>
     * The task is downloading on the FileDownloadService.
     */
    public static final byte connected = 2;
    /**
     * When the task on {@code progress} status, it means that the task is fetching data from the
     * back-end.
     * <p>
     * The task is downloading on the FileDownloadService.
     */
    public static final byte progress = 3;
    /**
     * When the task on {@code blockComplete} status, it means that the task has been completed
     * downloading successfully.
     * <p>
     * The task is completed downloading successfully and the action-flow is blocked for doing
     * something before callback completed method.
     */
    public static final byte blockComplete = 4;
    /**
     * When the task on {@code retry} status, it means that the task must occur some error, but
     * there is a valid chance to retry, so the task is retry to download again.
     * <p>
     * The task is restarting on the FileDownloadService.
     */
    public static final byte retry = 5;

    /**
     * When the task on {@code error} status, it means that the task must occur some error and there
     * isn't any valid chance to retry, so the task is finished with error.
     * <p>
     * The task is finished with an error.
     */
    public static final byte error = -1;
    /**
     * When the task on {@code paused} status, it means that the task is paused manually.
     * <p>
     * The task is finished with the pause action.
     */
    public static final byte paused = -2;
    /**
     * When the task on {@code completed} status, it means that the task is completed downloading
     * successfully.
     * <p>
     * The task is finished with completed downloading successfully.
     */
    public static final byte completed = -3;
    /**
     * When the task on {@code warn} status, it means that there is another same task(same url,
     * same path to store content) is running.
     * <p>
     * The task is finished with the warn status.
     */
    public static final byte warn = -4;

    /**
     * When the task on {@code INVALID_STATUS} status, it means that the task is IDLE.
     * <p>
     * The task is clear and it isn't launched.
     */
    public static final byte INVALID_STATUS = 0;

    public static boolean isOver(final int status) {
        return status < 0;
    }

    public static boolean isIng(final int status) {
        return status >= pending && status <= started;
    }

}
