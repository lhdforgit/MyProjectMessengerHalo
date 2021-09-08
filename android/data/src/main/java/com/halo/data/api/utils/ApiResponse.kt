/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.utils

import androidx.collection.ArrayMap
import retrofit2.Response
import java.io.IOException
import java.util.regex.Pattern

/**
 * @param <T>
 * @author ndn
 * Created by ndn
 * Created on 6/7/18.
 * Common class used by API responses.
 * [ApiResponse] is a simple wrapper around Retrofit2.Call class to convert its response into a LiveData.
</T> */
class ApiResponse<T> {

    val code: Int
    val body: T?
    val errorMessage: String?
    private val links: Map<String, String>

    val isSuccessful: Boolean
        get() = code in 200..299

    constructor(error: Throwable) {
        code = 500
        body = null
        errorMessage = error.message
        links = emptyMap()
    }

    constructor(response: Response<T>) {
        code = response.code()
        if (response.isSuccessful) {
            body = response.body()
            errorMessage = null
        } else {
            var message: String? = null
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody()!!.string()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            if (message == null || message.trim { it <= ' ' }.isEmpty()) {
                message = response.message()
            }
            errorMessage = message
            body = null
        }
        val linkHeader = response.headers().get("link")
        if (linkHeader == null) {
            links = emptyMap()
        } else {
            links = ArrayMap()
            val matcher = LINK_PATTERN.matcher(linkHeader)

            while (matcher.find()) {
                val count = matcher.groupCount()
                if (count == 2) {
                    links[matcher.group(2)] = matcher.group(1)
                }
            }
        }
    }

    override fun toString(): String {
        return "ApiResponse{" +
                "code=" + code +
                ", body=" + body +
                ", errorMessage='" + errorMessage + '\''.toString() +
                ", links=" + links +
                '}'.toString()
    }

    companion object {
        private val LINK_PATTERN = Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
    }
}
