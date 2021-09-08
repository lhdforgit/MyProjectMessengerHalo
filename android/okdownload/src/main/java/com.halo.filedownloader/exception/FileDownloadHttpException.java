/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.filedownloader.exception;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Throw this exception, when the HTTP status code is not {@link java.net.HttpURLConnection#HTTP_OK}
 * and not {@link java.net.HttpURLConnection#HTTP_PARTIAL} either.
 */
public class FileDownloadHttpException extends IOException {
    private final int mCode;
    private final Map<String, List<String>> mRequestHeaderMap;
    private final Map<String, List<String>> mResponseHeaderMap;

    public FileDownloadHttpException(final int code,
                                     final Map<String, List<String>> requestHeaderMap,
                                     final Map<String, List<String>> responseHeaderMap) {
        super(String.format(Locale.ENGLISH, "response code error: %d, %n request headers: %s %n "
                + "response headers: %s", code, requestHeaderMap, responseHeaderMap));

        this.mCode = code;
        this.mRequestHeaderMap = cloneSerializableMap(requestHeaderMap);
        this.mResponseHeaderMap = cloneSerializableMap(requestHeaderMap);
    }

    /**
     * @return the header of the current response.
     */
    public Map<String, List<String>> getRequestHeader() {
        return this.mRequestHeaderMap;
    }

    /**
     * @return the header of the current request.
     */
    public Map<String, List<String>> getResponseHeader() {
        return this.mResponseHeaderMap;
    }

    /**
     * @return the HTTP status code.
     */
    public int getCode() {
        return this.mCode;
    }

    private static Map<String, List<String>> cloneSerializableMap(
            final Map<String, List<String>> originMap) {
        final Map<String, List<String>> serialMap = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : originMap.entrySet()) {
            final String key = entry.getKey();
            final List<String> values = new ArrayList<>(entry.getValue());
            serialMap.put(key, values);
        }

        return serialMap;
    }
}