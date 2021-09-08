/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader.status;

import com.halo.filedownloader.model.FileDownloadStatus;
import com.halo.okdownload.DownloadTask;
import com.halo.okdownload.StatusUtil;

public class StatusAssist {

    private byte status = FileDownloadStatus.INVALID_STATUS;

    private DownloadTask downloadTask;

    public synchronized void setDownloadTask(DownloadTask downloadTask) {
        this.downloadTask = downloadTask;
    }

    public synchronized DownloadTask getDownloadTask() {
        return downloadTask;
    }

    public synchronized byte getStatus() {
        if (downloadTask == null) {
            return status;
        }
        StatusUtil.Status okDownloadStatus = StatusUtil.getStatus(downloadTask);
        status = convert(okDownloadStatus);
        return status;
    }

    synchronized byte convert(StatusUtil.Status status) {
        switch (status) {
            case COMPLETED:
                return FileDownloadStatus.completed;
            case IDLE:
                return FileDownloadStatus.paused;
            case UNKNOWN:
                return FileDownloadStatus.INVALID_STATUS;
            case PENDING:
                return FileDownloadStatus.pending;
            case RUNNING:
                return FileDownloadStatus.progress;
            default:
                return FileDownloadStatus.INVALID_STATUS;
        }
    }

    public synchronized boolean isUsing() {
        return getStatus() != FileDownloadStatus.INVALID_STATUS;
    }

    public synchronized boolean isOver() {
        return FileDownloadStatus.isOver(getStatus());
    }
}
