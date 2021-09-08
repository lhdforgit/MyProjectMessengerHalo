package com.halo.data.entities.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Create by ndn
 * Create on 6/8/21
 * com.halo.data.entities.mess.user
 */
data class User(
    // User Identification
    var userId: String? = null,
    // User provider. Available: messenger, hahalolo.
    var provider: String? = null,
    // User provider identification
    var providerId: String? = null,
    // User First name
    var firstName: String? = null,
    // User Last name
    var lastName: String? = null,
    // User avatar
    var avatar: String? = null,
    var cover: String?= null,
    var address: String?= null,
    var birthday: String?= null,
    var gender: String?= null,

    private var displayName: String? = null,

    private var fullName: String? = null
) {
    @SerializedName("presence")
    @Expose
    var presence: Presence? = null

    fun userName(): String {
        return fullName?.takeIf { it.isNotEmpty() } ?: kotlin.run {
            displayName?.takeIf { it.isNotEmpty() }?: "${firstName?:""} ${lastName?:""}"
        }
    }

    fun userStatus():String{
        return presence?.status?:""
    }

    fun isOnline(): Boolean {
        return false
    }
}