/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.halo.widget.room.table.StickerPackTable
import com.halo.widget.room.table.StickerTable

@Dao
interface StickerDao {

    // insert , update
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPacks(packs: MutableList<StickerPackTable>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(mutableList: MutableList<StickerTable>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: StickerTable)
    // load data

    @Query("SELECT * from StickerTable U ORDER BY U.stickerTime DESC")
    fun lastStickers(): MutableList<StickerTable>

    @Query("SELECT * from StickerPackTable U ")
    fun stickerPacks(): LiveData<MutableList<StickerPackTable>>

    @Query("SELECT * from StickerTable WHERE packId=:packId")
    fun stickers(packId: String): LiveData<MutableList<StickerTable>>

    @Query("SELECT * from StickerTable WHERE packId=:packId")
    fun stickers2(packId: String): MutableList<StickerTable>

    @Query("UPDATE StickerTable set stickerTime =:time WHERE stickerId=:stickerId")
    fun updateStickerTime(stickerId: String, time: Long)

    @Query("SELECT * from StickerTable WHERE stickerTime >=0 ORDER BY stickerTime DESC LIMIT 16")
    fun latestSticker(): LiveData<MutableList<StickerTable>>

}