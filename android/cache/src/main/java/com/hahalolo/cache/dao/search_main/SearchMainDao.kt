package com.hahalolo.cache.dao.search_main

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hahalolo.cache.entity.search_main.SearchEntity
import io.reactivex.Observable

@Dao
interface SearchMainDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg search: SearchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(searches: List<SearchEntity>)

    @Query("SELECT * FROM search_main_tb ORDER BY time DESC LIMIT :limit")
    fun getListSearchHistory(limit : Int) : LiveData<List<SearchEntity>>

    @Query("DELETE FROM search_main_tb")
    fun emptySearches()

    @Query("DELETE FROM search_main_tb  WHERE id = :id")
    fun deleteSearchById(id: String)

    @get:Query("SELECT COUNT(*) FROM search_main_tb")
    val countHistory: Observable<Int>
}