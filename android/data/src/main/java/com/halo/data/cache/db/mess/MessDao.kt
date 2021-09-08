/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.data.cache.db.mess

import androidx.room.*
import com.halo.data.entities.mess.oauth.MessOauth
import kotlinx.coroutines.flow.Flow

/**
 * @author ndn
 * Created by ndn
 * Created on 6/11/18.
 */
@Dao
interface MessDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(token: MessOauth)

    @Update
    suspend fun update(token: MessOauth)

    @Query("SELECT * FROM MessOauth")
    fun messOauthFlow(): Flow<MessOauth?>

    @Query("DELETE FROM MessOauth")
    suspend fun empty()
}