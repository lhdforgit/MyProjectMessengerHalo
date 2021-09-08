/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.entity

import android.text.TextUtils
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.halo.common.utils.ThumbImageUtils
import com.halo.data.room.table.MemberTable
import com.halo.data.room.table.MentionTable
import com.halo.data.room.table.UserTable
import com.halo.data.room.type.MemberStatusType
import com.halo.data.room.type.RoleType

class MemberEntity(
    @Embedded
    private var memberTable: MemberTable,
    @Relation(parentColumn = "userId", entityColumn = "userId")
    private var user: UserTable? = null
) {

    //TODO NEW FUNTION
    fun userId(): String {
        return memberTable.userId
    }

    fun memberName(): String {
        return memberTable.nickName?.takeIf { it.isNotEmpty() } ?: (user?.userName ?: "")
    }

    fun memberAvatar(): String {
        return user?.avatar ?: ""
    }

    fun isOnline(): Boolean {
        return TextUtils.equals(user?.status, "online")
    }

    //TODO LAST FUNTION

    fun isOwner(userId: String?): Boolean {
        return TextUtils.equals(memberTable.userId, userId)
    }

    fun getAvatar(): String {
        return ThumbImageUtils.thumbAvatar(user?.avatar ?: "")
    }

    fun getUserNameByFirstName(): String {
        return ""
    }

    fun getFullName(): String {
        return user?.userName ?: ""
    }

    fun isHaveNickname(): Boolean {
        return false
    }

    fun isActive(): Boolean {
        return TextUtils.equals(memberTable.status, MemberStatusType.ACTIVE)
    }

    fun memberRole(): Int {
        return RoleType.MEMBER
    }

    fun userTable(): UserTable? {
        return user
    }

    fun newMessage(): Int {
        return 0
    }

    fun isEnableNotify(): Boolean {
        return false
    }

    fun getMentionName(query: String?): String {
        return memberTable.nickName?.takeIf {
            it.isNotEmpty()
                    && (it.contains(query ?: "")
                    || memberTable.nickNameSearch?.contains(query ?: "") == true)
        } ?: (userTable()?.userName ?: "")
    }

    companion object {
        val TAG_ALL = "all"
    }
}