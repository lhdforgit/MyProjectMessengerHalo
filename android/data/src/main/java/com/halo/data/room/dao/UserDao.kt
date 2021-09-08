/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.dao

import androidx.room.*
import com.halo.data.room.table.UserTable

@Dao
abstract class UserDao {

    /*INSERT*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(word: UserTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUsers(list: MutableList<UserTable>)
}