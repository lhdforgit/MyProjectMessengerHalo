/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.halo.widget.room.dao.StickerDao
import com.halo.widget.room.table.StickerPackTable
import com.halo.widget.room.table.StickerTable

/**
 * Create by ndn
 * Create on 5/14/20
 * com.hahalolo.messenger.database.room
 */
@Database(
    entities = [StickerTable::class , StickerPackTable::class],
    version = 1
)
abstract class StickerDatabase : RoomDatabase() {
    abstract fun stickerDao(): StickerDao
}