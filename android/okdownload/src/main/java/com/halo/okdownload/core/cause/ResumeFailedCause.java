/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.okdownload.core.cause;

public enum ResumeFailedCause {
    INFO_DIRTY,
    FILE_NOT_EXIST,
    OUTPUT_STREAM_NOT_SUPPORT,
    RESPONSE_ETAG_CHANGED,
    RESPONSE_PRECONDITION_FAILED,
    RESPONSE_CREATED_RANGE_NOT_FROM_0,
    RESPONSE_RESET_RANGE_NOT_FROM_0,
    CONTENT_LENGTH_CHANGED,
}