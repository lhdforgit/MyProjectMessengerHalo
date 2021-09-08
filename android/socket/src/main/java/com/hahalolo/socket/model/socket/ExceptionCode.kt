package com.hahalolo.socket.model.socket

import androidx.annotation.IntDef
import com.hahalolo.socket.model.socket.ExceptionCode.Companion.ALREADY_AUTHENTICATED
import com.hahalolo.socket.model.socket.ExceptionCode.Companion.AUTHENTICATION_FAILED
import com.hahalolo.socket.model.socket.ExceptionCode.Companion.DECODE_ERROR
import com.hahalolo.socket.model.socket.ExceptionCode.Companion.INVALID_SESSION
import com.hahalolo.socket.model.socket.ExceptionCode.Companion.NOT_AUTHENTICATED
import com.hahalolo.socket.model.socket.ExceptionCode.Companion.UNKNOWN_ERROR
import com.hahalolo.socket.model.socket.ExceptionCode.Companion.UNKNOWN_OPCODE


@IntDef(
    UNKNOWN_ERROR,
    UNKNOWN_OPCODE,
    DECODE_ERROR,
    NOT_AUTHENTICATED,
    AUTHENTICATION_FAILED,
    ALREADY_AUTHENTICATED,
    INVALID_SESSION
)
annotation class ExceptionCode {
    companion object {
        const val UNKNOWN_ERROR = 4000
        const val UNKNOWN_OPCODE = 4001
        const val DECODE_ERROR = 4002
        const val NOT_AUTHENTICATED = 4003
        const val AUTHENTICATION_FAILED = 4004
        const val ALREADY_AUTHENTICATED = 4005
        const val INVALID_SESSION = 4006
    }
}