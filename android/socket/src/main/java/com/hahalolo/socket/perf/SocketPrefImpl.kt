/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.socket.perf

import android.content.Context
import android.content.SharedPreferences
import com.halo.data.cache.serializer.Serializer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author ndn
 * Created by ndn
 * Created on 10/16/18.
 */
@Singleton
class SocketPrefImpl : SocketPref {
    private var sharedPref: SharedPreferences
    private var serializer: Serializer? = null

    @Inject
    internal constructor(context: Context, serializer: Serializer?) {
        sharedPref = context.getSharedPreferences(SOCKET_TARGET_PREF, Context.MODE_PRIVATE)
        this.serializer = serializer
    }

    companion object {
        private const val SOCKET_TARGET_PREF = "socket-target-pref"
        private const val SOCKET_TOKEN_TARGET_PREF = "socket-token-target-pref"
        private const val SOCKET_SESSION_TARGET_PREF = "socket-session-target-pref"
        private const val SOCKET_HOST_TARGET_PREF = "socket-host-target-pref"
    }

    override fun insertToken(token: String) {
        val editor = sharedPref.edit()
        editor.putString(SOCKET_TOKEN_TARGET_PREF, token)
        editor.apply()
    }

    override fun getToken(): String {
        return sharedPref.getString(SOCKET_TOKEN_TARGET_PREF,"")?:""
    }

    override fun insertSessionId( token: String, sessionId: String) {
        val editor = sharedPref.edit()
        editor.putString(token, sessionId)
        editor.apply()
    }

    override fun getSessionId(token: String): String {
        return sharedPref.getString(token,"")?:""
    }

    override fun clearSession() {
        val editor = sharedPref.edit()
        editor.putString(SOCKET_SESSION_TARGET_PREF, null)
        editor.apply()
    }

    override fun insertSocketHost(url: String) {
        val editor = sharedPref.edit()
        editor.putString(SOCKET_HOST_TARGET_PREF, url)
        editor.apply()
    }

    override fun getSocketHost(): String {
        return sharedPref.getString(SOCKET_HOST_TARGET_PREF,"")?:""
    }

    override fun clearToken() {
        val editor = sharedPref.edit()
        editor.putString(SOCKET_TOKEN_TARGET_PREF, null)
        editor.apply()
    }
}