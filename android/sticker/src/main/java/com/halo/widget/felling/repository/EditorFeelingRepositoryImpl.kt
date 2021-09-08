/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.felling.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.common.collect.Iterators
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.halo.widget.felling.model.FeelingEntity
import com.halo.widget.felling.model.LastStickerEntity
import com.halo.widget.felling.model.StickerEntity
import com.halo.widget.felling.model.StickerPackEntity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class EditorFeelingRepositoryImpl
private constructor(val context: Context) : EditorFeelingRepository {

    private var stickerEntityPackList: MutableList<StickerPackEntity> = ArrayList()
    private var feelingEntityList: MutableLiveData<MutableList<FeelingEntity>> = MutableLiveData()

    private var sharedPref: SharedPreferences? = null

    init {
        try {
            sharedPref = context.getSharedPreferences(
                EditorFeelingRepositoryImpl_TARGET_PREF,
                Context.MODE_PRIVATE
            )
            val buf = StringBuilder()
            val json = context.assets.open(DATA_STICKER)
            val `in` = BufferedReader(InputStreamReader(json, "UTF-8"))
            var str: String?
            while (`in`.readLine().also { str = it } != null) {
                buf.append(str)
            }
            val listType = object :
                TypeToken<List<StickerPackEntity?>?>() {}.type
            stickerEntityPackList = Gson().fromJson(buf.toString(), listType)
            stickerEntityPackList.reverse()
            `in`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    override fun getFeelingModel(code: String?): FeelingEntity? {
        try {
            val buf = StringBuilder()
            val json = context.assets.open(DATA_FEELING)
            val `in` = BufferedReader(InputStreamReader(json, "UTF-8"))
            var str: String?
            while (`in`.readLine().also { str = it } != null) {
                buf.append(str)
            }
            val listType = object : TypeToken<MutableList<FeelingEntity>>() {}.type
            val feelingList = Gson().fromJson<MutableList<FeelingEntity>>(
                buf.toString(),
                listType
            )
            feelingEntityList.postValue(feelingList)
            `in`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        feelingEntityList.value?.takeIf {
            it.isNotEmpty()
        }?.run {
            val index = Iterators.indexOf(this.iterator()) {
                TextUtils.equals(it?.code, code)
            }
            if (index >= 0) return this[index]
        }
        return null
    }

    override fun addLastStickerEntity(stickerEntity: StickerEntity) {
        val content = sharedPref?.getString(LAST_STICKER_KEY, null)
        val listType = object : TypeToken<MutableList<LastStickerEntity>>() {}.type
        try {
            var currentList = Gson().fromJson<MutableList<LastStickerEntity>>(content, listType)
                ?: mutableListOf()
            val index = Iterators.indexOf(currentList.iterator()) {
                TextUtils.equals(stickerEntity.id, it?.sticker?.id)
            }
            if (index >= 0) {
                currentList.set(index, LastStickerEntity(System.currentTimeMillis(), stickerEntity))
            } else {
                currentList.add(LastStickerEntity(System.currentTimeMillis(), stickerEntity))
            }
            if (currentList.size > LAST_STICKER_SIZE) {
                currentList = currentList.subList(0, LAST_STICKER_SIZE)
            }
            currentList.sortBy { it.lastTime }
            sharedPref?.edit()?.putString(LAST_STICKER_KEY, Gson().toJson(currentList, listType))?.apply()
            lastStickersLive.value = currentList
        } catch (e: Exception) {

        }
    }

    private val lastStickersLive =  MutableLiveData<MutableList<LastStickerEntity>>()

    override fun getLastStickerEntity(): LiveData<MutableList<LastStickerEntity>> {
        if (lastStickersLive.value==null){
            val listType = object : TypeToken<MutableList<LastStickerEntity>>() {}.type
            try {
                val content = sharedPref?.getString(LAST_STICKER_KEY, null)
                val currentList = Gson().fromJson<MutableList<LastStickerEntity>>(content, listType) ?: mutableListOf()
                lastStickersLive.value = currentList
            } catch (e: Exception) {
            }
        }
        return lastStickersLive
    }

    @SuppressLint("DefaultLocale")
    override fun feelingList(query: String?): LiveData<MutableList<FeelingEntity>> {
        return Transformations.map(FeelingLiveData(context)) {
            it?.filter { entity ->
                if (TextUtils.isEmpty(query)) {
                    true
                } else {
                    entity.getContentSearch().toLowerCase().contains(query?.toLowerCase() ?: "")
                }
            }?.toMutableList() ?: mutableListOf()
        }
    }

    override fun stickerPackList(): List<StickerPackEntity> {
        return stickerEntityPackList
    }

    companion object {
        const val DATA_FEELING = "editor/feeling/feeling.json"
        const val DATA_STICKER = "editor/sticker/stickers.json"
        private const val EditorFeelingRepositoryImpl_TARGET_PREF = "EditorFeelingRepositoryImpl-target-pref"
        private const val LAST_STICKER_KEY = "LAST_STICKER-target-pref-key"
        private const val LAST_STICKER_SIZE = 20
        private var entity: EditorFeelingRepository? = null

        @JvmStatic
        fun getInstance(context: Context): EditorFeelingRepository {
            if (entity == null) {
                entity = EditorFeelingRepositoryImpl(context)
            }
            return entity!!
        }
    }
}