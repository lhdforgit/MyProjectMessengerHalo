/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.download;

import androidx.annotation.NonNull;

import com.halo.okdownload.core.Util;
import com.halo.okdownload.core.cause.ResumeFailedCause;
import com.halo.okdownload.core.exception.FileBusyAfterRunException;
import com.halo.okdownload.core.exception.InterruptException;
import com.halo.okdownload.core.exception.PreAllocateException;
import com.halo.okdownload.core.exception.ResumeFailedException;
import com.halo.okdownload.core.exception.ServerCanceledException;
import com.halo.okdownload.core.file.MultiPointOutputStream;

import java.io.IOException;
import java.net.SocketException;

public class DownloadCache {
    private String redirectLocation;
    private final MultiPointOutputStream outputStream;

    private volatile boolean preconditionFailed;
    private volatile boolean userCanceled;
    private volatile boolean serverCanceled;
    private volatile boolean unknownError;
    private volatile boolean fileBusyAfterRun;
    private volatile boolean preAllocateFailed;
    private volatile IOException realCause;

    DownloadCache(@NonNull MultiPointOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    private DownloadCache() {
        this.outputStream = null;
    }

    @NonNull
    MultiPointOutputStream getOutputStream() {
        if (outputStream == null) throw new IllegalArgumentException();
        return outputStream;
    }

    void setRedirectLocation(String redirectLocation) {
        this.redirectLocation = redirectLocation;
    }

    String getRedirectLocation() {
        return redirectLocation;
    }

    boolean isPreconditionFailed() {
        return preconditionFailed;
    }

    public boolean isUserCanceled() {
        return userCanceled;
    }

    boolean isServerCanceled() {
        return serverCanceled;
    }

    boolean isUnknownError() {
        return unknownError;
    }

    boolean isFileBusyAfterRun() {
        return fileBusyAfterRun;
    }

    public boolean isPreAllocateFailed() {
        return preAllocateFailed;
    }

    IOException getRealCause() {
        return realCause;
    }

    ResumeFailedCause getResumeFailedCause() {
        return ((ResumeFailedException) realCause).getResumeFailedCause();
    }

    public boolean isInterrupt() {
        return preconditionFailed || userCanceled || serverCanceled || unknownError
                || fileBusyAfterRun || preAllocateFailed;
    }

    public void setPreconditionFailed(IOException realCause) {
        this.preconditionFailed = true;
        this.realCause = realCause;
    }

    void setUserCanceled() {
        this.userCanceled = true;
    }

    public void setFileBusyAfterRun() {
        this.fileBusyAfterRun = true;
    }

    public void setServerCanceled(IOException realCause) {
        this.serverCanceled = true;
        this.realCause = realCause;
    }

    public void setUnknownError(IOException realCause) {
        this.unknownError = true;
        this.realCause = realCause;
    }

    public void setPreAllocateFailed(IOException realCause) {
        this.preAllocateFailed = true;
        this.realCause = realCause;
    }

    public void catchException(IOException e) {
        if (isUserCanceled()) return; // ignored

        if (e instanceof ResumeFailedException) {
            setPreconditionFailed(e);
        } else if (e instanceof ServerCanceledException) {
            setServerCanceled(e);
        } else if (e == FileBusyAfterRunException.HAHALOLO) {
            setFileBusyAfterRun();
        } else if (e instanceof PreAllocateException) {
            setPreAllocateFailed(e);
        } else if (e != InterruptException.HAHALOLO) {
            setUnknownError(e);
            if (!(e instanceof SocketException)) {
                // we know socket exception, so ignore it,  otherwise print stack trace.
                Util.d("DownloadCache", "catch unknown error " + e);
            }
        }
    }

    static class PreError extends DownloadCache {
        PreError(IOException realCause) {
            super(null);
            setUnknownError(realCause);
        }
    }
}