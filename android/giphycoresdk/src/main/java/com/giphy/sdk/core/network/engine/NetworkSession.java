/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.network.engine;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.giphy.sdk.core.network.response.GenericResponse;
import com.giphy.sdk.core.threading.ApiTask;

import java.util.Map;

/**
 * A generic interface that describes all the params of a low level GET request.
 */
public interface NetworkSession {
    <T extends GenericResponse> ApiTask<T> queryStringConnection(@NonNull final Uri serverUrl,
                                                                 @NonNull final String path,
                                                                 @NonNull final String method,
                                                                 @NonNull final Class<T> responseClass,
                                                                 @Nullable final Map<String, String> queryStrings,
                                                                 @Nullable final Map<String, String> headers);

    <T extends GenericResponse> ApiTask<T> postStringConnection(@NonNull final Uri serverUrl,
                                                                @NonNull final String path,
                                                                @NonNull final String method,
                                                                @NonNull final Class<T> responseClass,
                                                                @Nullable final Map<String, String> queryStrings,
                                                                @Nullable final Map<String, String> headers,
                                                                @Nullable final Object requestBody);
}
