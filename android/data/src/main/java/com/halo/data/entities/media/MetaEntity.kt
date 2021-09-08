/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.data.entities.media

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * @author ngannd
 * Create by ngannd on 19/06/2019
 */
@Parcelize
data class MetaEntity(
    @SerializedName("width")
    var width: Int,
    @SerializedName("height")
    var height: Int
) : Parcelable {

    val ratio: Float
        get() = try {
            if (width == 0 || height == 0) {
                1.0f
            } else height.toFloat() / width.toFloat()
        } catch (e: Exception) {
            e.printStackTrace()
            1.0f
        }
}