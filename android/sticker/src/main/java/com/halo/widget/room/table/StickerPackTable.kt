package com.halo.widget.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
class StickerPackTable constructor(
    @PrimaryKey
    @ColumnInfo(name = "packId")
    var id: String
) {
    @ColumnInfo(name = "packIcon")
    var packIcon: String? = null

    @ColumnInfo(name = "packUrl")
    var packUrl: String? = null

    @ColumnInfo(name = "packName")
    var packName: String? = null

    @ColumnInfo(name = "packEnable")
    var packEnable: Int = 1

}