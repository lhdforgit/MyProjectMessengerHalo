/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.common.resource

import com.halo.data.common.resource.StatusNetwork.LOADING
import com.halo.data.common.resource.StatusNetwork.SUCCESS
import java.net.HttpURLConnection

/**
 * @param <T>
 * @author ndn
 * Created by ndn
 * Created on 6/7/18.
 * A generic class that holds a value with its loading statusNetwork.
</T> */
class Resource<T>
constructor(
    val statusNetwork: Int,
    val data: T?,
    val message: String?
) {

    val isUnauthorized: Boolean
        get() = statusNetwork == 401

    val isLoading: Boolean
        get() = statusNetwork == LOADING

    val isError: Boolean
        get() = statusNetwork != LOADING && statusNetwork != SUCCESS

    val isSuccess: Boolean
        get() = statusNetwork == SUCCESS

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(SUCCESS, data, null)
        }

        fun <T> error(error: Int, msg: String?, data: T?): Resource<T> {
            return Resource(error, data, msg)
        }

        fun <T> error(throwable: Throwable): Resource<T> {
            return error(HttpURLConnection.HTTP_BAD_REQUEST, throwable.message, null)
        }

        fun <T> throwableError(throwable: Throwable): Resource<T> {
            return error(HttpURLConnection.HTTP_INTERNAL_ERROR, throwable.message, null)
        }

        fun <T> notFound(): Resource<T> {
            return error(HttpURLConnection.HTTP_NOT_FOUND, "Resource not found", null)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(LOADING, data, null)
        }
    }

    override fun toString(): String {
        return "Resource(statusNetwork=$statusNetwork, data=$data, message=$message)"
    }
}
