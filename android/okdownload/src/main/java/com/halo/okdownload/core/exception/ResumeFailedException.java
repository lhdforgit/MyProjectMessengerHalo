/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.exception;

import com.halo.okdownload.core.cause.ResumeFailedCause;

import java.io.IOException;

public class ResumeFailedException extends IOException {
    private final ResumeFailedCause resumeFailedCause;

    public ResumeFailedException(ResumeFailedCause cause) {
        super("Resume failed because of " + cause);
        this.resumeFailedCause = cause;
    }

    public ResumeFailedCause getResumeFailedCause() {
        return resumeFailedCause;
    }
}
