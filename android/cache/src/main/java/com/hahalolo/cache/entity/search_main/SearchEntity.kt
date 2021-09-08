package com.hahalolo.cache.entity.search_main

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "search_main_tb")
@Parcelize
data class SearchEntity(
    @PrimaryKey
    @SerializedName("id")
    var id: String = "",

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("type")
    var type: String? = null,

    @SerializedName("avatar")
    var avatar: String? = null,

    @SerializedName("time")
    var time: Long? = null,
) : Parcelable {
}