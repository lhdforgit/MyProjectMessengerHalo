package com.halo.data.entities.member

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.halo.data.entities.user.User

class Member {

    @SerializedName("workspaceId")
    @Expose
    var workspaceId: String? = null

    @SerializedName("channelId")
    @Expose
    var channelId: String? = null

    @SerializedName("userId")
    @Expose
    var userId: String? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @SerializedName("deletedAt")
    @Expose
    var deletedAt: String? = null

    @SerializedName("memberId")
    @Expose
    var memberId: String? = null

    @SerializedName("nickName")
    @Expose
    var nickName: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null

    @SerializedName("user")
    @Expose
    var user: User? = null

    fun memberName(): String {
        return nickName?.takeIf { it.isNotEmpty() }?: (user?.userName()?:"")
    }

    fun memberAvatar(): String{
        return user?.avatar?:""
    }

    fun createTime(): Long? = kotlin.runCatching { createdAt?.toLong() }.getOrNull()

    fun updateTime(): Long? = kotlin.runCatching { updatedAt?.toLong() }.getOrNull()

    fun deleteTime(): Long? = kotlin.runCatching { deletedAt?.toLong() }.getOrNull()
}