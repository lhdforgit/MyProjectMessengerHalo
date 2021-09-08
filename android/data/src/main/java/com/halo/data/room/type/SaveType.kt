package com.halo.data.room.type

import androidx.annotation.StringDef
import com.halo.data.room.type.SaveType.Companion.CACHE
import com.halo.data.room.type.SaveType.Companion.NEW
import com.halo.data.room.type.SaveType.Companion.OLD

@StringDef(CACHE, NEW , OLD)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class SaveType{
    companion object {
        //dữ liệu lưu trữ trong data base
        const val CACHE = "cache"
        //dữ liệu mới đc cập nhật lại
        const val NEW = "new"
        //dữ liệu đc cũ lấy ra để hiển thị, chưa đc cập nhật mới
        const val OLD = "old"
    }
}