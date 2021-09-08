/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader;

import com.halo.filedownloader.message.MessageSnapshot;

/**
 * @see com.halo.filedownloader.model.FileDownloadStatus
 */
interface IFileDownloadMessenger {

    /**
     * The task is just received to handle.
     * <p/>
     * FileDownloader accept the task.
     *
     * @return Whether allow it to begin.
     */
    boolean notifyBegin();

    /**
     * The task is pending.
     * <p/>
     * enqueue, and pending, waiting.
     */
    void notifyPending(MessageSnapshot snapshot);

    /**
     * The download runnable of the task has started running.
     * <p/>
     * Finish pending, and start download runnable.
     */
    void notifyStarted(MessageSnapshot snapshot);

    /**
     * The task is running.
     * <p/>
     * Already connected to the server, and received the Http-response.
     */
    void notifyConnected(MessageSnapshot snapshot);

    /**
     * The task is running.
     * <p/>
     * Fetching datum, and write to local disk.
     */
    void notifyProgress(MessageSnapshot snapshot);

    /**
     * The task is running.
     * <p/>
     * Already completed download, and block the current thread to do something, such as unzip,etc.
     */
    void notifyBlockComplete(MessageSnapshot snapshot);

    /**
     * The task over.
     * <p/>
     * Occur a exception when downloading, but has retry
     * chance {@link BaseDownloadTask#setAutoRetryTimes(int)}, so retry(re-connect,re-download).
     */
    void notifyRetry(MessageSnapshot snapshot);

    /**
     * The task over.
     * <p/>
     * There has already had some same Tasks(Same-URL & Same-SavePath) in Pending-Queue or is
     * running.
     */
    void notifyWarn(MessageSnapshot snapshot);

    /**
     * The task is over.
     * <p/>
     * Occur a exception, but don't has any chance to retry.
     *
     * @see com.halo.filedownloader.exception.FileDownloadHttpException
     * @see com.halo.filedownloader.exception.FileDownloadOutOfSpaceException
     * @see com.halo.filedownloader.exception.FileDownloadGiveUpRetryException
     */
    void notifyError(MessageSnapshot snapshot);

    /**
     * The task is over.
     * <p/>
     * Pause manually by {@link BaseDownloadTask#pause()}.
     *
     * @see BaseDownloadTask#pause()
     */
    void notifyPaused(MessageSnapshot snapshot);

    /**
     * The task is over.
     * <p/>
     * Achieve complete ceremony.
     */
    void notifyCompleted(MessageSnapshot snapshot);

    /**
     * Handover a message to {@link FileDownloadListener}.
     */
    void handoverMessage();

    /**
     * @return {@code true} if handover a message to {@link FileDownloadListener} directly(do not
     * need to post the callback to the main thread).
     * @see BaseDownloadTask#isSyncCallback()
     */
    boolean handoverDirectly();

    /**
     * @param task Re-appointment for this task, when this messenger has already accomplished the
     *             old one.
     */
    void reAppointment(BaseDownloadTask.IRunningTask task,
                       BaseDownloadTask.LifeCycleCallback callback);

    /**
     * The 'block completed'(status) message will be handover in the non-UI thread and block the
     * 'completed'(status) message.
     *
     * @return {@code true} if the status of the current message is
     * {@link com.halo.filedownloader.model.FileDownloadStatus#blockComplete}.
     */
    boolean isBlockingCompleted();

    /**
     * Discard this messenger.
     * <p>
     * If this messenger is discarded, all messages sent by this messenger or feature messages
     * handled by this messenger will be discard, no longer callback to the target Listener.
     */
    void discard();
}
