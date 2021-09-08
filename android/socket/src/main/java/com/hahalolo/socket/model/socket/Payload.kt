package com.hahalolo.socket.model.socket

import com.hahalolo.socket.model.data.Typing
import com.hahalolo.socket.model.data.User
import com.halo.data.entities.channel.Channel
import com.halo.data.entities.member.Member
import com.halo.data.entities.message.Message

sealed class Payload {
    data class Hello(val heartbeatIntervalMs: Int = 0) : Payload()
    data class Identify(val token: String?) : Payload()
    data class Ready(var sessionId: String?, var sessionTimeoutMs: Int = 0, var user: User?) :
        Payload()

    data class Replay(var offlineMessages: Int = 0) : Payload()

    data class ReplayAck(var token: String?, val sessionId: String?, var totalReplayed: Int = 0) :
        Payload()

    data class Resume(val token: String?, val sessionId: String?, val lastMessageRef: Int = 0) :
        Payload()

    data class Resumed(var sessionId: String?, var sessionTimeoutMs: Int = 0, var user: User?) :
        Payload()

    data class Ping(var event: String) : Payload()

    data class Exception(var message: String?, var code: Int = 0) : Payload()

    data class MessageCreate(val message: Message?): Payload()
    data class ChannelCreate(val channel: Channel?): Payload()
    data class MemberCreate(val member: Member?): Payload()
    data class UserTyping(val typing: Typing?): Payload()
}