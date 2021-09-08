/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.data

import android.app.Application
import androidx.room.Room
import com.halo.data.room.dao.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author ndn
 * Created by ndn
 * Created on 5/16/18.
 */
@Module
object ChatRoomModule {
    private const val CHAT_DB_NAME = "hahalolo.halome.db"

    @Singleton
    @Provides
    fun provideDb(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, CHAT_DB_NAME)
            .build()
    }

    @Singleton
    @Provides
    fun provideChannelDao(db: AppDatabase): ChannelDao {
        return db.channelDao()
    }

    @Singleton
    @Provides
    fun provideReadStateDao(db: AppDatabase): ReadStateDao {
        return db.readStateDao()
    }

    @Singleton
    @Provides
    fun provideMessageDao(db: AppDatabase): MessageDao {
        return db.messageDao()
    }

    @Singleton
    @Provides
    fun provideMemberDao(db: AppDatabase): MemberDao {
        return db.memberDao()
    }

    @Singleton
    @Provides
    fun provideUserDao(db: AppDatabase): UserDao {
        return db.userDao()
    }

    @Singleton
    @Provides
    fun provideMentionDao(db: AppDatabase): MentionDao {
        return db.mentionDao()
    }
}