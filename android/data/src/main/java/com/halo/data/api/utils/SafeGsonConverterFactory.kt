package com.halo.data.api.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

/**
 * Create by ndn
 * Create on 9/30/20
 * com.halo.data.api.utils
 */
class SafeGsonConverterFactory : Converter.Factory() {

    private var gson: Gson = GsonBuilder()
        .registerTypeAdapterFactory(SafeTypeAdapterFactory())
        .enableComplexMapKeySerialization()
        .serializeNulls()
        .create()
    private var safeGsonConverterFactory: GsonConverterFactory = GsonConverterFactory.create(gson)
    private var gsonConverterFactory: GsonConverterFactory = GsonConverterFactory.create()

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return safeGsonConverterFactory.responseBodyConverter(
            type, annotations, retrofit
        )
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        return gsonConverterFactory.requestBodyConverter(
            type,
            parameterAnnotations,
            methodAnnotations,
            retrofit
        )
    }
}