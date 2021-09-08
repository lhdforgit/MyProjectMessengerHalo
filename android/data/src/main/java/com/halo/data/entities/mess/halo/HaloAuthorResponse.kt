package com.halo.data.entities.mess.halo

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class HaloAuthorResponse {
    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("refreshToken")
    @Expose
    var refreshToken: String? = null
}