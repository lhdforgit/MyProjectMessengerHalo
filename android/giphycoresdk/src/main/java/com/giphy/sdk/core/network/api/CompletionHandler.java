/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.network.api;

/**
 * Completion handler callback. It's the main interface for getting the results or errors from
 * the network requests
 *
 * @param <T>
 */
public interface CompletionHandler<T> {
    public void onComplete(T result, Throwable e);
}
