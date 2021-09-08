package com.halo.data.entities.invite

import android.text.TextUtils
import com.halo.data.entities.channel.Channel
import com.halo.data.entities.member.Member

class InviteInfo(response: InviteInfoResponse) {
    var createdAt: String? = response.createdAt
    var expiredAt: String? = response.expiredAt
    val code = response.code
    val owner: String? = response.owner
    val channel: Channel? = response.channel?.apply {
        this.members = response.memberPopulated
    }
    var joined: Boolean = response.joined

    fun channelId(): String {
        return channel?.channelId ?: ""
    }

    fun channelName(): String {
        return channel?.channelName()?.takeIf { it.isNotEmpty() } ?: ""
    }

    fun channelAvatars(): MutableList<String> {
        return channel?.channelAvatars() ?: mutableListOf()
    }

    fun inviter(): Member? {
        return channel?.members?.find { TextUtils.equals(it.userId, owner) }
    }
}