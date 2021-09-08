/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader.notification;

import android.app.NotificationManager;
import android.content.Context;

import com.halo.filedownloader.model.FileDownloadStatus;
import com.halo.filedownloader.util.FileDownloadHelper;

/**
 * An atom notification item which identify with a downloading task, they have the same downloading
 * Id.
 *
 * @see FileDownloadNotificationHelper
 * @see FileDownloadNotificationListener
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseNotificationItem {

    private int id, sofar, total;
    private String title, desc;

    private int status = FileDownloadStatus.INVALID_STATUS;
    private int lastStatus = FileDownloadStatus.INVALID_STATUS;

    public BaseNotificationItem(final int id, final String title, final String desc) {
        this.id = id;

        this.title = title;
        this.desc = desc;
    }

    public void show(boolean isShowProgress) {
        show(isChanged(), getStatus(), isShowProgress);
    }

    /**
     * @param isShowProgress Whether there is a need to show the progress schedule changes
     */
    public abstract void show(boolean statusChanged, int status, boolean isShowProgress);

    public void update(final int sofar, final int total) {
        this.sofar = sofar;
        this.total = total;
        show(true);
    }

    public void updateStatus(final int status) {
        this.status = status;
    }

    public void cancel() {
        getManager().cancel(id);
    }

    private NotificationManager manager;

    protected NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) FileDownloadHelper.getAppContext().
                    getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSofar() {
        return sofar;
    }

    public void setSofar(int sofar) {
        this.sofar = sofar;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getStatus() {
        this.lastStatus = status;
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getLastStatus() {
        return lastStatus;
    }

    public boolean isChanged() {
        return this.lastStatus != status;
    }
}