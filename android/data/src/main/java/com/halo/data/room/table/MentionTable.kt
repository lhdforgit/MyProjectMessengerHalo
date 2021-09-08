package com.halo.data.room.table

import android.text.TextUtils
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class MentionTable(
    @ColumnInfo(name = "channelId")
    var channelId: String,
    @ColumnInfo(name = "msgId")
    var msgId: String,
    @ColumnInfo(name = "userId")
    var userId: String
) {
    @PrimaryKey
    @ColumnInfo(name = "mentId")
    var id: String = msgId + userId

    @ColumnInfo(name = "userInRoomId")
    var userInRoomId: String = channelId + userId

    fun isMentionAll(): Boolean {
        return TextUtils.equals("all", userId )
    }
}
