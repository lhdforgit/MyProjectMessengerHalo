package com.hahalolo.socket

import androidx.annotation.IntDef
import com.hahalolo.socket.SocketState.Companion.AUTHEN
import com.hahalolo.socket.SocketState.Companion.CONNECTED
import com.hahalolo.socket.SocketState.Companion.CONNECTING
import com.hahalolo.socket.SocketState.Companion.DISCONNECT
import com.hahalolo.socket.SocketState.Companion.NONE

@IntDef(
    NONE, CONNECTING, CONNECTED, DISCONNECT, AUTHEN
)
annotation class SocketState {
    companion object {
        const val NONE = 0
        const val CONNECTING = 1
        const val CONNECTED = 2
        const val DISCONNECT = 3
        const val AUTHEN = 4
    }
}