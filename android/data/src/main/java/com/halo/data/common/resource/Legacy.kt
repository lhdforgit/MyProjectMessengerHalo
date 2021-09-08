/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.common.resource

import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.awaitResponse
import java.util.concurrent.TimeoutException

sealed class Legacy<T>(val code: Int, val body: T?, val message: String?) {
    val isUnauthorized: Boolean
        get() = code == 401

    class Success<T>(data: T?) : Legacy<T>(StatusNetwork.SUCCESS, data, null)
    class Error<T>(data: T?, error: Int, message: String?) : Legacy<T>(error, data, message)
    class Loading<T> : Legacy<T>(StatusNetwork.LOADING, null, null)

    val isSuccess: Boolean
        get() = code == StatusNetwork.SUCCESS
    val isLoading: Boolean
        get() = code == StatusNetwork.LOADING

    companion object {

        fun <T> success(data: T?): Legacy<T> = Success(data)

        fun <T> error(error: Int, msg: String, data: T?): Legacy<T> = Error(data, error, msg)

        fun <T> loading(): Legacy<T> = Loading()

        suspend fun <T : Any> liveResponse(call: Call<T>): MutableLiveData<Legacy<T>> {
            val live = MutableLiveData<Legacy<T>>()
            live.postValue(loading())
            try {
                val response = call.awaitResponse()
                if (response.isSuccessful) {
                    live.postValue(success(response.body()))
                } else {
                    live.postValue(error(response.code(), response.message(), response.body()))
                }
                return live
            } catch (e: Exception) {
                if (e is TimeoutException) {
                    live.postValue(error(StatusNetwork.ERROR, e.message.toString(), null))
                } else {
                    live.postValue(error(StatusNetwork.ERROR, e.message.toString(), null))
                }
                return live
            }
        }

        suspend fun <T : Any> response(call: Call<T>): Legacy<T> {
            return try {
                val response = call.awaitResponse()
                if (response.isSuccessful) {
                    success(response.body())
                } else {
                    error(response.code(), response.message(), response.body())
                }
            } catch (e: Exception) {
                error(StatusNetwork.ERROR, e.message.toString(), null)
            }
        }
    }

    override fun toString(): String {
        return "Resource(statusNetwork=$code, data=$body, message=$message)"
    }
}