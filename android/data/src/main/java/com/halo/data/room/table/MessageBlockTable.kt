package com.halo.data.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class MessageBlockTable(
    @PrimaryKey
    @ColumnInfo(name = "bloId")
    var id: String,
    @ColumnInfo(name = "msgId")
    var msgId: String
) {

    @ColumnInfo(name = "bloDepth")
    var depth: Int = -1

    @ColumnInfo(name = "bloKey")
    var key: String? = null

    @ColumnInfo(name = "bloText")
    var text: String? = null

    @ColumnInfo(name = "bloType")
    var type: String? = null
}
