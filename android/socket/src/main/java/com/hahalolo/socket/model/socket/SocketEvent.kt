package com.hahalolo.socket.model.socket

object SocketEvent {
    const val HELLO = "hello"
    const val IDENTIFY = "identify"
    const val READY = "ready"
    const val RESUME = "resume"
    const val REPLAY = "replay"
    const val REPLAY_ACK = "replay-ack"
    const val RESUMED = "resumed"
    const val HEARTBEAT = "ping"
    const val HEARTBEAT_ACK = "pong"
    const val EXCEPTION = "exception"
    const val MESSAGE = "message"
}