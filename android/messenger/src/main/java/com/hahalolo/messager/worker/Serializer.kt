/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.worker

import com.google.gson.Gson

import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author ndn
 * Created by ndn
 * Created on 5/14/18.
 *
 *
 * Json Serializer/Deserializer.
 */
@Singleton
class Serializer @Inject
internal constructor() {

    private val gson = Gson()

    /**
     * Serialize an object to Json.
     *
     * @param object to serialize.
     */
    fun serialize(`object`: Any?, clazz: Class<*>): String {
        return `object`?.run {
            gson.toJson(`object`, clazz)
        } ?: ""
    }

    /**
     * Deserialize a json representation of an object.
     *
     * @param string A json string to deserialize.
     */
    fun <T> deserialize(string: String?, clazz: Class<T>): T {
        return gson.fromJson(string ?: "", clazz)
    }
}