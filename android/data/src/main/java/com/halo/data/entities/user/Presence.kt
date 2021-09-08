package com.halo.data.entities.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Presence {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null
}