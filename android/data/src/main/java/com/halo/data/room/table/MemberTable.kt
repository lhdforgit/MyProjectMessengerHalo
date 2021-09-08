/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.halo.data.room.StringUtils

@Entity
data class MemberTable constructor(

    @PrimaryKey
    @ColumnInfo(name = "memberId")
    var memberId: String,
    @ColumnInfo(name = "userId")
    var userId: String,
    @ColumnInfo(name = "channelId")
    var channelId: String,
    @ColumnInfo(name = "workspaceId")
    var workspaceId: String
) {

    @ColumnInfo(name = "userInRoomId")
    var userInRoomId: String = channelId + userId

    @ColumnInfo(name = "nickName")
    var nickName: String? = null
        set(value) {
            field = value
            nickNameSearch = StringUtils.deAccent(value)
        }

    @ColumnInfo(name = "nickNameSearch")
    var nickNameSearch: String? = null

    @ColumnInfo(name = "status")
    var status: String? = null

    @ColumnInfo(name = "createdAt")
    var createdAt: Long? = null

    @ColumnInfo(name = "updatedAt")
    var updatedAt: Long? = null

    @ColumnInfo(name = "deletedAt")
    var deletedAt: Long? = null
}