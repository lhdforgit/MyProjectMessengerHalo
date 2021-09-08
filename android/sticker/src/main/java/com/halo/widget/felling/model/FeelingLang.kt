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
data class FeelingLang(
    @SerializedName("key")
    var key: String? = null,
    @SerializedName("value")
    var value: String? = null
) : Parcelable