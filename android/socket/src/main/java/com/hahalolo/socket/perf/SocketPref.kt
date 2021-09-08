/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.socket.perf

/**
 * @author ndn
 * Created by ndn
 * Created on 10/24/18.
 */
interface SocketPref {

    fun insertToken(token:String)

    fun getToken() :String

    fun clearToken()

    fun insertSessionId(token: String, sessionId:String)

    fun getSessionId(token: String):String

    fun clearSession()

    fun insertSocketHost(url :String)

    fun getSocketHost():String
}