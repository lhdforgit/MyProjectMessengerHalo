/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.data.cache

import android.app.Application
import androidx.room.Room
import com.halo.data.cache.db.AppDb
import com.halo.data.cache.db.mess.MessDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author ndn
 * Created by ndn
 * Created on 5/16/18.
 */
@Module
object CacheModule {
    private const val DB_NAME = "halo.messenger.db"


    @JvmStatic
    @Singleton
    @Provides
    fun provideDb(app: Application): AppDb {
        return Room.databaseBuilder(app, AppDb::class.java, DB_NAME)
            // https://developer.android.com/reference/android/arch/persistence/room/RoomDatabase.Builder#fallbackToDestructiveMigration()
            //.fallbackToDestructiveMigration()
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideMessDao(db: AppDb): MessDao {
        return db.messDao()
    }
}