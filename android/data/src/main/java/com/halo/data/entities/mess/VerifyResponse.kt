package com.halo.data.entities.mess

data class VerifyResponse(
    var token: String? = null,
    var refreshToken: String? = null,
    var ok: String? = null,
    var message: String? = null
)