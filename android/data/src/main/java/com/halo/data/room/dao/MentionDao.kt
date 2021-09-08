/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.halo.data.room.table.MentionTable
import com.halo.data.room.table.ReadStateTable

@Dao
abstract class MentionDao {

    /*INSERT*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(table: MentionTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun inserts(tables: MutableList<MentionTable>)

}