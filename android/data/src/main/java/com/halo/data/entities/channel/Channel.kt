package com.halo.data.entities.channel

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.halo.data.common.utils.Strings
import com.halo.data.entities.member.Member
import com.halo.data.entities.message.Message
import com.halo.data.entities.user.User


class Channel {

    @SerializedName("workspaceId")
    @Expose
    var workspaceId: String? = null

    @SerializedName("channelId")
    @Expose
    var channelId: String? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("owner")
    @Expose
    var owner: String? = null

    @SerializedName("avatar")
    @Expose
    var avatar: String? = null

    @SerializedName("deletedAt")
    @Expose
    var deletedAt: String? = null

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null

    @SerializedName("dmId")
    @Expose
    var dmId: String? = null

    @SerializedName("dmStatus")
    @Expose
    var dmStatus: String? = null

    @SerializedName("lastMessageId")
    @Expose
    var lastMessageId: String? = null

    @SerializedName("recipientId")
    @Expose
    var recipientId: String? = null

    @SerializedName("members")
    @Expose
    var members: MutableList<Member>? = null

    @SerializedName("membersCount")
    @Expose
    var membersCount: Int = 0

    @SerializedName("lastMessage")
    @Expose
    var lastMessage: Message? = null

    @SerializedName("recipient")
    @Expose
    var recipient: User? = null


    fun isPrivateChannel(): Boolean {
        return dmId?.isNotEmpty() == true
    }

    fun isGroupChannel() : Boolean{
        if (!isSystemAccount() && !isPrivateChannel()) return true
        return  false
    }

    fun channelName(): String {
        return if (isPrivateChannel()) {
            return recipient?.userName()?:""
        } else {
            name?.takeIf { it.isNotEmpty() } ?: userNames()
        }
    }

    private fun userNames(): String {
        var result = ""
        val MAX = 3
        val size = members?.size ?: 0
        members?.takeIf { it.isNotEmpty() }?.forEachIndexed { index, member ->
            val isLast = (index == size - 1)
            if (index < MAX) {
                result = result + member.memberName() + (if (isLast) "" else " ")
            }
        }
        return result
    }

    fun channelAvatars(): MutableList<String> {
        return if (isPrivateChannel()) {
            mutableListOf(
                recipient?.avatar ?: ""
            )
        } else {
            avatar?.takeIf { it.isNotEmpty() }?.run {
                mutableListOf(this)
            } ?: kotlin.run {
                members?.takeIf { it.isNotEmpty() }?.map { it.memberAvatar() }?.toMutableList()
                    ?: mutableListOf("")
            }
        }
    }

    fun lastMessageContent(): String {
        lastMessage?.let { message ->
            members?.takeIf { it.isNotEmpty() }?.find { TextUtils.equals(it.userId, message.userId) }
                ?.run {
                    return message.content ?: ""
                }
        }
        return ""
    }

    fun timeCreate(): Long? {
        return kotlin.runCatching { createdAt?.toLong() }.getOrNull()
    }

    fun timeUpdate(): Long? {
        return kotlin.runCatching { updatedAt?.toLong() }.getOrNull()
    }

    fun timeDelete(): Long? {
        return kotlin.runCatching { deletedAt?.toLong() }.getOrNull()
    }

    fun lastMessageJson(): String? {
        return lastMessage?.runCatching { Gson().toJson(lastMessage) }?.getOrNull()
    }

    fun isOwner(userId: String): Boolean {
        return TextUtils.equals(userId, owner)
    }

    fun isSystemAccount(): Boolean{
        return  TextUtils.equals(owner, "0")
    }

    fun recipientJson(): String? {
        return recipient?.runCatching {
            Gson().toJson(this)
        }?.getOrNull()
    }
}