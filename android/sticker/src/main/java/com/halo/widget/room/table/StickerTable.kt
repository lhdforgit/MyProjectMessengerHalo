/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class StickerTable constructor(
        @PrimaryKey
        @ColumnInfo(name = "stickerId")
        var id: String
) {
    @ColumnInfo(name = "packId")
    var packId: String? = null

    @ColumnInfo(name = "stickerImage")
    var stickerImage: String? = null

    @ColumnInfo(name = "stickerUrl")
    var stickerUrl: String? = null

    @ColumnInfo(name = "stickerTime")
    var stickerTime: Long? = null

    @ColumnInfo(name = "stickerType")
    var stickerType: Int =1
}