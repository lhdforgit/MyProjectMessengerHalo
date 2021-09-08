package com.halo.data.entities.member

import android.text.TextUtils

data class MemberChannel(
    var channelId: String? = null,
    var workspaceId: String? = null,
    var userId: String? = null,
    var createdAt: String? = null,
    var deletedAt: String? = null,
    var memberId: String? = null,
    var nickName: String? = null,
    var updatedAt: String? = null,
    var roles: List<MemberRole>? = null,
    var user: MemberInfo? = null
) {
    val isMemberDelete: Boolean
        get() = deletedAt?.isNotEmpty() ?: false

    val isHasNickname: Boolean
        get() = !nickName.isNullOrEmpty()

    val displayName: String
        get() = if (isHasNickname) nickName ?: "" else user?.getFullName() ?: ""

    fun isUserLogin(userId: String): Boolean {
        return TextUtils.equals(this.userId, userId)
    }

    val isOwner: Boolean
        get() = roles?.any { it.name == "@owner" } ?: false

    val isAdmin: Boolean
        get() = roles?.any { it.name == "@admin" } ?: false

    val isMember: Boolean
        get() = roles?.any { it.name == "@everyone" } ?: false
}

data class MemberInfo(
    var firstName: String? = null,
    var lastName: String? = null,
    var avatar: String? = null,
    private var fullName: String? = null
) {
    fun getFullName(): String {
        return fullName?.takeIf { it.isNotEmpty() } ?: "${lastName ?: ""} ${firstName ?: ""}"
    }
}