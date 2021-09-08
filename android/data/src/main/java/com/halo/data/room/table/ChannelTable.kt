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
class ChannelTable constructor(
    @PrimaryKey
    @ColumnInfo(name = "channelId")
    var id: String,
    //info
    @ColumnInfo(name = "workspaceId")
    var workspaceId: String? = null,
    @ColumnInfo(name = "channelName")
    var name: String? = null,
    @ColumnInfo(name = "createAt")
    var createAt: Long? = null,
    @ColumnInfo(name = "updateAt")
    var updateAt: Long? = null
) {
    //info
    @ColumnInfo(name = "channelAvatar")
    var avatar: String? = null

    //last message
    @ColumnInfo(name = "lastMessageId")
    var lastMessageId: String? = null

    @ColumnInfo(name = "lastMessageJson")
    var lastMessageJson: String? = null

    @ColumnInfo(name = "recipientJson")
    var recipientJson:String?=null

    //member
    @ColumnInfo(name = "ownerId")
    var ownerId: String? = null

    @ColumnInfo(name = "recipientId")
    var recipientId: String? = null

    @ColumnInfo(name = "dmId")
    var dmId: String? = null

    @ColumnInfo(name = "membersCount")
    var membersCount: Int =0

    //time
    @ColumnInfo(name = "deleteAt")
    var deleteAt: Long? = null

    @ColumnInfo(name = "time")
    var time: Long? = updateAt?.takeIf { it> (createAt?:0) }?: createAt

    //support query
    @ColumnInfo(name = "nameSearch")
    var nameSearch: String? = StringUtils.deAccent(name)

    @ColumnInfo(name = "saveType")
    var saveType: String? = null

    @ColumnInfo(name = "bubbleSave")
    var bubbleSave: String? = StringUtils.deAccent(name)

}
