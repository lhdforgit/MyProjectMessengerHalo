/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.felling.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class StickerEntity(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("code")
    var code: String? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("url_full")
    var urlFull: String? = null,
    @SerializedName("type")
    var type: String? = null
) : Parcelable {

    fun getLinkUrl(): String {
        return urlFull?:""
    }
}