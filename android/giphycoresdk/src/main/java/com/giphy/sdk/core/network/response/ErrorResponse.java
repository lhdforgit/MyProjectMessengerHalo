/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.network.response;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.giphy.sdk.core.models.Meta;

public class ErrorResponse implements GenericResponse {
    private final Meta meta;

    public ErrorResponse(@NonNull int serverStatus, @Nullable String message) {
        meta = new Meta(serverStatus, message);
    }

    public Meta getMeta() {
        return meta;
    }
}
