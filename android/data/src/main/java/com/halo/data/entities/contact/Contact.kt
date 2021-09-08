package com.halo.data.entities.contact

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.halo.data.entities.user.User

data class Contact(
    @SerializedName("contactId")
    @Expose
    var contactId: String? = null,

    @SerializedName("provider")
    @Expose
    var provider: String? = null,

    @SerializedName("providerId")
    @Expose
    var providerId: String? = null,

    @SerializedName("status")
    @Expose
    var status: String? = null,

    @SerializedName("requestedByUserId")
    @Expose
    var requestedByUserId: String? = null,

    @SerializedName("requestedAt")
    @Expose
    var requestedAt: String? = null,

    @SerializedName("acceptedAt")
    @Expose
    var acceptedAt: String? = null,

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null,

    @SerializedName("blockedByUserId")
    @Expose
    var blockedByUserId: Any? = null,

    @SerializedName("blockedAt")
    @Expose
    var blockedAt: Any? = null,

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null,

    @SerializedName("targetId")
    @Expose
    var targetId: String? = null,

    @SerializedName("firstName")
    @Expose
    var firstName: String? = null,

    @SerializedName("lastName")
    @Expose
    var lastName: String? = null,

    @SerializedName("fullName")
    @Expose
    var fullName: String? = null,

    @SerializedName("aliasName")
    @Expose
    var aliasName: String? = null,

    @SerializedName("displayName")
    @Expose
    var displayName: String? = null,

    @SerializedName("avatar")
    @Expose
    var avatar: String? = null,

    @SerializedName("cover")
    @Expose
    var cover: String? = null,

    @SerializedName("birthday")
    @Expose
    var birthday: String? = null,

    @SerializedName("gender")
    @Expose
    var gender: String? = null,

    @SerializedName("address")
    @Expose
    var address: String? = null,

    private val city: Any? = null,

    @SerializedName("userId")
    @Expose val userId: String? = null,

    @SerializedName("presence")
    @Expose
    val presence: Presence? = null
){
    companion object{
        @JvmStatic
        fun transformUserToContact(user: User) : Contact {
            val contact = Contact()
            contact.fullName = user.userName()
            contact.avatar = user.avatar
            contact.cover = user.cover
            contact.address = user.address
            contact.birthday = user.birthday
            contact.gender = user.gender
            contact.aliasName = ""

            return contact
        }
    }
}

data class Presence(
    @SerializedName("updatedAt")
    @Expose
    private val updatedAt: String? = null,

    @SerializedName("status")
    @Expose
    private val status: String? = null
)
