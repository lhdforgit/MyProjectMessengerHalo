/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader.message;

import android.os.Parcel;

import com.halo.filedownloader.model.FileDownloadStatus;

import java.util.Locale;

/**
 * The message snapshot.
 */
public abstract class MessageSnapshot implements IMessageSnapshot {
    private final int id;
    protected boolean largeFile;

    MessageSnapshot(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Throwable getThrowable() {
        throw new NoFieldException("getThrowable", this);
    }

    @Override
    public int getRetryingTimes() {
        throw new NoFieldException("getRetryingTimes", this);
    }

    @Override
    public boolean isResuming() {
        throw new NoFieldException("isResuming", this);
    }

    @Override
    public String getEtag() {
        throw new NoFieldException("getEtag", this);
    }

    @Override
    public long getLargeSofarBytes() {
        throw new NoFieldException("getLargeSofarBytes", this);
    }

    @Override
    public long getLargeTotalBytes() {
        throw new NoFieldException("getLargeTotalBytes", this);
    }

    @Override
    public int getSmallSofarBytes() {
        throw new NoFieldException("getSmallSofarBytes", this);
    }

    @Override
    public int getSmallTotalBytes() {
        throw new NoFieldException("getSmallTotalBytes", this);
    }

    @Override
    public boolean isReusedDownloadedFile() {
        throw new NoFieldException("isReusedDownloadedFile", this);
    }

    @Override
    public String getFileName() {
        throw new NoFieldException("getFileName", this);
    }

    @Override
    public boolean isLargeFile() {
        return largeFile;
    }


    public interface IWarnMessageSnapshot {
        MessageSnapshot turnToPending();
    }


    MessageSnapshot(Parcel in) {
        this.id = in.readInt();
    }

    public static class NoFieldException extends IllegalStateException {
        NoFieldException(String methodName, MessageSnapshot snapshot) {
            super(String.format(Locale.ENGLISH, "There isn't a field for '%s' in this message"
                            + " %d %d %s",
                    methodName, snapshot.getId(), snapshot.getStatus(),
                    snapshot.getClass().getName()));
        }
    }

    // Started Snapshot
    public static class StartedMessageSnapshot extends MessageSnapshot {

        StartedMessageSnapshot(int id) {
            super(id);
        }

        StartedMessageSnapshot(Parcel in) {
            super(in);
        }

        @Override
        public byte getStatus() {
            return FileDownloadStatus.started;
        }
    }
}
