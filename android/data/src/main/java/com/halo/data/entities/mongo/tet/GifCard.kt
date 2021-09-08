package com.halo.data.entities.mongo.tet

import com.google.gson.annotations.SerializedName

/**
 * Create by ndn
 * Create on 1/6/21
 * com.halo.data.entities.mongo.tet
 */
data class GifCard(
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("path")
    var path: String? = null
)
