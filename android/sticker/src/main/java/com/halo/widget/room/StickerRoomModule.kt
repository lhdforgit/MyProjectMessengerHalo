/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.room

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.halo.widget.felling.model.StickerPackEntity
import com.halo.widget.felling.repository.EditorFeelingRepositoryImpl
import com.halo.widget.room.dao.StickerDao
import com.halo.widget.room.table.StickerPackTable
import com.halo.widget.room.table.StickerTable
import dagger.Module
import dagger.Provides
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Singleton

/**
 * @author ndn
 * Created by ndn
 * Created on 5/16/18.
 */
@Module
object StickerRoomModule {
    private const val STICKER_DB_NAME = "hahalolo_messenger.sticker.db"

    @Singleton
    @Provides
    fun provideDb(app: Application): StickerDatabase {
        return Room.databaseBuilder(app, StickerDatabase::class.java, STICKER_DB_NAME)
            .allowMainThreadQueries()
//            .addCallback(object : RoomDatabase.Callback() {
//                override fun onCreate(db: SupportSQLiteDatabase) {
//                    super.onCreate(db)
//                    createDefaultDateBase(app, db)
//                }
//
//                override fun onOpen(db: SupportSQLiteDatabase) {
//                    super.onOpen(db)
//                    val count1 = db.query("SELECT * FROM StickerPackTable").count
//                    val count2 = db.query("SELECT * FROM StickerTable").count
//                    if (count1 <= 0 || count2 <= 0) {
//                        createDefaultDateBase(app, db)
//                    }
//                }
//            })
            .build()
    }

    private fun createDefaultDateBase(app: Application, db: SupportSQLiteDatabase) {
        try {
            val buf = StringBuilder()
            val json = app.assets.open(EditorFeelingRepositoryImpl.DATA_STICKER)
            val `in` = BufferedReader(InputStreamReader(json, "UTF-8"))
            var str: String?
            while (`in`.readLine().also { str = it } != null) {
                buf.append(str)
            }
            val listType = object :
                TypeToken<List<StickerPackEntity?>?>() {}.type
            val stickerEntityPackList: MutableList<StickerPackEntity> =
                Gson().fromJson(buf.toString(), listType)
            stickerEntityPackList.reverse()

            val stickers = mutableListOf<StickerTable>()
            val packs = stickerEntityPackList.map { pack ->
                pack.stickers?.map { sticker ->
                    StickerTable(sticker.id ?: "").apply {
                        this.packId = pack.id
                        this.stickerImage = sticker.image
                        this.stickerUrl = sticker.urlFull
                    }
                }?.takeIf { it.isNotEmpty() }?.run {
                    stickers.addAll(this)
                }
                StickerPackTable(pack.id ?: "").apply {
                    this.packIcon = pack.image
                    this.packUrl = pack.image
                    this.packName = pack.name
                }
            }
            packs.forEach {
                try {
                    db.execSQL("INSERT INTO StickerPackTable(packId, packIcon, packUrl, packName, packEnable) VALUES ('${it.id}', '${it.packIcon}', '${it.packUrl}', '${it.packName}', ${it.packEnable})")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            stickers.forEach {
                try {
                    db.execSQL("INSERT INTO StickerTable(stickerId, packId, stickerImage, stickerUrl, stickerType) VALUES ('${it.id}', '${it.packId}', '${it.stickerImage}', '${it.stickerUrl}', ${it.stickerType} )")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            `in`.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Singleton
    @Provides
    fun provideStickerDao(db: StickerDatabase): StickerDao {
        return db.stickerDao()
    }
}