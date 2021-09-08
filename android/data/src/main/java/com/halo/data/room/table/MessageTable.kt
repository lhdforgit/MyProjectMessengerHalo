/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A basic class representing an entity that is a row in a one-column database table.
 *
 * @ Entity - You must annotate the class as an entity and supply a table name if not class name.
 * @ PrimaryKey - You must identify the primary key.
 * @ ColumnInfo - You must supply the column name if it is different from the variable name.
 *
 * See the documentation for the full rich set of annotations.
 * https://developer.android.com/topic/libraries/architecture/room.html
 */

@Entity
data class MessageTable constructor(
    @PrimaryKey
    @ColumnInfo(name = "msgId")
    var msgId: String,
    @ColumnInfo(name = "userId")
    var userId: String?,
    @ColumnInfo(name = "workspaceId")
    var workspaceId: String?,
    @ColumnInfo(name = "channelId")
    var channelId: String?,
    @ColumnInfo(name = "clientId")
    var clientId: String?,
    @ColumnInfo(name = "msgTimeCreate")
    var timeCreate: Long? = null,
    @ColumnInfo(name = "msgTimeUpdate")
    var timeUpdate: Long? = null,
    @ColumnInfo(name = "msgTimeSend")
    var timeSend: Long? = null
) {
    @ColumnInfo(name = "msgType")
    var type: String? = null

    @ColumnInfo(name = "msgContent")
    var content: String? = null

    @ColumnInfo(name = "msgReaction")
    var reactionJson: String? = null

    @ColumnInfo(name = "msgStatus")
    var status: Int? = null

    @ColumnInfo(name = "msgIsDownloading")
    var isDownloading: Boolean = false

    @ColumnInfo(name = "msgTime")
    var msgTime: Long? = if (timeSend != null) timeSend else timeCreate  // time for sort

    @ColumnInfo(name = "userInRoomId")
    var memberId: String = channelId + userId

    @ColumnInfo(name = "msgSave")
    var msgSave: String? = null

    @ColumnInfo(name = "msgBubbleSave")
    var msgBubbleSave: String? = null

    @ColumnInfo(name = "attachmentType")
    var attachmentType: String? = null

    @ColumnInfo(name = "msgAttachment")
    var attachmentJson: String? = null

    @ColumnInfo(name = "embedJson")
    var embedJson: String? = null

    @ColumnInfo(name = "metadataJson")
    var metadataJson: String? = null

    @ColumnInfo(name = "inviteJson")
    var inviteJson: String?=null

    @ColumnInfo(name = "msgTimeRevoked")
    var timeRevoked: Long? = null

    @ColumnInfo(name = "quoteMsgId")
    var quoteMsgId:String?=null

}


