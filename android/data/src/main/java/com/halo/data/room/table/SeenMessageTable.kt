/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class SeenMessageTable(
        @ColumnInfo(name = "userId")
        val userId: String,
        @ColumnInfo(name = "msgId")
        val msgId: String) {
    @PrimaryKey
    @ColumnInfo(name = "seenId")
    var seenId: String = userId + msgId

}