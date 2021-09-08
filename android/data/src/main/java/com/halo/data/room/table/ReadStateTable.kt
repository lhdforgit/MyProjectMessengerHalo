package com.halo.data.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ReadStateTable (
    @PrimaryKey
    @ColumnInfo(name = "channelId")
    var channelId: String,
    @ColumnInfo(name = "userId")
    var userId: String?,
    @ColumnInfo(name = "newMsg")
    var newMsg: Int
        )