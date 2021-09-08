/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.data.cache.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.halo.constant.HaloConfig
import com.halo.data.cache.db.mess.MessDao
import com.halo.data.entities.mess.oauth.MessOauth

/**
 * Main database description.
 */
@Database(
    entities = [
        MessOauth::class],
    version = HaloConfig.DATABASE_VERSION,
    exportSchema = true
)
abstract class AppDb : RoomDatabase() {
    abstract fun messDao(): MessDao
}