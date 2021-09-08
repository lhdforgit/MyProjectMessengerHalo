/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.network.engine;

import com.giphy.sdk.core.network.response.ErrorResponse;

public class ApiException extends Exception {

    private final ErrorResponse errorResponse;

    public ApiException(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public ApiException(String detailMessage, ErrorResponse errorResponse) {
        super(detailMessage);
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
